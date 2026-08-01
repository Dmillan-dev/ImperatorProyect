package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class DecisionCreationConflictException extends ConflictException {
    private static final long serialVersionUID = 1L;

    public DecisionCreationConflictException(DecisionId decisionId) {
        super(
                "DECISION_CREATION_CONFLICT",
                "Decision already exists with different immutable creation attributes: "
                        + idValue(decisionId)
        );
    }

    private static String idValue(DecisionId decisionId) {
        return Objects.requireNonNull(decisionId, "Decision id is required").value().toString();
    }
}
