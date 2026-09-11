package imperator.api.observability;

import imperator.application.exceptions.BusinessRuleViolationException;
import imperator.application.exceptions.ConflictException;
import imperator.application.exceptions.NotFoundException;
import imperator.application.exceptions.ValidationException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class ImperatorTelemetry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImperatorTelemetry.class);
    private static final Set<String> ROLES = Set.of(
            "ADMIN", "PLATFORM_ENGINEER", "FINANCE", "AUDITOR", "unknown"
    );

    private final MeterRegistry registry;

    public ImperatorTelemetry(MeterRegistry registry) {
        this.registry = registry;
    }

    public void httpRequestCompleted(String routeId, int status, long durationNanos) {
        safe(() -> {
            var event = LOGGER.atInfo()
                    .addKeyValue("event", "http.request.completed")
                    .addKeyValue("module", "http")
                    .addKeyValue("outcome", httpOutcome(status))
                    .addKeyValue("duration_ms", millis(durationNanos));
            if (routeId != null) {
                event.addKeyValue("route_id", routeId);
            }
            event.log("HTTP request completed");
        });
    }

    public void evidenceImport(boolean accepted, long durationNanos, Throwable failure) {
        String outcome = accepted ? "accepted" : "rejected";
        safe(() -> counter("imperator.evidence.import.lines", "outcome", outcome).increment());
        event("evidence.import.completed", "evidence", outcome, durationNanos, null, null);
        databaseFailure("import_evidence", failure);
    }

    public void connectorSync(
            String source,
            String outcome,
            long durationNanos,
            Throwable failure
    ) {
        String boundedSource = "aws".equals(source) ? "aws" : "github";
        String boundedOutcome = bounded(outcome);
        safe(() -> timer(
                "imperator.connector.sync",
                "source", boundedSource,
                "outcome", boundedOutcome
        ).record(durationNanos, TimeUnit.NANOSECONDS));
        event(
                "connector.sync.completed", "connector", boundedOutcome,
                durationNanos, "source_type", boundedSource
        );
        databaseFailure("connector_sync", failure);
    }

    public void decisionComposition(String outcome, long durationNanos, Throwable failure) {
        String boundedOutcome = bounded(outcome);
        safe(() -> timer(
                "imperator.decision.composition", "outcome", boundedOutcome
        ).record(durationNanos, TimeUnit.NANOSECONDS));
        event(
                "decision.composition.completed", "decision", boundedOutcome,
                durationNanos, null, null
        );
        databaseFailure("decision_composition", failure);
    }

    public void ledgerCommand(
            String command,
            String outcome,
            long durationNanos,
            Throwable failure
    ) {
        String boundedCommand = bounded(command);
        String boundedOutcome = bounded(outcome);
        safe(() -> timer(
                "imperator.ledger.command",
                "command", boundedCommand,
                "outcome", boundedOutcome
        ).record(durationNanos, TimeUnit.NANOSECONDS));
        event(
                "ledger.command.completed", "ledger", boundedOutcome,
                durationNanos, "command", boundedCommand
        );
        databaseFailure("ledger_command", failure);
    }

    public void businessValue(String outcome, long durationNanos, Throwable failure) {
        String boundedOutcome = bounded(outcome);
        safe(() -> timer(
                "imperator.business.value.projection", "outcome", boundedOutcome
        ).record(durationNanos, TimeUnit.NANOSECONDS));
        event(
                "business_value.projection.completed", "business_value",
                boundedOutcome, durationNanos, null, null
        );
        databaseFailure("business_value_projection", failure);
    }

    public void authenticationFailure(String reason) {
        String boundedReason = "invalid_token".equals(reason)
                ? "invalid_token"
                : "missing_token";
        safe(() -> counter(
                "imperator.security.authentication.failure", "reason", boundedReason
        ).increment());
        event("authentication.failed", "security", "denied", 0L, null, null);
    }

    public void authorizationDenial(String role, String routeId) {
        String boundedRole = ROLES.contains(role) ? role : "unknown";
        if (routeId != null) {
            safe(() -> counter(
                    "imperator.security.authorization.denial",
                    "role", boundedRole,
                    "route_id", routeId
            ).increment());
        }
        safe(() -> {
            var event = LOGGER.atInfo()
                    .addKeyValue("event", "authorization.denied")
                    .addKeyValue("module", "security")
                    .addKeyValue("outcome", "denied")
                    .addKeyValue("actor_role", boundedRole);
            if (routeId != null) {
                event.addKeyValue("route_id", routeId);
            }
            event.log("Authorization denied");
        });
    }

    public void databaseFailure(String operation, Throwable failure) {
        if (failure == null || !hasSqlCause(failure)) {
            return;
        }
        String boundedOperation = bounded(operation);
        safe(() -> counter(
                "imperator.database.operation.failure", "operation", boundedOperation
        ).increment());
        event("database.operation.failed", "database", "error", 0L, null, null);
    }

    public String failureOutcome(Throwable failure) {
        if (failure instanceof ConflictException) {
            return "conflict";
        }
        if (failure instanceof BusinessRuleViolationException exception
                && "BUSINESS_VALUE_NOT_READY".equals(exception.code())) {
            return "not_ready";
        }
        if (failure instanceof NotFoundException || failure instanceof ValidationException) {
            return "not_ready";
        }
        return "error";
    }

    @EventListener
    public void readinessChanged(AvailabilityChangeEvent<ReadinessState> event) {
        runtimeReadiness(event.getState().name().toLowerCase(Locale.ROOT));
    }

    public void runtimeReadiness(String outcome) {
        event(
                "runtime.readiness.changed", "runtime", bounded(outcome),
                0L, null, null
        );
    }

    private Counter counter(String name, String... tags) {
        return Counter.builder(name).tags(tags).register(registry);
    }

    private Timer timer(String name, String... tags) {
        return Timer.builder(name).tags(tags).register(registry);
    }

    private void event(
            String name,
            String module,
            String outcome,
            long durationNanos,
            String detailKey,
            String detailValue
    ) {
        safe(() -> {
            var event = LOGGER.atInfo()
                    .addKeyValue("event", name)
                    .addKeyValue("module", module)
                    .addKeyValue("outcome", outcome);
            if (durationNanos > 0L) {
                event.addKeyValue("duration_ms", millis(durationNanos));
            }
            if (detailKey != null && detailValue != null) {
                event.addKeyValue(detailKey, detailValue);
            }
            event.log("IMPERATOR operation completed");
        });
    }

    private String bounded(String value) {
        if (value == null || !value.matches("[A-Za-z0-9_]{1,48}")) {
            return "error";
        }
        return value.toLowerCase(Locale.ROOT);
    }

    private String httpOutcome(int status) {
        if (status >= 500) {
            return "server_error";
        }
        if (status >= 400) {
            return "client_error";
        }
        if (status >= 300) {
            return "redirection";
        }
        return "success";
    }

    private long millis(long durationNanos) {
        return Math.max(0L, TimeUnit.NANOSECONDS.toMillis(durationNanos));
    }

    private boolean hasSqlCause(Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            if (current instanceof SQLException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private void safe(Runnable observation) {
        try {
            observation.run();
        } catch (RuntimeException ignored) {
            // Telemetry is never authoritative and must not change product outcomes.
        }
    }
}
