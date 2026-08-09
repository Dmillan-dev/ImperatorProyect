package imperator.adapters.out.aws;

import imperator.adapters.out.postgresql.PostgresConnectionProvider;
import imperator.adapters.out.postgresql.PostgresDataSource;
import imperator.adapters.out.postgresql.PostgresEvidenceRepository;
import imperator.adapters.out.postgresql.PostgresTransactionRunner;
import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.application.synchronizeevidence.SynchronizeEvidenceCommand;
import imperator.application.synchronizeevidence.SynchronizeEvidenceResult;
import imperator.application.synchronizeevidence.SynchronizeEvidenceUseCase;
import imperator.application.synchronizeevidence.SynchronizationStatus;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.ports.out.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AwsEvidenceSyncIT {
    private static final String ACCOUNT = "123456789012";
    private static final SynchronizeEvidenceCommand COMMAND = new SynchronizeEvidenceCommand(
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-07-01T00:00:00Z"),
            UUID.fromString("7e9a5b66-15cb-4130-9bd3-e7ef7ae00475")
    );

    private final PostgresDataSource dataSource = new PostgresDataSource(
            requiredProperty("imperator.it.dbUrl"),
            requiredProperty("imperator.it.adminUser"),
            requiredProperty("imperator.it.adminPassword")
    );

    @BeforeEach
    void cleanApplicationTables() throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
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

    @Test
    void importsReplaysAndRejectsConflictingAwsEvidenceWithoutOverwrite()
            throws IOException, SQLException {
        PostgresConnectionProvider connections = new PostgresConnectionProvider(dataSource);
        EvidenceRepository repository = new PostgresEvidenceRepository(connections);
        PostgresTransactionRunner transactions = new PostgresTransactionRunner(connections);
        try (AwsContractStubServer server = new AwsContractStubServer(AwsContractStubServer.Mode.HAPPY);
             AwsSdkEvidenceSourceAdapter adapter = adapter(server)) {
            SynchronizeEvidenceUseCase useCase = useCase(adapter, repository, transactions);

            SynchronizeEvidenceResult first = useCase.synchronize(COMMAND);
            SynchronizeEvidenceResult replay = useCase.synchronize(COMMAND);

            assertEquals(SynchronizationStatus.COMPLETE, first.status());
            assertEquals(4, first.acceptedEvidenceCount());
            assertEquals(0, first.unchangedEvidenceCount());
            assertEquals(SynchronizationStatus.COMPLETE, replay.status());
            assertEquals(0, replay.acceptedEvidenceCount());
            assertEquals(4, replay.unchangedEvidenceCount());
            assertEquals(4, evidenceCount());
            assertEquals(
                    List.of("E-AWS-001", "E-AWS-002", "E-AWS-003", "E-AWS-004"),
                    first.evidenceReferences()
            );
            for (EvidenceId evidenceId : first.evidenceIds()) {
                Evidence evidence = repository.findById(evidenceId).orElseThrow();
                assertEquals("AWS", evidence.source());
                assertEquals("not_stored", evidence.rawPayloadMode());
                assertEquals("DRC-AOA-001", evidence.metadata().get("case_hint"));
                assertFalse(evidence.toString().contains("arn:aws:"));
                assertFalse(evidence.toString().contains("dummy-test-secret"));
            }
        }

        try (AwsContractStubServer changedServer = new AwsContractStubServer(
                AwsContractStubServer.Mode.NON_EUR_COST
        ); AwsSdkEvidenceSourceAdapter changedAdapter = adapter(changedServer)) {
            SynchronizeEvidenceUseCase changed = useCase(changedAdapter, repository, transactions);
            SynchronizeEvidenceResult conflict = changed.synchronize(COMMAND);

            assertEquals(SynchronizationStatus.SOURCE_IDENTITY_CONFLICT, conflict.status());
            assertEquals("SOURCE_IDENTITY_CONFLICT", conflict.failureCode());
            assertEquals(1, conflict.rejectedEvidenceCount());
            assertEquals(3, conflict.unchangedEvidenceCount());
            assertEquals(4, evidenceCount());
            Evidence originalCost = repository.findById(new EvidenceId(
                    UUID.fromString("439d74fc-8b76-59bc-8065-73caf2ec5e5f")
            )).orElseThrow();
            assertEquals("EUR", originalCost.metadata().get("currency"));
            assertEquals("410.00", originalCost.metadata().get("monthly_cost"));
            assertTrue(originalCost.canSupportApproval());
        }
    }

    private SynchronizeEvidenceUseCase useCase(
            AwsSdkEvidenceSourceAdapter adapter,
            EvidenceRepository repository,
            PostgresTransactionRunner transactions
    ) {
        return new SynchronizeEvidenceUseCase(
                adapter,
                new ImportEvidenceUseCase(repository, transactions),
                repository
        );
    }

    private AwsSdkEvidenceSourceAdapter adapter(AwsContractStubServer server) {
        AwsConnectorSettings settings = new AwsConnectorSettings(true, ACCOUNT, "eu-west-1");
        return new AwsSdkEvidenceSourceAdapter(
                settings,
                (ignored, metrics) -> AwsTestClients.create(settings, metrics, server.endpoint()),
                Clock.fixed(Instant.parse("2026-08-01T00:00:00Z"), ZoneOffset.UTC),
                System::nanoTime
        );
    }

    private int evidenceCount() throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM evidence")
        ) {
            result.next();
            return result.getInt(1);
        }
    }

    private static String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required integration property is missing: " + name);
        }
        return value;
    }
}
