package imperator.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "imperator.bedrock")
public record BedrockRuntimeProperties(
        boolean enabled,
        String region,
        String modelId,
        Integer maxOutputTokens,
        Float temperature
) {
    int effectiveMaxOutputTokens() {
        return maxOutputTokens == null ? 700 : maxOutputTokens;
    }

    float effectiveTemperature() {
        return temperature == null ? 0.0F : temperature;
    }

    @Override
    public String toString() {
        return "BedrockRuntimeProperties[enabled=" + enabled
                + ", region=" + region
                + ", modelId=" + modelId
                + ", maxOutputTokens=" + effectiveMaxOutputTokens()
                + ", temperature=" + effectiveTemperature() + "]";
    }
}
