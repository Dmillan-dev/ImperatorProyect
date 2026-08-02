package imperator.api.errors;

import imperator.application.exceptions.AuthorizationException;
import imperator.application.exceptions.BusinessRuleViolationException;
import imperator.application.exceptions.ConflictException;
import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.shared.DecisionId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imperator.bootstrap.ImperatorApplication;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import testsupport.security.JwtTestFixture;

class ApiErrorContractTest {
    private static final String TEST_PROFILE = "api-error-contract-test";
    private static final String BASE_PATH = "/api/v1/_error-contract";
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        context = application.run(JWT.arguments(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--spring.profiles.active=" + TEST_PROFILE,
                "--debug=false",
                "--logging.level.root=OFF"));

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
        JWT.close();
    }

    @Test
    void generatesCorrelationIdAndReturnsNotImplementedEnvelope()
            throws IOException, InterruptedException {
        HttpResponse<String> response = send(request("GET", BASE_PATH + "/not-implemented"));

        assertEnvelope(response, 501, ApiErrorCode.NOT_IMPLEMENTED);
        assertCanonicalUuid(correlationId(response));
    }

    @Test
    void preservesValidCorrelationIdAndNormalizesItToLowerCase()
            throws IOException, InterruptedException {
        String supplied = UUID.randomUUID().toString().toUpperCase();
        HttpRequest request = request("GET", BASE_PATH + "/not-implemented")
                .header(CorrelationIdFilter.HEADER_NAME, supplied)
                .build();

        HttpResponse<String> response = send(request);

        assertEnvelope(response, 501, ApiErrorCode.NOT_IMPLEMENTED);
        assertEquals(supplied.toLowerCase(), correlationId(response));
    }

    @Test
    void replacesBlankInvalidOrMultipleCorrelationIds()
            throws IOException, InterruptedException {
        String first = UUID.randomUUID().toString();
        String second = UUID.randomUUID().toString();
        HttpRequest request = request("GET", BASE_PATH + "/not-implemented")
                .header(CorrelationIdFilter.HEADER_NAME, first)
                .header(CorrelationIdFilter.HEADER_NAME, second)
                .build();

        HttpResponse<String> response = send(request);
        String actual = correlationId(response);

        assertEnvelope(response, 501, ApiErrorCode.NOT_IMPLEMENTED);
        assertCanonicalUuid(actual);
        assertNotEquals(first, actual);
        assertNotEquals(second, actual);

        HttpRequest invalidRequest = request("GET", BASE_PATH + "/not-implemented")
                .header(CorrelationIdFilter.HEADER_NAME, "not-a-uuid")
                .build();
        HttpResponse<String> invalidResponse = send(invalidRequest);
        assertEnvelope(invalidResponse, 501, ApiErrorCode.NOT_IMPLEMENTED);
        assertCanonicalUuid(correlationId(invalidResponse));

        HttpRequest blankRequest = request("GET", BASE_PATH + "/not-implemented")
                .header(CorrelationIdFilter.HEADER_NAME, "")
                .build();
        HttpResponse<String> blankResponse = send(blankRequest);
        assertEnvelope(blankResponse, 501, ApiErrorCode.NOT_IMPLEMENTED);
        assertCanonicalUuid(correlationId(blankResponse));
    }

    @Test
    void returnsBadRequestEnvelopeForMalformedJson()
            throws IOException, InterruptedException {
        HttpRequest request = request("POST", BASE_PATH + "/payload")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("{"))
                .build();

        assertEnvelope(send(request), 400, ApiErrorCode.BAD_REQUEST);
    }

    @Test
    void returnsNotFoundEnvelopeForUnknownResource()
            throws IOException, InterruptedException {
        assertEnvelope(
                send(request("GET", "/api/v1/unknown-resource")),
                404,
                ApiErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void returnsMethodNotAllowedEnvelope()
            throws IOException, InterruptedException {
        assertEnvelope(
                send(request("POST", BASE_PATH + "/not-implemented")),
                405,
                ApiErrorCode.METHOD_NOT_ALLOWED);
    }

    @Test
    void returnsSafeInternalServerErrorEnvelope()
            throws IOException, InterruptedException {
        HttpResponse<String> response = send(request("GET", BASE_PATH + "/failure"));

        assertEnvelope(response, 500, ApiErrorCode.INTERNAL_SERVER_ERROR);
        assertFalse(response.body().contains("restricted-internal-detail"));
        assertFalse(response.body().contains(IllegalStateException.class.getName()));
    }

    @Test
    void mapsEveryFrozenApplicationExceptionFamilyWithoutLosingItsCode()
            throws IOException, InterruptedException {
        assertEnvelope(send(request("GET", BASE_PATH + "/authorization")), 403,
                "GOVERNANCE_ACTION_FORBIDDEN", "Forbidden");
        assertEnvelope(send(request("GET", BASE_PATH + "/not-found")), 404,
                "DECISION_NOT_FOUND", "Decision not found: 10000000-0000-4000-8000-000000000001");
        assertEnvelope(send(request("GET", BASE_PATH + "/conflict")), 409,
                "LEDGER_OPERATION_CONFLICT", "Conflict");
        assertEnvelope(send(request("GET", BASE_PATH + "/business-rule")), 409,
                "BUSINESS_VALUE_NOT_READY", "Business Value is not ready");
        assertEnvelope(send(request("GET", BASE_PATH + "/validation")), 422,
                "FIELD_INVALID", "Field is invalid");
        assertEnvelope(send(request("GET", BASE_PATH + "/invalid-metadata")), 500,
                "INTERNAL_SERVER_ERROR", "Internal server error");
    }

    private static HttpRequest.Builder request(String method, String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JWT.authorizationHeader());

        return switch (method) {
            case "GET" -> builder.GET();
            case "POST" -> builder.POST(HttpRequest.BodyPublishers.noBody());
            default -> throw new IllegalArgumentException("Unsupported test method: " + method);
        };
    }

    private static HttpResponse<String> send(HttpRequest.Builder request)
            throws IOException, InterruptedException {
        return send(request.build());
    }

    private static HttpResponse<String> send(HttpRequest request)
            throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void assertEnvelope(
            HttpResponse<String> response,
            int expectedStatus,
            ApiErrorCode expectedCode
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

        assertEquals(
                Set.of("code", "message", "correlationId", "details"),
                fields);
        assertEquals(expectedCode.name(), body.get("code").asString());
        assertEquals(expectedCode.message(), body.get("message").asString());
        assertTrue(body.get("details").isObject());
        assertEquals(0, body.get("details").size());
        assertEquals(correlationId(response), body.get("correlationId").asString());
    }

    private static void assertEnvelope(
            HttpResponse<String> response,
            int expectedStatus,
            String expectedCode,
            String expectedMessage
    ) throws IOException {
        assertEquals(expectedStatus, response.statusCode());
        JsonNode body = JSON.readTree(response.body());
        assertEquals(expectedCode, body.get("code").asString());
        assertEquals(expectedMessage, body.get("message").asString());
        assertTrue(body.get("details").isObject());
        assertEquals(correlationId(response), body.get("correlationId").asString());
    }

    private static String correlationId(HttpResponse<String> response) {
        return response.headers()
                .firstValue(CorrelationIdFilter.HEADER_NAME)
                .orElseThrow(() -> new AssertionError("Correlation header is missing"));
    }

    private static void assertCanonicalUuid(String value) {
        assertEquals(36, value.length());
        assertEquals(value, UUID.fromString(value).toString());
    }

    @RestController
    @Profile(TEST_PROFILE)
    @RequestMapping(BASE_PATH)
    public static final class ErrorContractController {

        @GetMapping("/not-implemented")
        void notImplemented() {
            throw new NotImplementedApiException();
        }

        @PostMapping(
                value = "/payload",
                consumes = MediaType.APPLICATION_JSON_VALUE)
        void payload(@RequestBody ContractPayload payload) {
            // The malformed JSON contract fails before this method can execute.
        }

        @GetMapping("/failure")
        void failure() {
            throw new IllegalStateException("restricted-internal-detail");
        }

        @GetMapping("/authorization")
        void authorization() {
            throw new AuthorizationException("GOVERNANCE_ACTION_FORBIDDEN", "Forbidden");
        }

        @GetMapping("/not-found")
        void notFound() {
            throw new DecisionNotFoundException(
                    new DecisionId(UUID.fromString("10000000-0000-4000-8000-000000000001"))
            );
        }

        @GetMapping("/conflict")
        void conflict() {
            throw new ConflictException("LEDGER_OPERATION_CONFLICT", "Conflict");
        }

        @GetMapping("/business-rule")
        void businessRule() {
            throw new BusinessRuleViolationException(
                    "BUSINESS_VALUE_NOT_READY", "Business Value is not ready"
            );
        }

        @GetMapping("/validation")
        void validation() {
            throw new ValidationException("FIELD_INVALID", "Field is invalid");
        }

        @GetMapping("/invalid-metadata")
        void invalidMetadata() {
            throw new BusinessRuleViolationException(
                    "BUSINESS_VALUE_METADATA_INVALID", "restricted-internal-detail"
            );
        }
    }

    public record ContractPayload(String value) {
    }
}
