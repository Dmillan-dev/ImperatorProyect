package imperator.application.generaterecommendation;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;

import java.util.Set;

public record GenerateRecommendationCommand(
        RecommendationId recommendationId,
        DecisionId decisionId,
        Set<EvidenceId> evidenceIds,
        Timestamp generatedAt
) {
}
