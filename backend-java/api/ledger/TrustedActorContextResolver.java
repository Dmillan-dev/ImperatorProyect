package imperator.api.ledger;

import imperator.api.errors.ApiContractException;
import imperator.domain.shared.UserId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
public final class TrustedActorContextResolver {
    public static final String ACTOR_ID_HEADER = "X-Imperator-Actor-ID";
    public static final String ACTOR_ROLE_HEADER = "X-Imperator-Actor-Role";
    private static final Set<String> ROLES = Set.of("ADMIN", "PLATFORM_ENGINEER", "FINANCE", "AUDITOR");

    private final boolean enabled;

    public TrustedActorContextResolver(
            @Value("${imperator.security.trusted-actor.enabled:false}") boolean enabled
    ) {
        this.enabled = enabled;
    }

    ActorContext resolve(HttpServletRequest request) {
        if (!enabled) {
            throw new ApiContractException(
                    HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", "Authentication required"
            );
        }
        String actorId = singleHeader(request, ACTOR_ID_HEADER, true);
        String actorRole = singleHeader(request, ACTOR_ROLE_HEADER, true);
        if (!actorRole.equals(actorRole.toUpperCase(Locale.ROOT)) || !ROLES.contains(actorRole)) {
            throw invalid();
        }
        return new ActorContext(new UserId(canonicalUuid(actorId)), actorRole);
    }

    private String singleHeader(HttpServletRequest request, String name, boolean required) {
        Enumeration<String> values = request.getHeaders(name);
        if (values == null || !values.hasMoreElements()) {
            if (required) {
                throw new ApiContractException(
                        HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", "Authentication required"
                );
            }
            return null;
        }
        String value = values.nextElement();
        if (values.hasMoreElements() || value == null || value.isBlank()) {
            throw invalid();
        }
        return value.trim();
    }

    private UUID canonicalUuid(String value) {
        if (value.length() != 36 || !value.equals(value.toLowerCase(Locale.ROOT))) {
            throw invalid();
        }
        try {
            UUID parsed = UUID.fromString(value);
            if (!parsed.toString().equals(value)) {
                throw invalid();
            }
            return parsed;
        } catch (IllegalArgumentException exception) {
            throw invalid();
        }
    }

    private ApiContractException invalid() {
        return new ApiContractException(
                HttpStatus.UNAUTHORIZED, "ACTOR_CONTEXT_INVALID", "Actor context is invalid"
        );
    }

    record ActorContext(UserId actorId, String actorRole) {
    }
}
