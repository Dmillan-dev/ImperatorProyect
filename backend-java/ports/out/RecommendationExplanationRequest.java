package imperator.ports.out;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;

import java.util.List;
import java.util.Objects;

public record RecommendationExplanationRequest(
        RecommendationId recommendationId,
        DecisionId decisionId,
        String caseId,
        String businessNeed,
        RecommendationType recommendationType,
        String suggestedAction,
        String deterministicReason,
        ROIAmount estimatedSavings,
        ROIConfidence confidence,
        Severity risk,
        List<EvidenceId> evidenceIds,
        List<String> assumptionIds,
        String policyVersion
) {
    public RecommendationExplanationRequest {
        Objects.requireNonNull(recommendationId, "Explanation recommendation id is required");
        Objects.requireNonNull(decisionId, "Explanation decision id is required");
        caseId = requireText(caseId, "Explanation case id");
        businessNeed = requireText(businessNeed, "Explanation business need");
        Objects.requireNonNull(recommendationType, "Explanation recommendation type is required");
        suggestedAction = requireText(suggestedAction, "Explanation suggested action");
        deterministicReason = requireText(deterministicReason, "Explanation deterministic reason");
        Objects.requireNonNull(estimatedSavings, "Explanation estimated savings is required");
        Objects.requireNonNull(confidence, "Explanation confidence is required");
        Objects.requireNonNull(risk, "Explanation risk is required");
        evidenceIds = requireEvidenceIds(evidenceIds);
        assumptionIds = requireAssumptionIds(assumptionIds);
        policyVersion = requireText(policyVersion, "Explanation policy version");
    }

    private static List<EvidenceId> requireEvidenceIds(List<EvidenceId> evidenceIds) {
        Objects.requireNonNull(evidenceIds, "Explanation Evidence ids are required");
        if (evidenceIds.isEmpty() || evidenceIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Explanation Evidence ids must contain non-null values");
        }
        return List.copyOf(evidenceIds);
    }

    private static List<String> requireAssumptionIds(List<String> assumptionIds) {
        Objects.requireNonNull(assumptionIds, "Explanation assumption ids are required");
        List<String> normalized = assumptionIds.stream()
                .map(value -> requireText(value, "Explanation assumption id"))
                .toList();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Explanation assumption ids must not be empty");
        }
        return List.copyOf(normalized);
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

