package imperator.ports.out;

import imperator.domain.decision.Recommendation;
import imperator.domain.shared.RecommendationId;

import java.util.Optional;

public interface RecommendationRepository {
    void save(Recommendation recommendation);

    Optional<Recommendation> findById(RecommendationId id);

    boolean existsById(RecommendationId id);
}
