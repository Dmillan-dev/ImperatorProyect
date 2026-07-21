package imperator.application.createdecision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;

public record CreateDecisionResult(
        DecisionId decisionId,
        String caseId,
        EvidenceId originatingEvidenceId,
        DecisionStatus status,
        boolean originatingEvidenceCanSupportApproval,
        boolean originatingEvidenceRequiresReview
) {
}
