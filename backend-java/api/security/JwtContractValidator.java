package imperator.api.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

final class JwtContractValidator implements OAuth2TokenValidator<Jwt> {
    private static final OAuth2Error INVALID_TOKEN = new OAuth2Error("invalid_token");
    private static final Set<String> ROLES = Set.of(
            "ADMIN", "PLATFORM_ENGINEER", "FINANCE", "AUDITOR"
    );

    private final String audience;

    JwtContractValidator(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (!"RS256".equals(token.getHeaders().get("alg"))) {
            return failure();
        }
        Object kid = token.getHeaders().get("kid");
        if (!(kid instanceof String kidValue) || kidValue.isBlank()) {
            return failure();
        }
        if (token.getExpiresAt() == null || !token.getAudience().contains(audience)) {
            return failure();
        }
        if (!canonicalUuid(token.getSubject())) {
            return failure();
        }
        Object rawRole = token.getClaims().get("imperator_role");
        if (!(rawRole instanceof String role)
                || !role.equals(role.toUpperCase(Locale.ROOT))
                || !ROLES.contains(role)) {
            return failure();
        }
        return OAuth2TokenValidatorResult.success();
    }

    private boolean canonicalUuid(String value) {
        if (value == null || value.length() != 36 || !value.equals(value.toLowerCase(Locale.ROOT))) {
            return false;
        }
        try {
            return UUID.fromString(value).toString().equals(value);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private OAuth2TokenValidatorResult failure() {
        return OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
    }
}
