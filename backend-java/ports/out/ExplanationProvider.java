package imperator.ports.out;

import java.util.Optional;

public interface ExplanationProvider {
    Optional<RecommendationExplanation> generateExplanation(RecommendationExplanationRequest request);
}

