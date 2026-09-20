package imperator.adapters.out.bedrock;

import java.util.Objects;

public record BedrockConverseResult(
        String output,
        int inputTokens,
        int outputTokens,
        long latencyMillis
) {
    public BedrockConverseResult {
        output = Objects.requireNonNull(output, "Bedrock output is required");
        if (inputTokens < 0 || outputTokens < 0 || latencyMillis < 0) {
            throw new IllegalArgumentException("Bedrock usage metrics cannot be negative");
        }
    }
}
