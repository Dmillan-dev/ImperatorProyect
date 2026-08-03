package imperator.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "imperator.github")
public record GitHubRuntimeProperties(
        boolean enabled,
        String token,
        String organization,
        String repository
) {
    @Override
    public String toString() {
        return "GitHubRuntimeProperties[enabled=" + enabled
                + ", token=<redacted>, organization=" + organization
                + ", repository=" + repository + "]";
    }
}
