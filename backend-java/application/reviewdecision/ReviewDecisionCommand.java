package imperator.application.reviewdecision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

public record ReviewDecisionCommand(
        DecisionId decisionId,
        RecommendationId recommendationId,
        ReviewDecisionAction action,
        UserId reviewerId,
        Timestamp reviewedAt,
        String reviewReason
) {
}
