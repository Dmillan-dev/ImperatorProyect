package imperator.application.exceptions;

import imperator.domain.shared.EvidenceId;

import java.util.Objects;

public final class EvidenceNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

    public EvidenceNotFoundException(EvidenceId evidenceId) {
        super("EVIDENCE_NOT_FOUND", "Evidence not found: " + idValue(evidenceId));
    }

    private static String idValue(EvidenceId evidenceId) {
        return Objects.requireNonNull(evidenceId, "Evidence id is required").value().toString();
    }
}
