package imperator.application.generaterecommendation;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;

import java.util.Objects;
import java.util.Optional;

public record GenerateRecommendationResult(
        RecommendationId recommendationId,
        DecisionId decisionId,
        RecommendationType recommendationType,
        int evidenceCount,
        boolean linkedToDecision,
        ROIAmount estimatedSavings,
        ROIConfidence confidence,
        Severity risk,
        Optional<String> explanation
) {
    public GenerateRecommendationResult {
        Objects.requireNonNull(recommendationId, "Recommendation result id is required");
        Objects.requireNonNull(decisionId, "Recommendation result Decision id is required");
        Objects.requireNonNull(recommendationType, "Recommendation result type is required");
        Objects.requireNonNull(estimatedSavings, "Recommendation result estimated savings is required");
        Objects.requireNonNull(confidence, "Recommendation result confidence is required");
        Objects.requireNonNull(risk, "Recommendation result risk is required");
        explanation = Objects.requireNonNull(explanation, "Recommendation result explanation is required");
    }
}
