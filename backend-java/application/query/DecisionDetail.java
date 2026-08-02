package imperator.application.query;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.List;
import java.util.Optional;

public record DecisionDetail(
        DecisionId decisionId,
        String caseId,
        String title,
        String businessNeed,
        DecisionStatus status,
        UserId ownerId,
        UserId requiredApproverId,
        Optional<RecommendationId> recommendationId,
        Timestamp createdAt,
        Timestamp updatedAt,
        EvidenceId originatingEvidenceId,
        List<EvidenceId> evidenceIds,
        Optional<UserId> reviewedBy,
        Optional<Timestamp> reviewedAt,
        Optional<String> reviewReason
) {
    public DecisionDetail {
        evidenceIds = List.copyOf(evidenceIds);
    }
}
