package imperator.application.composecase;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;

import java.util.Objects;

public record ComposeDrcAoa001Result(
        String caseId,
        DecisionId decisionId,
        DecisionStatus decisionStatus,
        RecommendationId recommendationId,
        RecommendationType recommendationType,
        ROIAmount estimatedAnnualizedSavings,
        ROIConfidence confidence,
        Severity risk,
        int evidenceCount,
        boolean replayed,
        boolean resumed
) {
    public ComposeDrcAoa001Result {
        Objects.requireNonNull(caseId, "Composition case id is required");
        Objects.requireNonNull(decisionId, "Composition Decision id is required");
        Objects.requireNonNull(decisionStatus, "Composition Decision status is required");
        Objects.requireNonNull(recommendationId, "Composition Recommendation id is required");
        Objects.requireNonNull(recommendationType, "Composition Recommendation type is required");
        Objects.requireNonNull(estimatedAnnualizedSavings, "Composition estimated savings are required");
        Objects.requireNonNull(confidence, "Composition confidence is required");
        Objects.requireNonNull(risk, "Composition risk is required");
    }
}
