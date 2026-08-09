package imperator.bootstrap;

import imperator.adapters.out.github.GitHubConnectorSettings;
import imperator.adapters.out.github.GitHubRestAdapter;
import imperator.ports.out.EvidenceSourcePort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GitHubRuntimeProperties.class)
public final class GitHubRuntimeConfiguration {

    @Bean("githubEvidenceSourcePort")
    EvidenceSourcePort githubEvidenceSourcePort(GitHubRuntimeProperties properties) {
        return new GitHubRestAdapter(new GitHubConnectorSettings(
                properties.enabled(),
                properties.token(),
                properties.organization(),
                properties.repository()
        ));
    }
}
