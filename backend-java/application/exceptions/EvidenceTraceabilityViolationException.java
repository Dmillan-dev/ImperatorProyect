package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;

import java.util.Objects;

public final class EvidenceTraceabilityViolationException extends BusinessRuleViolationException {
    private static final long serialVersionUID = 1L;

    public EvidenceTraceabilityViolationException(EvidenceId evidenceId, DecisionId decisionId, String reason) {
        super("EVIDENCE_TRACEABILITY_VIOLATION", message(evidenceId, decisionId, reason));
    }

    private static String message(EvidenceId evidenceId, DecisionId decisionId, String reason) {
        Objects.requireNonNull(evidenceId, "Evidence id is required");
        Objects.requireNonNull(decisionId, "Decision id is required");
        return "Evidence " + evidenceId.value() + " is not traceable to decision "
                + decisionId.value() + ": " + requireReason(reason);
    }

    private static String requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Traceability rejected";
        }
        String normalized = reason.trim();
        return normalized;
    }
}
