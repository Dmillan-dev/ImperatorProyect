package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class InvalidDecisionTransitionException extends BusinessRuleViolationException {
    private static final long serialVersionUID = 1L;

    public InvalidDecisionTransitionException(DecisionId decisionId, String reason) {
        super("INVALID_DECISION_TRANSITION", message(decisionId, reason));
    }

    public InvalidDecisionTransitionException(DecisionId decisionId, String reason, Throwable cause) {
        super("INVALID_DECISION_TRANSITION", message(decisionId, reason), cause);
    }

    private static String message(DecisionId decisionId, String reason) {
        Objects.requireNonNull(decisionId, "Decision id is required");
        return "Invalid decision transition for decision " + decisionId.value() + ": " + requireReason(reason);
    }

    private static String requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Transition rejected";
        }
        String normalized = reason.trim();
        return normalized;
    }
}
