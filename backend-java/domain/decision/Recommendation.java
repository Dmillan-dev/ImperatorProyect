package imperator.domain.decision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.Objects;
import java.util.Set;

public record Recommendation(
        RecommendationId id,
        DecisionId decisionId,
        RecommendationType type,
        String suggestedAction,
        String reason,
        Set<EvidenceId> evidenceIds,
        ROIAmount estimatedSavings,
        ROIConfidence confidence,
        Severity risk,
        UserId ownerId,
        UserId requiredApproverId,
        Timestamp createdAt
) {
    public Recommendation {
        id = Objects.requireNonNull(id, "Recommendation id is required");
        decisionId = Objects.requireNonNull(decisionId, "Recommendation decision id is required");
        type = Objects.requireNonNull(type, "Recommendation type is required");
        suggestedAction = requireText(suggestedAction, "Recommendation suggested action");
        reason = requireText(reason, "Recommendation reason");
        evidenceIds = requireEvidence(evidenceIds);
        estimatedSavings = Objects.requireNonNull(estimatedSavings, "Recommendation estimated savings is required");
        confidence = Objects.requireNonNull(confidence, "Recommendation confidence is required");
        risk = Objects.requireNonNull(risk, "Recommendation risk is required");
        ownerId = Objects.requireNonNull(ownerId, "Recommendation owner is required");
        requiredApproverId = Objects.requireNonNull(requiredApproverId, "Recommendation approver is required");
        createdAt = Objects.requireNonNull(createdAt, "Recommendation creation timestamp is required");
    }

    public String suggests() {
        return suggestedAction;
    }

    public ROIAmount estimates() {
        return estimatedSavings;
    }

    public String explains() {
        return reason;
    }

    public boolean isSupportedBy(EvidenceId evidenceId) {
        return evidenceIds.contains(Objects.requireNonNull(evidenceId, "Evidence id is required"));
    }

    public boolean belongsTo(DecisionId decisionId) {
        return this.decisionId.equals(Objects.requireNonNull(decisionId, "Decision id is required"));
    }

    @Override
    public boolean equals(Object candidate) {
        return candidate instanceof Recommendation recommendation && id.equals(recommendation.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private static Set<EvidenceId> requireEvidence(Set<EvidenceId> evidenceIds) {
        Objects.requireNonNull(evidenceIds, "Recommendation evidence is required");
        if (evidenceIds.isEmpty()) {
            throw new IllegalArgumentException("Recommendation must reference evidence");
        }
        return Set.copyOf(evidenceIds);
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

