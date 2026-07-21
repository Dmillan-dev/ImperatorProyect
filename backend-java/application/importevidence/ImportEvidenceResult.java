package imperator.application.importevidence;

import imperator.domain.shared.EvidenceId;

public record ImportEvidenceResult(
        EvidenceId evidenceId,
        String correlationKey,
        boolean canSupportApproval,
        boolean requiresReview
) {
}
