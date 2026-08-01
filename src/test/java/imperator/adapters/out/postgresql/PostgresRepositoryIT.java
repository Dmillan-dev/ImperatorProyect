package imperator.adapters.out.postgresql;

import imperator.application.appendledgerentry.AppendLedgerEntryCommand;
import imperator.application.appendledgerentry.AppendLedgerEntryResult;
import imperator.application.appendledgerentry.AppendLedgerEntryUseCase;
import imperator.application.createdecision.CreateDecisionCommand;
import imperator.application.createdecision.CreateDecisionResult;
import imperator.application.createdecision.CreateDecisionUseCase;
import imperator.application.exceptions.DecisionCreationConflictException;
import imperator.application.generaterecommendation.GenerateRecommendationCommand;
import imperator.application.generaterecommendation.GenerateRecommendationUseCase;
import imperator.application.importevidence.ImportEvidenceCommand;
import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.application.reviewdecision.ReviewDecisionAction;
import imperator.application.reviewdecision.ReviewDecisionCommand;
import imperator.application.reviewdecision.ReviewDecisionUseCase;
import imperator.bootstrap.ImperatorApplication;
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
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.CreateDecisionInputPort;
import imperator.ports.in.GenerateRecommendationInputPort;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

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
    private PostgresConnectionProvider connectionProvider;
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
        connectionProvider = new PostgresConnectionProvider(appDataSource);
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
    void springRuntimeCompositionUsesCertifiedApplicationRole() throws SQLException {
        SpringApplication application = new SpringApplication(ImperatorApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setLogStartupInfo(false);
        application.setDefaultProperties(Map.of(
                "imperator.postgresql.enabled", "true",
                "imperator.postgresql.url", requiredProperty("imperator.it.dbUrl"),
                "imperator.postgresql.username", appRole,
                "imperator.postgresql.password", requiredProperty("imperator.it.appPassword")
        ));

        try (ConfigurableApplicationContext context = application.run(
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF"
        )) {
            assertAll(
                    () -> assertTrue(context.containsBean("postgresDataSource")),
                    () -> assertTrue(context.containsBean("postgresConnectionProvider")),
                    () -> assertTrue(context.getBean(ImportEvidenceInputPort.class)
                            instanceof ImportEvidenceUseCase),
                    () -> assertTrue(context.getBean(CreateDecisionInputPort.class)
                            instanceof CreateDecisionUseCase),
                    () -> assertTrue(context.getBean(GenerateRecommendationInputPort.class)
                            instanceof GenerateRecommendationUseCase),
                    () -> assertTrue(context.getBean(ReviewDecisionInputPort.class)
                            instanceof ReviewDecisionUseCase),
                    () -> assertTrue(context.getBean(AppendLedgerEntryInputPort.class)
                            instanceof AppendLedgerEntryUseCase)
            );

            DataSource runtimeDataSource = context.getBean(DataSource.class);
            try (Connection connection = runtimeDataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT current_user")) {
                assertTrue(resultSet.next());
                assertEquals(appRole, resultSet.getString(1));
            }
        }
    }

    @Test
    void missingRepositoryIdentifiersReturnTheFrozenEmptyBehaviour() {
        EvidenceId missingEvidenceId = evidenceId();
        DecisionId missingDecisionId = decisionId();
        RecommendationId missingRecommendationId = recommendationId();
        LedgerEntryId missingLedgerEntryId = ledgerEntryId();

        assertAll(
                () -> assertFalse(evidenceRepository.existsById(missingEvidenceId)),
                () -> assertTrue(evidenceRepository.findById(missingEvidenceId).isEmpty()),
                () -> assertFalse(decisionRepository.existsById(missingDecisionId)),
                () -> assertTrue(decisionRepository.findById(missingDecisionId).isEmpty()),
                () -> assertFalse(recommendationRepository.existsById(missingRecommendationId)),
                () -> assertTrue(recommendationRepository.findById(missingRecommendationId).isEmpty()),
                () -> assertTrue(ledgerRepository.findById(missingLedgerEntryId).isEmpty()),
                () -> assertTrue(ledgerRepository.findByDecisionId(missingDecisionId).isEmpty())
        );
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
        ROIAmount realizedSaving = new ROIAmount(Money.eur(new BigDecimal("118.25")));
        LedgerEntry resultValidatedEntry = new LedgerEntry(
                ledgerEntryId(),
                decision.id(),
                Optional.of(recommendation.id()),
                userId(),
                "FINOPS_APPROVER",
                timestamp(7),
                LedgerEntryType.RESULT_VALIDATED,
                "Realized saving validated",
                "Post-change evidence confirms the realized saving",
                Set.of(),
                Optional.empty(),
                Optional.of(realizedSaving),
                Optional.empty(),
                Optional.empty(),
                Optional.of(secondEntry.id()),
                Map.of("source", "postgresql-it")
        );
        ledgerRepository.append(resultValidatedEntry);

        assertLedgerRoundTrip(
                resultValidatedEntry,
                ledgerRepository.findById(resultValidatedEntry.id()).orElseThrow()
        );
        List<LedgerEntry> history = ledgerRepository.findByDecisionId(decision.id());
        assertAll(
                () -> assertEquals(3, history.size()),
                () -> assertEquals(firstEntry.id(), history.get(0).id()),
                () -> assertEquals(secondEntry.id(), history.get(1).id()),
                () -> assertEquals(resultValidatedEntry.id(), history.get(2).id()),
                () -> assertLedgerRoundTrip(secondEntry, history.get(1)),
                () -> assertLedgerRoundTrip(resultValidatedEntry, history.get(2))
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
    void duplicateRepositoryWritesFailWithoutCorruptingExistingAggregates() {
        PersistedDecisionGraph graph = persistApprovedDecisionGraph("case-repository-duplicates");

        assertRepositoryConstraint(
                "pk_evidence",
                () -> evidenceRepository.save(graph.evidence())
        );
        assertRepositoryConstraint(
                "pk_recommendations",
                () -> recommendationRepository.save(graph.recommendation())
        );

        Recommendation competingRecommendation = recommendation(graph.decision(), graph.evidence().id());
        assertRepositoryConstraint(
                "uq_recommendations_decision_id",
                () -> recommendationRepository.save(competingRecommendation)
        );

        LedgerEntry entry = ledgerEntry(
                graph.decision(),
                graph.recommendation(),
                graph.evidence().id(),
                timestamp(5),
                Optional.empty()
        );
        ledgerRepository.append(entry);
        assertRepositoryConstraint("pk_ledger_entries", () -> ledgerRepository.append(entry));

        assertAll(
                () -> assertEvidenceRoundTrip(
                        graph.evidence(),
                        evidenceRepository.findById(graph.evidence().id()).orElseThrow()
                ),
                () -> assertRecommendationRoundTrip(
                        graph.recommendation(),
                        recommendationRepository.findById(graph.recommendation().id()).orElseThrow()
                ),
                () -> assertEquals(1, ledgerRepository.findByDecisionId(graph.decision().id()).size())
        );
    }

    @Test
    void representativeSchemaConstraintsRejectInvalidWrites() {
        PersistedDecisionGraph graph = persistApprovedDecisionGraph("case-schema-constraints");

        assertDatabaseViolation(
                "23505",
                "pk_evidence",
                () -> executeAdminPrepared(
                        "INSERT INTO evidence SELECT * FROM evidence WHERE id = ?",
                        graph.evidence().id().value()
                )
        );
        assertDatabaseViolation(
                "23502",
                "timestamp",
                () -> executeAdminPrepared(
                        "INSERT INTO evidence (id) VALUES (?)",
                        UUID.randomUUID()
                )
        );
        assertDatabaseViolation(
                "23503",
                "fk_decision_evidence_evidence",
                () -> executeAdminPrepared(
                        "INSERT INTO decision_evidence (decision_id, evidence_id) VALUES (?, ?)",
                        graph.decision().id().value(),
                        UUID.randomUUID()
                )
        );
        assertDatabaseViolation(
                "23505",
                "pk_decision_evidence",
                () -> executeAdminPrepared(
                        "INSERT INTO decision_evidence (decision_id, evidence_id) VALUES (?, ?)",
                        graph.decision().id().value(),
                        graph.evidence().id().value()
                )
        );
        assertDatabaseViolation(
                "23505",
                "uq_recommendations_decision_id",
                () -> executeAdminPrepared(
                        """
                        INSERT INTO recommendations (
                            id,
                            decision_id,
                            type,
                            suggested_action,
                            reason,
                            estimated_saving_amount,
                            estimated_saving_currency,
                            confidence_percentage,
                            risk,
                            owner_id,
                            required_approver_id,
                            created_at
                        )
                        SELECT
                            ?,
                            decision_id,
                            type,
                            suggested_action,
                            reason,
                            estimated_saving_amount,
                            estimated_saving_currency,
                            confidence_percentage,
                            risk,
                            owner_id,
                            required_approver_id,
                            created_at
                        FROM recommendations
                        WHERE id = ?
                        """,
                        UUID.randomUUID(),
                        graph.recommendation().id().value()
                )
        );
        assertDatabaseViolation(
                "23514",
                "ck_recommendations_confidence_percentage",
                () -> executeAdminPrepared(
                        "UPDATE recommendations SET confidence_percentage = 101 WHERE id = ?",
                        graph.recommendation().id().value()
                )
        );
        assertDatabaseViolation(
                "23514",
                "ck_evidence_severity",
                () -> executeAdminPrepared(
                        "UPDATE evidence SET severity = 'INVALID' WHERE id = ?",
                        graph.evidence().id().value()
                )
        );

        LedgerEntry pairProbe = new LedgerEntry(
                ledgerEntryId(),
                graph.decision().id(),
                Optional.empty(),
                userId(),
                "FINOPS_APPROVER",
                timestamp(5),
                LedgerEntryType.CASE_CLOSED,
                "Case closed",
                "Constraint verification probe",
                Set.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of("source", "postgresql-it")
        );
        ledgerRepository.append(pairProbe);
        assertDatabaseViolation(
                "23514",
                "ck_ledger_entries_estimated_saving_pair",
                () -> executeAdminPrepared(
                        "UPDATE ledger_entries SET estimated_saving_amount = 1.00 WHERE id = ?",
                        pairProbe.id().value()
                )
        );
        assertDatabaseViolation(
                "23514",
                "ck_ledger_entries_realized_saving_pair",
                () -> executeAdminPrepared(
                        "UPDATE ledger_entries SET realized_saving_amount = 1.00 WHERE id = ?",
                        pairProbe.id().value()
                )
        );
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
                "Jira",
                "business_context",
                "IMP-214",
                "ai-onboarding-assistant",
                "business_context_requested",
                Severity.INFO,
                "head-customer-success",
                "business_context",
                "AI operating cost requires a model review",
                "Establishes the business need for DRC-AOA-001",
                "DRC-AOA-001",
                "INTERNAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.of("evidence_ref", "E-JIRA-001")
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

        assertTrue(
                ledgerRepository.findByDecisionId(decisionId).isEmpty(),
                "ReviewDecision must not append a ledger entry implicitly"
        );

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
    void deterministicDecisionCreationIsIdempotentAndCannotResetProgressedState() throws SQLException {
        Evidence evidence = eligibleDecisionEvidence();
        evidenceRepository.save(evidence);
        DecisionId id = decisionId();
        UserId ownerId = userId();
        UserId approverId = userId();
        CreateDecisionCommand command = decisionCreationCommand(
                id,
                evidence.id(),
                "Evaluate AI model cost",
                ownerId,
                approverId
        );
        CreateDecisionUseCase useCase = new CreateDecisionUseCase(
                evidenceRepository,
                decisionRepository,
                transactionRunner
        );

        CreateDecisionResult first = useCase.createDecision(command);
        CreateDecisionResult retry = useCase.createDecision(command);
        Decision progressed = decisionRepository.findById(id).orElseThrow();
        progressed.defer(approverId, timestamp(2), "More usage evidence is required");
        decisionRepository.save(progressed);

        CreateDecisionResult progressedRetry = useCase.createDecision(command);
        DecisionCreationConflictException conflict = assertThrows(
                DecisionCreationConflictException.class,
                () -> useCase.createDecision(decisionCreationCommand(
                        id,
                        evidence.id(),
                        "Replace the entire AI platform",
                        ownerId,
                        approverId
                ))
        );
        Decision stored = decisionRepository.findById(id).orElseThrow();

        assertAll(
                () -> assertEquals(first, retry),
                () -> assertEquals(DecisionStatus.CREATED, first.status()),
                () -> assertEquals(DecisionStatus.DEFERRED, progressedRetry.status()),
                () -> assertEquals("DECISION_CREATION_CONFLICT", conflict.code()),
                () -> assertEquals("Evaluate AI model cost", stored.title()),
                () -> assertEquals(DecisionStatus.DEFERRED, stored.status()),
                () -> assertEquals(Optional.of(timestamp(2)), stored.reviewedAt()),
                () -> assertEquals(
                        Optional.of("More usage evidence is required"),
                        stored.reviewReason()
                ),
                () -> assertTrue(stored.recommendationId().isEmpty()),
                () -> assertTrue(ledgerRepository.findByDecisionId(id).isEmpty()),
                () -> assertEquals(1, countRows("SELECT COUNT(*) FROM decisions WHERE id = ?", id.value())),
                () -> assertEquals(
                        1,
                        countRows(
                                "SELECT COUNT(*) FROM decision_evidence WHERE decision_id = ?",
                                id.value()
                        )
                ),
                () -> assertEquals(
                        0,
                        countRows(
                                "SELECT COUNT(*) FROM recommendations WHERE decision_id = ?",
                                id.value()
                        )
                ),
                () -> assertEquals(
                        0,
                        countRows(
                                "SELECT COUNT(*) FROM ledger_entries WHERE decision_id = ?",
                                id.value()
                        )
                )
        );
    }

    @Test
    void equivalentConcurrentCreationPersistsOneDecisionIdentity() throws Exception {
        Evidence evidence = eligibleDecisionEvidence();
        evidenceRepository.save(evidence);
        DecisionId id = decisionId();
        CreateDecisionCommand command = decisionCreationCommand(
                id,
                evidence.id(),
                "Evaluate AI model cost",
                userId(),
                userId()
        );
        CreateDecisionUseCase useCase = new CreateDecisionUseCase(
                evidenceRepository,
                decisionRepository,
                transactionRunner
        );

        List<CreateDecisionResult> results = runConcurrently(
                () -> useCase.createDecision(command),
                () -> useCase.createDecision(command)
        );
        Decision stored = decisionRepository.findById(id).orElseThrow();

        assertAll(
                () -> assertEquals(results.get(0), results.get(1)),
                () -> assertEquals(DecisionStatus.CREATED, stored.status()),
                () -> assertEquals(1, stored.evidenceIds().size()),
                () -> assertEquals(1, countRows("SELECT COUNT(*) FROM decisions WHERE id = ?", id.value())),
                () -> assertEquals(
                        1,
                        countRows(
                                "SELECT COUNT(*) FROM decision_evidence WHERE decision_id = ?",
                                id.value()
                        )
                )
        );
    }

    @Test
    void conflictingConcurrentCreationNeverOverwritesTheWinningTuple() throws Exception {
        Evidence evidence = eligibleDecisionEvidence();
        evidenceRepository.save(evidence);
        DecisionId id = decisionId();
        UserId ownerId = userId();
        UserId approverId = userId();
        CreateDecisionUseCase useCase = new CreateDecisionUseCase(
                evidenceRepository,
                decisionRepository,
                transactionRunner
        );
        CreateDecisionCommand firstCommand = decisionCreationCommand(
                id,
                evidence.id(),
                "Evaluate AI model cost",
                ownerId,
                approverId
        );
        CreateDecisionCommand competingCommand = decisionCreationCommand(
                id,
                evidence.id(),
                "Replace the entire AI platform",
                ownerId,
                approverId
        );

        List<CreationAttempt> attempts = runConcurrently(
                () -> attemptCreation(useCase, firstCommand),
                () -> attemptCreation(useCase, competingCommand)
        );
        Decision stored = decisionRepository.findById(id).orElseThrow();
        long successes = attempts.stream().filter(CreationAttempt::successful).count();
        long conflicts = attempts.stream().filter(attempt -> !attempt.successful()).count();

        assertAll(
                () -> assertEquals(1, successes),
                () -> assertEquals(1, conflicts),
                () -> assertTrue(attempts.stream()
                        .filter(attempt -> !attempt.successful())
                        .allMatch(attempt -> "DECISION_CREATION_CONFLICT".equals(attempt.conflictCode()))),
                () -> assertTrue(Set.of(
                        "Evaluate AI model cost",
                        "Replace the entire AI platform"
                ).contains(stored.title())),
                () -> assertEquals(DecisionStatus.CREATED, stored.status()),
                () -> assertEquals(1, countRows("SELECT COUNT(*) FROM decisions WHERE id = ?", id.value())),
                () -> assertEquals(
                        1,
                        countRows(
                                "SELECT COUNT(*) FROM decision_evidence WHERE decision_id = ?",
                                id.value()
                        )
                )
        );
    }

    @Test
    void recommendationAndDecisionRollbackTogetherOnSecondRepositoryFailure() {
        Evidence evidence = evidence("case-cross-repository-rollback");
        evidenceRepository.save(evidence);
        Decision decision = decision(evidence);
        decisionRepository.save(decision);

        RecommendationId recommendationId = recommendationId();
        TrackingTransactionRunner trackedTransactions = new TrackingTransactionRunner(transactionRunner);
        AtomicBoolean explanationInvoked = new AtomicBoolean();
        DecisionRepository failAfterSave = new FailAfterSaveDecisionRepository(decisionRepository);
        GenerateRecommendationUseCase useCase = new GenerateRecommendationUseCase(
                failAfterSave,
                evidenceRepository,
                recommendationRepository,
                request -> {
                    assertFalse(
                            trackedTransactions.isExecuting(),
                            "ExplanationProvider must run before the database transaction begins"
                    );
                    explanationInvoked.set(true);
                    return Optional.empty();
                },
                trackedTransactions
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
                () -> assertTrue(explanationInvoked.get()),
                () -> assertFalse(recommendationRepository.existsById(recommendationId)),
                () -> assertTrue(
                        decisionRepository.findById(decision.id()).orElseThrow().recommendationId().isEmpty()
                )
        );
    }

    @Test
    void importEvidenceUseCaseRollsBackWhenItsFinalWriteFails() {
        EvidenceId evidenceId = evidenceId();
        ImportEvidenceUseCase useCase = new ImportEvidenceUseCase(
                new FailAfterSaveEvidenceRepository(evidenceRepository),
                transactionRunner
        );

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> useCase.importEvidence(importEvidenceCommand(evidenceId, "case-import-rollback"))
        );

        assertAll(
                () -> assertEquals("Injected failure after EvidenceRepository.save", failure.getMessage()),
                () -> assertFalse(evidenceRepository.existsById(evidenceId)),
                () -> assertTrue(evidenceRepository.findById(evidenceId).isEmpty())
        );
    }

    @Test
    void createDecisionUseCaseRollsBackWhenItsFinalWriteFails() {
        Evidence evidence = eligibleDecisionEvidence();
        evidenceRepository.save(evidence);
        DecisionId newDecisionId = decisionId();
        CreateDecisionUseCase useCase = new CreateDecisionUseCase(
                evidenceRepository,
                new FailAfterSaveDecisionRepository(decisionRepository),
                transactionRunner
        );

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> useCase.createDecision(new CreateDecisionCommand(
                        newDecisionId,
                        evidence.id(),
                        "Right-size workload",
                        "Reduce recurring infrastructure cost",
                        userId(),
                        userId(),
                        timestamp(1)
                ))
        );

        assertAll(
                () -> assertEquals(
                        "Injected failure after DecisionRepository.createIfAbsent",
                        failure.getMessage()
                ),
                () -> assertFalse(decisionRepository.existsById(newDecisionId)),
                () -> assertTrue(decisionRepository.findById(newDecisionId).isEmpty()),
                () -> assertTrue(evidenceRepository.existsById(evidence.id()))
        );
    }

    @Test
    void reviewDecisionUseCaseRollsBackAndNeverAppendsLedger() {
        PersistedDecisionGraph graph = persistRecommendationGraph("case-review-rollback");
        graph.decision().markUnderReview(timestamp(3));
        decisionRepository.save(graph.decision());

        ReviewDecisionUseCase useCase = new ReviewDecisionUseCase(
                new FailAfterSaveDecisionRepository(decisionRepository),
                recommendationRepository,
                transactionRunner
        );

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> useCase.reviewDecision(new ReviewDecisionCommand(
                        graph.decision().id(),
                        graph.recommendation().id(),
                        ReviewDecisionAction.APPROVE,
                        userId(),
                        timestamp(4),
                        "Injected review rollback"
                ))
        );

        Decision storedDecision = decisionRepository.findById(graph.decision().id()).orElseThrow();
        assertAll(
                () -> assertEquals("Injected failure after DecisionRepository.save", failure.getMessage()),
                () -> assertEquals(DecisionStatus.UNDER_REVIEW, storedDecision.status()),
                () -> assertTrue(storedDecision.reviewedBy().isEmpty()),
                () -> assertTrue(storedDecision.reviewedAt().isEmpty()),
                () -> assertTrue(storedDecision.reviewReason().isEmpty()),
                () -> assertTrue(ledgerRepository.findByDecisionId(graph.decision().id()).isEmpty())
        );
    }

    @Test
    void appendLedgerEntryUseCaseRollsBackWhenItsFinalWriteFails() {
        PersistedDecisionGraph graph = persistApprovedDecisionGraph("case-ledger-use-case-rollback");
        LedgerEntryId newEntryId = ledgerEntryId();
        AppendLedgerEntryUseCase useCase = new AppendLedgerEntryUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                new FailAfterAppendLedgerRepository(ledgerRepository),
                transactionRunner
        );

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> useCase.appendLedgerEntry(approvedLedgerCommand(graph, newEntryId))
        );

        assertAll(
                () -> assertEquals("Injected failure after LedgerRepository.append", failure.getMessage()),
                () -> assertTrue(ledgerRepository.findById(newEntryId).isEmpty()),
                () -> assertTrue(ledgerRepository.findByDecisionId(graph.decision().id()).isEmpty()),
                () -> assertEquals(
                        DecisionStatus.APPROVED,
                        decisionRepository.findById(graph.decision().id()).orElseThrow().status()
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

    private void executeAdminPrepared(String sql, Object... parameters) throws SQLException {
        try (
                Connection connection = adminDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int index = 0; index < parameters.length; index++) {
                statement.setObject(index + 1, parameters[index]);
            }
            statement.executeUpdate();
        }
    }

    private int countRows(String sql, UUID id) throws SQLException {
        try (
                Connection connection = adminDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return resultSet.getInt(1);
            }
        }
    }

    private void assertDatabaseViolation(
            String expectedSqlState,
            String expectedMessageFragment,
            SqlOperation operation
    ) {
        SQLException exception = assertThrows(SQLException.class, operation::execute);
        assertAll(
                () -> assertEquals(expectedSqlState, exception.getSQLState()),
                () -> assertTrue(
                        exception.getMessage().contains(expectedMessageFragment),
                        () -> "Expected PostgreSQL error to reference "
                                + expectedMessageFragment
                                + " but was: "
                                + exception.getMessage()
                )
        );
    }

    private void assertRepositoryConstraint(String expectedConstraint, Runnable operation) {
        IllegalStateException exception = assertThrows(IllegalStateException.class, operation::run);
        SQLException sqlException = findSqlCause(exception);
        assertAll(
                () -> assertEquals("23505", sqlException.getSQLState()),
                () -> assertTrue(
                        sqlException.getMessage().contains(expectedConstraint),
                        () -> "Expected PostgreSQL error to reference "
                                + expectedConstraint
                                + " but was: "
                                + sqlException.getMessage()
                )
        );
    }

    private SQLException findSqlCause(Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            if (current instanceof SQLException sqlException) {
                return sqlException;
            }
            current = current.getCause();
        }
        throw new AssertionError("Expected a PostgreSQL SQLException cause", failure);
    }

    private PersistedDecisionGraph persistRecommendationGraph(String correlationKey) {
        Evidence evidence = evidence(correlationKey);
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
        return new PersistedDecisionGraph(evidence, decision, recommendation);
    }

    private PersistedDecisionGraph persistApprovedDecisionGraph(String correlationKey) {
        PersistedDecisionGraph graph = persistRecommendationGraph(correlationKey);
        graph.decision().markUnderReview(timestamp(3));
        graph.decision().approve(userId(), timestamp(4), "Approved for integration certification");
        decisionRepository.save(graph.decision());
        return graph;
    }

    private ImportEvidenceCommand importEvidenceCommand(EvidenceId id, String correlationKey) {
        return new ImportEvidenceCommand(
                id,
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
                Map.of("environment", "integration")
        );
    }

    private AppendLedgerEntryCommand approvedLedgerCommand(
            PersistedDecisionGraph graph,
            LedgerEntryId entryId
    ) {
        return new AppendLedgerEntryCommand(
                entryId,
                graph.decision().id(),
                Optional.of(graph.recommendation().id()),
                userId(),
                "FINOPS_APPROVER",
                timestamp(5),
                LedgerEntryType.APPROVED,
                "Recommendation approved",
                "Savings and risk are acceptable",
                Set.of(graph.evidence().id()),
                Optional.of(SAVING),
                Optional.empty(),
                Optional.of(CONFIDENCE),
                Optional.of(Severity.MEDIUM),
                Optional.empty(),
                Map.of("source", "postgresql-it")
        );
    }

    private CreateDecisionCommand decisionCreationCommand(
            DecisionId id,
            EvidenceId evidenceId,
            String title,
            UserId ownerId,
            UserId approverId
    ) {
        return new CreateDecisionCommand(
                id,
                evidenceId,
                title,
                "Reduce recurring AI operating cost",
                ownerId,
                approverId,
                timestamp(1)
        );
    }

    private CreationAttempt attemptCreation(
            CreateDecisionUseCase useCase,
            CreateDecisionCommand command
    ) {
        try {
            useCase.createDecision(command);
            return new CreationAttempt(true, null);
        } catch (DecisionCreationConflictException conflict) {
            return new CreationAttempt(false, conflict.code());
        }
    }

    private <T> List<T> runConcurrently(
            Supplier<T> firstOperation,
            Supplier<T> secondOperation
    ) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        Future<T> first = executor.submit(
                () -> executeAfterStart(ready, start, firstOperation)
        );
        Future<T> second = executor.submit(
                () -> executeAfterStart(ready, start, secondOperation)
        );

        try {
            assertTrue(ready.await(10, TimeUnit.SECONDS), "Concurrent tasks did not become ready");
            start.countDown();
            return List.of(
                    first.get(15, TimeUnit.SECONDS),
                    second.get(15, TimeUnit.SECONDS)
            );
        } finally {
            start.countDown();
            executor.shutdownNow();
            assertTrue(
                    executor.awaitTermination(10, TimeUnit.SECONDS),
                    "Concurrent task executor did not terminate"
            );
        }
    }

    private <T> T executeAfterStart(
            CountDownLatch ready,
            CountDownLatch start,
            Supplier<T> operation
    ) {
        ready.countDown();
        try {
            if (!start.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timed out waiting for concurrent creation start");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrent creation was interrupted", exception);
        }
        return operation.get();
    }

    private Evidence eligibleDecisionEvidence() {
        return new Evidence(
                evidenceId(),
                timestamp(0),
                "Jira",
                "business_context",
                "IMP-214",
                "ai-onboarding-assistant",
                "business_context_requested",
                Severity.INFO,
                "head-customer-success",
                "business_context",
                "AI operating cost requires a model review",
                "Establishes the business need for DRC-AOA-001",
                "DRC-AOA-001",
                "INTERNAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.of("evidence_ref", "E-JIRA-001")
        );
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

    @FunctionalInterface
    private interface SqlOperation {
        void execute() throws SQLException;
    }

    private record PersistedDecisionGraph(
            Evidence evidence,
            Decision decision,
            Recommendation recommendation
    ) {
    }

    private record CreationAttempt(boolean successful, String conflictCode) {
    }

    private static final class FailAfterSaveEvidenceRepository implements EvidenceRepository {
        private final EvidenceRepository delegate;

        private FailAfterSaveEvidenceRepository(EvidenceRepository delegate) {
            this.delegate = Objects.requireNonNull(delegate, "Evidence repository is required");
        }

        @Override
        public void save(Evidence evidence) {
            delegate.save(evidence);
            throw new IllegalStateException("Injected failure after EvidenceRepository.save");
        }

        @Override
        public Optional<Evidence> findById(EvidenceId id) {
            return delegate.findById(id);
        }

        @Override
        public boolean existsById(EvidenceId id) {
            return delegate.existsById(id);
        }
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
        public Decision createIfAbsent(Decision decision) {
            delegate.createIfAbsent(decision);
            throw new IllegalStateException(
                    "Injected failure after DecisionRepository.createIfAbsent"
            );
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

    private static final class FailAfterAppendLedgerRepository implements LedgerRepository {
        private final LedgerRepository delegate;

        private FailAfterAppendLedgerRepository(LedgerRepository delegate) {
            this.delegate = Objects.requireNonNull(delegate, "Ledger repository is required");
        }

        @Override
        public void append(LedgerEntry entry) {
            delegate.append(entry);
            throw new IllegalStateException("Injected failure after LedgerRepository.append");
        }

        @Override
        public Optional<LedgerEntry> findById(LedgerEntryId id) {
            return delegate.findById(id);
        }

        @Override
        public List<LedgerEntry> findByDecisionId(DecisionId decisionId) {
            return delegate.findByDecisionId(decisionId);
        }
    }

    private static final class TrackingTransactionRunner implements TransactionRunner {
        private final TransactionRunner delegate;
        private boolean executing;

        private TrackingTransactionRunner(TransactionRunner delegate) {
            this.delegate = Objects.requireNonNull(delegate, "Transaction runner is required");
        }

        @Override
        public <T> T execute(Supplier<T> operation) {
            if (executing) {
                return delegate.execute(operation);
            }

            executing = true;
            try {
                return delegate.execute(operation);
            } finally {
                executing = false;
            }
        }

        private boolean isExecuting() {
            return executing;
        }
    }
}
