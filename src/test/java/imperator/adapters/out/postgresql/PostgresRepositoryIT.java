package imperator.adapters.out.postgresql;

import imperator.application.appendledgerentry.AppendLedgerEntryCommand;
import imperator.application.appendledgerentry.AppendLedgerEntryResult;
import imperator.application.appendledgerentry.AppendLedgerEntryUseCase;
import imperator.application.createdecision.CreateDecisionCommand;
import imperator.application.createdecision.CreateDecisionUseCase;
import imperator.application.generaterecommendation.GenerateRecommendationCommand;
import imperator.application.generaterecommendation.GenerateRecommendationUseCase;
import imperator.application.importevidence.ImportEvidenceCommand;
import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.application.reviewdecision.ReviewDecisionAction;
import imperator.application.reviewdecision.ReviewDecisionCommand;
import imperator.application.reviewdecision.ReviewDecisionUseCase;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class PostgresRepositoryIT {
    private static final Instant BASE_TIME = Instant.parse("2026-07-29T10:00:00Z");
    private static final ROIAmount SAVING = new ROIAmount(Money.eur(new BigDecimal("125.50")));
    private static final ROIConfidence CONFIDENCE = new ROIConfidence(85);

    private String databaseName;
    private String appRole;
    private PostgresDataSource adminDataSource;
    private PostgresDataSource appDataSource;
    private EvidenceRepository evidenceRepository;
    private DecisionRepository decisionRepository;
    private RecommendationRepository recommendationRepository;
    private LedgerRepository ledgerRepository;
    private TransactionRunner transactionRunner;

    @BeforeAll
    void prepareDatabasePrincipalsAndAdapters() throws SQLException {
        databaseName = requiredProperty("imperator.it.database");
        appRole = requiredProperty("imperator.it.appUser");
        requireSafeTestIdentifier(databaseName, "imperator_it_");
        requireSafeTestIdentifier(appRole, "imperator_it_");

        String jdbcUrl = requiredProperty("imperator.it.dbUrl");
        adminDataSource = new PostgresDataSource(
                jdbcUrl,
                requiredProperty("imperator.it.adminUser"),
                requiredProperty("imperator.it.adminPassword")
        );
        verifyTargetDatabase();
        provisionApplicationRole(requiredProperty("imperator.it.appPassword"));

        appDataSource = new PostgresDataSource(
                jdbcUrl,
                appRole,
                requiredProperty("imperator.it.appPassword")
        );
        PostgresConnectionProvider connectionProvider = new PostgresConnectionProvider(appDataSource);
        evidenceRepository = new PostgresEvidenceRepository(connectionProvider);
        decisionRepository = new PostgresDecisionRepository(connectionProvider);
        recommendationRepository = new PostgresRecommendationRepository(connectionProvider);
        ledgerRepository = new PostgresLedgerRepository(connectionProvider);
        transactionRunner = new PostgresTransactionRunner(connectionProvider);
    }

    @BeforeEach
    void cleanApplicationTables() throws SQLException {
        executeAdmin("""
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

    @AfterAll
    void removeApplicationTestRole() throws SQLException {
        if (adminDataSource == null || appRole == null) {
            return;
        }
        executeAdmin("DROP OWNED BY " + quoteIdentifier(appRole));
        executeAdmin("DROP ROLE IF EXISTS " + quoteIdentifier(appRole));
    }

    @Test
    void connectsToPostgres18AndFindsSuccessfulFlywayV1() throws SQLException {
        try (Connection connection = appDataSource.getConnection()) {
            assertAll(
                    () -> assertEquals("PostgreSQL", connection.getMetaData().getDatabaseProductName()),
                    () -> assertTrue(
                            connection.getMetaData().getDatabaseProductVersion().startsWith("18.2"),
                            "PostgreSQL 18.2 is required by this sprint gate"
                    ),
                    () -> assertEquals(18, connection.getMetaData().getDatabaseMajorVersion())
            );
        }

        try (
                Connection connection = adminDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("""
                        SELECT COUNT(*)
                        FROM flyway_schema_history
                        WHERE version = '1'
                          AND success
                        """);
                ResultSet resultSet = statement.executeQuery()
        ) {
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
        }
    }

    @Test
    void allRepositoryMethodsPersistAndRehydrateDeterministically() {
        Evidence evidence = evidence("case-repository-round-trip");

        assertFalse(evidenceRepository.existsById(evidence.id()));
        evidenceRepository.save(evidence);
        assertTrue(evidenceRepository.existsById(evidence.id()));
        assertEvidenceRoundTrip(evidence, evidenceRepository.findById(evidence.id()).orElseThrow());

        Decision decision = decision(evidence);
        assertFalse(decisionRepository.existsById(decision.id()));
        decisionRepository.save(decision);
        assertTrue(decisionRepository.existsById(decision.id()));
        assertDecisionRoundTrip(decision, decisionRepository.findById(decision.id()).orElseThrow());

        Recommendation recommendation = recommendation(decision, evidence.id());
        decision.attachRecommendation(recommendation.id(), timestamp(2));
        transactionRunner.execute(() -> {
            recommendationRepository.save(recommendation);
            decisionRepository.save(decision);
            return null;
        });

        assertTrue(recommendationRepository.existsById(recommendation.id()));
        assertRecommendationRoundTrip(
                recommendation,
                recommendationRepository.findById(recommendation.id()).orElseThrow()
        );
        assertDecisionRoundTrip(decision, decisionRepository.findById(decision.id()).orElseThrow());

        decision.markUnderReview(timestamp(3));
        decision.approve(userId(), timestamp(4), "Approved after deterministic repository verification");
        decisionRepository.save(decision);

        LedgerEntry firstEntry = ledgerEntry(
                decision,
                recommendation,
                evidence.id(),
                timestamp(5),
                Optional.empty()
        );
        LedgerEntry secondEntry = ledgerEntry(
                decision,
                recommendation,
                evidence.id(),
                timestamp(6),
                Optional.of(firstEntry.id())
        );

        assertTrue(ledgerRepository.findByDecisionId(decision.id()).isEmpty());
        ledgerRepository.append(firstEntry);
        ledgerRepository.append(secondEntry);

        assertLedgerRoundTrip(firstEntry, ledgerRepository.findById(firstEntry.id()).orElseThrow());
        List<LedgerEntry> history = ledgerRepository.findByDecisionId(decision.id());
        assertAll(
                () -> assertEquals(2, history.size()),
                () -> assertEquals(firstEntry.id(), history.get(0).id()),
                () -> assertEquals(secondEntry.id(), history.get(1).id()),
                () -> assertLedgerRoundTrip(secondEntry, history.get(1))
        );
    }

    @Test
    void aggregateChildFailuresRollbackTheirParentRows() {
        Evidence evidence = evidence("case-local-rollback");
        evidenceRepository.save(evidence);

        Decision invalidDecision = decision(evidence);
        invalidDecision.addEvidence(evidenceId(), timestamp(1));
        assertThrows(IllegalStateException.class, () -> decisionRepository.save(invalidDecision));
        assertFalse(decisionRepository.existsById(invalidDecision.id()));

        Decision validDecision = decision(evidence);
        decisionRepository.save(validDecision);

        Recommendation invalidRecommendation = recommendation(validDecision, evidenceId());
        assertThrows(
                IllegalStateException.class,
                () -> recommendationRepository.save(invalidRecommendation)
        );
        assertFalse(recommendationRepository.existsById(invalidRecommendation.id()));

        Recommendation validRecommendation = recommendation(validDecision, evidence.id());
        validDecision.attachRecommendation(validRecommendation.id(), timestamp(2));
        transactionRunner.execute(() -> {
            recommendationRepository.save(validRecommendation);
            decisionRepository.save(validDecision);
            return null;
        });
        validDecision.markUnderReview(timestamp(3));
        validDecision.approve(userId(), timestamp(4), "Approved for rollback verification");
        decisionRepository.save(validDecision);

        LedgerEntry invalidLedgerEntry = ledgerEntry(
                validDecision,
                validRecommendation,
                evidenceId(),
                timestamp(5),
                Optional.empty()
        );
        assertThrows(IllegalStateException.class, () -> ledgerRepository.append(invalidLedgerEntry));
        assertTrue(ledgerRepository.findById(invalidLedgerEntry.id()).isEmpty());
    }

    @Test
    void applicationFlowPersistsEvidenceDecisionRecommendationReviewAndLedger() {
        EvidenceId evidenceId = evidenceId();
        DecisionId decisionId = decisionId();
        RecommendationId recommendationId = recommendationId();
        UserId ownerId = userId();
        UserId approverId = userId();

        ImportEvidenceUseCase importEvidence = new ImportEvidenceUseCase(evidenceRepository, transactionRunner);
        CreateDecisionUseCase createDecision = new CreateDecisionUseCase(
                evidenceRepository,
                decisionRepository,
                transactionRunner
        );
        GenerateRecommendationUseCase generateRecommendation = new GenerateRecommendationUseCase(
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                request -> Optional.empty(),
                transactionRunner
        );
        ReviewDecisionUseCase reviewDecision = new ReviewDecisionUseCase(
                decisionRepository,
                recommendationRepository,
                transactionRunner
        );
        AppendLedgerEntryUseCase appendLedgerEntry = new AppendLedgerEntryUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );

        importEvidence.importEvidence(new ImportEvidenceCommand(
                evidenceId,
                timestamp(0),
                "billing-export",
                "POSTGRESQL_IT",
                "invoice-277",
                "cloud-account",
                "cost_anomaly",
                Severity.HIGH,
                "integration-suite",
                "COST",
                "A workload is oversized",
                "The workload can reduce operating cost",
                "case-vertical-flow",
                "INTERNAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.of("environment", "integration")
        ));

        createDecision.createDecision(new CreateDecisionCommand(
                decisionId,
                evidenceId,
                "Right-size workload",
                "Reduce recurring infrastructure cost",
                ownerId,
                approverId,
                timestamp(1)
        ));

        generateRecommendation.generateRecommendation(new GenerateRecommendationCommand(
                recommendationId,
                decisionId,
                RecommendationType.RIGHTSIZE_INSTANCE,
                "Reduce the instance allocation",
                "Canonical evidence shows sustained over-provisioning",
                Set.of(evidenceId),
                SAVING,
                CONFIDENCE,
                Severity.MEDIUM,
                timestamp(2)
        ));

        reviewDecision.reviewDecision(new ReviewDecisionCommand(
                decisionId,
                recommendationId,
                ReviewDecisionAction.START_REVIEW,
                approverId,
                timestamp(3),
                "Review started"
        ));
        reviewDecision.reviewDecision(new ReviewDecisionCommand(
                decisionId,
                recommendationId,
                ReviewDecisionAction.APPROVE,
                approverId,
                timestamp(4),
                "Savings and risk are acceptable"
        ));

        LedgerEntryId ledgerEntryId = ledgerEntryId();
        AppendLedgerEntryResult ledgerResult = appendLedgerEntry.appendLedgerEntry(new AppendLedgerEntryCommand(
                ledgerEntryId,
                decisionId,
                Optional.of(recommendationId),
                approverId,
                "FINOPS_APPROVER",
                timestamp(5),
                LedgerEntryType.APPROVED,
                "Recommendation approved",
                "Savings and risk are acceptable",
                Set.of(evidenceId),
                Optional.of(SAVING),
                Optional.empty(),
                Optional.of(CONFIDENCE),
                Optional.of(Severity.MEDIUM),
                Optional.empty(),
                Map.of("source", "postgresql-it")
        ));

        Decision storedDecision = decisionRepository.findById(decisionId).orElseThrow();
        assertAll(
                () -> assertEquals(DecisionStatus.APPROVED, storedDecision.status()),
                () -> assertEquals(Optional.of(recommendationId), storedDecision.recommendationId()),
                () -> assertTrue(evidenceRepository.existsById(evidenceId)),
                () -> assertTrue(recommendationRepository.existsById(recommendationId)),
                () -> assertEquals(ledgerEntryId, ledgerResult.ledgerEntryId()),
                () -> assertEquals(1, ledgerRepository.findByDecisionId(decisionId).size())
        );
    }

    @Test
    void recommendationAndDecisionRollbackTogetherOnSecondRepositoryFailure() {
        Evidence evidence = evidence("case-cross-repository-rollback");
        evidenceRepository.save(evidence);
        Decision decision = decision(evidence);
        decisionRepository.save(decision);

        RecommendationId recommendationId = recommendationId();
        DecisionRepository failAfterSave = new FailAfterSaveDecisionRepository(decisionRepository);
        GenerateRecommendationUseCase useCase = new GenerateRecommendationUseCase(
                failAfterSave,
                evidenceRepository,
                recommendationRepository,
                request -> Optional.empty(),
                transactionRunner
        );

        GenerateRecommendationCommand command = new GenerateRecommendationCommand(
                recommendationId,
                decision.id(),
                RecommendationType.RIGHTSIZE_INSTANCE,
                "Reduce allocation",
                "Evidence supports the change",
                Set.of(evidence.id()),
                SAVING,
                CONFIDENCE,
                Severity.MEDIUM,
                timestamp(2)
        );

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> useCase.generateRecommendation(command)
        );
        assertEquals("Injected failure after DecisionRepository.save", failure.getMessage());
        assertAll(
                () -> assertFalse(recommendationRepository.existsById(recommendationId)),
                () -> assertTrue(
                        decisionRepository.findById(decision.id()).orElseThrow().recommendationId().isEmpty()
                )
        );
    }

    @Test
    void applicationRoleCannotUpdateOrDeleteLedgerHistory() throws SQLException {
        Evidence evidence = evidence("case-ledger-permissions");
        evidenceRepository.save(evidence);
        Decision decision = decision(evidence);
        decisionRepository.save(decision);
        Recommendation recommendation = recommendation(decision, evidence.id());
        decision.attachRecommendation(recommendation.id(), timestamp(2));
        transactionRunner.execute(() -> {
            recommendationRepository.save(recommendation);
            decisionRepository.save(decision);
            return null;
        });
        decision.markUnderReview(timestamp(3));
        decision.approve(userId(), timestamp(4), "Approved for permission verification");
        decisionRepository.save(decision);

        LedgerEntry entry = ledgerEntry(
                decision,
                recommendation,
                evidence.id(),
                timestamp(5),
                Optional.empty()
        );
        ledgerRepository.append(entry);

        assertPermissionDenied(
                "UPDATE ledger_entries SET reason = reason WHERE id = ?",
                entry.id().value()
        );
        assertPermissionDenied(
                "DELETE FROM ledger_entries WHERE id = ?",
                entry.id().value()
        );
        assertPermissionDenied(
                "UPDATE ledger_evidence_snapshots SET evidence_id = evidence_id WHERE ledger_entry_id = ?",
                entry.id().value()
        );
        assertPermissionDenied(
                "DELETE FROM ledger_evidence_snapshots WHERE ledger_entry_id = ?",
                entry.id().value()
        );

        assertLedgerRoundTrip(entry, ledgerRepository.findById(entry.id()).orElseThrow());
    }

    private void verifyTargetDatabase() throws SQLException {
        try (
                Connection connection = adminDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT current_database()");
                ResultSet resultSet = statement.executeQuery()
        ) {
            if (!resultSet.next() || !databaseName.equals(resultSet.getString(1))) {
                throw new IllegalStateException("Integration database identity does not match the protected test target");
            }
        }
    }

    private void provisionApplicationRole(String appPassword) throws SQLException {
        boolean roleExists;
        try (
                Connection connection = adminDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = ?)"
                )
        ) {
            statement.setString(1, appRole);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                roleExists = resultSet.getBoolean(1);
            }
        }

        if (!roleExists) {
            executeAdmin(
                    "CREATE ROLE " + quoteIdentifier(appRole)
                            + " LOGIN PASSWORD " + quoteLiteral(appPassword)
            );
        } else {
            executeAdmin(
                    "ALTER ROLE " + quoteIdentifier(appRole)
                            + " LOGIN PASSWORD " + quoteLiteral(appPassword)
            );
        }

        executeAdmin("REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM " + quoteIdentifier(appRole));
        executeAdmin("GRANT CONNECT ON DATABASE " + quoteIdentifier(databaseName) + " TO " + quoteIdentifier(appRole));
        executeAdmin("GRANT USAGE ON SCHEMA public TO " + quoteIdentifier(appRole));
        executeAdmin("GRANT SELECT, INSERT ON TABLE evidence TO " + quoteIdentifier(appRole));
        executeAdmin("GRANT SELECT, INSERT, UPDATE ON TABLE decisions TO " + quoteIdentifier(appRole));
        executeAdmin(
                "GRANT SELECT, INSERT, DELETE ON TABLE decision_evidence TO " + quoteIdentifier(appRole)
        );
        executeAdmin("GRANT SELECT, INSERT ON TABLE recommendations TO " + quoteIdentifier(appRole));
        executeAdmin(
                "GRANT SELECT, INSERT ON TABLE recommendation_evidence TO " + quoteIdentifier(appRole)
        );
        executeAdmin("""
                GRANT SELECT, INSERT
                ON TABLE ledger_entries, ledger_evidence_snapshots
                TO %s
                """.formatted(quoteIdentifier(appRole)));
    }

    private void executeAdmin(String sql) throws SQLException {
        try (
                Connection connection = adminDataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute(sql);
        }
    }

    private void assertPermissionDenied(String sql, UUID id) {
        SQLException exception = assertThrows(SQLException.class, () -> {
            try (
                    Connection connection = appDataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                statement.setObject(1, id);
                statement.executeUpdate();
            }
        });
        assertEquals("42501", exception.getSQLState());
    }

    private Evidence evidence(String correlationKey) {
        return new Evidence(
                evidenceId(),
                timestamp(0),
                "billing-export",
                "POSTGRESQL_IT",
                "source-" + UUID.randomUUID(),
                "cloud-account",
                "cost_anomaly",
                Severity.HIGH,
                "integration-suite",
                "COST",
                "A workload is oversized",
                "The workload can reduce operating cost",
                correlationKey,
                "INTERNAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.of("environment", "integration", "sprint", "2.7.7")
        );
    }

    private Decision decision(Evidence evidence) {
        return Decision.create(
                decisionId(),
                evidence.correlationKey(),
                "Right-size workload",
                "Reduce recurring infrastructure cost",
                evidence.id(),
                userId(),
                userId(),
                timestamp(1)
        );
    }

    private Recommendation recommendation(Decision decision, EvidenceId evidenceId) {
        return new Recommendation(
                recommendationId(),
                decision.id(),
                RecommendationType.RIGHTSIZE_INSTANCE,
                "Reduce the instance allocation",
                "Canonical evidence shows sustained over-provisioning",
                Set.of(evidenceId),
                SAVING,
                CONFIDENCE,
                Severity.MEDIUM,
                decision.ownerId(),
                decision.requiredApproverId(),
                timestamp(2)
        );
    }

    private LedgerEntry ledgerEntry(
            Decision decision,
            Recommendation recommendation,
            EvidenceId evidenceId,
            Timestamp occurredAt,
            Optional<LedgerEntryId> previousEntryId
    ) {
        return new LedgerEntry(
                ledgerEntryId(),
                decision.id(),
                Optional.of(recommendation.id()),
                userId(),
                "FINOPS_APPROVER",
                occurredAt,
                LedgerEntryType.APPROVED,
                "Recommendation approved",
                "Savings and risk are acceptable",
                Set.of(evidenceId),
                Optional.of(SAVING),
                Optional.empty(),
                Optional.of(CONFIDENCE),
                Optional.of(Severity.MEDIUM),
                previousEntryId,
                Map.of("source", "postgresql-it")
        );
    }

    private void assertEvidenceRoundTrip(Evidence expected, Evidence actual) {
        assertAll(
                () -> assertEquals(expected.id(), actual.id()),
                () -> assertEquals(expected.timestamp(), actual.timestamp()),
                () -> assertEquals(expected.source(), actual.source()),
                () -> assertEquals(expected.sourceType(), actual.sourceType()),
                () -> assertEquals(expected.sourceObjectRef(), actual.sourceObjectRef()),
                () -> assertEquals(expected.entity(), actual.entity()),
                () -> assertEquals(expected.eventType(), actual.eventType()),
                () -> assertEquals(expected.severity(), actual.severity()),
                () -> assertEquals(expected.actor(), actual.actor()),
                () -> assertEquals(expected.evidenceType(), actual.evidenceType()),
                () -> assertEquals(expected.observedFact(), actual.observedFact()),
                () -> assertEquals(expected.businessMeaning(), actual.businessMeaning()),
                () -> assertEquals(expected.correlationKey(), actual.correlationKey()),
                () -> assertEquals(expected.sensitivity(), actual.sensitivity()),
                () -> assertEquals(expected.confidence(), actual.confidence()),
                () -> assertEquals(expected.reviewStatus(), actual.reviewStatus()),
                () -> assertEquals(expected.rawPayloadMode(), actual.rawPayloadMode()),
                () -> assertEquals(expected.metadata(), actual.metadata())
        );
    }

    private void assertDecisionRoundTrip(Decision expected, Decision actual) {
        assertAll(
                () -> assertEquals(expected.id(), actual.id()),
                () -> assertEquals(expected.caseId(), actual.caseId()),
                () -> assertEquals(expected.title(), actual.title()),
                () -> assertEquals(expected.businessNeed(), actual.businessNeed()),
                () -> assertEquals(expected.originatingEvidenceId(), actual.originatingEvidenceId()),
                () -> assertEquals(expected.ownerId(), actual.ownerId()),
                () -> assertEquals(expected.requiredApproverId(), actual.requiredApproverId()),
                () -> assertEquals(expected.createdAt(), actual.createdAt()),
                () -> assertEquals(expected.status(), actual.status()),
                () -> assertEquals(expected.recommendationId(), actual.recommendationId()),
                () -> assertEquals(expected.reviewedBy(), actual.reviewedBy()),
                () -> assertEquals(expected.reviewedAt(), actual.reviewedAt()),
                () -> assertEquals(expected.reviewReason(), actual.reviewReason()),
                () -> assertEquals(expected.updatedAt(), actual.updatedAt()),
                () -> assertEquals(expected.evidenceIds(), actual.evidenceIds())
        );
    }

    private void assertRecommendationRoundTrip(Recommendation expected, Recommendation actual) {
        assertAll(
                () -> assertEquals(expected.id(), actual.id()),
                () -> assertEquals(expected.decisionId(), actual.decisionId()),
                () -> assertEquals(expected.type(), actual.type()),
                () -> assertEquals(expected.suggestedAction(), actual.suggestedAction()),
                () -> assertEquals(expected.reason(), actual.reason()),
                () -> assertEquals(expected.evidenceIds(), actual.evidenceIds()),
                () -> assertEquals(expected.estimatedSavings(), actual.estimatedSavings()),
                () -> assertEquals(expected.confidence(), actual.confidence()),
                () -> assertEquals(expected.risk(), actual.risk()),
                () -> assertEquals(expected.ownerId(), actual.ownerId()),
                () -> assertEquals(expected.requiredApproverId(), actual.requiredApproverId()),
                () -> assertEquals(expected.createdAt(), actual.createdAt())
        );
    }

    private void assertLedgerRoundTrip(LedgerEntry expected, LedgerEntry actual) {
        assertAll(
                () -> assertEquals(expected.id(), actual.id()),
                () -> assertEquals(expected.decisionId(), actual.decisionId()),
                () -> assertEquals(expected.recommendationId(), actual.recommendationId()),
                () -> assertEquals(expected.actorId(), actual.actorId()),
                () -> assertEquals(expected.actorRole(), actual.actorRole()),
                () -> assertEquals(expected.occurredAt(), actual.occurredAt()),
                () -> assertEquals(expected.entryType(), actual.entryType()),
                () -> assertEquals(expected.changeSummary(), actual.changeSummary()),
                () -> assertEquals(expected.reason(), actual.reason()),
                () -> assertEquals(expected.evidenceSnapshotIds(), actual.evidenceSnapshotIds()),
                () -> assertEquals(expected.estimatedSaving(), actual.estimatedSaving()),
                () -> assertEquals(expected.realizedSaving(), actual.realizedSaving()),
                () -> assertEquals(expected.confidenceSnapshot(), actual.confidenceSnapshot()),
                () -> assertEquals(expected.riskSnapshot(), actual.riskSnapshot()),
                () -> assertEquals(expected.previousEntryId(), actual.previousEntryId()),
                () -> assertEquals(expected.metadata(), actual.metadata())
        );
    }

    private Timestamp timestamp(long minuteOffset) {
        return new Timestamp(BASE_TIME.plusSeconds(minuteOffset * 60));
    }

    private EvidenceId evidenceId() {
        return new EvidenceId(UUID.randomUUID());
    }

    private DecisionId decisionId() {
        return new DecisionId(UUID.randomUUID());
    }

    private RecommendationId recommendationId() {
        return new RecommendationId(UUID.randomUUID());
    }

    private LedgerEntryId ledgerEntryId() {
        return new LedgerEntryId(UUID.randomUUID());
    }

    private UserId userId() {
        return new UserId(UUID.randomUUID());
    }

    private String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required integration property: " + name);
        }
        return value.trim();
    }

    private void requireSafeTestIdentifier(String value, String prefix) {
        if (!value.startsWith(prefix) || !value.matches("[a-z][a-z0-9_]{2,62}")) {
            throw new IllegalStateException("Unsafe integration-test identifier: " + value);
        }
    }

    private String quoteIdentifier(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String quoteLiteral(String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    private static final class FailAfterSaveDecisionRepository implements DecisionRepository {
        private final DecisionRepository delegate;

        private FailAfterSaveDecisionRepository(DecisionRepository delegate) {
            this.delegate = Objects.requireNonNull(delegate, "Decision repository is required");
        }

        @Override
        public void save(Decision decision) {
            delegate.save(decision);
            throw new IllegalStateException("Injected failure after DecisionRepository.save");
        }

        @Override
        public Optional<Decision> findById(DecisionId id) {
            return delegate.findById(id);
        }

        @Override
        public boolean existsById(DecisionId id) {
            return delegate.existsById(id);
        }
    }
}
