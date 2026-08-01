package imperator.application.exceptions;

import imperator.domain.shared.DecisionId;

import java.util.Objects;

public final class RecommendationCreationConflictException extends ConflictException {
    private static final long serialVersionUID = 1L;

    public RecommendationCreationConflictException(DecisionId decisionId) {
        super(
                "RECOMMENDATION_CREATION_CONFLICT",
                "Decision already owns a Recommendation with different immutable creation attributes: "
                        + idValue(decisionId)
        );
    }

    private static String idValue(DecisionId decisionId) {
        return Objects.requireNonNull(decisionId, "Decision id is required").value().toString();
    }
}
