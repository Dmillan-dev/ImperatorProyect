package imperator.application.reviewdecision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.UserId;

public record ReviewDecisionResult(
        DecisionId decisionId,
        RecommendationId recommendationId,
        DecisionStatus status,
        UserId reviewerId,
        String reviewReason,
        boolean ledgerEntryRequired
) {
}
