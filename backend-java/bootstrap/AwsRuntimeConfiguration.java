package imperator.bootstrap;

import imperator.adapters.out.aws.AwsConnectorSettings;
import imperator.adapters.out.aws.AwsSdkEvidenceSourceAdapter;
import imperator.ports.out.EvidenceSourcePort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AwsRuntimeProperties.class)
public final class AwsRuntimeConfiguration {

    @Bean(value = "awsEvidenceSourcePort", destroyMethod = "close")
    EvidenceSourcePort awsEvidenceSourcePort(AwsRuntimeProperties properties) {
        return new AwsSdkEvidenceSourceAdapter(new AwsConnectorSettings(
                properties.enabled(),
                properties.expectedAccountId(),
                properties.region()
        ));
    }
}
