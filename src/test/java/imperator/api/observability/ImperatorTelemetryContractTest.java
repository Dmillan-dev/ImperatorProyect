package imperator.api.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImperatorTelemetryContractTest {
    private static final Set<String> FORBIDDEN_TAGS = Set.of(
            "correlation_id", "decision_id", "evidence_id", "actor", "subject",
            "email", "repository", "account", "exception", "message", "path"
    );

    @Test
    void recordsOnlyTheFrozenMetersAndBoundedTags() {
        SimpleMeterRegistry registry = registry();
        ImperatorTelemetry telemetry = new ImperatorTelemetry(registry);

        telemetry.evidenceImport(true, 1_000_000L, null);
        telemetry.evidenceImport(false, 1_000_000L, null);
        telemetry.connectorSync("github", "complete", 1_000_000L, null);
        telemetry.decisionComposition("created", 1_000_000L, null);
        telemetry.ledgerCommand("approve", "success", 1_000_000L, null);
        telemetry.businessValue("ready", 1_000_000L, null);
        telemetry.authenticationFailure("missing_token");
        telemetry.authorizationDenial("ADMIN", "R01");
        telemetry.databaseFailure(
                "get_decision",
                new IllegalStateException("sentinel-db-detail", new SQLException("secret"))
        );

        assertEquals(1.0, registry.get("imperator.evidence.import.lines")
                .tag("outcome", "accepted").counter().count());
        assertEquals(1L, registry.get("imperator.connector.sync")
                .tag("source", "github").timer().count());
        assertEquals(1L, registry.get("imperator.decision.composition")
                .tag("outcome", "created").timer().count());
        assertEquals(1L, registry.get("imperator.ledger.command")
                .tags("command", "approve", "outcome", "success").timer().count());
        assertEquals(1L, registry.get("imperator.business.value.projection")
                .tag("outcome", "ready").timer().count());
        assertEquals(1.0, registry.get("imperator.security.authentication.failure")
                .tag("reason", "missing_token").counter().count());
        assertEquals(1.0, registry.get("imperator.security.authorization.denial")
                .tags("role", "ADMIN", "route_id", "R01").counter().count());
        assertEquals(1.0, registry.get("imperator.database.operation.failure")
                .tag("operation", "get_decision").counter().count());

        assertTrue(registry.getMeters().size() <= 256);
        assertFalse(registry.getMeters().stream()
                .flatMap(meter -> meter.getId().getTags().stream())
                .anyMatch(tag -> FORBIDDEN_TAGS.contains(tag.getKey())));
    }

    @Test
    void deniesUnknownImperatorMetersAndChangedLabelSets() {
        SimpleMeterRegistry registry = registry();

        Counter.builder("imperator.uncontracted")
                .tag("outcome", "success")
                .register(registry)
                .increment();
        Counter.builder("imperator.evidence.import.lines")
                .tags("outcome", "accepted", "correlation_id", "sentinel")
                .register(registry)
                .increment();

        assertNull(registry.find("imperator.uncontracted").meter());
        assertNull(registry.find("imperator.evidence.import.lines").meter());
    }

    @Test
    void normalizesCallerControlledValuesBeforeTheyReachMeters() {
        SimpleMeterRegistry registry = registry();
        ImperatorTelemetry telemetry = new ImperatorTelemetry(registry);

        telemetry.connectorSync("sentinel-provider", "sentinel-secret-value", 1L, null);
        telemetry.authorizationDenial("sentinel-user", null);

        var connector = registry.get("imperator.connector.sync").timer();
        assertEquals("github", connector.getId().getTag("source"));
        assertEquals("error", connector.getId().getTag("outcome"));
        assertTrue(registry.find("imperator.security.authorization.denial").meters().isEmpty());
    }

    private SimpleMeterRegistry registry() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        registry.config().meterFilter(new ImperatorMeterContractFilter());
        return registry;
    }
}
