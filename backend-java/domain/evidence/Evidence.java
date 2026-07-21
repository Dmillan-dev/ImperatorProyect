package imperator.domain.evidence;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record Evidence(
        EvidenceId id,
        Timestamp timestamp,
        String source,
        String sourceType,
        String sourceObjectRef,
        String entity,
        String eventType,
        Severity severity,
        String actor,
        String evidenceType,
        String observedFact,
        String businessMeaning,
        String correlationKey,
        String sensitivity,
        String confidence,
        String reviewStatus,
        String rawPayloadMode,
        Map<String, String> metadata
) {
    private static final String RAW_PAYLOAD_NOT_STORED = "not_stored";
    private static final String UNKNOWN_ACTOR = "unknown";
    private static final Set<String> SENSITIVITY_VALUES = Set.of("PUBLIC", "INTERNAL", "CONFIDENTIAL", "RESTRICTED");
    private static final Set<String> CONFIDENCE_VALUES = Set.of("LOW", "MEDIUM", "HIGH");
    private static final Set<String> REVIEW_STATUS_VALUES = Set.of(
            "ACCEPTED",
            "REJECTED",
            "MISSING",
            "STALE",
            "DISPUTED",
            "NEEDS_REVIEW"
    );

    public Evidence {
        id = Objects.requireNonNull(id, "Evidence id is required");
        timestamp = Objects.requireNonNull(timestamp, "Evidence timestamp is required");
        source = requireText(source, "Evidence source");
        sourceType = requireText(sourceType, "Evidence source type");
        sourceObjectRef = requireText(sourceObjectRef, "Evidence source object reference");
        entity = requireText(entity, "Evidence entity");
        eventType = requireText(eventType, "Evidence event type");
        severity = Objects.requireNonNull(severity, "Evidence severity is required");
        actor = actor == null || actor.isBlank() ? UNKNOWN_ACTOR : actor.trim();
        evidenceType = requireText(evidenceType, "Evidence type");
        observedFact = requireText(observedFact, "Evidence observed fact");
        businessMeaning = requireText(businessMeaning, "Evidence business meaning");
        correlationKey = requireText(correlationKey, "Evidence correlation key");
        sensitivity = requireControlledValue(sensitivity, "Evidence sensitivity", SENSITIVITY_VALUES);
        confidence = requireControlledValue(confidence, "Evidence confidence", CONFIDENCE_VALUES);
        reviewStatus = requireControlledValue(reviewStatus, "Evidence review status", REVIEW_STATUS_VALUES);
        rawPayloadMode = requireRawPayloadNotStored(rawPayloadMode);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean canSupportApproval() {
        return "ACCEPTED".equals(reviewStatus);
    }

    public boolean requiresReview() {
        return Set.of("MISSING", "STALE", "DISPUTED", "NEEDS_REVIEW").contains(reviewStatus);
    }

    public boolean sameIdentityAs(Evidence other) {
        return other != null && id.equals(other.id);
    }

    @Override
    public boolean equals(Object candidate) {
        return candidate instanceof Evidence evidence && id.equals(evidence.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String requireControlledValue(String value, String fieldName, Set<String> allowedValues) {
        String normalized = requireText(value, fieldName).toUpperCase(Locale.ROOT);
        if (!allowedValues.contains(normalized)) {
            throw new IllegalArgumentException(fieldName + " has an unsupported value");
        }
        return normalized;
    }

    private static String requireRawPayloadNotStored(String rawPayloadMode) {
        String normalized = requireText(rawPayloadMode, "Evidence raw payload mode").toLowerCase(Locale.ROOT);
        if (!RAW_PAYLOAD_NOT_STORED.equals(normalized)) {
            throw new IllegalArgumentException("Evidence raw payload mode must be not_stored");
        }
        return normalized;
    }
}
