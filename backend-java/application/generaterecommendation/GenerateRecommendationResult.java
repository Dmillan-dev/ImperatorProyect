package imperator.application.generaterecommendation;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;

public record GenerateRecommendationResult(
        RecommendationId recommendationId,
        DecisionId decisionId,
        int evidenceCount,
        boolean linkedToDecision,
        ROIAmount estimatedSavings,
        ROIConfidence confidence,
        Severity risk
) {
}
