package imperator.ports.out;

import java.util.Objects;

public record RecommendationExplanation(String text) {
    public RecommendationExplanation {
        Objects.requireNonNull(text, "Recommendation explanation text is required");
        text = text.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Recommendation explanation text must not be blank");
        }
    }
}

