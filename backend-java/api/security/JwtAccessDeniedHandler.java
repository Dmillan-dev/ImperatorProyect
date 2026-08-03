package imperator.api.security;

import imperator.api.errors.ApiErrorCode;
import imperator.api.errors.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        String correlationId = CorrelationIdFilter.currentCorrelationId(request);
        SecurityErrorResponseWriter.write(
                response, ApiErrorCode.ACCESS_DENIED, correlationId, false
        );
    }
}
