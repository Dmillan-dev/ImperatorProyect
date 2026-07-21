package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class DecisionNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

    public DecisionNotFoundException(DecisionId decisionId) {
        super("DECISION_NOT_FOUND", "Decision not found: " + idValue(decisionId));
    }

    private static String idValue(DecisionId decisionId) {
        return Objects.requireNonNull(decisionId, "Decision id is required").value().toString();
    }
}
