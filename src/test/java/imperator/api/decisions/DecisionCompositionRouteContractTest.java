package imperator.api.decisions;

import imperator.application.composecase.ComposeDrcAoa001Result;
import imperator.bootstrap.ImperatorApplication;
import imperator.domain.shared.Currency;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.Money;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.ports.in.ComposeDrcAoa001InputPort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import testsupport.security.JwtTestFixture;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecisionCompositionRouteContractTest {
    private static final String ROUTE = "/api/v1/decisions";
    private static final String DECISION_ID = "10000000-0000-4000-8000-000000000001";
    private static final String RECOMMENDATION_ID = "50000000-0000-4000-8000-000000000001";
    private static final String ORIGIN_ID = "20000000-0000-4000-8000-000000000001";
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();
    private static final AtomicInteger CALLS = new AtomicInteger();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        application.addInitializers(applicationContext ->
                applicationContext.getBeanFactory().registerSingleton(
                        "compositionRoutePort",
                        (ComposeDrcAoa001InputPort) command -> {
                            CALLS.incrementAndGet();
                            boolean replayed = command.recommendationId().value().toString().endsWith("0002");
                            return new ComposeDrcAoa001Result(
                                    command.caseId(),
                                    command.decisionId(),
                                    DecisionStatus.CREATED,
                                    command.recommendationId(),
                                    RecommendationType.MODEL_CHANGE,
                                    new ROIAmount(new Money(new BigDecimal("19440.00"), Currency.EUR)),
                                    new ROIConfidence(92),
                                    Severity.LOW,
                                    28,
                                    replayed,
                                    false
                            );
                        }
                ));
        context = application.run(JWT.arguments(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF"
        ));
        port = ((ServletWebServerApplicationContext) context).getWebServer().getPort();
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
    void resetCalls() {
        CALLS.set(0);
    }

    @Test
    void mapsTheExactCanonicalRequestAndReturnsTheFrozenResponse() throws Exception {
        HttpResponse<String> response = send(body(RECOMMENDATION_ID), "ADMIN");

        assertEquals(201, response.statusCode(), response.body());
        JsonNode result = JSON.readTree(response.body());
        assertEquals(Set.of(
                "caseId", "decisionId", "decisionStatus", "recommendationId",
                "recommendationType", "estimatedAnnualizedSavings", "confidence",
                "risk", "evidenceCount", "workspacePath", "replayed", "resumed"
        ), result.properties().stream().map(Map.Entry::getKey).collect(Collectors.toSet()));
        assertEquals("DRC-AOA-001", result.get("caseId").asString());
        assertEquals(DECISION_ID, result.get("decisionId").asString());
        assertEquals("CREATED", result.get("decisionStatus").asString());
        assertEquals("MODEL_CHANGE", result.get("recommendationType").asString());
        assertEquals("19440.00", result.get("estimatedAnnualizedSavings").get("amount").asString());
        assertEquals("EUR", result.get("estimatedAnnualizedSavings").get("currency").asString());
        assertEquals(92, result.get("confidence").asInt());
        assertEquals("LOW", result.get("risk").asString());
        assertEquals(28, result.get("evidenceCount").asInt());
        assertEquals("/decisions/" + DECISION_ID, result.get("workspacePath").asString());
        assertEquals(false, result.get("replayed").asBoolean());
        assertEquals(false, result.get("resumed").asBoolean());
        assertEquals(1, CALLS.get());
    }

    @Test
    void returns200ForAnEquivalentCompleteReplay() throws Exception {
        String replayRecommendationId = "50000000-0000-4000-8000-000000000002";

        HttpResponse<String> response = send(body(replayRecommendationId), "ADMIN");

        assertEquals(200, response.statusCode(), response.body());
        assertEquals(true, JSON.readTree(response.body()).get("replayed").asBoolean());
        assertEquals(1, CALLS.get());
    }

    @Test
    void rejectsNonAdminBeforeTheCompositionPort() throws Exception {
        HttpResponse<String> response = send(body(RECOMMENDATION_ID), "PLATFORM_ENGINEER");

        assertEquals(403, response.statusCode(), response.body());
        assertEquals("ACCESS_DENIED", JSON.readTree(response.body()).get("code").asString());
        assertEquals(0, CALLS.get());
    }

    @Test
    void rejectsUnknownFieldsAndActorMismatchBeforeTheCompositionPort() throws Exception {
        String unknownField = body(RECOMMENDATION_ID).replace("}", ",\"unexpected\":true}");
        HttpResponse<String> unknown = send(unknownField, "ADMIN");
        String mismatchedActor = body(RECOMMENDATION_ID).replace(
                JwtTestFixture.DEFAULT_ACTOR_ID.toString(),
                "40000000-0000-4000-8000-000000000099"
        );
        HttpResponse<String> mismatch = send(mismatchedActor, "ADMIN");

        assertEquals(422, unknown.statusCode(), unknown.body());
        assertEquals("COMPOSITION_FIELDS_INVALID", JSON.readTree(unknown.body()).get("code").asString());
        assertEquals(422, mismatch.statusCode(), mismatch.body());
        assertEquals("COMPOSITION_APPROVER_MISMATCH", JSON.readTree(mismatch.body()).get("code").asString());
        assertEquals(0, CALLS.get());
    }

    @Test
    void rejectsDuplicateEvidenceAndNonV4BusinessIds() throws Exception {
        String duplicateIds = evidenceIds().replace(
                "\"20000000-0000-4000-8000-000000000028\"",
                "\"20000000-0000-4000-8000-000000000001\""
        );
        HttpResponse<String> duplicate = send(body(RECOMMENDATION_ID, duplicateIds), "ADMIN");
        HttpResponse<String> nonV4 = send(
                body(RECOMMENDATION_ID).replace(
                        DECISION_ID,
                        "10000000-0000-1000-8000-000000000001"
                ),
                "ADMIN"
        );

        assertEquals(422, duplicate.statusCode(), duplicate.body());
        assertEquals("COMPOSITION_EVIDENCE_DUPLICATE", JSON.readTree(duplicate.body()).get("code").asString());
        assertEquals(422, nonV4.statusCode(), nonV4.body());
        assertEquals("COMPOSITION_ID_INVALID", JSON.readTree(nonV4.body()).get("code").asString());
        assertEquals(0, CALLS.get());
    }

    @Test
    void rejectsMalformedJsonAsAnUnprocessableCompositionRequest() throws Exception {
        HttpResponse<String> response = send("{\"caseId\":", "ADMIN");

        assertEquals(422, response.statusCode(), response.body());
        assertEquals(
                "COMPOSITION_REQUEST_INVALID",
                JSON.readTree(response.body()).get("code").asString()
        );
        assertEquals(0, CALLS.get());
    }

    private static HttpResponse<String> send(String body, String role)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + ROUTE))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JWT.authorizationHeader(JwtTestFixture.DEFAULT_ACTOR_ID, role))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String body(String recommendationId) {
        return body(recommendationId, evidenceIds());
    }

    private static String body(String recommendationId, String evidenceIds) {
        return """
                {"caseId":"DRC-AOA-001","decisionId":"%s","recommendationId":"%s","originatingEvidenceId":"%s","evidenceIds":[%s],"title":"Optimize AI onboarding assistant cost","businessNeed":"Reduce recurring AI expenditure without losing exception-handling quality","ownerId":"30000000-0000-4000-8000-000000000001","requiredApproverId":"%s","decisionCreatedAt":"2026-07-01T09:00:00Z","recommendationGeneratedAt":"2026-07-01T09:01:00Z"}
                """.formatted(
                        DECISION_ID,
                        recommendationId,
                        ORIGIN_ID,
                        evidenceIds,
                        JwtTestFixture.DEFAULT_ACTOR_ID
                ).trim();
    }

    private static String evidenceIds() {
        return IntStream.rangeClosed(1, 28)
                .mapToObj(index -> "\"20000000-0000-4000-8000-%012d\"".formatted(index))
                .collect(Collectors.joining(","));
    }
}
