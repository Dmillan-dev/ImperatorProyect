package imperator.adapters.out.postgresql;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.ports.out.ExplanationStatus;
import imperator.ports.out.RecommendationExplanationRecord;
import imperator.ports.out.RecommendationExplanationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PostgresRecommendationExplanationRepository
        implements RecommendationExplanationRepository {
    private static final String INSERT_EXPLANATION_SQL = """
            INSERT INTO recommendation_explanations (
                id, recommendation_id, status, explanation_text, provider, model_id,
                prompt_version, requested_at, completed_at, input_tokens, output_tokens,
                latency_ms, failure_code
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (recommendation_id, provider, model_id, prompt_version) DO NOTHING
            """;
    private static final String INSERT_EVIDENCE_SQL = """
            INSERT INTO recommendation_explanation_evidence (explanation_id, evidence_id)
            VALUES (?, ?)
            """;
    private static final String INSERT_ASSUMPTION_SQL = """
            INSERT INTO recommendation_explanation_assumptions (explanation_id, assumption_id)
            VALUES (?, ?)
            """;
    private static final String SELECT_LATEST_SQL = """
            SELECT id, recommendation_id, status, explanation_text, provider, model_id,
                   prompt_version, requested_at, completed_at, input_tokens, output_tokens,
                   latency_ms, failure_code
            FROM recommendation_explanations
            WHERE recommendation_id = ?
            ORDER BY requested_at DESC, id DESC
            LIMIT 1
            """;
    private static final String SELECT_GENERATION_SQL = """
            SELECT id, recommendation_id, status, explanation_text, provider, model_id,
                   prompt_version, requested_at, completed_at, input_tokens, output_tokens,
                   latency_ms, failure_code
            FROM recommendation_explanations
            WHERE recommendation_id = ?
              AND provider = ?
              AND model_id = ?
              AND prompt_version = ?
            """;
    private static final String SELECT_EVIDENCE_SQL = """
            SELECT evidence_id
            FROM recommendation_explanation_evidence
            WHERE explanation_id = ?
            ORDER BY evidence_id
            """;
    private static final String SELECT_ASSUMPTIONS_SQL = """
            SELECT assumption_id
            FROM recommendation_explanation_assumptions
            WHERE explanation_id = ?
            ORDER BY assumption_id
            """;

    private final PostgresConnectionProvider connectionProvider;

    public PostgresRecommendationExplanationRepository(PostgresConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "Connection provider is required");
    }

    @Override
    public void save(RecommendationExplanationRecord explanation) {
        RecommendationExplanationRecord item = Objects.requireNonNull(explanation, "Explanation is required");
        try {
            PostgresLocalTransactions.execute(connectionProvider, connection -> {
                if (insertExplanation(connection, item)) {
                    insertEvidence(connection, item);
                    insertAssumptions(connection, item);
                }
            });
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not persist Recommendation explanation " + item.explanationId(), exception
            );
        }
    }

    @Override
    public Optional<RecommendationExplanationRecord> findLatestByRecommendationId(RecommendationId id) {
        RecommendationId recommendationId = Objects.requireNonNull(id, "Recommendation id is required");
        try (PostgresConnectionProvider.ConnectionLease lease = connectionProvider.acquire()) {
            Connection connection = lease.connection();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_LATEST_SQL)) {
                statement.setObject(1, recommendationId.value());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    UUID explanationId = resultSet.getObject("id", UUID.class);
                    return Optional.of(from(
                            resultSet,
                            findEvidence(connection, explanationId),
                            findAssumptions(connection, explanationId)
                    ));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not find Recommendation explanation " + recommendationId.value(), exception
            );
        }
    }

    @Override
    public Optional<RecommendationExplanationRecord> findByGeneration(
            RecommendationId id,
            String provider,
            String modelId,
            String promptVersion
    ) {
        RecommendationId recommendationId = Objects.requireNonNull(id, "Recommendation id is required");
        Objects.requireNonNull(provider, "Explanation provider is required");
        Objects.requireNonNull(modelId, "Explanation model id is required");
        Objects.requireNonNull(promptVersion, "Explanation prompt version is required");
        try (PostgresConnectionProvider.ConnectionLease lease = connectionProvider.acquire()) {
            Connection connection = lease.connection();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_GENERATION_SQL)) {
                statement.setObject(1, recommendationId.value());
                statement.setString(2, provider);
                statement.setString(3, modelId);
                statement.setString(4, promptVersion);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    UUID explanationId = resultSet.getObject("id", UUID.class);
                    return Optional.of(from(
                            resultSet,
                            findEvidence(connection, explanationId),
                            findAssumptions(connection, explanationId)
                    ));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not find Recommendation explanation generation "
                            + recommendationId.value(), exception
            );
        }
    }

    private boolean insertExplanation(Connection connection, RecommendationExplanationRecord item)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_EXPLANATION_SQL)) {
            statement.setObject(1, item.explanationId());
            statement.setObject(2, item.recommendationId().value());
            statement.setString(3, item.status().name());
            setNullableText(statement, 4, item.text());
            statement.setString(5, item.provider());
            statement.setString(6, item.modelId());
            statement.setString(7, item.promptVersion());
            statement.setTimestamp(8, java.sql.Timestamp.from(item.requestedAt().value()));
            statement.setTimestamp(9, java.sql.Timestamp.from(item.completedAt().value()));
            setNullableInteger(statement, 10, item.inputTokens());
            setNullableInteger(statement, 11, item.outputTokens());
            setNullableLong(statement, 12, item.latencyMillis());
            setNullableText(statement, 13, item.failureCode());
            return statement.executeUpdate() == 1;
        }
    }

    private void insertEvidence(Connection connection, RecommendationExplanationRecord item) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_EVIDENCE_SQL)) {
            for (EvidenceId evidenceId : item.evidenceIds()) {
                statement.setObject(1, item.explanationId());
                statement.setObject(2, evidenceId.value());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertAssumptions(Connection connection, RecommendationExplanationRecord item) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_ASSUMPTION_SQL)) {
            for (String assumptionId : item.assumptionIds()) {
                statement.setObject(1, item.explanationId());
                statement.setString(2, assumptionId);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<EvidenceId> findEvidence(Connection connection, UUID explanationId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_EVIDENCE_SQL)) {
            statement.setObject(1, explanationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<EvidenceId> items = new ArrayList<>();
                while (resultSet.next()) {
                    items.add(new EvidenceId(resultSet.getObject("evidence_id", UUID.class)));
                }
                return List.copyOf(items);
            }
        }
    }

    private List<String> findAssumptions(Connection connection, UUID explanationId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ASSUMPTIONS_SQL)) {
            statement.setObject(1, explanationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> items = new ArrayList<>();
                while (resultSet.next()) {
                    items.add(resultSet.getString("assumption_id"));
                }
                return List.copyOf(items);
            }
        }
    }

    private RecommendationExplanationRecord from(
            ResultSet resultSet,
            List<EvidenceId> evidenceIds,
            List<String> assumptionIds
    ) throws SQLException {
        return new RecommendationExplanationRecord(
                resultSet.getObject("id", UUID.class),
                new RecommendationId(resultSet.getObject("recommendation_id", UUID.class)),
                ExplanationStatus.valueOf(resultSet.getString("status")),
                Optional.ofNullable(resultSet.getString("explanation_text")),
                resultSet.getString("provider"),
                resultSet.getString("model_id"),
                resultSet.getString("prompt_version"),
                new Timestamp(resultSet.getTimestamp("requested_at").toInstant()),
                new Timestamp(resultSet.getTimestamp("completed_at").toInstant()),
                optionalInteger(resultSet, "input_tokens"),
                optionalInteger(resultSet, "output_tokens"),
                optionalLong(resultSet, "latency_ms"),
                Optional.ofNullable(resultSet.getString("failure_code")),
                evidenceIds,
                assumptionIds
        );
    }

    private static Optional<Integer> optionalInteger(ResultSet resultSet, String column) throws SQLException {
        int value = resultSet.getInt(column);
        return resultSet.wasNull() ? Optional.empty() : Optional.of(value);
    }

    private static Optional<Long> optionalLong(ResultSet resultSet, String column) throws SQLException {
        long value = resultSet.getLong(column);
        return resultSet.wasNull() ? Optional.empty() : Optional.of(value);
    }

    private static void setNullableText(PreparedStatement statement, int index, Optional<String> value)
            throws SQLException {
        if (value.isPresent()) {
            statement.setString(index, value.orElseThrow());
        } else {
            statement.setNull(index, Types.VARCHAR);
        }
    }

    private static void setNullableInteger(PreparedStatement statement, int index, Optional<Integer> value)
            throws SQLException {
        if (value.isPresent()) {
            statement.setInt(index, value.orElseThrow());
        } else {
            statement.setNull(index, Types.INTEGER);
        }
    }

    private static void setNullableLong(PreparedStatement statement, int index, Optional<Long> value)
            throws SQLException {
        if (value.isPresent()) {
            statement.setLong(index, value.orElseThrow());
        } else {
            statement.setNull(index, Types.BIGINT);
        }
    }
}
