package imperator.api.security;

import imperator.api.errors.ApiErrorCode;
import imperator.api.errors.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

final class SecurityErrorResponseWriter {
    private SecurityErrorResponseWriter() {
    }

    static void write(
            HttpServletResponse response,
            ApiErrorCode errorCode,
            String correlationId,
            boolean bearerChallenge
    ) throws IOException {
        response.setStatus(errorCode.status().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (bearerChallenge) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }
        response.setHeader(CorrelationIdFilter.HEADER_NAME, correlationId);
        response.getWriter().write(errorBody(errorCode, correlationId));
    }

    private static String errorBody(ApiErrorCode errorCode, String correlationId) {
        return "{\"code\":\"" + errorCode.name()
                + "\",\"message\":\"" + errorCode.message()
                + "\",\"correlationId\":\"" + correlationId
                + "\",\"details\":{}}";
    }
}
