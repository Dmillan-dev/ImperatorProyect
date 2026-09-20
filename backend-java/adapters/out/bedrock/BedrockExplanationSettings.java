package imperator.adapters.out.bedrock;

import software.amazon.awssdk.regions.Region;

import java.util.Objects;

public record BedrockExplanationSettings(
        String region,
        String modelId,
        int maxOutputTokens,
        float temperature
) {
    public BedrockExplanationSettings {
        region = requireText(region, "Bedrock region", 64);
        modelId = requireText(modelId, "Bedrock model id", 300);
        if (maxOutputTokens < 128 || maxOutputTokens > 2048) {
            throw new IllegalArgumentException("Bedrock max output tokens must be between 128 and 2048");
        }
        if (!Float.isFinite(temperature) || temperature < 0.0F || temperature > 0.3F) {
            throw new IllegalArgumentException("Bedrock temperature must be between 0.0 and 0.3");
        }
        Region.of(region);
    }

    public Region sdkRegion() {
        return Region.of(region);
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
