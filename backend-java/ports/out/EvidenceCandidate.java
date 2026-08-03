package imperator.ports.out;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

import java.util.Map;
import java.util.Objects;

public record EvidenceCandidate(
        EvidenceId evidenceId,
        String evidenceReference,
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
        String caseHint,
        String correlationKey,
        String sensitivity,
        String confidence,
        String reviewStatus,
        String rawPayloadMode,
        Map<String, String> metadata
) {
    public EvidenceCandidate {
        evidenceId = Objects.requireNonNull(evidenceId, "Candidate Evidence ID is required");
        evidenceReference = requireText(evidenceReference, "Candidate Evidence reference");
        timestamp = Objects.requireNonNull(timestamp, "Candidate timestamp is required");
        source = requireText(source, "Candidate source");
        sourceType = requireText(sourceType, "Candidate source type");
        sourceObjectRef = requireText(sourceObjectRef, "Candidate source object reference");
        entity = requireText(entity, "Candidate entity");
        eventType = requireText(eventType, "Candidate event type");
        severity = Objects.requireNonNull(severity, "Candidate severity is required");
        actor = requireText(actor, "Candidate actor");
        evidenceType = requireText(evidenceType, "Candidate Evidence type");
        observedFact = requireText(observedFact, "Candidate observed fact");
        businessMeaning = requireText(businessMeaning, "Candidate business meaning");
        caseHint = requireText(caseHint, "Candidate case hint");
        correlationKey = requireText(correlationKey, "Candidate correlation key");
        sensitivity = requireText(sensitivity, "Candidate sensitivity");
        confidence = requireText(confidence, "Candidate confidence");
        reviewStatus = requireText(reviewStatus, "Candidate review status");
        rawPayloadMode = requireText(rawPayloadMode, "Candidate raw payload mode");
        metadata = Map.copyOf(Objects.requireNonNull(metadata, "Candidate metadata is required"));
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
