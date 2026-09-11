package imperator.api.observability;

import imperator.api.errors.CorrelationIdFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CorrelationMdcContractTest {

    @Test
    void installsCanonicalCorrelationForTheRequestAndClearsItAfterSuccess()
            throws IOException, ServletException {
        String supplied = UUID.randomUUID().toString().toUpperCase();
        Map<String, String> responseHeaders = new HashMap<>();
        HttpServletRequest request = request(supplied);
        HttpServletResponse response = response(responseHeaders);

        new CorrelationIdFilter().doFilter(request, response, assertingChain(supplied));

        assertEquals(supplied.toLowerCase(), responseHeaders.get(CorrelationIdFilter.HEADER_NAME));
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
    }

    @Test
    void clearsCorrelationAfterDownstreamFailure() {
        HttpServletRequest request = request(null);
        HttpServletResponse response = response(new HashMap<>());
        FilterChain failing = (ignoredRequest, ignoredResponse) -> {
            assertCanonical(MDC.get(CorrelationIdFilter.MDC_KEY));
            throw new ServletException("sentinel-downstream-detail");
        };

        assertThrows(
                ServletException.class,
                () -> new CorrelationIdFilter().doFilter(request, response, failing)
        );
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
    }

    private FilterChain assertingChain(String supplied) {
        return (ignoredRequest, ignoredResponse) -> assertEquals(
                supplied.toLowerCase(), MDC.get(CorrelationIdFilter.MDC_KEY)
        );
    }

    private HttpServletRequest request(String correlationId) {
        Map<String, Object> attributes = new HashMap<>();
        return (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, arguments) -> switch (method.getName()) {
                    case "getAttribute" -> attributes.get((String) arguments[0]);
                    case "setAttribute" -> {
                        attributes.put((String) arguments[0], arguments[1]);
                        yield null;
                    }
                    case "removeAttribute" -> {
                        attributes.remove((String) arguments[0]);
                        yield null;
                    }
                    case "getHeaders" -> correlationId == null
                            ? Collections.emptyEnumeration()
                            : Collections.enumeration(Collections.singleton(correlationId));
                    case "getDispatcherType" -> DispatcherType.REQUEST;
                    case "getMethod" -> "GET";
                    case "getRequestURI" -> "/api/v1/decisions";
                    case "isAsyncStarted" -> false;
                    default -> defaultValue(method.getReturnType());
                }
        );
    }

    private HttpServletResponse response(Map<String, String> headers) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, arguments) -> {
                    if ("setHeader".equals(method.getName())) {
                        headers.put((String) arguments[0], (String) arguments[1]);
                    }
                    return defaultValue(method.getReturnType());
                }
        );
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (boolean.class.equals(type)) {
            return false;
        }
        if (char.class.equals(type)) {
            return '\0';
        }
        return 0;
    }

    private void assertCanonical(String value) {
        assertEquals(value, UUID.fromString(value).toString());
    }
}
