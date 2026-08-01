package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class RecommendationNotReadyException extends BusinessRuleViolationException {
    private static final long serialVersionUID = 1L;

    public RecommendationNotReadyException(DecisionId decisionId, String reason, Throwable cause) {
        super(
                "RECOMMENDATION_NOT_READY",
                "Recommendation policy is not ready for Decision " + idValue(decisionId) + ": " + reason,
                cause
        );
    }

    private static String idValue(DecisionId decisionId) {
        return Objects.requireNonNull(decisionId, "Decision id is required").value().toString();
    }
}
