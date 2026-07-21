package imperator.application.exceptions;

import imperator.domain.shared.EvidenceId;

import java.util.Objects;

public final class DuplicateEvidenceException extends ConflictException {
    private static final long serialVersionUID = 1L;

    public DuplicateEvidenceException(EvidenceId evidenceId) {
        super("DUPLICATE_EVIDENCE", "Evidence already exists: " + idValue(evidenceId));
    }

    private static String idValue(EvidenceId evidenceId) {
        return Objects.requireNonNull(evidenceId, "Evidence id is required").value().toString();
    }
}
