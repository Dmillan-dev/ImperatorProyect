package imperator.application.generaterecommendation;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

import java.util.Set;

public record GenerateRecommendationCommand(
        RecommendationId recommendationId,
        DecisionId decisionId,
        RecommendationType recommendationType,
        String suggestedAction,
        String deterministicReason,
        Set<EvidenceId> evidenceIds,
        ROIAmount estimatedSavings,
        ROIConfidence confidence,
        Severity risk,
        Timestamp generatedAt
) {
}
