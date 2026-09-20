package imperator.ports.out;

import imperator.domain.shared.RecommendationId;

import java.util.Optional;

public interface RecommendationExplanationRepository {
    void save(RecommendationExplanationRecord explanation);

    Optional<RecommendationExplanationRecord> findLatestByRecommendationId(RecommendationId recommendationId);

    default Optional<RecommendationExplanationRecord> findByGeneration(
            RecommendationId recommendationId,
            String provider,
            String modelId,
            String promptVersion
    ) {
        return findLatestByRecommendationId(recommendationId)
                .filter(item -> item.matches(provider, modelId, promptVersion));
    }
}
