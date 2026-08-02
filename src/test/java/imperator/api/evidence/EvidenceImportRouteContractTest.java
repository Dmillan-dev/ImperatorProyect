package imperator.api.evidence;

import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.bootstrap.ImperatorApplication;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import testsupport.security.JwtTestFixture;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvidenceImportRouteContractTest {
    private static final String ROUTE = "/api/v1/evidence/import";
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static InMemoryEvidenceRepository repository;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        repository = new InMemoryEvidenceRepository();
        TransactionRunner transactionRunner = new TransactionRunner() {
            @Override
            public <T> T execute(Supplier<T> operation) {
                return operation.get();
            }
        };
        ImportEvidenceInputPort inputPort = new ImportEvidenceUseCase(repository, transactionRunner);
        application.addInitializers(applicationContext -> {
            applicationContext.getBeanFactory().registerSingleton(
                    "evidenceImportTestRepository",
                    repository
            );
            applicationContext.getBeanFactory().registerSingleton(
                    "evidenceImportTestTransactionRunner",
                    transactionRunner
            );
            applicationContext.getBeanFactory().registerSingleton(
                    "evidenceImportTestInputPort",
                    inputPort
            );
        });
        context = application.run(JWT.arguments(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--spring.main.banner-mode=off",
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

    @BeforeEach
    void clearEvidence() {
        repository.clear();
    }

    @Test
    void importsValidLinesAndReturnsSafeIndependentRejections()
            throws IOException, InterruptedException {
        UUID acceptedId = UUID.randomUUID();
        String body = String.join("\n",
                validLine(acceptedId, "INTERNAL"),
                validLine(acceptedId, "INTERNAL"),
                "{not-json}",
                validLine(UUID.randomUUID(), "RESTRICTED"));

        HttpResponse<String> response = send(
                "POST",
                ROUTE,
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.ofString(body)
        );

        assertEquals(200, response.statusCode());
        assertJsonContentType(response);
        JsonNode result = JSON.readTree(response.body());
        assertEquals("PARTIAL", result.get("status").asString());
        assertEquals(1, result.get("accepted").asInt());
        assertEquals(3, result.get("rejected").asInt());
        assertEquals(4, result.get("items").size());
        assertEquals("ACCEPTED", result.get("items").get(0).get("status").asString());
        assertEquals("DUPLICATE", result.get("items").get(1).get("reason").asString());
        assertEquals("MALFORMED_JSON", result.get("items").get(2).get("reason").asString());
        assertEquals("RESTRICTED_EVIDENCE", result.get("items").get(3).get("reason").asString());
        assertEquals(1, repository.size());
        assertTrue(repository.existsById(new EvidenceId(acceptedId)));
        assertCanonicalCorrelationHeader(response);
    }

    @Test
    void rejectsUnsupportedContentTypeUsingTheExistingErrorEnvelope()
            throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                "POST",
                ROUTE,
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.ofString("{}")
        );

        assertErrorEnvelope(
                response,
                415,
                "UNSUPPORTED_MEDIA_TYPE",
                "Unsupported media type"
        );
    }

    @Test
    void rejectsUnsupportedSourcesAndInconsistentStaleEvidencePerLine()
            throws IOException, InterruptedException {
        String unsupportedSource = validLine(UUID.randomUUID(), "INTERNAL")
                .replace("\"source\":\"AWS\"", "\"source\":\"Salesforce\"");
        String stalePolicy = validLine(UUID.randomUUID(), "INTERNAL")
                .replace("\"freshness\":\"fresh\"", "\"freshness\":\"stale\"");

        HttpResponse<String> response = send(
                "POST",
                ROUTE,
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.ofString(unsupportedSource + "\n" + stalePolicy)
        );

        assertEquals(200, response.statusCode());
        JsonNode result = JSON.readTree(response.body());
        assertEquals("REJECTED", result.get("status").asString());
        assertEquals(0, result.get("accepted").asInt());
        assertEquals(2, result.get("rejected").asInt());
        assertEquals("UNSUPPORTED_SOURCE", result.get("items").get(0).get("reason").asString());
        assertEquals("STALE_POLICY", result.get("items").get(1).get("reason").asString());
        assertEquals(0, repository.size());
    }

    @Test
    void rejectsTrailingAndDuplicateJsonContentPerLine()
            throws IOException, InterruptedException {
        String trailingContent = validLine(UUID.randomUUID(), "INTERNAL") + " {}";
        String duplicateField = validLine(UUID.randomUUID(), "INTERNAL")
                .replace(
                        "\"schema_version\":\"1\"",
                        "\"schema_version\":\"1\",\"schema_version\":\"1\""
                );

        HttpResponse<String> response = send(
                "POST",
                ROUTE,
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.ofString(trailingContent + "\n" + duplicateField)
        );

        assertEquals(200, response.statusCode());
        JsonNode result = JSON.readTree(response.body());
        assertEquals("REJECTED", result.get("status").asString());
        assertEquals(0, result.get("accepted").asInt());
        assertEquals(2, result.get("rejected").asInt());
        assertEquals("MALFORMED_JSON", result.get("items").get(0).get("reason").asString());
        assertEquals("MALFORMED_JSON", result.get("items").get(1).get("reason").asString());
        assertEquals(0, repository.size());
    }

    @Test
    void rejectsEmptyAndInvalidlyEncodedRequestsUsingTheExistingErrorEnvelope()
            throws IOException, InterruptedException {
        HttpResponse<String> empty = send(
                "POST",
                ROUTE,
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.noBody()
        );
        assertErrorEnvelope(empty, 400, "BAD_REQUEST", "Bad request");

        HttpResponse<String> invalidUtf8 = send(
                "POST",
                ROUTE,
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.ofByteArray(new byte[]{(byte) 0xC3, (byte) 0x28})
        );
        assertErrorEnvelope(invalidUtf8, 400, "BAD_REQUEST", "Bad request");
    }

    @Test
    void rejectsOversizedRequestsBeforeAnyLineIsProcessed()
            throws IOException, InterruptedException {
        byte[] body = new byte[EvidenceNdjsonImporter.MAX_PAYLOAD_BYTES + 1];
        java.util.Arrays.fill(body, (byte) 'x');

        HttpResponse<String> response = send(
                "POST",
                ROUTE,
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.ofByteArray(body)
        );

        assertErrorEnvelope(response, 413, "PAYLOAD_TOO_LARGE", "Payload too large");
        assertEquals(0, repository.size());
    }

    @Test
    void keepsTheUnversionedRouteUnavailable()
            throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                "POST",
                "/evidence/import",
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                HttpRequest.BodyPublishers.ofString(validLine(UUID.randomUUID(), "INTERNAL"))
        );

        assertErrorEnvelope(response, 404, "RESOURCE_NOT_FOUND", "Resource not found");
    }

    @Test
    void rejectsUnsupportedMethodsUsingTheExistingErrorEnvelope()
            throws IOException, InterruptedException {
        for (String method : Set.of("GET", "PUT", "PATCH", "DELETE")) {
            HttpResponse<String> response = send(
                    method,
                    ROUTE,
                    EvidenceController.NDJSON_MEDIA_TYPE,
                    MediaType.APPLICATION_JSON_VALUE,
                    HttpRequest.BodyPublishers.noBody()
            );

            assertErrorEnvelope(response, 405, "METHOD_NOT_ALLOWED", "Method not allowed");
        }
    }

    @Test
    void preservesTheJsonErrorEnvelopeForEveryExistingAcceptContract()
            throws IOException, InterruptedException {
        for (String accept : Set.of(
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.TEXT_HTML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.ALL_VALUE)) {
            HttpResponse<String> response = send(
                    "POST",
                    ROUTE,
                    MediaType.APPLICATION_JSON_VALUE,
                    accept,
                    HttpRequest.BodyPublishers.ofString("{}")
            );

            assertErrorEnvelope(
                    response,
                    415,
                    "UNSUPPORTED_MEDIA_TYPE",
                    "Unsupported media type"
            );
        }
    }

    @Test
    void rejectsAnUnsupportedSuccessRepresentationUsingTheErrorEnvelope()
            throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                "POST",
                ROUTE,
                EvidenceController.NDJSON_MEDIA_TYPE,
                MediaType.TEXT_HTML_VALUE,
                HttpRequest.BodyPublishers.ofString(validLine(UUID.randomUUID(), "INTERNAL"))
        );

        assertErrorEnvelope(response, 406, "NOT_ACCEPTABLE", "Not acceptable");
        assertEquals(0, repository.size());
    }

    private static HttpResponse<String> send(
            String method,
            String path,
            String contentType,
            String accept,
            HttpRequest.BodyPublisher body
    ) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", accept)
                .header("Authorization", JWT.authorizationHeader());
        if (contentType != null) {
            request.header("Content-Type", contentType);
        }
        return client.send(
                request.method(method, body).build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private static String validLine(UUID id, String sensitivity) {
        return """
                {"id":"%s","schema_version":"1","timestamp":"2026-06-30T23:59:59Z","source":"AWS","source_type":"cloud_cost","source_object_ref":"cost-export/2026-06","entity":"onboarding-assistant","event_type":"cloud_cost_observed","severity":"info","actor":"aws-cost-export","evidence_type":"cloud_cost","case_hint":"DRC-AOA-001","observed_fact":"AWS monthly cost is EUR410","business_meaning":"Provides infrastructure cost input","correlation_key":"DRC-AOA-001","sensitivity":"%s","confidence":"high","freshness":"fresh","review_status":"accepted","metadata":{"evidence_ref":"E-AWS-001","period":"2026-06","currency":"EUR","monthly_cost":"410.00"},"raw_payload":{"mode":"not_stored"}}
                """.formatted(id, sensitivity).trim();
    }

    private static void assertErrorEnvelope(
            HttpResponse<String> response,
            int expectedStatus,
            String expectedCode,
            String expectedMessage
    ) throws IOException {
        assertEquals(expectedStatus, response.statusCode());
        assertJsonContentType(response);

        JsonNode body = JSON.readTree(response.body());
        Set<String> fields = body.properties().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());

        assertEquals(Set.of("code", "message", "correlationId", "details"), fields);
        assertEquals(expectedCode, body.get("code").asString());
        assertEquals(expectedMessage, body.get("message").asString());
        assertTrue(body.get("details").isObject());
        assertEquals(0, body.get("details").size());
        assertEquals(correlationId(response), body.get("correlationId").asString());
    }

    private static void assertJsonContentType(HttpResponse<String> response) {
        assertTrue(response.headers()
                .firstValue("Content-Type")
                .orElse("")
                .startsWith(MediaType.APPLICATION_JSON_VALUE));
    }

    private static void assertCanonicalCorrelationHeader(HttpResponse<String> response) {
        String correlationId = correlationId(response);
        assertEquals(correlationId, UUID.fromString(correlationId).toString());
    }

    private static String correlationId(HttpResponse<String> response) {
        return response.headers()
                .firstValue("X-Correlation-ID")
                .orElseThrow(() -> new AssertionError("Correlation header is missing"));
    }

    static final class InMemoryEvidenceRepository implements EvidenceRepository {
        private final Map<EvidenceId, Evidence> evidence = new LinkedHashMap<>();

        @Override
        public void save(Evidence item) {
            evidence.put(item.id(), item);
        }

        @Override
        public Optional<Evidence> findById(EvidenceId id) {
            return Optional.ofNullable(evidence.get(id));
        }

        @Override
        public boolean existsById(EvidenceId id) {
            return evidence.containsKey(id);
        }

        int size() {
            return evidence.size();
        }

        void clear() {
            evidence.clear();
        }
    }
}
