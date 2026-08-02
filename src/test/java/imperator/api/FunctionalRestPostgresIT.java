package imperator.api;

import imperator.adapters.out.postgresql.PostgresDataSource;
import imperator.bootstrap.ImperatorApplication;
import imperator.domain.decision.Decision;
import imperator.domain.decision.DrcAoa001RecommendationPolicy;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.support.DrcAoa001EvidenceFixture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import testsupport.security.JwtTestFixture;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class FunctionalRestPostgresIT {
    private static final Instant CREATED_AT = Instant.parse("2026-07-30T10:00:00Z");
    private static final UserId APPROVER = userId(90);
    private static final DecisionId APPROVE_DECISION = decisionId(1);
    private static final DecisionId REJECT_DECISION = decisionId(2);
    private static final DecisionId DEFER_DECISION = decisionId(3);
    private static final RecommendationId APPROVE_RECOMMENDATION = recommendationId(1);

    private DataSource adminDataSource;
    private ConfigurableApplicationContext context;
    private HttpClient client;
    private int port;
    private EvidenceId traceEvidenceId;
    private final JwtTestFixture jwt = new JwtTestFixture();

    @BeforeAll
    void startCertifiedRuntimeAndSeedPolicyPack() throws SQLException {
        String url = requiredProperty("imperator.it.dbUrl");
        String adminUser = requiredProperty("imperator.it.adminUser");
        String adminPassword = requiredProperty("imperator.it.adminPassword");
        adminDataSource = new PostgresDataSource(url, adminUser, adminPassword);
        cleanTables();

        SpringApplication application = jwt.application();
        application.setDefaultProperties(Map.of(
                "imperator.postgresql.enabled", "true",
                "imperator.postgresql.url", url,
                "imperator.postgresql.username", adminUser,
                "imperator.postgresql.password", adminPassword
        ));
        context = application.run(jwt.arguments(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF"
        ));
        port = ((ServletWebServerApplicationContext) context).getWebServer().getPort();
        client = HttpClient.newHttpClient();
        seedDecision(APPROVE_DECISION, APPROVE_RECOMMENDATION);
        seedDecision(REJECT_DECISION, recommendationId(2));
        seedDecision(DEFER_DECISION, recommendationId(3));
    }

    @AfterAll
    void stopRuntime() throws SQLException {
        if (client != null) {
            client.close();
        }
        if (context != null) {
            context.close();
        }
        jwt.close();
        if (adminDataSource != null) {
            cleanTables();
        }
    }

    @Test
    void certifiesAllD086ReadAndGovernanceRoutesAgainstPostgres18()
            throws IOException, InterruptedException, SQLException {
        try (Connection connection = adminDataSource.getConnection()) {
            assertTrue(connection.getMetaData().getDatabaseProductVersion().startsWith("18.2"));
        }

        String approvalId = ledgerUuid(1).toString();
        assertCreated(postReview(APPROVE_DECISION, "approve", approvalId, "ADMIN", ""));
        String rejectionId = ledgerUuid(2).toString();
        assertCreated(postReview(REJECT_DECISION, "reject", rejectionId, "ADMIN", ""));
        String deferralId = ledgerUuid(3).toString();
        assertCreated(postReview(
                DEFER_DECISION,
                "defer",
                deferralId,
                "ADMIN",
                "\"requiredEvidence\":\"quality report\""
        ));

        String implementationId = ledgerUuid(4).toString();
        assertCreated(post(
                path(APPROVE_DECISION, "mark-implemented"),
                implementationId,
                "PLATFORM_ENGINEER",
                """
                        {"occurredAt":"2026-07-30T10:03:00Z","reason":"Deployed","evidenceIds":["%s"],"expectedPreviousEntryId":"%s","period":"2026-07"}
                        """.formatted(traceEvidenceId.value(), approvalId).trim()
        ));

        String validationId = ledgerUuid(5).toString();
        assertCreated(post(
                path(APPROVE_DECISION, "validate-result"),
                validationId,
                "FINANCE",
                """
                        {"occurredAt":"2026-07-30T10:04:00Z","reason":"Validated","evidenceIds":["%s"],"expectedPreviousEntryId":"%s","period":"2026-07","annualizedBaselineCost":{"amount":"28080.00","currency":"EUR"},"annualizedPostActionCost":{"amount":"8640.00","currency":"EUR"},"actualTransitionCost":{"amount":"0.00","currency":"EUR"}}
                        """.formatted(traceEvidenceId.value(), implementationId).trim()
        ));

        assertLedgerActor(approvalId, "ADMIN");
        assertLedgerActor(rejectionId, "ADMIN");
        assertLedgerActor(deferralId, "ADMIN");
        assertLedgerActor(implementationId, "PLATFORM_ENGINEER");
        assertLedgerActor(validationId, "FINANCE");

        for (String route : Set.of(
                "/api/v1/decisions",
                "/api/v1/decisions/" + APPROVE_DECISION.value(),
                "/api/v1/decisions/" + APPROVE_DECISION.value() + "/timeline",
                "/api/v1/decisions/" + APPROVE_DECISION.value() + "/evidence",
                "/api/v1/decisions/" + APPROVE_DECISION.value() + "/roi",
                "/api/v1/recommendations/" + APPROVE_RECOMMENDATION.value(),
                "/api/v1/decisions/" + APPROVE_DECISION.value() + "/ledger",
                "/api/v1/business-value?decisionId=" + APPROVE_DECISION.value(),
                "/api/v1/ledger"
        )) {
            HttpResponse<String> response = get(route);
            assertEquals(200, response.statusCode(), route + ": " + response.body());
            assertNotEquals(501, response.statusCode());
        }
    }

    private void seedDecision(DecisionId decisionId, RecommendationId recommendationId) {
        EvidenceRepository evidenceRepository = context.getBean(EvidenceRepository.class);
        DecisionRepository decisionRepository = context.getBean(DecisionRepository.class);
        RecommendationRepository recommendationRepository = context.getBean(RecommendationRepository.class);
        EvidenceId originId = evidenceId(1);
        Set<Evidence> evidence = DrcAoa001EvidenceFixture.completePack(
                originId, new Timestamp(CREATED_AT.minusSeconds(60)), true
        );
        evidence.forEach(item -> {
            if (!evidenceRepository.existsById(item.id())) {
                evidenceRepository.save(item);
            }
        });
        traceEvidenceId = DrcAoa001EvidenceFixture.origin(evidence).id();

        Decision decision = Decision.create(
                decisionId,
                DrcAoa001RecommendationPolicy.CASE_ID,
                "Reduce AI onboarding cost",
                "Recover avoidable AI spend",
                originId,
                userId(80),
                APPROVER,
                new Timestamp(CREATED_AT)
        );
        decisionRepository.save(decision);
        Recommendation recommendation = new DrcAoa001RecommendationPolicy().evaluate(
                recommendationId,
                decision,
                evidence,
                new Timestamp(CREATED_AT.plusSeconds(60))
        );
        recommendationRepository.save(recommendation);
        decision.attachRecommendation(recommendation.id(), recommendation.createdAt());
        decisionRepository.save(decision);
    }

    private HttpResponse<String> postReview(
            DecisionId decisionId,
            String action,
            String idempotencyKey,
            String role,
            String extraField
    ) throws IOException, InterruptedException {
        String suffix = extraField.isBlank() ? "" : "," + extraField;
        String body = "{\"reviewedAt\":\"2026-07-30T10:02:00Z\",\"reason\":\"Governance outcome\""
                + suffix + "}";
        return post(path(decisionId, action), idempotencyKey, role, body);
    }

    private HttpResponse<String> post(String path, String idempotencyKey, String role, String body)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Idempotency-Key", idempotencyKey)
                .header("Authorization", jwt.authorizationHeader(APPROVER.value(), role))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        return client.send(
                HttpRequest.newBuilder(uri(path))
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", jwt.authorizationHeader(APPROVER.value(), "ADMIN"))
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private void assertCreated(HttpResponse<String> response) {
        assertEquals(201, response.statusCode(), response.body());
    }

    private void assertLedgerActor(String entryId, String expectedRole) throws SQLException {
        try (
                Connection connection = adminDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT actor_id::text, actor_role FROM ledger_entries WHERE id = ?::uuid"
                )
        ) {
            statement.setString(1, entryId);
            try (ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                assertEquals(APPROVER.value().toString(), result.getString(1));
                assertEquals(expectedRole, result.getString(2));
                assertTrue(!result.next());
            }
        }
    }

    private String path(DecisionId decisionId, String action) {
        return "/api/v1/decisions/" + decisionId.value() + "/ledger/" + action;
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

    private void cleanTables() throws SQLException {
        try (Connection connection = adminDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("""
                    TRUNCATE TABLE
                        ledger_evidence_snapshots,
                        ledger_entries,
                        recommendation_evidence,
                        recommendations,
                        decision_evidence,
                        decisions,
                        evidence
                    """);
        }
    }

    private static String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required integration property is missing: " + name);
        }
        return value;
    }

    private static DecisionId decisionId(long value) {
        return new DecisionId(uuid(0x1000000000000000L, value));
    }

    private static RecommendationId recommendationId(long value) {
        return new RecommendationId(uuid(0x2000000000000000L, value));
    }

    private static EvidenceId evidenceId(long value) {
        return new EvidenceId(uuid(0x3000000000000000L, value));
    }

    private static UserId userId(long value) {
        return new UserId(uuid(0x4000000000000000L, value));
    }

    private static UUID ledgerUuid(long value) {
        return uuid(0x5000000000000000L, value);
    }

    private static UUID uuid(long most, long least) {
        return new UUID(most | 0x0000000000004000L, 0x8000000000000000L | least);
    }
}
