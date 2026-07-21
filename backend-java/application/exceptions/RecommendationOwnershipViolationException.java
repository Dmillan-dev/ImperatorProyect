package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationId;

import java.util.Objects;

public final class RecommendationOwnershipViolationException extends BusinessRuleViolationException {
    private static final long serialVersionUID = 1L;

    public RecommendationOwnershipViolationException(RecommendationId recommendationId, DecisionId decisionId) {
        super(
                "RECOMMENDATION_OWNERSHIP_VIOLATION",
                "Recommendation " + recommendationValue(recommendationId)
                        + " does not belong to decision " + decisionValue(decisionId)
        );
    }

    private static String recommendationValue(RecommendationId recommendationId) {
        return Objects.requireNonNull(recommendationId, "Recommendation id is required").value().toString();
    }

    private static String decisionValue(DecisionId decisionId) {
        return Objects.requireNonNull(decisionId, "Decision id is required").value().toString();
    }
}
