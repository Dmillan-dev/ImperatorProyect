package imperator.api.decisions;

import imperator.application.query.DecisionTimelineItem;
import imperator.application.query.EvidenceSummary;
import imperator.application.query.PageResult;
import imperator.bootstrap.ImperatorApplication;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.ports.in.GetDecisionEvidenceInputPort;
import imperator.ports.in.GetDecisionTimelineInputPort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import testsupport.security.JwtTestFixture;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class RbacEvidenceVisibilityContractTest {
    private static final UUID DECISION_ID =
            UUID.fromString("10000000-0000-4000-8000-000000000001");
    private static final Instant OBSERVED_AT = Instant.parse("2026-07-01T12:00:00Z");
    private static final String REDACTED = "[REDACTED]";
    private static final EvidenceSummary PUBLIC = evidence(
            1, "Manual", "business_context", "business_context_requested", "PUBLIC"
    );
    private static final EvidenceSummary INTERNAL = evidence(
            2, "Jira", "business_context", "decision_status_observed", "INTERNAL"
    );
    private static final EvidenceSummary CONFIDENTIAL_CODE = evidence(
            3, "GitHub", "code_deployment", "code_change_merged", "CONFIDENTIAL"
    );
    private static final EvidenceSummary CONFIDENTIAL_COST = evidence(
            4, "AWS", "cloud_cost", "cloud_cost_observed", "CONFIDENTIAL"
    );
    private static final EvidenceSummary CONFIDENTIAL_REJECTED_PAYLOAD = evidence(
            5, "OpenAI", "ai_consumption", "ai_sensitive_payload_rejected", "CONFIDENTIAL"
    );
    private static final EvidenceSummary RESTRICTED = evidence(
            6, "OpenAI", "ai_consumption", "ai_usage_observed", "RESTRICTED"
    );
    private static final EvidenceSummary UNKNOWN_SENSITIVITY = evidence(
            7, "GitHub", "code_deployment", "code_change_merged", "UNKNOWN"
    );
    private static final List<EvidenceSummary> EVIDENCE = List.of(
            PUBLIC, INTERNAL, CONFIDENTIAL_CODE, CONFIDENTIAL_COST,
            CONFIDENTIAL_REJECTED_PAYLOAD, RESTRICTED, UNKNOWN_SENSITIVITY
    );
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        application.addInitializers(applicationContext -> {
            var beans = applicationContext.getBeanFactory();
            beans.registerSingleton("rbacEvidence", (GetDecisionEvidenceInputPort) query ->
                    new PageResult<>(
                            EVIDENCE, query.pageRequest().page(), query.pageRequest().size(),
                            EVIDENCE.size(), 1
                    ));
            beans.registerSingleton("rbacTimeline", (GetDecisionTimelineInputPort) query ->
                    new PageResult<>(
                            timeline(), query.pageRequest().page(), query.pageRequest().size(),
                            EVIDENCE.size(), 1
                    ));
        });
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

    @Test
    void exposesPublicAndInternalAndAppliesExactConfidentialPolicy()
            throws IOException, InterruptedException {
        for (String role : List.of("ADMIN", "PLATFORM_ENGINEER", "FINANCE", "AUDITOR")) {
            Map<String, JsonNode> items = evidenceById(getEvidence(role));
            assertEquals(PUBLIC.observedFact(), observed(items, PUBLIC));
            assertEquals(INTERNAL.observedFact(), observed(items, INTERNAL));
            assertEquals(CONFIDENTIAL_COST.observedFact(), observed(items, CONFIDENTIAL_COST));
            assertRestrictedRedaction(items.get(id(RESTRICTED)), RESTRICTED);

            if ("FINANCE".equals(role)) {
                assertConfidentialRedaction(items.get(id(CONFIDENTIAL_CODE)), CONFIDENTIAL_CODE);
                assertConfidentialRedaction(
                        items.get(id(CONFIDENTIAL_REJECTED_PAYLOAD)),
                        CONFIDENTIAL_REJECTED_PAYLOAD
                );
                assertConfidentialRedaction(
                        items.get(id(UNKNOWN_SENSITIVITY)), UNKNOWN_SENSITIVITY
                );
            } else {
                assertEquals(CONFIDENTIAL_CODE.observedFact(), observed(items, CONFIDENTIAL_CODE));
                assertEquals(
                        CONFIDENTIAL_REJECTED_PAYLOAD.observedFact(),
                        observed(items, CONFIDENTIAL_REJECTED_PAYLOAD)
                );
                assertEquals(
                        UNKNOWN_SENSITIVITY.observedFact(),
                        observed(items, UNKNOWN_SENSITIVITY)
                );
            }
        }
    }

    @Test
    void appliesTheSameEvidenceDecisionToTimelineContent()
            throws IOException, InterruptedException {
        Map<String, JsonNode> finance = timelineById(getTimeline("FINANCE"));
        assertEquals(REDACTED, finance.get(id(CONFIDENTIAL_CODE)).get("summary").asString());
        assertNull(finance.get(id(CONFIDENTIAL_CODE)).get("source").stringValue());
        assertNull(finance.get(id(CONFIDENTIAL_CODE)).get("actor").stringValue());
        assertEquals(
                CONFIDENTIAL_COST.observedFact(),
                finance.get(id(CONFIDENTIAL_COST)).get("summary").asString()
        );
        assertEquals(REDACTED, finance.get(id(RESTRICTED)).get("summary").asString());

        Map<String, JsonNode> admin = timelineById(getTimeline("ADMIN"));
        assertEquals(
                CONFIDENTIAL_CODE.observedFact(),
                admin.get(id(CONFIDENTIAL_CODE)).get("summary").asString()
        );
        assertEquals(REDACTED, admin.get(id(RESTRICTED)).get("summary").asString());
    }

    private static HttpResponse<String> getEvidence(String role)
            throws IOException, InterruptedException {
        return get("/api/v1/decisions/" + DECISION_ID + "/evidence", role);
    }

    private static HttpResponse<String> getTimeline(String role)
            throws IOException, InterruptedException {
        return get("/api/v1/decisions/" + DECISION_ID + "/timeline", role);
    }

    private static HttpResponse<String> get(String path, String role)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JWT.authorizationHeader(
                        JwtTestFixture.DEFAULT_ACTOR_ID, role
                ))
                .GET().build();
        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode(), response.body());
        return response;
    }

    private static Map<String, JsonNode> evidenceById(HttpResponse<String> response)
            throws IOException {
        Map<String, JsonNode> items = itemsById(response, "evidenceId");
        assertEquals(EVIDENCE.size(), items.size());
        for (JsonNode item : items.values()) {
            assertEquals(16, item.size());
            assertFalse(item.has("metadata"));
            assertFalse(item.has("rawPayload"));
        }
        return items;
    }

    private static Map<String, JsonNode> timelineById(HttpResponse<String> response)
            throws IOException {
        return itemsById(response, "referenceId");
    }

    private static Map<String, JsonNode> itemsById(
            HttpResponse<String> response,
            String idField
    ) throws IOException {
        Map<String, JsonNode> items = new LinkedHashMap<>();
        for (JsonNode item : JSON.readTree(response.body()).get("items")) {
            items.put(item.get(idField).asString(), item);
        }
        return Map.copyOf(items);
    }

    private static void assertConfidentialRedaction(
            JsonNode item,
            EvidenceSummary original
    ) {
        assertEquals(original.source(), item.get("source").asString());
        assertEquals(original.sourceType(), item.get("sourceType").asString());
        assertEquals(original.eventType(), item.get("eventType").asString());
        assertEquals(original.evidenceType(), item.get("evidenceType").asString());
        for (String field : List.of(
                "sourceObjectRef", "entity", "actor", "observedFact",
                "businessMeaning", "correlationKey"
        )) {
            assertEquals(REDACTED, item.get(field).asString(), field);
        }
    }

    private static void assertRestrictedRedaction(
            JsonNode item,
            EvidenceSummary original
    ) {
        assertEquals(id(original), item.get("evidenceId").asString());
        assertEquals(original.timestamp().value().toString(), item.get("timestamp").asString());
        assertEquals(original.sensitivity(), item.get("sensitivity").asString());
        assertEquals(original.reviewStatus(), item.get("reviewStatus").asString());
        assertEquals(original.severity().value(), item.get("severity").asString());
        assertEquals(original.confidence(), item.get("confidence").asString());
        for (String field : List.of(
                "source", "sourceType", "sourceObjectRef", "entity", "eventType", "actor",
                "evidenceType", "observedFact", "businessMeaning", "correlationKey"
        )) {
            assertEquals(REDACTED, item.get(field).asString(), field);
        }
    }

    private static String observed(Map<String, JsonNode> items, EvidenceSummary evidence) {
        return items.get(id(evidence)).get("observedFact").asString();
    }

    private static String id(EvidenceSummary evidence) {
        return evidence.evidenceId().value().toString();
    }

    private static List<DecisionTimelineItem> timeline() {
        return EVIDENCE.stream().map(item -> new DecisionTimelineItem(
                "EVIDENCE", item.evidenceId().value(), item.timestamp(), item.observedFact(),
                Optional.of(item.source()), Optional.of(item.actor()),
                Optional.of(item.confidence()), Optional.empty(), List.of(item.evidenceId())
        )).toList();
    }

    private static EvidenceSummary evidence(
            int suffix,
            String source,
            String evidenceType,
            String eventType,
            String sensitivity
    ) {
        UUID id = new UUID(0x3000000000004000L, 0x8000000000000000L + suffix);
        return new EvidenceSummary(
                new EvidenceId(id), new Timestamp(OBSERVED_AT.plusSeconds(suffix)), source,
                evidenceType, "source-ref-" + suffix, "onboarding-assistant", eventType,
                Severity.INFO, "actor-" + suffix, evidenceType, "observed-" + suffix,
                "meaning-" + suffix, "DRC-AOA-001", sensitivity, "HIGH", "ACCEPTED"
        );
    }
}
