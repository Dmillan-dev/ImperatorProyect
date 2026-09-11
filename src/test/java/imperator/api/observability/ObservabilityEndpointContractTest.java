package imperator.api.observability;

import imperator.bootstrap.ObservabilityRuntimeConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import testsupport.security.JwtTestFixture;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObservabilityEndpointContractTest {
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int applicationPort;
    private static int managementPort;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        application.setDefaultProperties(ObservabilityRuntimeConfiguration.runtimeDefaults());
        context = application.run(JWT.arguments(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--management.server.address=127.0.0.1",
                "--management.server.port=0",
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF"
        ));
        applicationPort = ((ServletWebServerApplicationContext) context)
                .getWebServer().getPort();
        managementPort = context.getEnvironment()
                .getRequiredProperty("local.management.port", Integer.class);
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopRuntime() {
        if (client != null) {
            client.close();
        }
        if (context != null) {
            context.close();
        }
        JWT.close();
    }

    @Test
    void exposesProcessLivenessAndDatabaseAwareReadinessOnTheApplicationPort()
            throws IOException, InterruptedException {
        assertHealth(get(applicationPort, "/livez"), 200);
        assertHealth(get(applicationPort, "/readyz"), 200);
        assertEquals(404, get(applicationPort, "/actuator/health").statusCode());
    }

    @Test
    void exposesOnlyHealthAndPrometheusOnTheManagementPort()
            throws IOException, InterruptedException {
        assertRootHealth(get(managementPort, "/actuator/health"));
        assertHealth(get(managementPort, "/actuator/health/liveness"), 200);
        assertHealth(get(managementPort, "/actuator/health/readiness"), 200);
        assertEquals(200, get(managementPort, "/actuator/prometheus").statusCode());

        for (String endpoint : Set.of(
                "env", "configprops", "beans", "mappings", "loggers", "heapdump",
                "threaddump", "shutdown", "caches", "conditions", "sessions",
                "scheduledtasks"
        )) {
            assertEquals(404, get(managementPort, "/actuator/" + endpoint).statusCode(), endpoint);
        }
    }

    @Test
    void exportsBoundedHttpAndSecurityMetricsWithoutRequestIdentifiers()
            throws IOException, InterruptedException {
        String identifier = UUID.randomUUID().toString();
        assertEquals(
                401,
                get(applicationPort, "/api/v1/decisions/" + identifier).statusCode()
        );

        String metrics = get(managementPort, "/actuator/prometheus").body();
        assertTrue(metrics.contains("jvm_memory_used_bytes"));
        assertTrue(metrics.contains("process_uptime_seconds"));
        assertTrue(metrics.contains("http_server_requests"));
        assertTrue(metrics.contains("imperator_security_authentication_failure_total"));
        assertTrue(metrics.contains("reason=\"missing_token\""));
        assertFalse(metrics.contains(identifier));
        assertFalse(metrics.contains("exception="));
    }

    private static HttpResponse<String> get(int port, String path)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void assertHealth(HttpResponse<String> response, int status)
            throws IOException {
        assertEquals(status, response.statusCode());
        JsonNode body = JSON.readTree(response.body());
        Set<String> fields = body.properties().stream()
                .map(entry -> entry.getKey())
                .collect(Collectors.toUnmodifiableSet());
        assertEquals(Set.of("status"), fields);
        assertNotNull(body.get("status"));
    }

    private static void assertRootHealth(HttpResponse<String> response) throws IOException {
        assertEquals(200, response.statusCode());
        JsonNode body = JSON.readTree(response.body());
        Set<String> fields = body.properties().stream()
                .map(entry -> entry.getKey())
                .collect(Collectors.toUnmodifiableSet());
        assertEquals(Set.of("status", "groups"), fields);
        assertNotNull(body.get("status"));
        assertNotNull(body.get("groups"));
    }
}
