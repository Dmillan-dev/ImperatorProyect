package imperator.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "imperator.postgresql")
public record PostgresRuntimeProperties(
        String url,
        String username,
        String password
) {
    public PostgresRuntimeProperties {
        url = requireText(url, "PostgreSQL URL");
        username = requireText(username, "PostgreSQL username");
        password = requireText(password, "PostgreSQL password");
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
