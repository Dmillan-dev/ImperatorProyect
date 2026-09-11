package imperator.api.observability;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class PostgresReadinessHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;
    private final ImperatorTelemetry telemetry;
    private final AtomicReference<Boolean> lastReady = new AtomicReference<>();

    public PostgresReadinessHealthIndicator(
            DataSource dataSource,
            ImperatorTelemetry telemetry
    ) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource is required");
        this.telemetry = Objects.requireNonNull(telemetry, "Telemetry is required");
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            boolean ready = connection.isValid(2);
            recordTransition(ready);
            return ready ? Health.up().build() : Health.down().build();
        } catch (SQLException failure) {
            telemetry.databaseFailure("readiness_probe", failure);
            recordTransition(false);
            return Health.down().build();
        }
    }

    private void recordTransition(boolean ready) {
        Boolean previous = lastReady.getAndSet(ready);
        if (previous == null || previous != ready) {
            telemetry.runtimeReadiness(ready ? "accepting_traffic" : "refusing_traffic");
        }
    }
}
