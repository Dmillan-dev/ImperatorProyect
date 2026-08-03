package imperator.api.security;

import imperator.api.errors.ApiErrorCode;
import imperator.api.errors.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        ApiErrorCode errorCode = authenticationException instanceof OAuth2AuthenticationException
                ? ApiErrorCode.INVALID_TOKEN
                : ApiErrorCode.AUTHENTICATION_REQUIRED;
        String correlationId = CorrelationIdFilter.currentCorrelationId(request);
        SecurityErrorResponseWriter.write(response, errorCode, correlationId, true);
    }
}
