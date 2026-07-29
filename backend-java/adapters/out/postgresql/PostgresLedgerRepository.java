package imperator.adapters.out.postgresql;

import imperator.adapters.out.postgresql.mapper.PostgresLedgerEntryMapper;
import imperator.adapters.out.postgresql.model.PostgresLedgerEntryRecord;
import imperator.adapters.out.postgresql.model.PostgresLedgerEvidenceSnapshotRecord;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.LedgerEntryId;
import imperator.ports.out.LedgerRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PostgresLedgerRepository implements LedgerRepository {
    private static final String INSERT_SQL = """
            INSERT INTO ledger_entries (
                id,
                decision_id,
                recommendation_id,
                actor_id,
                actor_role,
                occurred_at,
                entry_type,
                change_summary,
                reason,
                estimated_saving_amount,
                estimated_saving_currency,
                realized_saving_amount,
                realized_saving_currency,
                confidence_percentage,
                risk,
                previous_entry_id,
                metadata
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String INSERT_EVIDENCE_SNAPSHOT_SQL = """
            INSERT INTO ledger_evidence_snapshots (
                ledger_entry_id,
                evidence_id
            ) VALUES (?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT
                id,
                decision_id,
                recommendation_id,
                actor_id,
                actor_role,
                occurred_at,
                entry_type,
                change_summary,
                reason,
                estimated_saving_amount,
                estimated_saving_currency,
                realized_saving_amount,
                realized_saving_currency,
                confidence_percentage,
                risk,
                previous_entry_id,
                metadata
            FROM ledger_entries
            WHERE id = ?
            """;

    private static final String SELECT_BY_DECISION_ID_SQL = """
            SELECT
                id,
                decision_id,
                recommendation_id,
                actor_id,
                actor_role,
                occurred_at,
                entry_type,
                change_summary,
                reason,
                estimated_saving_amount,
                estimated_saving_currency,
                realized_saving_amount,
                realized_saving_currency,
                confidence_percentage,
                risk,
                previous_entry_id,
                metadata
            FROM ledger_entries
            WHERE decision_id = ?
            ORDER BY occurred_at ASC, id ASC
            """;

    private static final String SELECT_EVIDENCE_SNAPSHOTS_SQL = """
            SELECT
                ledger_entry_id,
                evidence_id
            FROM ledger_evidence_snapshots
            WHERE ledger_entry_id = ?
            ORDER BY evidence_id ASC
            """;

    private final PostgresConnectionProvider connectionProvider;
    private final PostgresLedgerEntryMapper mapper;

    public PostgresLedgerRepository(PostgresConnectionProvider connectionProvider) {
        this(connectionProvider, new PostgresLedgerEntryMapper());
    }

    PostgresLedgerRepository(
            PostgresConnectionProvider connectionProvider,
            PostgresLedgerEntryMapper mapper
    ) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "Connection provider is required");
        this.mapper = Objects.requireNonNull(mapper, "Ledger entry mapper is required");
    }

    @Override
    public void append(LedgerEntry entry) {
        LedgerEntry item = Objects.requireNonNull(entry, "Ledger entry is required");
        PostgresLedgerEntryRecord record = mapper.toRecord(item);
        List<PostgresLedgerEvidenceSnapshotRecord> evidenceSnapshotRecords = mapper.toEvidenceSnapshotRecords(item);

        try {
            PostgresLocalTransactions.execute(connectionProvider, connection -> {
                appendEntry(connection, record);
                appendEvidenceSnapshots(connection, evidenceSnapshotRecords);
            });
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not append ledger entry " + item.id().value(), exception);
        }
    }

    @Override
    public Optional<LedgerEntry> findById(LedgerEntryId id) {
        LedgerEntryId ledgerEntryId = Objects.requireNonNull(id, "Ledger entry id is required");

        try (PostgresConnectionProvider.ConnectionLease connectionLease = connectionProvider.acquire()) {
            Connection connection = connectionLease.connection();
            Optional<PostgresLedgerEntryRecord> record = findRecordById(connection, ledgerEntryId);
            if (record.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapper.toDomain(record.get(), findEvidenceSnapshots(connection, ledgerEntryId.value())));
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find ledger entry " + ledgerEntryId.value(), exception);
        }
    }

    @Override
    public List<LedgerEntry> findByDecisionId(DecisionId decisionId) {
        DecisionId id = Objects.requireNonNull(decisionId, "Decision id is required");

        try (PostgresConnectionProvider.ConnectionLease connectionLease = connectionProvider.acquire()) {
            Connection connection = connectionLease.connection();
            List<LedgerEntry> ledgerEntries = new ArrayList<>();
            for (PostgresLedgerEntryRecord record : findRecordsByDecisionId(connection, id)) {
                ledgerEntries.add(mapper.toDomain(record, findEvidenceSnapshots(connection, record.id())));
            }
            return List.copyOf(ledgerEntries);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find ledger entries for decision " + id.value(), exception);
        }
    }

    private void appendEntry(Connection connection, PostgresLedgerEntryRecord record) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, record.id());
            statement.setObject(2, record.decisionId());
            setNullableUuid(statement, 3, record.recommendationId());
            statement.setObject(4, record.actorId());
            statement.setString(5, record.actorRole());
            statement.setTimestamp(6, Timestamp.from(record.occurredAt()));
            statement.setString(7, record.entryType());
            statement.setString(8, record.changeSummary());
            statement.setString(9, record.reason());
            setNullableBigDecimal(statement, 10, record.estimatedSavingAmount());
            statement.setString(11, record.estimatedSavingCurrency());
            setNullableBigDecimal(statement, 12, record.realizedSavingAmount());
            statement.setString(13, record.realizedSavingCurrency());
            setNullableInteger(statement, 14, record.confidencePercentage());
            statement.setString(15, record.risk());
            setNullableUuid(statement, 16, record.previousEntryId());
            statement.setString(17, PostgresFlatMetadataJson.toJson(record.metadata()));
            statement.executeUpdate();
        }
    }

    private void appendEvidenceSnapshots(
            Connection connection,
            List<PostgresLedgerEvidenceSnapshotRecord> evidenceSnapshotRecords
    ) throws SQLException {
        if (evidenceSnapshotRecords.isEmpty()) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(INSERT_EVIDENCE_SNAPSHOT_SQL)) {
            for (PostgresLedgerEvidenceSnapshotRecord evidenceSnapshotRecord : evidenceSnapshotRecords) {
                statement.setObject(1, evidenceSnapshotRecord.ledgerEntryId());
                statement.setObject(2, evidenceSnapshotRecord.evidenceId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Optional<PostgresLedgerEntryRecord> findRecordById(
            Connection connection,
            LedgerEntryId id
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setObject(1, id.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(recordFrom(resultSet));
            }
        }
    }

    private List<PostgresLedgerEntryRecord> findRecordsByDecisionId(
            Connection connection,
            DecisionId decisionId
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_DECISION_ID_SQL)) {
            statement.setObject(1, decisionId.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<PostgresLedgerEntryRecord> records = new ArrayList<>();
                while (resultSet.next()) {
                    records.add(recordFrom(resultSet));
                }
                return List.copyOf(records);
            }
        }
    }

    private List<PostgresLedgerEvidenceSnapshotRecord> findEvidenceSnapshots(
            Connection connection,
            UUID ledgerEntryId
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_EVIDENCE_SNAPSHOTS_SQL)) {
            statement.setObject(1, ledgerEntryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<PostgresLedgerEvidenceSnapshotRecord> records = new ArrayList<>();
                while (resultSet.next()) {
                    records.add(new PostgresLedgerEvidenceSnapshotRecord(
                            resultSet.getObject("ledger_entry_id", UUID.class),
                            resultSet.getObject("evidence_id", UUID.class)
                    ));
                }
                return List.copyOf(records);
            }
        }
    }

    private PostgresLedgerEntryRecord recordFrom(ResultSet resultSet) throws SQLException {
        return new PostgresLedgerEntryRecord(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("decision_id", UUID.class),
                resultSet.getObject("recommendation_id", UUID.class),
                resultSet.getObject("actor_id", UUID.class),
                resultSet.getString("actor_role"),
                resultSet.getTimestamp("occurred_at").toInstant(),
                resultSet.getString("entry_type"),
                resultSet.getString("change_summary"),
                resultSet.getString("reason"),
                resultSet.getBigDecimal("estimated_saving_amount"),
                resultSet.getString("estimated_saving_currency"),
                resultSet.getBigDecimal("realized_saving_amount"),
                resultSet.getString("realized_saving_currency"),
                nullableInteger(resultSet, "confidence_percentage"),
                resultSet.getString("risk"),
                resultSet.getObject("previous_entry_id", UUID.class),
                PostgresFlatMetadataJson.fromJson(resultSet.getString("metadata"))
        );
    }

    private Integer nullableInteger(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : value;
    }

    private void setNullableUuid(PreparedStatement statement, int parameterIndex, UUID value) throws SQLException {
        statement.setObject(parameterIndex, value);
    }

    private void setNullableBigDecimal(
            PreparedStatement statement,
            int parameterIndex,
            BigDecimal value
    ) throws SQLException {
        statement.setBigDecimal(parameterIndex, value);
    }

    private void setNullableInteger(PreparedStatement statement, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            statement.setObject(parameterIndex, null);
        } else {
            statement.setInt(parameterIndex, value);
        }
    }

}
