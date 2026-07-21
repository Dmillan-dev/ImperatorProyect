package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class DecisionAlreadyClosedException extends ConflictException {
    private static final long serialVersionUID = 1L;

    public DecisionAlreadyClosedException(DecisionId decisionId) {
        super("DECISION_ALREADY_CLOSED", "Decision is already closed: " + idValue(decisionId));
    }

    private static String idValue(DecisionId decisionId) {
        return Objects.requireNonNull(decisionId, "Decision id is required").value().toString();
    }
}
