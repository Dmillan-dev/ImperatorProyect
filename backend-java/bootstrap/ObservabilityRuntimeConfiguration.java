package imperator.bootstrap;

import imperator.api.observability.ImperatorHttpRequestFilter;
import imperator.api.observability.ImperatorMeterContractFilter;
import imperator.api.observability.PostgresReadinessHealthIndicator;
import imperator.api.observability.ImperatorServerRequestObservationConvention;
import imperator.api.observability.ImperatorTelemetry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationConvention;

import java.util.Map;
import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "imperator.observability",
        name = "enabled",
        havingValue = "true"
)
public class ObservabilityRuntimeConfiguration {
    private static final Map<String, Object> RUNTIME_DEFAULTS = Map.ofEntries(
            Map.entry("imperator.observability.enabled", "true"),
            Map.entry("spring.application.name", "imperator-backend"),
            Map.entry("management.server.address", "0.0.0.0"),
            Map.entry("management.server.port", "9090"),
            Map.entry("management.endpoints.access.default", "none"),
            Map.entry("management.endpoint.health.access", "read-only"),
            Map.entry("management.endpoint.prometheus.access", "read-only"),
            Map.entry("management.endpoints.web.exposure.include", "health,prometheus"),
            Map.entry("management.endpoints.jmx.exposure.exclude", "*"),
            Map.entry("management.endpoints.web.discovery.enabled", "false"),
            Map.entry("management.endpoint.health.show-details", "never"),
            Map.entry("management.endpoint.health.show-components", "never"),
            Map.entry("management.endpoint.health.probes.enabled", "true"),
            Map.entry("management.endpoint.health.probes.add-additional-paths", "true"),
            Map.entry("management.endpoint.health.group.liveness.include", "livenessState"),
            Map.entry("management.endpoint.health.group.readiness.include", "readinessState,db"),
            Map.entry("management.endpoint.health.validate-group-membership", "false"),
            Map.entry(
                    "management.metrics.distribution.percentiles-histogram.http.server.requests",
                    "true"
            ),
            Map.entry("logging.structured.format.console", "ecs"),
            Map.entry("logging.structured.ecs.service.name", "imperator-backend"),
            Map.entry("logging.structured.json.context.include", "true"),
            Map.entry("logging.structured.json.exclude", "error,tags")
    );

    public static Map<String, Object> runtimeDefaults() {
        return RUNTIME_DEFAULTS;
    }

    @Bean
    ImperatorTelemetry imperatorTelemetry(MeterRegistry meterRegistry) {
        return new ImperatorTelemetry(meterRegistry);
    }

    @Bean
    ImperatorHttpRequestFilter imperatorHttpRequestFilter(ImperatorTelemetry telemetry) {
        return new ImperatorHttpRequestFilter(telemetry);
    }

    @Bean
    ServerRequestObservationConvention imperatorServerRequestObservationConvention() {
        return new ImperatorServerRequestObservationConvention();
    }

    @Bean
    MeterFilter imperatorMeterContractFilter() {
        return new ImperatorMeterContractFilter();
    }

    @Bean("db")
    @ConditionalOnProperty(
            prefix = "imperator.postgresql",
            name = "enabled",
            havingValue = "true"
    )
    HealthIndicator postgresReadinessHealthIndicator(
            DataSource dataSource,
            ImperatorTelemetry telemetry
    ) {
        return new PostgresReadinessHealthIndicator(dataSource, telemetry);
    }
}
