package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class DecisionAlreadyHasRecommendationException extends ConflictException {
    private static final long serialVersionUID = 1L;

    public DecisionAlreadyHasRecommendationException(DecisionId decisionId) {
        super("DECISION_ALREADY_HAS_RECOMMENDATION", "Decision already has a recommendation: " + idValue(decisionId));
    }

    private static String idValue(DecisionId decisionId) {
        return Objects.requireNonNull(decisionId, "Decision id is required").value().toString();
    }
}
