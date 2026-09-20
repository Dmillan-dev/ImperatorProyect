package imperator.application.query;

import imperator.application.exceptions.RecommendationNotFoundException;
import imperator.application.exceptions.ValidationException;
import imperator.ports.in.GetRecommendationInputPort;
import imperator.ports.out.MvpReadModelQueryPort;
import imperator.ports.out.RecommendationExplanationRecord;
import imperator.ports.out.RecommendationExplanationRepository;

import java.util.Objects;
import java.util.Optional;

public final class GetRecommendationUseCase implements GetRecommendationInputPort {
    private final MvpReadModelQueryPort readModel;
    private final RecommendationExplanationRepository explanationRepository;

    public GetRecommendationUseCase(MvpReadModelQueryPort readModel) {
        this(readModel, unavailableExplanationRepository());
    }

    public GetRecommendationUseCase(
            MvpReadModelQueryPort readModel,
            RecommendationExplanationRepository explanationRepository
    ) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
        this.explanationRepository = Objects.requireNonNull(
                explanationRepository, "Explanation repository is required"
        );
    }

    @Override
    public RecommendationView getRecommendation(GetRecommendationQuery query) {
        if (query == null || query.recommendationId() == null) {
            throw new ValidationException("RECOMMENDATION_ID_REQUIRED", "Recommendation id is required");
        }
        return readModel.findRecommendation(query.recommendationId())
                .map(item -> QuerySupport.recommendation(
                        item,
                        latestExplanation(item.id())
                ))
                .orElseThrow(() -> new RecommendationNotFoundException(query.recommendationId()));
    }

    private Optional<RecommendationExplanationRecord> latestExplanation(
            imperator.domain.shared.RecommendationId recommendationId
    ) {
        try {
            Optional<RecommendationExplanationRecord> result =
                    explanationRepository.findLatestByRecommendationId(recommendationId);
            return result == null ? Optional.empty() : result;
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private static RecommendationExplanationRepository unavailableExplanationRepository() {
        return new RecommendationExplanationRepository() {
            @Override
            public void save(RecommendationExplanationRecord explanation) {
                // Read-only composition does not persist explanation attempts.
            }

            @Override
            public Optional<RecommendationExplanationRecord> findLatestByRecommendationId(
                    imperator.domain.shared.RecommendationId recommendationId
            ) {
                return Optional.empty();
            }
        };
    }
}
