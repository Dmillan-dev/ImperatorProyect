package imperator.adapters.out.bedrock;

public interface BedrockConverseGateway extends AutoCloseable {
    BedrockConverseResult converse(String systemPrompt, String userPrompt);

    @Override
    default void close() {
        // Test and in-memory gateways do not own resources.
    }
}
