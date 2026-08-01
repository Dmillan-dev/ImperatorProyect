package imperator.adapters.out.postgresql;

import imperator.adapters.out.postgresql.mapper.PostgresRecommendationMapper;
import imperator.adapters.out.postgresql.model.PostgresRecommendationEvidenceRecord;
import imperator.adapters.out.postgresql.model.PostgresRecommendationRecord;
import imperator.domain.decision.Recommendation;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationId;
import imperator.ports.out.RecommendationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PostgresRecommendationRepository implements RecommendationRepository {
    private static final String INSERT_SQL = """
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
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String INSERT_IF_ABSENT_SQL = INSERT_SQL + """
            ON CONFLICT DO NOTHING
            """;

    private static final String LOCK_DECISION_SQL = """
            SELECT id
            FROM decisions
            WHERE id = ?
            FOR UPDATE
            """;

    private static final String INSERT_EVIDENCE_LINK_SQL = """
            INSERT INTO recommendation_evidence (
                recommendation_id,
                evidence_id
            ) VALUES (?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT
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
            FROM recommendations
            WHERE id = ?
            """;

    private static final String SELECT_BY_DECISION_ID_SQL = """
            SELECT
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
            FROM recommendations
            WHERE decision_id = ?
            """;

    private static final String SELECT_EVIDENCE_LINKS_SQL = """
            SELECT
                recommendation_id,
                evidence_id
            FROM recommendation_evidence
            WHERE recommendation_id = ?
            """;

    private static final String EXISTS_BY_ID_SQL = """
            SELECT 1
            FROM recommendations
            WHERE id = ?
            """;

    private final PostgresConnectionProvider connectionProvider;
    private final PostgresRecommendationMapper mapper;

    public PostgresRecommendationRepository(PostgresConnectionProvider connectionProvider) {
        this(connectionProvider, new PostgresRecommendationMapper());
    }

    PostgresRecommendationRepository(
            PostgresConnectionProvider connectionProvider,
            PostgresRecommendationMapper mapper
    ) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "Connection provider is required");
        this.mapper = Objects.requireNonNull(mapper, "Recommendation mapper is required");
    }

    @Override
    public void save(Recommendation recommendation) {
        Recommendation item = Objects.requireNonNull(recommendation, "Recommendation is required");
        PostgresRecommendationRecord record = mapper.toRecord(item);
        List<PostgresRecommendationEvidenceRecord> evidenceRecords = mapper.toEvidenceRecords(item);

        try {
            PostgresLocalTransactions.execute(connectionProvider, connection -> {
                saveRecommendation(connection, record);
                saveEvidenceLinks(connection, evidenceRecords);
            });
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not persist recommendation " + item.id().value(), exception);
        }
    }

    @Override
    public Recommendation createIfAbsent(Recommendation recommendation) {
        Recommendation candidate = Objects.requireNonNull(recommendation, "Recommendation is required");
        PostgresRecommendationRecord record = mapper.toRecord(candidate);
        List<PostgresRecommendationEvidenceRecord> evidenceRecords = mapper.toEvidenceRecords(candidate);
        Recommendation[] persisted = new Recommendation[1];

        try {
            PostgresLocalTransactions.execute(connectionProvider, connection -> {
                lockOwningDecision(connection, candidate.decisionId());
                if (insertRecommendationIfAbsent(connection, record)) {
                    saveEvidenceLinks(connection, evidenceRecords);
                }
                persisted[0] = findAuthoritativeRecommendation(connection, candidate);
            });
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not create recommendation if absent " + candidate.id().value(),
                    exception
            );
        }

        return Objects.requireNonNull(persisted[0], "Persisted recommendation is required");
    }

    @Override
    public Optional<Recommendation> findById(RecommendationId id) {
        RecommendationId recommendationId = Objects.requireNonNull(id, "Recommendation id is required");

        try (PostgresConnectionProvider.ConnectionLease connectionLease = connectionProvider.acquire()) {
            Connection connection = connectionLease.connection();
            Optional<PostgresRecommendationRecord> record = findRecordById(connection, recommendationId);
            if (record.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapper.toDomain(record.get(), findEvidenceLinks(connection, recommendationId)));
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find recommendation " + recommendationId.value(), exception);
        }
    }

    @Override
    public boolean existsById(RecommendationId id) {
        RecommendationId recommendationId = Objects.requireNonNull(id, "Recommendation id is required");

        try (PostgresConnectionProvider.ConnectionLease connectionLease = connectionProvider.acquire()) {
            Connection connection = connectionLease.connection();
            try (PreparedStatement statement = connection.prepareStatement(EXISTS_BY_ID_SQL)) {
                statement.setObject(1, recommendationId.value());
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not check recommendation existence " + recommendationId.value(), exception);
        }
    }

    private void saveRecommendation(Connection connection, PostgresRecommendationRecord record) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, record.id());
            statement.setObject(2, record.decisionId());
            statement.setString(3, record.type());
            statement.setString(4, record.suggestedAction());
            statement.setString(5, record.reason());
            statement.setBigDecimal(6, record.estimatedSavingAmount());
            statement.setString(7, record.estimatedSavingCurrency());
            statement.setInt(8, record.confidencePercentage());
            statement.setString(9, record.risk());
            statement.setObject(10, record.ownerId());
            statement.setObject(11, record.requiredApproverId());
            statement.setTimestamp(12, Timestamp.from(record.createdAt()));
            statement.executeUpdate();
        }
    }

    private boolean insertRecommendationIfAbsent(
            Connection connection,
            PostgresRecommendationRecord record
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_IF_ABSENT_SQL)) {
            bindRecommendationRecord(statement, record);
            return statement.executeUpdate() == 1;
        }
    }

    private void lockOwningDecision(Connection connection, DecisionId decisionId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(LOCK_DECISION_SQL)) {
            statement.setObject(1, decisionId.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Owning Decision was not found: " + decisionId.value());
                }
            }
        }
    }

    private void saveEvidenceLinks(
            Connection connection,
            List<PostgresRecommendationEvidenceRecord> evidenceRecords
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_EVIDENCE_LINK_SQL)) {
            for (PostgresRecommendationEvidenceRecord evidenceRecord : evidenceRecords) {
                statement.setObject(1, evidenceRecord.recommendationId());
                statement.setObject(2, evidenceRecord.evidenceId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Optional<PostgresRecommendationRecord> findRecordById(
            Connection connection,
            RecommendationId id
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

    private Optional<PostgresRecommendationRecord> findRecordByDecisionId(
            Connection connection,
            DecisionId decisionId
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_DECISION_ID_SQL)) {
            statement.setObject(1, decisionId.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(recordFrom(resultSet));
            }
        }
    }

    private Recommendation findAuthoritativeRecommendation(
            Connection connection,
            Recommendation candidate
    ) throws SQLException {
        Optional<PostgresRecommendationRecord> byId = findRecordById(connection, candidate.id());
        Optional<PostgresRecommendationRecord> authoritative = byId.isPresent()
                ? byId
                : findRecordByDecisionId(connection, candidate.decisionId());
        PostgresRecommendationRecord record = authoritative
                .orElseThrow(() -> new SQLException(
                        "Recommendation was not found after atomic create-if-absent: " + candidate.id().value()
                ));
        RecommendationId authoritativeId = new RecommendationId(record.id());
        return mapper.toDomain(record, findEvidenceLinks(connection, authoritativeId));
    }

    private List<PostgresRecommendationEvidenceRecord> findEvidenceLinks(
            Connection connection,
            RecommendationId id
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_EVIDENCE_LINKS_SQL)) {
            statement.setObject(1, id.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<PostgresRecommendationEvidenceRecord> records = new ArrayList<>();
                while (resultSet.next()) {
                    records.add(new PostgresRecommendationEvidenceRecord(
                            resultSet.getObject("recommendation_id", UUID.class),
                            resultSet.getObject("evidence_id", UUID.class)
                    ));
                }
                return List.copyOf(records);
            }
        }
    }

    private PostgresRecommendationRecord recordFrom(ResultSet resultSet) throws SQLException {
        return new PostgresRecommendationRecord(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("decision_id", UUID.class),
                resultSet.getString("type"),
                resultSet.getString("suggested_action"),
                resultSet.getString("reason"),
                resultSet.getBigDecimal("estimated_saving_amount"),
                resultSet.getString("estimated_saving_currency"),
                resultSet.getInt("confidence_percentage"),
                resultSet.getString("risk"),
                resultSet.getObject("owner_id", UUID.class),
                resultSet.getObject("required_approver_id", UUID.class),
                resultSet.getTimestamp("created_at").toInstant()
        );
    }

    private void bindRecommendationRecord(
            PreparedStatement statement,
            PostgresRecommendationRecord record
    ) throws SQLException {
        statement.setObject(1, record.id());
        statement.setObject(2, record.decisionId());
        statement.setString(3, record.type());
        statement.setString(4, record.suggestedAction());
        statement.setString(5, record.reason());
        statement.setBigDecimal(6, record.estimatedSavingAmount());
        statement.setString(7, record.estimatedSavingCurrency());
        statement.setInt(8, record.confidencePercentage());
        statement.setString(9, record.risk());
        statement.setObject(10, record.ownerId());
        statement.setObject(11, record.requiredApproverId());
        statement.setTimestamp(12, Timestamp.from(record.createdAt()));
    }

}
