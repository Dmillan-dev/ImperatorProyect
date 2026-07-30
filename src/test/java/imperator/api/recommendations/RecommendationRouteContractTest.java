package imperator.api.recommendations;

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

class RecommendationRouteContractTest {
    private static final String RECOMMENDATION_ID =
            "ef87c1bb-4cd5-42d5-9366-5151270c6286";
    private static final String COLLECTION_ROUTE = "/api/v1/recommendations";
    private static final String DETAIL_ROUTE =
            COLLECTION_ROUTE + "/" + RECOMMENDATION_ID;
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
    void returnsNotImplementedForTheRecommendationDetail()
            throws IOException, InterruptedException {
        HttpResponse<String> response =
                send("GET", DETAIL_ROUTE, MediaType.APPLICATION_JSON_VALUE);

        assertErrorEnvelope(response, 501, "NOT_IMPLEMENTED", "Not implemented");
    }

    @Test
    void keepsThePostMvpCollectionRouteUnavailable()
            throws IOException, InterruptedException {
        HttpResponse<String> response =
                send("GET", COLLECTION_ROUTE, MediaType.APPLICATION_JSON_VALUE);

        assertErrorEnvelope(
                response,
                404,
                "RESOURCE_NOT_FOUND",
                "Resource not found");
    }

    @Test
    void keepsTheUnversionedDetailRouteUnavailable()
            throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                "GET",
                "/recommendations/" + RECOMMENDATION_ID,
                MediaType.APPLICATION_JSON_VALUE);

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
                    send(method, DETAIL_ROUTE, MediaType.APPLICATION_JSON_VALUE);

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
                DETAIL_ROUTE,
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
            HttpResponse<String> response = send("GET", DETAIL_ROUTE, accept);

            assertErrorEnvelope(response, 501, "NOT_IMPLEMENTED", "Not implemented");
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
