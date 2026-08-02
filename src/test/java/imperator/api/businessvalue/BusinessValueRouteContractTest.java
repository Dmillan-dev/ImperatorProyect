package imperator.api.businessvalue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;

import imperator.api.errors.CorrelationIdFilter;
import imperator.bootstrap.ImperatorApplication;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class BusinessValueRouteContractTest {
    private static final String BUSINESS_VALUE_ID =
            "3ef41daa-7334-486c-8fd9-1811bc716505";
    private static final String ROUTE = "/api/v1/business-value";
    private static final JsonMapper JSON = JsonMapper.shared();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = new SpringApplication(ImperatorApplication.class);
        context = application.run(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF");

        ServletWebServerApplicationContext webContext =
                (ServletWebServerApplicationContext) context;
        port = webContext.getWebServer().getPort();
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
    }

    @Test
    void requiresTheFrozenDecisionSelector()
            throws IOException, InterruptedException {
        HttpResponse<String> response =
                send("GET", ROUTE, MediaType.APPLICATION_JSON_VALUE);

        assertErrorEnvelope(response, 400, "BAD_REQUEST", "Bad request");

        HttpResponse<String> unavailable = send(
                "GET", ROUTE + "?decisionId=" + BUSINESS_VALUE_ID, MediaType.APPLICATION_JSON_VALUE
        );
        assertErrorEnvelope(unavailable, 503, "SERVICE_UNAVAILABLE", "Service unavailable");
    }

    @Test
    void keepsUndefinedBusinessValueDetailRoutesUnavailable()
            throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                "GET",
                ROUTE + "/" + BUSINESS_VALUE_ID,
                MediaType.APPLICATION_JSON_VALUE);

        assertErrorEnvelope(
                response,
                404,
                "RESOURCE_NOT_FOUND",
                "Resource not found");
    }

    @Test
    void keepsTheUnversionedRouteUnavailable()
            throws IOException, InterruptedException {
        HttpResponse<String> response =
                send("GET", "/business-value", MediaType.APPLICATION_JSON_VALUE);

        assertErrorEnvelope(
                response,
                404,
                "RESOURCE_NOT_FOUND",
                "Resource not found");
    }

    @Test
    void rejectsUnsupportedMethodsUsingTheExistingErrorEnvelope()
            throws IOException, InterruptedException {
        for (String method : Set.of("POST", "PUT", "PATCH", "DELETE")) {
            HttpResponse<String> response =
                    send(method, ROUTE, MediaType.APPLICATION_JSON_VALUE);

            assertErrorEnvelope(
                    response,
                    405,
                    "METHOD_NOT_ALLOWED",
                    "Method not allowed");
        }
    }

    @Test
    void returnsOneNormalizedCorrelationIdInBothHeaderAndBody()
            throws IOException, InterruptedException {
        String suppliedCorrelationId = UUID.randomUUID().toString().toUpperCase();
        HttpResponse<String> response = send(
                "GET",
                ROUTE,
                MediaType.APPLICATION_JSON_VALUE,
                suppliedCorrelationId);

        String correlationId = correlationId(response);
        assertEquals(suppliedCorrelationId.toLowerCase(), correlationId);
        assertEquals(
                correlationId,
                JSON.readTree(response.body()).get("correlationId").asString());
    }

    @Test
    void returnsTheJsonErrorEnvelopeForEveryRequiredAcceptHeader()
            throws IOException, InterruptedException {
        for (String accept : Set.of(
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.TEXT_HTML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.ALL_VALUE)) {
            HttpResponse<String> response = send("GET", ROUTE, accept);

            if (Set.of(MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XML_VALUE).contains(accept)) {
                assertErrorEnvelope(response, 406, "NOT_ACCEPTABLE", "Not acceptable");
            } else {
                assertErrorEnvelope(response, 400, "BAD_REQUEST", "Bad request");
            }
        }
    }

    private static HttpResponse<String> send(
            String method,
            String path,
            String accept
    ) throws IOException, InterruptedException {
        return send(method, path, accept, null);
    }

    private static HttpResponse<String> send(
            String method,
            String path,
            String accept,
            String correlationId
    ) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", accept)
                .method(method, HttpRequest.BodyPublishers.noBody());
        if (correlationId != null) {
            request.header(CorrelationIdFilter.HEADER_NAME, correlationId);
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static void assertErrorEnvelope(
            HttpResponse<String> response,
            int expectedStatus,
            String expectedCode,
            String expectedMessage
    ) throws IOException {
        assertEquals(expectedStatus, response.statusCode());
        assertTrue(response.headers()
                .firstValue("Content-Type")
                .orElse("")
                .startsWith(MediaType.APPLICATION_JSON_VALUE));

        JsonNode body = JSON.readTree(response.body());
        Set<String> fields = body.properties().stream()
                .map(entry -> entry.getKey())
                .collect(Collectors.toUnmodifiableSet());

        assertEquals(Set.of("code", "message", "correlationId", "details"), fields);
        assertEquals(expectedCode, body.get("code").asString());
        assertEquals(expectedMessage, body.get("message").asString());
        assertTrue(body.get("details").isObject());
        assertEquals(0, body.get("details").size());
        assertEquals(correlationId(response), body.get("correlationId").asString());
    }

    private static String correlationId(HttpResponse<String> response) {
        return response.headers()
                .firstValue(CorrelationIdFilter.HEADER_NAME)
                .orElseThrow(() -> new AssertionError("Correlation header is missing"));
    }
}
