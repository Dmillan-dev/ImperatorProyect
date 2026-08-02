package imperator.api.security;

import imperator.api.errors.ApiContractException;
import imperator.domain.shared.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class JwtActorContextResolver {
    public AuthenticatedActor resolve(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)
                || !authentication.isAuthenticated()) {
            throw invalidToken();
        }

        String subject = jwtAuthentication.getToken().getSubject();
        String role = jwtAuthentication.getToken().getClaimAsString("imperator_role");
        try {
            return new AuthenticatedActor(new UserId(UUID.fromString(subject)), role);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw invalidToken();
        }
    }

    private ApiContractException invalidToken() {
        return new ApiContractException(
                HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid authentication token"
        );
    }

    public record AuthenticatedActor(UserId actorId, String actorRole) {
    }
}
