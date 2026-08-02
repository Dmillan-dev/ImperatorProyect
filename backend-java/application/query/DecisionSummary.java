package imperator.application.query;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.Optional;

public record DecisionSummary(
        DecisionId decisionId,
        String caseId,
        String title,
        String businessNeed,
        DecisionStatus status,
        UserId ownerId,
        UserId requiredApproverId,
        Optional<RecommendationId> recommendationId,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
