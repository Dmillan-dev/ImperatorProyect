package imperator.ports.out;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;

import java.util.Objects;

public record RecommendationExplanationRequest(
        DecisionId decisionId,
        String caseId,
        String businessNeed,
        RecommendationType recommendationType,
        String suggestedAction,
        String deterministicReason,
        ROIAmount estimatedSavings,
        ROIConfidence confidence,
        Severity risk,
        int evidenceCount
) {
    public RecommendationExplanationRequest {
        Objects.requireNonNull(decisionId, "Explanation decision id is required");
        caseId = requireText(caseId, "Explanation case id");
        businessNeed = requireText(businessNeed, "Explanation business need");
        Objects.requireNonNull(recommendationType, "Explanation recommendation type is required");
        suggestedAction = requireText(suggestedAction, "Explanation suggested action");
        deterministicReason = requireText(deterministicReason, "Explanation deterministic reason");
        Objects.requireNonNull(estimatedSavings, "Explanation estimated savings is required");
        Objects.requireNonNull(confidence, "Explanation confidence is required");
        Objects.requireNonNull(risk, "Explanation risk is required");
        if (evidenceCount <= 0) {
            throw new IllegalArgumentException("Explanation evidence count must be positive");
        }
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

