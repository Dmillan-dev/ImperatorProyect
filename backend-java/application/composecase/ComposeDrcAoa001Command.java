package imperator.application.composecase;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.Set;

public record ComposeDrcAoa001Command(
        String caseId,
        DecisionId decisionId,
        RecommendationId recommendationId,
        EvidenceId originatingEvidenceId,
        Set<EvidenceId> evidenceIds,
        String title,
        String businessNeed,
        UserId ownerId,
        UserId requiredApproverId,
        UserId initiatingActorId,
        Timestamp decisionCreatedAt,
        Timestamp recommendationGeneratedAt
) {
}
