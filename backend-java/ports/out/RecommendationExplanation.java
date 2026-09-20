package imperator.ports.out;

import java.util.Objects;

public record RecommendationExplanation(
        String text,
        String provider,
        String modelId,
        String promptVersion,
        int inputTokens,
        int outputTokens,
        long latencyMillis
) {
    private static final int MAX_TEXT_LENGTH = 6000;

    public RecommendationExplanation(String text) {
        this(text, "in-process", "unspecified", "imperator-explanation-v1", 0, 0, 0L);
    }

    public RecommendationExplanation {
        Objects.requireNonNull(text, "Recommendation explanation text is required");
        text = text.trim();
        if (text.isEmpty() || text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException("Recommendation explanation text must contain 1 to 6000 characters");
        }
        provider = requireText(provider, "Recommendation explanation provider", 80);
        modelId = requireText(modelId, "Recommendation explanation model id", 300);
        promptVersion = requireText(promptVersion, "Recommendation explanation prompt version", 80);
        if (inputTokens < 0 || outputTokens < 0 || latencyMillis < 0) {
            throw new IllegalArgumentException("Recommendation explanation metrics cannot be negative");
        }
    }

    private static String requireText(String value, String field, int maxLength) {
        Objects.requireNonNull(value, field + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " must contain 1 to " + maxLength + " characters");
        }
        return normalized;
    }
}

