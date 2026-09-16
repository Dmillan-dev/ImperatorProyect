package imperator.api.security;

import imperator.api.errors.ApiErrorCode;
import imperator.api.errors.CorrelationIdFilter;
import imperator.api.observability.ImperatorRouteCatalog;
import imperator.api.observability.ImperatorTelemetry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectProvider<ImperatorTelemetry> telemetryProvider;

    public JwtAccessDeniedHandler(ObjectProvider<ImperatorTelemetry> telemetryProvider) {
        this.telemetryProvider = telemetryProvider;
    }

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
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        if (telemetry != null) {
            telemetry.authorizationDenial(
                    currentRole(),
                    ImperatorRouteCatalog.routeId(
                            request.getMethod(), request.getRequestURI()
                    ).orElse(null)
            );
        }
    }

    private String currentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "unknown";
        }
        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(this::isFrozenRole)
                .findFirst()
                .orElse("unknown");
    }

    private boolean isFrozenRole(String role) {
        return "ADMIN".equals(role)
                || "PLATFORM_ENGINEER".equals(role)
                || "FINANCE".equals(role)
                || "AUDITOR".equals(role);
    }
}
