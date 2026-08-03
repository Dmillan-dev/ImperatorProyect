package imperator.adapters.out.github;

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
import imperator.ports.out.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class GitHubEvidenceSyncIT {
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
    void importsAndReplaysGitHubEvidenceAgainstCertifiedPostgreSql() throws IOException, SQLException {
        PostgresConnectionProvider connections = new PostgresConnectionProvider(dataSource);
        EvidenceRepository repository = new PostgresEvidenceRepository(connections);
        PostgresTransactionRunner transactions = new PostgresTransactionRunner(connections);
        try (GitHubContractStubServer server = new GitHubContractStubServer(
                GitHubContractStubServer.Mode.HAPPY
        )) {
            GitHubRestAdapter adapter = new GitHubRestAdapter(
                    new GitHubConnectorSettings(
                            true,
                            "github_pat_postgresql-certification",
                            "acme",
                            "imperator-demo"
                    ),
                    server.baseUri(),
                    HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(5))
                            .followRedirects(HttpClient.Redirect.NEVER)
                            .build(),
                    JsonMapper.builder().build(),
                    ignored -> {
                    },
                    Clock.fixed(Instant.parse("2026-07-01T00:00:00Z"), ZoneOffset.UTC),
                    System::nanoTime
            );
            SynchronizeEvidenceUseCase useCase = new SynchronizeEvidenceUseCase(
                    adapter,
                    new ImportEvidenceUseCase(repository, transactions),
                    repository
            );
            SynchronizeEvidenceCommand command = new SynchronizeEvidenceCommand(
                    Instant.parse("2026-03-01T00:00:00Z"),
                    Instant.parse("2026-07-01T00:00:00Z"),
                    UUID.fromString("ad0195f0-74f3-4aad-a233-236c785d8b6e")
            );

            SynchronizeEvidenceResult first = useCase.synchronize(command);
            SynchronizeEvidenceResult replay = useCase.synchronize(command);

            assertEquals(SynchronizationStatus.COMPLETE, first.status());
            assertEquals(3, first.acceptedEvidenceCount());
            assertEquals(0, first.unchangedEvidenceCount());
            assertEquals(SynchronizationStatus.COMPLETE, replay.status());
            assertEquals(0, replay.acceptedEvidenceCount());
            assertEquals(3, replay.unchangedEvidenceCount());
            assertEquals(3, evidenceCount());
            assertEquals(
                    List.of("E-GH-002", "E-GH-001", "E-GH-003"),
                    first.evidenceReferences()
            );
            for (imperator.domain.shared.EvidenceId evidenceId : first.evidenceIds()) {
                Evidence evidence = repository.findById(evidenceId).orElseThrow();
                assertEquals("GitHub", evidence.source());
                assertEquals("not_stored", evidence.rawPayloadMode());
                assertFalse(evidence.toString().contains("raw-title-must-not-persist"));
                assertFalse(evidence.toString().contains("raw-body-must-not-persist"));
                assertTrue(evidence.metadata().containsKey("case_hint"));
            }
        }
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
