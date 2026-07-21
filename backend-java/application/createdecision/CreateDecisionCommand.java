package imperator.application.createdecision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

public record CreateDecisionCommand(
        DecisionId decisionId,
        EvidenceId originatingEvidenceId,
        String title,
        String businessNeed,
        UserId ownerId,
        UserId requiredApproverId,
        Timestamp createdAt
) {
}
