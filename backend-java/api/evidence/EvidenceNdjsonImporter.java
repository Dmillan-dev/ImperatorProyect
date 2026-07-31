package imperator.api.evidence;

import imperator.application.exceptions.DuplicateEvidenceException;
import imperator.application.exceptions.ValidationException;
import imperator.application.importevidence.ImportEvidenceCommand;
import imperator.application.importevidence.ImportEvidenceResult;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.ports.in.ImportEvidenceInputPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;

final class EvidenceNdjsonImporter {
    static final int MAX_PAYLOAD_BYTES = 5 * 1024 * 1024;
    static final int MAX_LINES = 10_000;
    static final int MAX_LINE_BYTES = 64 * 1024;

    private static final String MVP_CASE = "DRC-AOA-001";
    private static final String SCHEMA_VERSION = "1";
    private static final int MAX_METADATA_ENTRIES = 32;
    private static final int MAX_METADATA_KEY_LENGTH = 64;
    private static final int MAX_METADATA_VALUE_LENGTH = 512;
    private static final Pattern NON_TOKEN = Pattern.compile("[^a-z0-9]+");
    private static final Set<String> ROOT_FIELDS = Set.of(
            "id",
            "schema_version",
            "timestamp",
            "ingested_at",
            "source",
            "source_type",
            "source_object_ref",
            "entity",
            "event_type",
            "severity",
            "actor",
            "evidence_type",
            "case_hint",
            "observed_fact",
            "business_meaning",
            "correlation_key",
            "sensitivity",
            "confidence",
            "freshness",
            "review_status",
            "metadata",
            "raw_payload"
    );
    private static final Set<String> RAW_PAYLOAD_FIELDS = Set.of(
            "mode",
            "hash",
            "ref",
            "redaction_status"
    );
    private static final Set<String> SOURCE_TYPES = Set.of(
            "business_context",
            "code",
            "deployment",
            "cloud_cost",
            "ai_usage",
            "usage",
            "manual"
    );
    private static final Map<String, String> SOURCES = Map.ofEntries(
            Map.entry("jira", "Jira"),
            Map.entry("github", "GitHub"),
            Map.entry("aws", "AWS"),
            Map.entry("openai", "OpenAI"),
            Map.entry("anthropic", "Anthropic Claude"),
            Map.entry("anthropic_claude", "Anthropic Claude"),
            Map.entry("claude", "Anthropic Claude"),
            Map.entry("openai_anthropic_claude", "OpenAI + Anthropic Claude"),
            Map.entry("deployment", "Deployment"),
            Map.entry("manual", "Manual"),
            Map.entry("manual_import", "Manual"),
            Map.entry("product_analytics", "Product Analytics")
    );
    private static final Set<String> EVENT_TYPES = Set.of(
            "business_context_requested",
            "decision_created",
            "decision_status_observed",
            "business_owner_observed",
            "business_value_proxy_observed",
            "code_change_merged",
            "code_review_observed",
            "deployment_reference_observed",
            "technical_owner_observed",
            "implementation_feasibility_observed",
            "cloud_cost_observed",
            "cloud_resource_tag_observed",
            "cloud_utilization_observed",
            "cloud_owner_observed",
            "cloud_attribution_gap_observed",
            "ai_usage_observed",
            "ai_cost_observed",
            "ai_model_observed",
            "ai_application_observed",
            "ai_quality_review_observed",
            "ai_sensitive_payload_rejected",
            "usage_signal_observed",
            "active_user_count_observed",
            "value_proxy_observed",
            "usage_signal_missing",
            "roi_assumption_observed",
            "approval_path_observed"
    );
    private static final Map<String, String> EVIDENCE_TYPES = Map.ofEntries(
            Map.entry("business_context", "business_context"),
            Map.entry("business_context_evidence", "business_context"),
            Map.entry("code_deployment", "code_deployment"),
            Map.entry("code_and_deployment_evidence", "code_deployment"),
            Map.entry("cloud_cost", "cloud_cost"),
            Map.entry("cloud_cost_evidence", "cloud_cost"),
            Map.entry("cloud_utilization", "cloud_utilization"),
            Map.entry("cloud_utilization_evidence", "cloud_utilization"),
            Map.entry("ai_consumption", "ai_consumption"),
            Map.entry("ai_consumption_evidence", "ai_consumption"),
            Map.entry("ai_quality_review", "ai_quality_review"),
            Map.entry("ai_quality_review_evidence", "ai_quality_review"),
            Map.entry("usage_value_signal", "usage_value_signal"),
            Map.entry("usage_or_value_signal_evidence", "usage_value_signal"),
            Map.entry("owner_approval", "owner_approval"),
            Map.entry("owner_and_approval_evidence", "owner_approval"),
            Map.entry("roi_assumption", "roi_assumption")
    );
    private static final Set<String> FRESHNESS_VALUES = Set.of(
            "fresh",
            "stale",
            "unknown",
            "not_applicable"
    );
    private static final Set<String> FORBIDDEN_METADATA_KEYS = Set.of(
            "prompt",
            "completion",
            "raw_prompt",
            "raw_completion",
            "request_body",
            "response_body",
            "raw_payload",
            "content"
    );

    private final Supplier<ImportEvidenceInputPort> inputPortSupplier;
    private final Clock clock;
    private final JsonMapper jsonMapper;

    EvidenceNdjsonImporter(Supplier<ImportEvidenceInputPort> inputPortSupplier, Clock clock) {
        this(inputPortSupplier, clock, JsonMapper.shared());
    }

    EvidenceNdjsonImporter(
            Supplier<ImportEvidenceInputPort> inputPortSupplier,
            Clock clock,
            JsonMapper jsonMapper
    ) {
        this.inputPortSupplier = Objects.requireNonNull(inputPortSupplier, "Input port supplier is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
        this.jsonMapper = Objects.requireNonNull(jsonMapper, "JSON mapper is required")
                .rebuild()
                .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();
    }

    EvidenceImportResponse importPayload(byte[] payload) {
        List<String> lines = requestLines(payload);
        ImportEvidenceInputPort inputPort = inputPortSupplier.get();
        if (inputPort == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        List<EvidenceImportItemResponse> results = new ArrayList<>(lines.size());
        for (int index = 0; index < lines.size(); index++) {
            results.add(importLine(inputPort, index + 1, lines.get(index)));
        }
        return EvidenceImportResponse.from(results);
    }

    private EvidenceImportItemResponse importLine(
            ImportEvidenceInputPort inputPort,
            int lineNumber,
            String rawLine
    ) {
        EvidenceImportLine line;
        ImportEvidenceCommand command;
        try {
            line = parseLine(rawLine);
            command = toCommand(line);
        } catch (LineRejectedException exception) {
            return EvidenceImportItemResponse.rejected(
                    lineNumber,
                    exception.evidenceId(),
                    exception.reason()
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            return EvidenceImportItemResponse.rejected(lineNumber, null, "VALIDATION_FAILED");
        }

        try {
            ImportEvidenceResult result = inputPort.importEvidence(command);
            return EvidenceImportItemResponse.accepted(lineNumber, result.evidenceId().value());
        } catch (DuplicateEvidenceException exception) {
            return EvidenceImportItemResponse.rejected(lineNumber, line.id(), "DUPLICATE");
        } catch (ValidationException exception) {
            return EvidenceImportItemResponse.rejected(lineNumber, line.id(), "VALIDATION_FAILED");
        }
    }

    private List<String> requestLines(byte[] payload) {
        if (payload == null || payload.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (payload.length > MAX_PAYLOAD_BYTES) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE);
        }

        String body = decodeUtf8(payload);
        if (body.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String[] split = body.split("\\n", -1);
        int length = split.length;
        if (length > 0 && stripCarriageReturn(split[length - 1]).isEmpty()) {
            length--;
        }
        if (length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (length > MAX_LINES) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE);
        }

        List<String> lines = new ArrayList<>(length);
        for (int index = 0; index < length; index++) {
            String line = stripCarriageReturn(split[index]);
            if (line.getBytes(StandardCharsets.UTF_8).length > MAX_LINE_BYTES) {
                throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE);
            }
            lines.add(line);
        }
        return lines;
    }

    private String decodeUtf8(byte[] payload) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(payload))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, exception);
        }
    }

    private EvidenceImportLine parseLine(String rawLine) {
        if (rawLine.isBlank()) {
            throw rejected("MALFORMED_JSON", null);
        }

        JsonNode root;
        try {
            root = jsonMapper.readTree(rawLine);
        } catch (JacksonException exception) {
            throw rejected("MALFORMED_JSON", null);
        }
        if (root == null || !root.isObject()) {
            throw rejected("MALFORMED_JSON", null);
        }

        UUID id = parseUuid(requiredText(root, "id", null), null);
        rejectUnknownFields(root, ROOT_FIELDS, id);

        String schemaVersion = requiredText(root, "schema_version", id);
        if (!SCHEMA_VERSION.equals(schemaVersion)) {
            throw rejected("SCHEMA_VERSION_UNSUPPORTED", id);
        }

        Instant timestamp = parseInstant(requiredText(root, "timestamp", id), id);
        Instant sourceIngestedAt = optionalInstant(root, "ingested_at", id);
        String source = normalizedSource(requiredText(root, "source", id), id);
        String sourceType = controlledToken(
                requiredText(root, "source_type", id),
                SOURCE_TYPES,
                "UNSUPPORTED_SOURCE",
                id
        );
        String eventType = controlledToken(
                requiredText(root, "event_type", id),
                EVENT_TYPES,
                "UNSUPPORTED_EVENT_TYPE",
                id
        );
        String evidenceType = normalizedEvidenceType(requiredText(root, "evidence_type", id), id);
        String caseHint = optionalText(root, "case_hint", id);
        if (caseHint != null && !MVP_CASE.equals(caseHint)) {
            throw rejected("UNSUPPORTED_CASE", id);
        }
        String correlationKey = requiredText(root, "correlation_key", id);
        if (!MVP_CASE.equals(correlationKey)) {
            throw rejected("UNSUPPORTED_CASE", id);
        }

        String sensitivity = token(requiredText(root, "sensitivity", id)).toUpperCase(Locale.ROOT);
        if ("RESTRICTED".equals(sensitivity)) {
            throw rejected("RESTRICTED_EVIDENCE", id);
        }
        String freshness = controlledToken(
                requiredText(root, "freshness", id),
                FRESHNESS_VALUES,
                "INVALID_FRESHNESS",
                id
        );
        String reviewStatus = token(requiredText(root, "review_status", id)).toUpperCase(Locale.ROOT);
        validateFreshnessPolicy(freshness, reviewStatus, id);

        JsonNode rawPayload = requiredObject(root, "raw_payload", id);
        rejectUnknownFields(rawPayload, RAW_PAYLOAD_FIELDS, id);
        String rawPayloadMode = token(requiredText(rawPayload, "mode", id));
        if (!"not_stored".equals(rawPayloadMode)) {
            throw rejected("RAW_PAYLOAD_FORBIDDEN", id);
        }

        Map<String, String> metadata = metadata(root, id);
        copyOptionalRawPayloadMetadata(rawPayload, metadata, id);

        return new EvidenceImportLine(
                id,
                schemaVersion,
                timestamp,
                sourceIngestedAt,
                source,
                sourceType,
                requiredText(root, "source_object_ref", id),
                requiredText(root, "entity", id),
                eventType,
                requiredText(root, "severity", id),
                optionalText(root, "actor", id),
                evidenceType,
                caseHint,
                requiredText(root, "observed_fact", id),
                requiredText(root, "business_meaning", id),
                correlationKey,
                sensitivity,
                requiredText(root, "confidence", id),
                freshness,
                reviewStatus,
                rawPayloadMode,
                Map.copyOf(metadata)
        );
    }

    private ImportEvidenceCommand toCommand(EvidenceImportLine line) {
        Map<String, String> metadata = new LinkedHashMap<>(line.metadata());
        metadata.put("schema_version", line.schemaVersion());
        metadata.put("freshness", line.freshness());
        metadata.put("ingested_at", clock.instant().toString());
        if (line.sourceIngestedAt() != null) {
            metadata.put("source_ingested_at", line.sourceIngestedAt().toString());
        }
        if (line.caseHint() != null) {
            metadata.put("case_hint", line.caseHint());
        }

        return new ImportEvidenceCommand(
                new EvidenceId(line.id()),
                new Timestamp(line.timestamp()),
                line.source(),
                line.sourceType(),
                line.sourceObjectRef(),
                line.entity(),
                line.eventType(),
                new Severity(line.severity()),
                line.actor(),
                line.evidenceType(),
                line.observedFact(),
                line.businessMeaning(),
                line.correlationKey(),
                line.sensitivity(),
                line.confidence(),
                line.reviewStatus(),
                line.rawPayloadMode(),
                Map.copyOf(metadata)
        );
    }

    private Map<String, String> metadata(JsonNode root, UUID id) {
        JsonNode metadataNode = requiredObject(root, "metadata", id);
        if (metadataNode.size() > MAX_METADATA_ENTRIES) {
            throw rejected("INVALID_METADATA", id);
        }

        Map<String, String> metadata = new LinkedHashMap<>();
        for (Map.Entry<String, JsonNode> entry : metadataNode.properties()) {
            String key = entry.getKey().trim();
            JsonNode valueNode = entry.getValue();
            if (key.isEmpty()
                    || key.length() > MAX_METADATA_KEY_LENGTH
                    || FORBIDDEN_METADATA_KEYS.contains(token(key))
                    || valueNode == null
                    || !valueNode.isString()) {
                throw rejected("INVALID_METADATA", id);
            }
            String value = valueNode.asString().trim();
            if (value.length() > MAX_METADATA_VALUE_LENGTH) {
                throw rejected("INVALID_METADATA", id);
            }
            metadata.put(key, value);
        }
        return metadata;
    }

    private void copyOptionalRawPayloadMetadata(
            JsonNode rawPayload,
            Map<String, String> metadata,
            UUID id
    ) {
        for (String field : List.of("hash", "ref", "redaction_status")) {
            String value = optionalText(rawPayload, field, id);
            if (value != null) {
                metadata.put("raw_payload_" + field, value);
            }
        }
    }

    private void validateFreshnessPolicy(String freshness, String reviewStatus, UUID id) {
        if (("stale".equals(freshness) && !"STALE".equals(reviewStatus))
                || (!"stale".equals(freshness) && "STALE".equals(reviewStatus))
                || ("unknown".equals(freshness) && !"NEEDS_REVIEW".equals(reviewStatus))) {
            throw rejected("STALE_POLICY", id);
        }
    }

    private String normalizedSource(String value, UUID id) {
        String source = SOURCES.get(token(value));
        if (source == null) {
            throw rejected("UNSUPPORTED_SOURCE", id);
        }
        return source;
    }

    private String normalizedEvidenceType(String value, UUID id) {
        String evidenceType = EVIDENCE_TYPES.get(token(value));
        if (evidenceType == null) {
            throw rejected("UNSUPPORTED_EVIDENCE_TYPE", id);
        }
        return evidenceType;
    }

    private String controlledToken(
            String value,
            Set<String> allowed,
            String rejectionReason,
            UUID id
    ) {
        String normalized = token(value);
        if (!allowed.contains(normalized)) {
            throw rejected(rejectionReason, id);
        }
        return normalized;
    }

    private String requiredText(JsonNode parent, String field, UUID id) {
        String value = optionalText(parent, field, id);
        if (value == null) {
            throw rejected("INVALID_FIELD", id);
        }
        return value;
    }

    private String optionalText(JsonNode parent, String field, UUID id) {
        JsonNode node = parent.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isString()) {
            throw rejected("INVALID_FIELD", id);
        }
        String value = node.asString().trim();
        if (value.isEmpty()) {
            throw rejected("INVALID_FIELD", id);
        }
        return value;
    }

    private JsonNode requiredObject(JsonNode parent, String field, UUID id) {
        JsonNode node = parent.get(field);
        if (node == null || !node.isObject()) {
            throw rejected("INVALID_FIELD", id);
        }
        return node;
    }

    private void rejectUnknownFields(JsonNode object, Set<String> allowed, UUID id) {
        boolean unsupported = object.properties().stream()
                .map(Map.Entry::getKey)
                .anyMatch(field -> !allowed.contains(field));
        if (unsupported) {
            throw rejected("INVALID_FIELD", id);
        }
    }

    private UUID parseUuid(String value, UUID id) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw rejected("INVALID_FIELD", id);
        }
    }

    private Instant parseInstant(String value, UUID id) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException exception) {
            throw rejected("INVALID_FIELD", id);
        }
    }

    private Instant optionalInstant(JsonNode parent, String field, UUID id) {
        String value = optionalText(parent, field, id);
        return value == null ? null : parseInstant(value, id);
    }

    private String token(String value) {
        String lower = value.trim().toLowerCase(Locale.ROOT);
        return NON_TOKEN.matcher(lower).replaceAll("_").replaceAll("^_|_$", "");
    }

    private String stripCarriageReturn(String line) {
        return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
    }

    private LineRejectedException rejected(String reason, UUID evidenceId) {
        return new LineRejectedException(reason, evidenceId);
    }

    private static final class LineRejectedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private final String reason;
        private final UUID evidenceId;

        private LineRejectedException(String reason, UUID evidenceId) {
            super(reason);
            this.reason = reason;
            this.evidenceId = evidenceId;
        }

        private String reason() {
            return reason;
        }

        private UUID evidenceId() {
            return evidenceId;
        }
    }
}
