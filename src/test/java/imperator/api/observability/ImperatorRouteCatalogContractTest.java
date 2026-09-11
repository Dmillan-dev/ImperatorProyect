package imperator.api.observability;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImperatorRouteCatalogContractTest {
    private static final String ID = "5b2f135b-6a45-4f1b-bfac-7e5fdc2f4574";

    @Test
    void mapsEveryFrozenProductRouteToItsStableIdentifier() {
        List<RouteFixture> routes = List.of(
                route("R01", "POST", "/api/v1/evidence/import"),
                route("R02", "GET", "/api/v1/decisions"),
                route("R03", "GET", "/api/v1/decisions/" + ID),
                route("R04", "GET", "/api/v1/decisions/" + ID + "/timeline"),
                route("R05", "GET", "/api/v1/decisions/" + ID + "/evidence"),
                route("R06", "GET", "/api/v1/decisions/" + ID + "/roi"),
                route("R07", "GET", "/api/v1/recommendations/" + ID),
                route("R08", "GET", "/api/v1/decisions/" + ID + "/ledger"),
                route("R09", "POST", "/api/v1/decisions/" + ID + "/ledger/approve"),
                route("R10", "POST", "/api/v1/decisions/" + ID + "/ledger/reject"),
                route("R11", "POST", "/api/v1/decisions/" + ID + "/ledger/defer"),
                route("R12", "POST", "/api/v1/decisions/" + ID + "/ledger/mark-implemented"),
                route("R13", "POST", "/api/v1/decisions/" + ID + "/ledger/validate-result"),
                route("R14", "GET", "/api/v1/business-value"),
                route("R15", "GET", "/api/v1/ledger"),
                route("R16", "POST", "/api/v1/decisions")
        );

        routes.forEach(route -> assertEquals(
                route.id(),
                ImperatorRouteCatalog.routeId(route.method(), route.path()).orElseThrow()
        ));
    }

    @Test
    void exposesOnlyContractedTemplatesOrUnknownToHttpMetrics() {
        assertEquals(
                "/api/v1/decisions/{id}",
                ImperatorRouteCatalog.metricTemplate(
                        "GET", "/api/v1/decisions/{id}"
                )
        );
        assertEquals(
                "/actuator/prometheus",
                ImperatorRouteCatalog.metricTemplate(
                        "GET", "/actuator/prometheus"
                )
        );
        assertEquals(
                "UNKNOWN",
                ImperatorRouteCatalog.metricTemplate(
                        "GET", "/api/v1/decisions/" + ID
                )
        );
        assertTrue(ImperatorRouteCatalog.routeId("GET", "/api/v1/unknown").isEmpty());
    }

    private static RouteFixture route(String id, String method, String path) {
        return new RouteFixture(id, method, path);
    }

    private record RouteFixture(String id, String method, String path) {
    }
}
