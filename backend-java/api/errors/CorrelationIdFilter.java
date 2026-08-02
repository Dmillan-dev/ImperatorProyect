package imperator.api.errors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class CorrelationIdFilter extends OncePerRequestFilter {
    public static final String HEADER_NAME = "X-Correlation-ID";
    static final String REQUEST_ATTRIBUTE =
            CorrelationIdFilter.class.getName() + ".correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = currentCorrelationId(request);
        request.setAttribute(REQUEST_ATTRIBUTE, correlationId);
        response.setHeader(HEADER_NAME, correlationId);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    public static String currentCorrelationId(HttpServletRequest request) {
        Object existing = request.getAttribute(REQUEST_ATTRIBUTE);
        if (existing instanceof String correlationId) {
            return correlationId;
        }
        return resolveHeader(request);
    }

    static String currentCorrelationId(WebRequest request) {
        Object existing = request.getAttribute(
                REQUEST_ATTRIBUTE,
                RequestAttributes.SCOPE_REQUEST);
        if (existing instanceof String correlationId) {
            return correlationId;
        }

        String correlationId = generate();
        request.setAttribute(
                REQUEST_ATTRIBUTE,
                correlationId,
                RequestAttributes.SCOPE_REQUEST);
        return correlationId;
    }

    private static String resolveHeader(HttpServletRequest request) {
        Enumeration<String> values = request.getHeaders(HEADER_NAME);
        if (values == null || !values.hasMoreElements()) {
            return generate();
        }

        String candidate = values.nextElement();
        if (values.hasMoreElements()) {
            return generate();
        }

        return canonicalize(candidate);
    }

    private static String canonicalize(String candidate) {
        if (candidate == null || candidate.length() != 36) {
            return generate();
        }

        try {
            String canonical = UUID.fromString(candidate).toString();
            return canonical.equalsIgnoreCase(candidate) ? canonical : generate();
        } catch (IllegalArgumentException exception) {
            return generate();
        }
    }

    private static String generate() {
        return UUID.randomUUID().toString();
    }
}
