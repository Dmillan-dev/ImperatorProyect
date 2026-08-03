package imperator.api.security;

import imperator.api.errors.CorrelationIdFilter;
import imperator.application.appendledgerentry.AppendLedgerEntryResult;
import imperator.application.importevidence.ImportEvidenceResult;
import imperator.application.reviewdecision.ReviewDecisionResult;
import imperator.bootstrap.ImperatorApplication;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.RecommendationId;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import testsupport.security.JwtTestFixture;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RbacAuthorizationContractTest {
    private static final String ADMIN = "ADMIN";
    private static final String PLATFORM_ENGINEER = "PLATFORM_ENGINEER";
    private static final String FINANCE = "FINANCE";
    private static final String AUDITOR = "AUDITOR";
    private static final Set<String> ALL_ROLES = Set.of(
            ADMIN, PLATFORM_ENGINEER, FINANCE, AUDITOR
    );
    private static final Set<String> FROZEN_MAPPINGS = Set.of(
            "POST /api/v1/evidence/import",
            "GET /api/v1/decisions",
            "GET /api/v1/decisions/{id}",
            "GET /api/v1/decisions/{id}/timeline",
            "GET /api/v1/decisions/{id}/evidence",
            "GET /api/v1/decisions/{id}/roi",
            "GET /api/v1/recommendations/{id}",
            "GET /api/v1/decisions/{id}/ledger",
            "POST /api/v1/decisions/{id}/ledger/approve",
            "POST /api/v1/decisions/{id}/ledger/reject",
            "POST /api/v1/decisions/{id}/ledger/defer",
            "POST /api/v1/decisions/{id}/ledger/mark-implemented",
            "POST /api/v1/decisions/{id}/ledger/validate-result",
            "GET /api/v1/business-value",
            "GET /api/v1/ledger"
    );
    private static final UUID DECISION_ID =
            UUID.fromString("10000000-0000-4000-8000-000000000001");
    private static final UUID RECOMMENDATION_ID =
            UUID.fromString("20000000-0000-4000-8000-000000000002");
    private static final UUID EVIDENCE_ID =
            UUID.fromString("30000000-0000-4000-8000-000000000003");
    private static final List<RoutePolicy> ROUTES = List.of(
            new RoutePolicy("POST", "/api/v1/evidence/import", Set.of(ADMIN), evidenceBody(),
                    "application/x-ndjson"),
            read("/api/v1/decisions"),
            read("/api/v1/decisions/" + DECISION_ID),
            read("/api/v1/decisions/" + DECISION_ID + "/timeline"),
            read("/api/v1/decisions/" + DECISION_ID + "/evidence"),
            read("/api/v1/decisions/" + DECISION_ID + "/roi"),
            read("/api/v1/recommendations/" + RECOMMENDATION_ID),
            read("/api/v1/decisions/" + DECISION_ID + "/ledger"),
            command("approve", Set.of(ADMIN), reviewBody()),
            command("reject", Set.of(ADMIN), reviewBody()),
            command("defer", Set.of(ADMIN, PLATFORM_ENGINEER, FINANCE), deferBody()),
            command("mark-implemented", Set.of(PLATFORM_ENGINEER), implementationBody()),
            command("validate-result", Set.of(FINANCE), validationBody()),
            read("/api/v1/business-value?decisionId=" + DECISION_ID),
            read("/api/v1/ledger")
    );
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();
    private static final AtomicInteger IMPORT_CALLS = new AtomicInteger();
    private static final AtomicInteger REVIEW_CALLS = new AtomicInteger();
    private static final AtomicInteger APPEND_CALLS = new AtomicInteger();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        application.addInitializers(applicationContext -> {
            var beans = applicationContext.getBeanFactory();
            beans.registerSingleton("rbacImport", (ImportEvidenceInputPort) command -> {
                IMPORT_CALLS.incrementAndGet();
                return new ImportEvidenceResult(
                        command.evidenceId(), command.correlationKey(), true, false
                );
            });
            beans.registerSingleton("rbacReview", (ReviewDecisionInputPort) command -> {
                REVIEW_CALLS.incrementAndGet();
                return new ReviewDecisionResult(
                        command.ledgerEntryId(), command.decisionId(),
                        new RecommendationId(RECOMMENDATION_ID), DecisionStatus.APPROVED,
                        command.reviewerId(), command.reviewerRole(), command.reviewReason(), false
                );
            });
            beans.registerSingleton("rbacAppend", (AppendLedgerEntryInputPort) command -> {
                APPEND_CALLS.incrementAndGet();
                return new AppendLedgerEntryResult(
                        command.ledgerEntryId(), command.decisionId(),
                        java.util.Optional.of(new RecommendationId(RECOMMENDATION_ID)),
                        command.entryType(), command.occurredAt(),
                        command.evidenceSnapshotIds().size(), java.util.Optional.empty(), false
                );
            });
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
    void enforcesTheCompleteFifteenRouteByFourRoleMatrix()
            throws IOException, InterruptedException {
        IMPORT_CALLS.set(0);
        REVIEW_CALLS.set(0);
        APPEND_CALLS.set(0);

        assertEquals(15, ROUTES.size());
        for (RoutePolicy route : ROUTES) {
            for (String role : ALL_ROLES) {
                HttpResponse<String> response = send(route, role);
                if (route.allowedRoles().contains(role)) {
                    if ("POST".equals(route.method())) {
                        assertTrue(
                                response.statusCode() >= 200 && response.statusCode() < 300,
                                route + " " + role + " returned " + response.statusCode()
                                        + ": " + response.body()
                        );
                    } else {
                        assertNotEquals(401, response.statusCode(), route + " " + role);
                        assertNotEquals(403, response.statusCode(), route + " " + role);
                    }
                } else {
                    assertAccessDenied(response);
                }
            }
        }

        assertEquals(1, IMPORT_CALLS.get());
        assertEquals(5, REVIEW_CALLS.get());
        assertEquals(2, APPEND_CALLS.get());
    }

    @Test
    void preservesUnknownRouteAndUnsupportedMethodContracts()
            throws IOException, InterruptedException {
        HttpResponse<String> unknown = sendRaw(
                "GET", "/api/v1/unknown", null, null, ADMIN
        );
        assertEquals(404, unknown.statusCode());

        for (String method : Set.of("PUT", "PATCH", "DELETE")) {
            HttpResponse<String> response = sendRaw(
                    method, "/api/v1/evidence/import", null,
                    MediaType.APPLICATION_JSON_VALUE, ADMIN
            );
            assertEquals(405, response.statusCode(), method);
            assertEquals("METHOD_NOT_ALLOWED", JSON.readTree(response.body()).get("code").asString());
        }
    }

    @Test
    void registersExactlyTheFifteenFrozenApiMappings() {
        RequestMappingHandlerMapping handlerMapping = context.getBean(
                "requestMappingHandlerMapping", RequestMappingHandlerMapping.class
        );
        Set<String> actual = new HashSet<>();
        handlerMapping.getHandlerMethods().keySet().forEach(mapping ->
                mapping.getPatternValues().stream()
                        .filter(pattern -> pattern.startsWith("/api/v1/"))
                        .forEach(pattern -> mapping.getMethodsCondition().getMethods()
                                .forEach(method -> actual.add(method.name() + " " + pattern)))
        );

        assertEquals(FROZEN_MAPPINGS, actual);
    }

    @Test
    void returnsTheSameJsonDenialForEveryRepresentativeAcceptValue()
            throws IOException, InterruptedException {
        int callsBefore = IMPORT_CALLS.get();
        for (String accept : Set.of("text/plain", "application/xml", "*/*")) {
            HttpResponse<String> response = sendRaw(
                    "POST", "/api/v1/evidence/import", evidenceBody(),
                    "application/x-ndjson", PLATFORM_ENGINEER, accept
            );
            assertAccessDenied(response);
        }
        assertEquals(callsBefore, IMPORT_CALLS.get());
    }

    private static HttpResponse<String> send(RoutePolicy route, String role)
            throws IOException, InterruptedException {
        return sendRaw(route.method(), route.path(), route.body(), route.contentType(), role);
    }

    private static HttpResponse<String> sendRaw(
            String method,
            String path,
            String body,
            String contentType,
            String role
    ) throws IOException, InterruptedException {
        return sendRaw(method, path, body, contentType, role, MediaType.APPLICATION_JSON_VALUE);
    }

    private static HttpResponse<String> sendRaw(
            String method,
            String path,
            String body,
            String contentType,
            String role,
            String accept
    ) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", accept)
                .header("Authorization", JWT.authorizationHeader(
                        JwtTestFixture.DEFAULT_ACTOR_ID, role
                ));
        if (contentType != null) {
            request.header("Content-Type", contentType);
        }
        if (path.contains("/ledger/") && "POST".equals(method)) {
            request.header("Idempotency-Key", UUID.randomUUID().toString());
        }
        request.method(
                method,
                body == null
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body)
        );
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static void assertAccessDenied(HttpResponse<String> response) throws IOException {
        assertEquals(403, response.statusCode(), response.body());
        assertFalse(response.headers().firstValue("WWW-Authenticate").isPresent());
        assertTrue(response.headers().firstValue("Content-Type").orElse("")
                .startsWith(MediaType.APPLICATION_JSON_VALUE));
        JsonNode body = JSON.readTree(response.body());
        assertEquals(
                Set.of("code", "message", "correlationId", "details"),
                body.properties().stream().map(entry -> entry.getKey())
                        .collect(Collectors.toUnmodifiableSet())
        );
        assertEquals("ACCESS_DENIED", body.get("code").asString());
        assertEquals("Access denied", body.get("message").asString());
        assertTrue(body.get("details").isObject());
        assertEquals(0, body.get("details").size());
        assertEquals(
                response.headers().firstValue(CorrelationIdFilter.HEADER_NAME).orElseThrow(),
                body.get("correlationId").asString()
        );
    }

    private static RoutePolicy read(String path) {
        return new RoutePolicy("GET", path, ALL_ROLES, null, null);
    }

    private static RoutePolicy command(String action, Set<String> roles, String body) {
        return new RoutePolicy(
                "POST", "/api/v1/decisions/" + DECISION_ID + "/ledger/" + action,
                roles, body, MediaType.APPLICATION_JSON_VALUE
        );
    }

    private static String evidenceBody() {
        return """
                {"id":"30000000-0000-4000-8000-000000000003","schema_version":"1","timestamp":"2026-06-30T23:59:59Z","source":"AWS","source_type":"cloud_cost","source_object_ref":"cost-export/2026-06","entity":"onboarding-assistant","event_type":"cloud_cost_observed","severity":"info","actor":"aws-cost-export","evidence_type":"cloud_cost","case_hint":"DRC-AOA-001","observed_fact":"AWS monthly cost is EUR410","business_meaning":"Provides infrastructure cost input","correlation_key":"DRC-AOA-001","sensitivity":"INTERNAL","confidence":"high","freshness":"fresh","review_status":"accepted","metadata":{"evidence_ref":"E-AWS-001","currency":"EUR","monthly_cost":"410.00"},"raw_payload":{"mode":"not_stored"}}
                """.trim();
    }

    private static String reviewBody() {
        return """
                {"reviewedAt":"2026-07-01T12:00:00Z","reason":"Governance outcome"}
                """.trim();
    }

    private static String deferBody() {
        return """
                {"reviewedAt":"2026-07-01T12:00:00Z","reason":"Need evidence","requiredEvidence":"quality report"}
                """.trim();
    }

    private static String implementationBody() {
        return """
                {"occurredAt":"2026-07-01T12:00:00Z","reason":"Deployed","evidenceIds":["30000000-0000-4000-8000-000000000003"],"expectedPreviousEntryId":"40000000-0000-4000-8000-000000000004","period":"2026-07"}
                """.trim();
    }

    private static String validationBody() {
        return """
                {"occurredAt":"2026-07-01T12:00:00Z","reason":"Validated","evidenceIds":["30000000-0000-4000-8000-000000000003"],"expectedPreviousEntryId":"40000000-0000-4000-8000-000000000004","period":"2026-07","annualizedBaselineCost":{"amount":"28080.00","currency":"EUR"},"annualizedPostActionCost":{"amount":"8640.00","currency":"EUR"},"actualTransitionCost":{"amount":"0.00","currency":"EUR"}}
                """.trim();
    }

    private record RoutePolicy(
            String method,
            String path,
            Set<String> allowedRoles,
            String body,
            String contentType
    ) {
        private RoutePolicy {
            allowedRoles = Set.copyOf(allowedRoles);
        }
    }
}
