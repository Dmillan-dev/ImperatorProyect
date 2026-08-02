package imperator.api.security;

import imperator.api.errors.ApiErrorCode;
import imperator.api.errors.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        response.setHeader(CorrelationIdFilter.HEADER_NAME, correlationId);
        response.getWriter().write(errorBody(errorCode, correlationId));
    }

    private String errorBody(ApiErrorCode errorCode, String correlationId) {
        return "{\"code\":\"" + errorCode.name()
                + "\",\"message\":\"" + errorCode.message()
                + "\",\"correlationId\":\"" + correlationId
                + "\",\"details\":{}}";
    }
}
