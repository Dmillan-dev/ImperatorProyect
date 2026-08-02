package imperator.api;

import imperator.application.appendledgerentry.AppendLedgerEntryResult;
import imperator.application.businessvalue.BusinessValueProjection;
import imperator.application.importevidence.ImportEvidenceResult;
import imperator.application.query.DecisionDetail;
import imperator.application.query.DecisionRoiView;
import imperator.application.query.DecisionSummary;
import imperator.application.query.DecisionTimelineItem;
import imperator.application.query.EvidenceSummary;
import imperator.application.query.LedgerEntryView;
import imperator.application.query.PageResult;
import imperator.application.query.RecommendationView;
import imperator.application.reviewdecision.ReviewDecisionResult;
import imperator.bootstrap.ImperatorApplication;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.Currency;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.Money;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.GetDecisionEvidenceInputPort;
import imperator.ports.in.GetDecisionInputPort;
import imperator.ports.in.GetDecisionLedgerInputPort;
import imperator.ports.in.GetDecisionRoiInputPort;
import imperator.ports.in.GetDecisionTimelineInputPort;
import imperator.ports.in.GetRecommendationInputPort;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.ListDecisionsInputPort;
import imperator.ports.in.ListLedgerEntriesInputPort;
import imperator.ports.in.ProjectBusinessValueInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import testsupport.security.JwtTestFixture;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionalRestApiContractTest {
    private static final UUID DECISION_UUID = UUID.fromString("10000000-0000-4000-8000-000000000001");
    private static final UUID RECOMMENDATION_UUID = UUID.fromString("20000000-0000-4000-8000-000000000002");
    private static final UUID EVIDENCE_UUID = UUID.fromString("30000000-0000-4000-8000-000000000003");
    private static final UUID LEDGER_UUID = UUID.fromString("40000000-0000-4000-8000-000000000004");
    private static final UUID ACTOR_UUID = UUID.fromString("50000000-0000-4000-8000-000000000005");
    private static final UUID REPLAY_UUID = UUID.fromString("60000000-0000-4000-8000-000000000099");
    private static final Instant NOW = Instant.parse("2026-07-01T12:00:00Z");
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        application.addInitializers(applicationContext -> registerPorts(applicationContext.getBeanFactory()));
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
    void allFrozenReadRoutesDelegateAndReturnJsonInsteadOf501()
            throws IOException, InterruptedException {
        List<String> routes = List.of(
                "/api/v1/decisions",
                "/api/v1/decisions/" + DECISION_UUID,
                "/api/v1/decisions/" + DECISION_UUID + "/timeline",
                "/api/v1/decisions/" + DECISION_UUID + "/evidence",
                "/api/v1/decisions/" + DECISION_UUID + "/roi",
                "/api/v1/recommendations/" + RECOMMENDATION_UUID,
                "/api/v1/decisions/" + DECISION_UUID + "/ledger",
                "/api/v1/business-value?decisionId=" + DECISION_UUID,
                "/api/v1/ledger"
        );
        for (String route : routes) {
            HttpResponse<String> response = get(route);
            assertEquals(200, response.statusCode(), route);
            assertNotEquals(501, response.statusCode(), route);
            assertTrue(response.headers().firstValue("Content-Type").orElse("")
                    .startsWith(MediaType.APPLICATION_JSON_VALUE));
        }

        JsonNode list = JSON.readTree(get("/api/v1/decisions").body());
        assertEquals(java.util.Set.of("items", "page", "size", "totalItems", "totalPages"),
                list.properties().stream().map(Map.Entry::getKey).collect(java.util.stream.Collectors.toSet()));
        JsonNode roi = JSON.readTree(get("/api/v1/decisions/" + DECISION_UUID + "/roi").body());
        assertEquals("19440.00", roi.get("estimatedAnnualizedRecovery").get("amount").asString());
        assertEquals("EUR", roi.get("estimatedAnnualizedRecovery").get("currency").asString());
    }

    @Test
    void evidenceImportPreservesTheExistingFunctionalPartialSuccessRoute()
            throws IOException, InterruptedException {
        String line = """
                {"id":"%s","schema_version":"1","timestamp":"2026-06-30T23:59:59Z","source":"AWS","source_type":"cloud_cost","source_object_ref":"cost-export/2026-06","entity":"onboarding-assistant","event_type":"cloud_cost_observed","severity":"info","actor":"aws-cost-export","evidence_type":"cloud_cost","case_hint":"DRC-AOA-001","observed_fact":"AWS monthly cost is EUR410","business_meaning":"Provides infrastructure cost input","correlation_key":"DRC-AOA-001","sensitivity":"INTERNAL","confidence":"high","freshness":"fresh","review_status":"accepted","metadata":{"evidence_ref":"E-AWS-001","currency":"EUR","monthly_cost":"410.00"},"raw_payload":{"mode":"not_stored"}}
                """.formatted(EVIDENCE_UUID).trim();
        HttpRequest request = HttpRequest.newBuilder(uri("/api/v1/evidence/import"))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", "application/x-ndjson")
                .header("Authorization", JWT.authorizationHeader(ACTOR_UUID, "ADMIN"))
                .POST(HttpRequest.BodyPublishers.ofString(line))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, JSON.readTree(response.body()).get("accepted").asInt());
    }

    @Test
    void allFiveLedgerCommandsUseJwtActorAndReturn201()
            throws IOException, InterruptedException {
        String base = "/api/v1/decisions/" + DECISION_UUID + "/ledger/";
        Map<String, String> requests = Map.of(
                "approve", reviewBody(),
                "reject", reviewBody(),
                "defer", """
                        {"reviewedAt":"2026-07-01T12:00:00Z","reason":"Need more evidence","requiredEvidence":"quality report"}
                        """.trim(),
                "mark-implemented", """
                        {"occurredAt":"2026-07-01T12:00:00Z","reason":"Deployed","evidenceIds":["%s"],"expectedPreviousEntryId":"%s","period":"2026-07"}
                        """.formatted(EVIDENCE_UUID, LEDGER_UUID).trim(),
                "validate-result", """
                        {"occurredAt":"2026-07-01T12:00:00Z","reason":"Validated","evidenceIds":["%s"],"expectedPreviousEntryId":"%s","period":"2026-07","annualizedBaselineCost":{"amount":"28080.00","currency":"EUR"},"annualizedPostActionCost":{"amount":"8640.00","currency":"EUR"},"actualTransitionCost":{"amount":"0.00","currency":"EUR"}}
                        """.formatted(EVIDENCE_UUID, LEDGER_UUID).trim()
        );
        int suffix = 10;
        for (Map.Entry<String, String> request : requests.entrySet()) {
            String idempotencyKey = new UUID(0x6000000000004000L, 0x8000000000000000L + suffix++).toString();
            HttpResponse<String> response = post(base + request.getKey(), request.getValue(), idempotencyKey);
            assertEquals(201, response.statusCode(), request.getKey() + ": " + response.body());
            assertNotEquals(501, response.statusCode());
            assertEquals(false, JSON.readTree(response.body()).get("replayed").asBoolean());
        }
    }

    @Test
    void postMvpRoutesRemainAbsent()
            throws IOException, InterruptedException {
        assertEquals(404, get("/api/v1/recommendations").statusCode());
        assertEquals(404, get("/api/v1/ledger/" + LEDGER_UUID).statusCode());
    }

    @Test
    void enforcesPaginationJwtActorAndIdempotencyTransportContracts()
            throws IOException, InterruptedException {
        HttpResponse<String> pagination = get("/api/v1/decisions?size=0");
        assertEquals(400, pagination.statusCode());
        assertEquals("INVALID_PAGINATION", JSON.readTree(pagination.body()).get("code").asString());

        HttpResponse<String> replay = post(
                "/api/v1/decisions/" + DECISION_UUID + "/ledger/approve", reviewBody(), REPLAY_UUID.toString()
        );
        assertEquals(200, replay.statusCode());
        assertEquals(true, JSON.readTree(replay.body()).get("replayed").asBoolean());

        HttpRequest spoofedActor = commandRequest(reviewBody())
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .header("X-Imperator-Actor-ID", UUID.randomUUID().toString())
                .header("X-Imperator-Actor-Role", "AUDITOR")
                .build();
        HttpResponse<String> actorResponse = client.send(spoofedActor, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, actorResponse.statusCode());
        assertEquals(ACTOR_UUID.toString(), JSON.readTree(actorResponse.body()).get("reviewerId").asString());
        assertEquals("ADMIN", JSON.readTree(actorResponse.body()).get("reviewerRole").asString());

        HttpRequest missingIdempotency = commandRequest(reviewBody()).build();
        HttpResponse<String> idempotency = client.send(
                missingIdempotency, HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, idempotency.statusCode());
        assertEquals("IDEMPOTENCY_KEY_REQUIRED", JSON.readTree(idempotency.body()).get("code").asString());
    }

    private static void registerPorts(
            org.springframework.beans.factory.config.ConfigurableListableBeanFactory beans
    ) {
        DecisionSummary summary = decisionSummary();
        beans.registerSingleton("functionalListDecisions", (ListDecisionsInputPort) query ->
                new PageResult<>(List.of(summary), query.pageRequest().page(), query.pageRequest().size(), 1, 1));
        beans.registerSingleton("functionalGetDecision", (GetDecisionInputPort) query -> decisionDetail());
        beans.registerSingleton("functionalTimeline", (GetDecisionTimelineInputPort) query ->
                new PageResult<>(List.of(timelineItem()), query.pageRequest().page(), query.pageRequest().size(), 1, 1));
        beans.registerSingleton("functionalEvidence", (GetDecisionEvidenceInputPort) query ->
                new PageResult<>(List.of(evidenceSummary()), query.pageRequest().page(), query.pageRequest().size(), 1, 1));
        beans.registerSingleton("functionalRoi", (GetDecisionRoiInputPort) query -> roi());
        beans.registerSingleton("functionalRecommendation", (GetRecommendationInputPort) query -> recommendation());
        beans.registerSingleton("functionalDecisionLedger", (GetDecisionLedgerInputPort) query ->
                new PageResult<>(List.of(ledger()), query.pageRequest().page(), query.pageRequest().size(), 1, 1));
        beans.registerSingleton("functionalLedgerList", (ListLedgerEntriesInputPort) query ->
                new PageResult<>(List.of(ledger()), query.pageRequest().page(), query.pageRequest().size(), 1, 1));
        beans.registerSingleton("functionalBusinessValue", (ProjectBusinessValueInputPort) (id, explanation) ->
                businessValue());
        beans.registerSingleton("functionalImport", (ImportEvidenceInputPort) command ->
                new ImportEvidenceResult(command.evidenceId(), command.correlationKey(), true, false));
        beans.registerSingleton("functionalReview", (ReviewDecisionInputPort) command ->
                new ReviewDecisionResult(
                        command.ledgerEntryId(), command.decisionId(), new RecommendationId(RECOMMENDATION_UUID),
                        DecisionStatus.APPROVED, command.reviewerId(), command.reviewerRole(),
                        command.reviewReason(), REPLAY_UUID.equals(command.ledgerEntryId().value())
                ));
        beans.registerSingleton("functionalAppend", (AppendLedgerEntryInputPort) command ->
                new AppendLedgerEntryResult(
                        command.ledgerEntryId(), command.decisionId(),
                        Optional.of(new RecommendationId(RECOMMENDATION_UUID)), command.entryType(),
                        command.occurredAt(), command.evidenceSnapshotIds().size(), Optional.empty(), false
                ));
    }

    private static DecisionSummary decisionSummary() {
        return new DecisionSummary(
                new DecisionId(DECISION_UUID), "DRC-AOA-001", "Reduce AI cost", "Recover spend",
                DecisionStatus.APPROVED, new UserId(ACTOR_UUID), new UserId(ACTOR_UUID),
                Optional.of(new RecommendationId(RECOMMENDATION_UUID)), new Timestamp(NOW), new Timestamp(NOW)
        );
    }

    private static DecisionDetail decisionDetail() {
        DecisionSummary item = decisionSummary();
        return new DecisionDetail(
                item.decisionId(), item.caseId(), item.title(), item.businessNeed(), item.status(),
                item.ownerId(), item.requiredApproverId(), item.recommendationId(), item.createdAt(),
                item.updatedAt(), new EvidenceId(EVIDENCE_UUID), List.of(new EvidenceId(EVIDENCE_UUID)),
                Optional.of(new UserId(ACTOR_UUID)), Optional.of(new Timestamp(NOW)), Optional.of("Approved")
        );
    }

    private static DecisionTimelineItem timelineItem() {
        return new DecisionTimelineItem(
                "DECISION", DECISION_UUID, new Timestamp(NOW), "Reduce AI cost", Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), List.of(new EvidenceId(EVIDENCE_UUID))
        );
    }

    private static EvidenceSummary evidenceSummary() {
        return new EvidenceSummary(
                new EvidenceId(EVIDENCE_UUID), new Timestamp(NOW), "AWS", "cloud_cost", "cost-export",
                "onboarding-assistant", "cloud_cost_observed", Severity.INFO, "aws", "cloud_cost",
                "Monthly cost observed", "Cost baseline", "DRC-AOA-001", "INTERNAL", "HIGH", "ACCEPTED"
        );
    }

    private static DecisionRoiView roi() {
        return new DecisionRoiView(
                new DecisionId(DECISION_UUID), new RecommendationId(RECOMMENDATION_UUID),
                eur("2340.00"), eur("720.00"), eur("0.00"), eur("1620.00"), eur("19440.00"),
                new ROIConfidence(92), Severity.LOW, "DRC-AOA-001-v1", List.of(new EvidenceId(EVIDENCE_UUID))
        );
    }

    private static RecommendationView recommendation() {
        return new RecommendationView(
                new RecommendationId(RECOMMENDATION_UUID), new DecisionId(DECISION_UUID),
                RecommendationType.MODEL_CHANGE, "Use a lower-cost model", "Deterministic cost policy",
                eur("19440.00"), new ROIConfidence(92), Severity.LOW, new UserId(ACTOR_UUID),
                new UserId(ACTOR_UUID), new Timestamp(NOW), List.of(new EvidenceId(EVIDENCE_UUID))
        );
    }

    private static LedgerEntryView ledger() {
        return new LedgerEntryView(
                new LedgerEntryId(LEDGER_UUID), new DecisionId(DECISION_UUID),
                Optional.of(new RecommendationId(RECOMMENDATION_UUID)), new UserId(ACTOR_UUID), "ADMIN",
                new Timestamp(NOW), LedgerEntryType.APPROVED, "Decision approved", "Approved",
                List.of(new EvidenceId(EVIDENCE_UUID)), Optional.of(eur("19440.00")), Optional.empty(),
                Optional.of(new ROIConfidence(92)), Optional.of(Severity.LOW), Optional.empty(), Map.of()
        );
    }

    private static BusinessValueProjection businessValue() {
        BusinessValueProjection.LedgerFact fact = new BusinessValueProjection.LedgerFact(
                new LedgerEntryId(LEDGER_UUID), LedgerEntryType.APPROVED, new UserId(ACTOR_UUID), "ADMIN",
                new Timestamp(NOW), Optional.empty(), List.of(new EvidenceId(EVIDENCE_UUID))
        );
        return new BusinessValueProjection(
                "DRC-AOA-001", "DRC-AOA-001", new DecisionId(DECISION_UUID), "Reduce AI cost",
                "Recover spend", DecisionStatus.APPROVED, new Timestamp(NOW),
                new RecommendationId(RECOMMENDATION_UUID), RecommendationType.MODEL_CHANGE,
                "Use a lower-cost model", "Deterministic cost policy", Optional.empty(), new Timestamp(NOW),
                eur("19440.00"), eur("19440.00"), new BigDecimal("0.00"), eur("28080.00"),
                eur("8640.00"), eur("0.00"), new ROIConfidence(92), Severity.LOW, "DRC-AOA-001-v1",
                List.of(new EvidenceId(EVIDENCE_UUID)), List.of("A-ROI-001"),
                new LedgerEntryId(LEDGER_UUID), new LedgerEntryId(LEDGER_UUID),
                new LedgerEntryId(LEDGER_UUID), List.of(fact)
        );
    }

    private static ROIAmount eur(String amount) {
        return new ROIAmount(new Money(new BigDecimal(amount), Currency.EUR));
    }

    private static String reviewBody() {
        return """
                {"reviewedAt":"2026-07-01T12:00:00Z","reason":"Approved"}
                """.trim();
    }

    private static HttpResponse<String> get(String path) throws IOException, InterruptedException {
        return client.send(
                HttpRequest.newBuilder(uri(path))
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", JWT.authorizationHeader(ACTOR_UUID, "ADMIN"))
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private static HttpResponse<String> post(String path, String body, String idempotencyKey)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Idempotency-Key", idempotencyKey)
                .header("Authorization", JWT.authorizationHeader(ACTOR_UUID, "ADMIN"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpRequest.Builder commandRequest(String body) {
        return HttpRequest.newBuilder(uri(
                        "/api/v1/decisions/" + DECISION_UUID + "/ledger/approve"
                ))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JWT.authorizationHeader(ACTOR_UUID, "ADMIN"))
                .POST(HttpRequest.BodyPublishers.ofString(body));
    }

    private static URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }
}
