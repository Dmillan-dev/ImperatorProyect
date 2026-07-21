package imperator.application.exceptions;

import imperator.domain.shared.RecommendationId;

import java.util.Objects;

public final class RecommendationNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

    public RecommendationNotFoundException(RecommendationId recommendationId) {
        super("RECOMMENDATION_NOT_FOUND", "Recommendation not found: " + idValue(recommendationId));
    }

    private static String idValue(RecommendationId recommendationId) {
        return Objects.requireNonNull(recommendationId, "Recommendation id is required").value().toString();
    }
}
