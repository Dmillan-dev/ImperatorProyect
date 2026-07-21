package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class LedgerAppendRejectedException extends BusinessRuleViolationException {
    private static final long serialVersionUID = 1L;

    public LedgerAppendRejectedException(DecisionId decisionId, String reason) {
        super("LEDGER_APPEND_REJECTED", message(decisionId, reason));
    }

    public LedgerAppendRejectedException(DecisionId decisionId, String reason, Throwable cause) {
        super("LEDGER_APPEND_REJECTED", message(decisionId, reason), cause);
    }

    private static String message(DecisionId decisionId, String reason) {
        Objects.requireNonNull(decisionId, "Decision id is required");
        return "Ledger append rejected for decision " + decisionId.value() + ": " + requireReason(reason);
    }

    private static String requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Append rejected";
        }
        String normalized = reason.trim();
        return normalized;
    }
}
