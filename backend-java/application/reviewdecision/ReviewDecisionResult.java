package imperator.application.reviewdecision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.UserId;

public record ReviewDecisionResult(
        LedgerEntryId ledgerEntryId,
        DecisionId decisionId,
        RecommendationId recommendationId,
        DecisionStatus status,
        UserId reviewerId,
        String reviewerRole,
        String reviewReason,
        boolean replayed
) {
}
