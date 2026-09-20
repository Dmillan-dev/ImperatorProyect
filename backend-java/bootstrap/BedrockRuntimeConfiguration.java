package imperator.bootstrap;

import imperator.adapters.out.bedrock.AwsBedrockConverseGateway;
import imperator.adapters.out.bedrock.BedrockExplanationProvider;
import imperator.adapters.out.bedrock.BedrockExplanationSettings;
import imperator.ports.out.ExplanationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "imperator.bedrock",
        name = "enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(BedrockRuntimeProperties.class)
public final class BedrockRuntimeConfiguration {

    @Bean(destroyMethod = "close")
    ExplanationProvider bedrockExplanationProvider(BedrockRuntimeProperties properties) {
        BedrockExplanationSettings settings = new BedrockExplanationSettings(
                properties.region(),
                properties.modelId(),
                properties.effectiveMaxOutputTokens(),
                properties.effectiveTemperature()
        );
        return new BedrockExplanationProvider(
                settings,
                AwsBedrockConverseGateway.production(settings)
        );
    }
}
