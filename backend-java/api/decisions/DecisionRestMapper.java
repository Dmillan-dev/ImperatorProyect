package imperator.api.decisions;

import imperator.api.errors.ApiContractException;
import imperator.api.security.JwtActorContextResolver;
import imperator.application.composecase.ComposeDrcAoa001Command;
import imperator.application.composecase.ComposeDrcAoa001Result;
import imperator.application.query.DecisionDetail;
import imperator.application.query.DecisionRoiView;
import imperator.application.query.DecisionSummary;
import imperator.application.query.DecisionTimelineItem;
import imperator.application.query.EvidenceSummary;
import imperator.application.query.PageRequest;
import imperator.application.query.PageResult;
import imperator.application.query.SortDirection;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import org.springframework.http.HttpStatus;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

final class DecisionRestMapper {
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final Set<String> COMPOSITION_FIELDS = Set.of(
            "caseId",
            "decisionId",
            "recommendationId",
            "originatingEvidenceId",
            "evidenceIds",
            "title",
            "businessNeed",
            "ownerId",
            "requiredApproverId",
            "decisionCreatedAt",
            "recommendationGeneratedAt"
    );

    private DecisionRestMapper() {
    }

    static DecisionId decisionId(String raw) {
        return new DecisionId(canonicalUuid(raw));
    }

    static DecisionApiModels.ComposeDrcAoa001Request compositionRequest(byte[] body) {
        if (body == null || body.length == 0) {
            throw unprocessable("COMPOSITION_REQUEST_INVALID", "Composition request must be a JSON object");
        }
        JsonNode root;
        try {
            root = JSON.readTree(body);
        } catch (JacksonException exception) {
            throw unprocessable("COMPOSITION_REQUEST_INVALID", "Composition request must contain valid JSON");
        }
        if (root == null || !root.isObject()) {
            throw unprocessable("COMPOSITION_REQUEST_INVALID", "Composition request must be a JSON object");
        }
        Set<String> actualFields = root.properties().stream()
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (!COMPOSITION_FIELDS.equals(actualFields)) {
            throw unprocessable(
                    "COMPOSITION_FIELDS_INVALID",
                    "Composition request must contain exactly the frozen fields"
            );
        }

        return new DecisionApiModels.ComposeDrcAoa001Request(
                requiredText(root, "caseId"),
                requiredText(root, "decisionId"),
                requiredText(root, "recommendationId"),
                requiredText(root, "originatingEvidenceId"),
                evidenceIds(root.get("evidenceIds")),
                requiredText(root, "title"),
                requiredText(root, "businessNeed"),
                requiredText(root, "ownerId"),
                requiredText(root, "requiredApproverId"),
                requiredText(root, "decisionCreatedAt"),
                requiredText(root, "recommendationGeneratedAt")
        );
    }

    static ComposeDrcAoa001Command compositionCommand(
            DecisionApiModels.ComposeDrcAoa001Request request,
            JwtActorContextResolver.AuthenticatedActor actor
    ) {
        UUID decisionId = compositionUuid(request.decisionId(), true);
        UUID recommendationId = compositionUuid(request.recommendationId(), true);
        UUID originId = compositionUuid(request.originatingEvidenceId(), false);
        UUID ownerId = compositionUuid(request.ownerId(), false);
        UUID approverId = compositionUuid(request.requiredApproverId(), false);
        if (!approverId.equals(actor.actorId().value())) {
            throw unprocessable(
                    "COMPOSITION_APPROVER_MISMATCH",
                    "Required approver must equal the authenticated actor"
            );
        }

        Set<EvidenceId> evidenceIds = new LinkedHashSet<>();
        for (String value : request.evidenceIds()) {
            EvidenceId evidenceId = new EvidenceId(compositionUuid(value, false));
            if (!evidenceIds.add(evidenceId)) {
                throw unprocessable(
                        "COMPOSITION_EVIDENCE_DUPLICATE",
                        "Composition Evidence ids must be distinct"
                );
            }
        }

        return new ComposeDrcAoa001Command(
                request.caseId(),
                new DecisionId(decisionId),
                new RecommendationId(recommendationId),
                new EvidenceId(originId),
                Set.copyOf(evidenceIds),
                request.title(),
                request.businessNeed(),
                new UserId(ownerId),
                new UserId(approverId),
                actor.actorId(),
                timestamp(request.decisionCreatedAt()),
                timestamp(request.recommendationGeneratedAt())
        );
    }

    static DecisionApiModels.ComposeDrcAoa001Response response(ComposeDrcAoa001Result result) {
        String decisionId = uuid(result.decisionId().value());
        return new DecisionApiModels.ComposeDrcAoa001Response(
                result.caseId(),
                decisionId,
                result.decisionStatus().value(),
                uuid(result.recommendationId().value()),
                result.recommendationType().value(),
                money(result.estimatedAnnualizedSavings()),
                result.confidence().percentage(),
                result.risk().value(),
                result.evidenceCount(),
                "/decisions/" + decisionId,
                result.replayed(),
                result.resumed()
        );
    }

    static PageRequest pageRequest(
            String page,
            String size,
            String sort,
            String direction,
            String defaultSort,
            SortDirection defaultDirection
    ) {
        try {
            int parsedPage = page == null ? 0 : Integer.parseInt(page);
            int parsedSize = size == null ? 20 : Integer.parseInt(size);
            String parsedSort = sort == null ? defaultSort : sort;
            SortDirection parsedDirection = direction == null
                    ? defaultDirection
                    : SortDirection.valueOf(direction.toUpperCase(Locale.ROOT));
            return new PageRequest(parsedPage, parsedSize, parsedSort, parsedDirection);
        } catch (IllegalArgumentException exception) {
            throw badRequest("INVALID_PAGINATION", "Pagination parameters are invalid");
        }
    }

    static DecisionApiModels.DecisionSummaryResponse response(DecisionSummary item) {
        return new DecisionApiModels.DecisionSummaryResponse(
                uuid(item.decisionId().value()), item.caseId(), item.title(), item.businessNeed(),
                item.status().value(), uuid(item.ownerId().value()), uuid(item.requiredApproverId().value()),
                item.recommendationId().map(value -> uuid(value.value())).orElse(null),
                item.createdAt().value().toString(), item.updatedAt().value().toString()
        );
    }

    static DecisionApiModels.DecisionDetailResponse response(DecisionDetail item) {
        return new DecisionApiModels.DecisionDetailResponse(
                uuid(item.decisionId().value()), item.caseId(), item.title(), item.businessNeed(),
                item.status().value(), uuid(item.ownerId().value()), uuid(item.requiredApproverId().value()),
                item.recommendationId().map(value -> uuid(value.value())).orElse(null),
                item.createdAt().value().toString(), item.updatedAt().value().toString(),
                uuid(item.originatingEvidenceId().value()), item.evidenceIds().stream()
                        .map(EvidenceId::value).map(DecisionRestMapper::uuid).toList(),
                item.reviewedBy().map(value -> uuid(value.value())).orElse(null),
                item.reviewedAt().map(value -> value.value().toString()).orElse(null),
                item.reviewReason().orElse(null)
        );
    }

    static DecisionApiModels.TimelineItemResponse response(DecisionTimelineItem item) {
        return new DecisionApiModels.TimelineItemResponse(
                item.type(), uuid(item.referenceId()), item.occurredAt().value().toString(), item.summary(),
                item.source().orElse(null), item.actor().orElse(null), item.confidenceLabel().orElse(null),
                item.confidencePercentage().orElse(null), item.evidenceIds().stream()
                        .map(EvidenceId::value).map(DecisionRestMapper::uuid).toList()
        );
    }

    static DecisionApiModels.EvidenceSummaryResponse response(EvidenceSummary item) {
        return new DecisionApiModels.EvidenceSummaryResponse(
                uuid(item.evidenceId().value()), item.timestamp().value().toString(), item.source(),
                item.sourceType(), item.sourceObjectRef(), item.entity(), item.eventType(),
                item.severity().value(), item.actor(), item.evidenceType(), item.observedFact(),
                item.businessMeaning(), item.correlationKey(), item.sensitivity(), item.confidence(),
                item.reviewStatus()
        );
    }

    static DecisionApiModels.RoiResponse response(DecisionRoiView item) {
        return new DecisionApiModels.RoiResponse(
                uuid(item.decisionId().value()), uuid(item.recommendationId().value()),
                money(item.currentMonthlyCost()), money(item.projectedMonthlyCost()),
                money(item.transitionCost()), money(item.estimatedMonthlyRecovery()),
                money(item.estimatedAnnualizedRecovery()), item.confidence().percentage(),
                item.risk().value(), item.policyVersion(), item.assumptionEvidenceIds().stream()
                        .map(EvidenceId::value).map(DecisionRestMapper::uuid).toList()
        );
    }

    static <S, T> DecisionApiModels.PageResponse<T> response(
            PageResult<S> page,
            Function<S, T> mapper
    ) {
        return new DecisionApiModels.PageResponse<>(
                page.items().stream().map(mapper).toList(), page.page(), page.size(),
                page.totalItems(), page.totalPages()
        );
    }

    private static DecisionApiModels.MoneyResponse money(ROIAmount value) {
        return new DecisionApiModels.MoneyResponse(
                value.value().amount().toPlainString(), value.value().currency().code()
        );
    }

    private static List<String> evidenceIds(JsonNode node) {
        if (node == null || !node.isArray()) {
            throw unprocessable(
                    "COMPOSITION_EVIDENCE_SET_INVALID",
                    "Composition evidenceIds must be an array"
            );
        }
        List<String> values = node.values().stream()
                .map(value -> requiredTextValue(value, "evidenceIds"))
                .toList();
        if (values.size() != 28) {
            throw unprocessable(
                    "COMPOSITION_EVIDENCE_SET_INVALID",
                    "Composition requires exactly 28 Evidence ids"
            );
        }
        return values;
    }

    private static String requiredText(JsonNode object, String field) {
        JsonNode value = object.get(field);
        return requiredTextValue(value, field);
    }

    private static String requiredTextValue(JsonNode value, String field) {
        if (value == null || !value.isString() || value.asString().isBlank()) {
            throw unprocessable("COMPOSITION_FIELD_INVALID", "Composition field is required: " + field);
        }
        return value.asString();
    }

    private static UUID compositionUuid(String value, boolean requireVersionFour) {
        if (value == null || value.length() != 36 || !value.equals(value.toLowerCase(Locale.ROOT))) {
            throw unprocessable("COMPOSITION_ID_INVALID", "UUID must use canonical lower-case form");
        }
        try {
            UUID parsed = UUID.fromString(value);
            if (!parsed.toString().equals(value) || requireVersionFour && parsed.version() != 4) {
                throw unprocessable(
                        "COMPOSITION_ID_INVALID",
                        requireVersionFour
                                ? "Decision and Recommendation ids must be canonical UUID v4"
                                : "UUID must use canonical lower-case form"
                );
            }
            return parsed;
        } catch (IllegalArgumentException exception) {
            throw unprocessable("COMPOSITION_ID_INVALID", "UUID must use canonical lower-case form");
        }
    }

    private static Timestamp timestamp(String value) {
        if (!value.endsWith("Z")) {
            throw unprocessable("COMPOSITION_TIMESTAMP_INVALID", "Timestamp must be an explicit UTC instant");
        }
        try {
            return new Timestamp(Instant.parse(value));
        } catch (DateTimeParseException exception) {
            throw unprocessable("COMPOSITION_TIMESTAMP_INVALID", "Timestamp must be an explicit UTC instant");
        }
    }

    private static UUID canonicalUuid(String raw) {
        if (raw == null || raw.length() != 36 || !raw.equals(raw.toLowerCase(Locale.ROOT))) {
            throw badRequest("INVALID_UUID", "UUID must use canonical lower-case form");
        }
        try {
            UUID parsed = UUID.fromString(raw);
            if (!parsed.toString().equals(raw)) {
                throw badRequest("INVALID_UUID", "UUID must use canonical lower-case form");
            }
            return parsed;
        } catch (IllegalArgumentException exception) {
            throw badRequest("INVALID_UUID", "UUID must use canonical lower-case form");
        }
    }

    private static String uuid(UUID value) {
        return value.toString();
    }

    private static ApiContractException badRequest(String code, String message) {
        return new ApiContractException(HttpStatus.BAD_REQUEST, code, message);
    }

    private static ApiContractException unprocessable(String code, String message) {
        return new ApiContractException(HttpStatus.valueOf(422), code, message);
    }
}
