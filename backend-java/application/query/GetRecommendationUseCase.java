package imperator.application.query;

import imperator.application.exceptions.RecommendationNotFoundException;
import imperator.application.exceptions.ValidationException;
import imperator.ports.in.GetRecommendationInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Objects;

public final class GetRecommendationUseCase implements GetRecommendationInputPort {
    private final MvpReadModelQueryPort readModel;

    public GetRecommendationUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public RecommendationView getRecommendation(GetRecommendationQuery query) {
        if (query == null || query.recommendationId() == null) {
            throw new ValidationException("RECOMMENDATION_ID_REQUIRED", "Recommendation id is required");
        }
        return readModel.findRecommendation(query.recommendationId())
                .map(QuerySupport::recommendation)
                .orElseThrow(() -> new RecommendationNotFoundException(query.recommendationId()));
    }
}
