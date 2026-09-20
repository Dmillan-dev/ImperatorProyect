package imperator.ports.out;

import java.util.Optional;

public interface ExplanationProvider {
    Optional<RecommendationExplanation> generateExplanation(RecommendationExplanationRequest request);

    default String providerName() {
        return "in-process";
    }

    default String modelId() {
        return "unspecified";
    }

    default String promptVersion() {
        return "imperator-explanation-v1";
    }
}

