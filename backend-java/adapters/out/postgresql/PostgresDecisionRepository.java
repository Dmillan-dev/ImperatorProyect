package imperator.adapters.out.postgresql;

import imperator.adapters.out.postgresql.mapper.PostgresDecisionMapper;
import imperator.adapters.out.postgresql.model.PostgresDecisionEvidenceRecord;
import imperator.adapters.out.postgresql.model.PostgresDecisionRecord;
import imperator.domain.decision.Decision;
import imperator.domain.shared.DecisionId;
import imperator.ports.out.DecisionRepository;

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

public final class PostgresDecisionRepository implements DecisionRepository {
    private static final String INSERT_IF_ABSENT_SQL = """
            INSERT INTO decisions (
                id,
                case_id,
                title,
                business_need,
                originating_evidence_id,
                owner_id,
                required_approver_id,
                created_at,
                status,
                recommendation_id,
                reviewed_by,
                reviewed_at,
                review_reason,
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String UPSERT_SQL = """
            INSERT INTO decisions (
                id,
                case_id,
                title,
                business_need,
                originating_evidence_id,
                owner_id,
                required_approver_id,
                created_at,
                status,
                recommendation_id,
                reviewed_by,
                reviewed_at,
                review_reason,
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                case_id = EXCLUDED.case_id,
                title = EXCLUDED.title,
                business_need = EXCLUDED.business_need,
                originating_evidence_id = EXCLUDED.originating_evidence_id,
                owner_id = EXCLUDED.owner_id,
                required_approver_id = EXCLUDED.required_approver_id,
                created_at = EXCLUDED.created_at,
                status = EXCLUDED.status,
                recommendation_id = EXCLUDED.recommendation_id,
                reviewed_by = EXCLUDED.reviewed_by,
                reviewed_at = EXCLUDED.reviewed_at,
                review_reason = EXCLUDED.review_reason,
                updated_at = EXCLUDED.updated_at
            """;

    private static final String DELETE_EVIDENCE_LINKS_SQL = """
            DELETE FROM decision_evidence
            WHERE decision_id = ?
            """;

    private static final String INSERT_EVIDENCE_LINK_SQL = """
            INSERT INTO decision_evidence (
                decision_id,
                evidence_id
            ) VALUES (?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT
                id,
                case_id,
                title,
                business_need,
                originating_evidence_id,
                owner_id,
                required_approver_id,
                created_at,
                status,
                recommendation_id,
                reviewed_by,
                reviewed_at,
                review_reason,
                updated_at
            FROM decisions
            WHERE id = ?
            """;

    private static final String SELECT_BY_ID_FOR_UPDATE_SQL = SELECT_BY_ID_SQL + """
            FOR UPDATE
            """;

    private static final String SELECT_EVIDENCE_LINKS_SQL = """
            SELECT
                decision_id,
                evidence_id
            FROM decision_evidence
            WHERE decision_id = ?
            """;

    private static final String EXISTS_BY_ID_SQL = """
            SELECT 1
            FROM decisions
            WHERE id = ?
            """;

    private final PostgresConnectionProvider connectionProvider;
    private final PostgresDecisionMapper mapper;

    public PostgresDecisionRepository(PostgresConnectionProvider connectionProvider) {
        this(connectionProvider, new PostgresDecisionMapper());
    }

    PostgresDecisionRepository(PostgresConnectionProvider connectionProvider, PostgresDecisionMapper mapper) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "Connection provider is required");
        this.mapper = Objects.requireNonNull(mapper, "Decision mapper is required");
    }

    @Override
    public void save(Decision decision) {
        Decision item = Objects.requireNonNull(decision, "Decision is required");
        PostgresDecisionRecord record = mapper.toRecord(item);
        List<PostgresDecisionEvidenceRecord> evidenceRecords = mapper.toEvidenceRecords(item);

        try {
            PostgresLocalTransactions.execute(connectionProvider, connection -> {
                saveDecision(connection, record);
                replaceEvidenceLinks(connection, item.id(), evidenceRecords);
            });
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not persist decision " + item.id().value(), exception);
        }
    }

    @Override
    public Decision createIfAbsent(Decision decision) {
        Decision candidate = Objects.requireNonNull(decision, "Decision is required");
        PostgresDecisionRecord record = mapper.toRecord(candidate);
        List<PostgresDecisionEvidenceRecord> evidenceRecords = mapper.toEvidenceRecords(candidate);
        Decision[] persisted = new Decision[1];

        try {
            PostgresLocalTransactions.execute(connectionProvider, connection -> {
                if (insertDecisionIfAbsent(connection, record)) {
                    insertEvidenceLinks(connection, evidenceRecords);
                }
                persisted[0] = findRequiredDecision(connection, candidate.id());
            });
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not create decision if absent " + candidate.id().value(),
                    exception
            );
        }

        return Objects.requireNonNull(persisted[0], "Persisted decision is required");
    }

    @Override
    public Optional<Decision> findById(DecisionId id) {
        DecisionId decisionId = Objects.requireNonNull(id, "Decision id is required");

        try (PostgresConnectionProvider.ConnectionLease connectionLease = connectionProvider.acquire()) {
            Connection connection = connectionLease.connection();
            Optional<PostgresDecisionRecord> record = findRecordById(connection, decisionId);
            if (record.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapper.toDomain(record.get(), findEvidenceLinks(connection, decisionId)));
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find decision " + decisionId.value(), exception);
        }
    }

    @Override
    public boolean existsById(DecisionId id) {
        DecisionId decisionId = Objects.requireNonNull(id, "Decision id is required");

        try (PostgresConnectionProvider.ConnectionLease connectionLease = connectionProvider.acquire()) {
            Connection connection = connectionLease.connection();
            try (PreparedStatement statement = connection.prepareStatement(EXISTS_BY_ID_SQL)) {
                statement.setObject(1, decisionId.value());
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not check decision existence " + decisionId.value(), exception);
        }
    }

    private void saveDecision(Connection connection, PostgresDecisionRecord record) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPSERT_SQL)) {
            bindDecisionRecord(statement, record);
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<Decision> findByIdForUpdate(DecisionId id) {
        DecisionId decisionId = Objects.requireNonNull(id, "Decision id is required");

        try (PostgresConnectionProvider.ConnectionLease connectionLease = connectionProvider.acquire()) {
            Connection connection = connectionLease.connection();
            if (connection.getAutoCommit()) {
                throw new IllegalStateException("Decision row locking requires an active transaction");
            }
            Optional<PostgresDecisionRecord> record = findRecordByIdForUpdate(connection, decisionId);
            if (record.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapper.toDomain(record.get(), findEvidenceLinks(connection, decisionId)));
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not lock decision " + decisionId.value(), exception);
        }
    }

    private boolean insertDecisionIfAbsent(
            Connection connection,
            PostgresDecisionRecord record
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_IF_ABSENT_SQL)) {
            bindDecisionRecord(statement, record);
            return statement.executeUpdate() == 1;
        }
    }

    private void replaceEvidenceLinks(
            Connection connection,
            DecisionId decisionId,
            List<PostgresDecisionEvidenceRecord> evidenceRecords
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_EVIDENCE_LINKS_SQL)) {
            statement.setObject(1, decisionId.value());
            statement.executeUpdate();
        }

        insertEvidenceLinks(connection, evidenceRecords);
    }

    private void insertEvidenceLinks(
            Connection connection,
            List<PostgresDecisionEvidenceRecord> evidenceRecords
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_EVIDENCE_LINK_SQL)) {
            for (PostgresDecisionEvidenceRecord evidenceRecord : evidenceRecords) {
                statement.setObject(1, evidenceRecord.decisionId());
                statement.setObject(2, evidenceRecord.evidenceId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Decision findRequiredDecision(Connection connection, DecisionId id) throws SQLException {
        PostgresDecisionRecord record = findRecordById(connection, id)
                .orElseThrow(() -> new SQLException(
                        "Decision was not found after atomic create-if-absent: " + id.value()
                ));
        return mapper.toDomain(record, findEvidenceLinks(connection, id));
    }

    private Optional<PostgresDecisionRecord> findRecordById(Connection connection, DecisionId id) throws SQLException {
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

    private List<PostgresDecisionEvidenceRecord> findEvidenceLinks(Connection connection, DecisionId id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_EVIDENCE_LINKS_SQL)) {
            statement.setObject(1, id.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<PostgresDecisionEvidenceRecord> records = new ArrayList<>();
                while (resultSet.next()) {
                    records.add(new PostgresDecisionEvidenceRecord(
                            resultSet.getObject("decision_id", UUID.class),
                            resultSet.getObject("evidence_id", UUID.class)
                    ));
                }
                return List.copyOf(records);
            }
        }
    }

    private PostgresDecisionRecord recordFrom(ResultSet resultSet) throws SQLException {
        return new PostgresDecisionRecord(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("case_id"),
                resultSet.getString("title"),
                resultSet.getString("business_need"),
                resultSet.getObject("originating_evidence_id", UUID.class),
                resultSet.getObject("owner_id", UUID.class),
                resultSet.getObject("required_approver_id", UUID.class),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getString("status"),
                resultSet.getObject("recommendation_id", UUID.class),
                resultSet.getObject("reviewed_by", UUID.class),
                nullableInstant(resultSet, "reviewed_at"),
                resultSet.getString("review_reason"),
                resultSet.getTimestamp("updated_at").toInstant()
        );
    }

    private Instant nullableInstant(ResultSet resultSet, String columnName) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnName);
        return timestamp == null ? null : timestamp.toInstant();
    }

    private void setNullableUuid(PreparedStatement statement, int parameterIndex, UUID value) throws SQLException {
        if (value == null) {
            statement.setObject(parameterIndex, null);
        } else {
            statement.setObject(parameterIndex, value);
        }
    }

    private void setNullableTimestamp(PreparedStatement statement, int parameterIndex, Instant value) throws SQLException {
        if (value == null) {
            statement.setTimestamp(parameterIndex, null);
        } else {
            statement.setTimestamp(parameterIndex, Timestamp.from(value));
        }
    }

    private Optional<PostgresDecisionRecord> findRecordByIdForUpdate(
            Connection connection,
            DecisionId id
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_FOR_UPDATE_SQL)) {
            statement.setObject(1, id.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(recordFrom(resultSet));
            }
        }
    }

    private void bindDecisionRecord(
            PreparedStatement statement,
            PostgresDecisionRecord record
    ) throws SQLException {
        statement.setObject(1, record.id());
        statement.setString(2, record.caseId());
        statement.setString(3, record.title());
        statement.setString(4, record.businessNeed());
        statement.setObject(5, record.originatingEvidenceId());
        statement.setObject(6, record.ownerId());
        statement.setObject(7, record.requiredApproverId());
        statement.setTimestamp(8, Timestamp.from(record.createdAt()));
        statement.setString(9, record.status());
        setNullableUuid(statement, 10, record.recommendationId());
        setNullableUuid(statement, 11, record.reviewedBy());
        setNullableTimestamp(statement, 12, record.reviewedAt());
        statement.setString(13, record.reviewReason());
        statement.setTimestamp(14, Timestamp.from(record.updatedAt()));
    }

}
