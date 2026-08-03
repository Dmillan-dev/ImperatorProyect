package imperator.adapters.out.github;

import java.util.Optional;
import java.util.regex.Pattern;

public final class GitHubConnectorSettings {
    private static final Pattern ORGANIZATION = Pattern.compile(
            "[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?"
    );
    private static final Pattern REPOSITORY = Pattern.compile("[A-Za-z0-9._-]{1,100}");

    private final boolean enabled;
    private final String token;
    private final String organization;
    private final String repository;

    public GitHubConnectorSettings(
            boolean enabled,
            String token,
            String organization,
            String repository
    ) {
        this.enabled = enabled;
        this.token = normalize(token);
        this.organization = normalize(organization);
        this.repository = normalize(repository);
    }

    boolean enabled() {
        return enabled;
    }

    String token() {
        return token;
    }

    String organization() {
        return organization;
    }

    String repository() {
        return repository;
    }

    String sourceReference() {
        if (organization.isEmpty() || repository.isEmpty()) {
            return "";
        }
        return organization + "/" + repository;
    }

    Optional<String> validationFailure() {
        if (!enabled) {
            return Optional.empty();
        }
        if (token.isEmpty() || containsControlCharacter(token)) {
            return Optional.of("GITHUB_TOKEN_REQUIRED");
        }
        if (!ORGANIZATION.matcher(organization).matches()) {
            return Optional.of("GITHUB_ORGANIZATION_INVALID");
        }
        if (!REPOSITORY.matcher(repository).matches()
                || ".".equals(repository)
                || "..".equals(repository)) {
            return Optional.of("GITHUB_REPOSITORY_INVALID");
        }
        return Optional.empty();
    }

    private static boolean containsControlCharacter(String value) {
        return value.chars().anyMatch(character -> Character.isISOControl(character));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
