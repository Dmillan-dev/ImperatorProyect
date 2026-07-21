package imperator.application.generaterecommendation;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationId;

import java.util.Optional;

public record GenerateRecommendationResult(
        RecommendationId recommendationId,
        DecisionId decisionId,
        int evidenceCount,
        boolean linkedToDecision,
        Optional<String> explanation
) {
}
