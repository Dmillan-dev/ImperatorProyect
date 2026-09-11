package imperator.api.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public final class ImperatorHttpRequestFilter extends OncePerRequestFilter {
    private final ImperatorTelemetry telemetry;

    public ImperatorHttpRequestFilter(ImperatorTelemetry telemetry) {
        this.telemetry = telemetry;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long started = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            telemetry.httpRequestCompleted(
                    ImperatorRouteCatalog.routeId(
                            request.getMethod(),
                            request.getRequestURI()
                    ).orElse(null),
                    response.getStatus(),
                    System.nanoTime() - started
            );
        }
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }
}
