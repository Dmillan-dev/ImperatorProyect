package imperator.api.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import java.util.Enumeration;

final class StrictBearerTokenResolver implements BearerTokenResolver {
    private static final OAuth2Error INVALID_REQUEST = new OAuth2Error("invalid_request");
    private final DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        Enumeration<String> values = request.getHeaders(HttpHeaders.AUTHORIZATION);
        if (values != null && values.hasMoreElements()) {
            values.nextElement();
            if (values.hasMoreElements()) {
                throw new OAuth2AuthenticationException(INVALID_REQUEST);
            }
        }
        return delegate.resolve(request);
    }
}
