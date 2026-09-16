package imperator.api.observability;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ImperatorRouteCatalog {
    private static final List<Route> ROUTES = List.of(
            route("R01", "POST", "/api/v1/evidence/import"),
            route("R02", "GET", "/api/v1/decisions"),
            route("R03", "GET", "/api/v1/decisions/{id}"),
            route("R04", "GET", "/api/v1/decisions/{id}/timeline"),
            route("R05", "GET", "/api/v1/decisions/{id}/evidence"),
            route("R06", "GET", "/api/v1/decisions/{id}/roi"),
            route("R07", "GET", "/api/v1/recommendations/{id}"),
            route("R08", "GET", "/api/v1/decisions/{id}/ledger"),
            route("R09", "POST", "/api/v1/decisions/{id}/ledger/approve"),
            route("R10", "POST", "/api/v1/decisions/{id}/ledger/reject"),
            route("R11", "POST", "/api/v1/decisions/{id}/ledger/defer"),
            route("R12", "POST", "/api/v1/decisions/{id}/ledger/mark-implemented"),
            route("R13", "POST", "/api/v1/decisions/{id}/ledger/validate-result"),
            route("R14", "GET", "/api/v1/business-value"),
            route("R15", "GET", "/api/v1/ledger"),
            route("R16", "POST", "/api/v1/decisions")
    );
    private static final List<String> OPERATIONAL_PATHS = List.of(
            "/livez",
            "/readyz",
            "/actuator/health",
            "/actuator/health/liveness",
            "/actuator/health/readiness",
            "/actuator/prometheus"
    );

    private ImperatorRouteCatalog() {
    }

    public static Optional<String> routeId(String method, String path) {
        return ROUTES.stream()
                .filter(route -> route.matches(method, path))
                .map(Route::id)
                .findFirst();
    }

    public static String metricTemplate(String method, String pathPattern) {
        if (pathPattern == null || pathPattern.isBlank()) {
            return "UNKNOWN";
        }
        Optional<Route> productRoute = ROUTES.stream()
                .filter(route -> route.method().equals(method))
                .filter(route -> route.template().equals(pathPattern))
                .findFirst();
        if (productRoute.isPresent()) {
            return productRoute.orElseThrow().template();
        }
        return OPERATIONAL_PATHS.contains(pathPattern) ? pathPattern : "UNKNOWN";
    }

    private static Route route(String id, String method, String template) {
        int identifier = template.indexOf("{id}");
        String expression = identifier < 0
                ? Pattern.quote(template)
                : Pattern.quote(template.substring(0, identifier))
                        + "[^/]+"
                        + Pattern.quote(template.substring(identifier + 4));
        return new Route(id, method, template, Pattern.compile("^" + expression + "$"));
    }

    private record Route(String id, String method, String template, Pattern path) {
        boolean matches(String requestMethod, String requestPath) {
            return method.equals(requestMethod) && path.matcher(requestPath).matches();
        }
    }
}
