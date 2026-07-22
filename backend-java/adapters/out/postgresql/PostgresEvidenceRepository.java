package imperator.adapters.out.postgresql;

import imperator.adapters.out.postgresql.mapper.PostgresEvidenceMapper;
import imperator.adapters.out.postgresql.model.PostgresEvidenceRecord;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.ports.out.EvidenceRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

public final class PostgresEvidenceRepository implements EvidenceRepository {
    private static final String INSERT_SQL = """
            INSERT INTO evidence (
                id,
                timestamp,
                source,
                source_type,
                source_object_ref,
                entity,
                event_type,
                severity,
                actor,
                evidence_type,
                observed_fact,
                business_meaning,
                correlation_key,
                sensitivity,
                confidence,
                review_status,
                raw_payload_mode,
                metadata
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT
                id,
                timestamp,
                source,
                source_type,
                source_object_ref,
                entity,
                event_type,
                severity,
                actor,
                evidence_type,
                observed_fact,
                business_meaning,
                correlation_key,
                sensitivity,
                confidence,
                review_status,
                raw_payload_mode,
                metadata
            FROM evidence
            WHERE id = ?
            """;

    private static final String EXISTS_BY_ID_SQL = """
            SELECT 1
            FROM evidence
            WHERE id = ?
            """;

    private final DataSource dataSource;
    private final PostgresEvidenceMapper mapper;

    public PostgresEvidenceRepository(DataSource dataSource) {
        this(dataSource, new PostgresEvidenceMapper());
    }

    PostgresEvidenceRepository(DataSource dataSource, PostgresEvidenceMapper mapper) {
        this.dataSource = Objects.requireNonNull(dataSource, "Data source is required");
        this.mapper = Objects.requireNonNull(mapper, "Evidence mapper is required");
    }

    @Override
    public void save(Evidence evidence) {
        PostgresEvidenceRecord record = mapper.toRecord(Objects.requireNonNull(evidence, "Evidence is required"));

        try {
            PostgresLocalTransactions.execute(dataSource, connection -> {
                try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
                    bindRecord(statement, record);
                    statement.executeUpdate();
                }
            });
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not persist evidence " + evidence.id().value(), exception);
        }
    }

    @Override
    public Optional<Evidence> findById(EvidenceId id) {
        EvidenceId evidenceId = Objects.requireNonNull(id, "Evidence id is required");

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)
        ) {
            statement.setObject(1, evidenceId.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapper.toDomain(recordFrom(resultSet)));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find evidence " + evidenceId.value(), exception);
        }
    }

    @Override
    public boolean existsById(EvidenceId id) {
        EvidenceId evidenceId = Objects.requireNonNull(id, "Evidence id is required");

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(EXISTS_BY_ID_SQL)
        ) {
            statement.setObject(1, evidenceId.value());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not check evidence existence " + evidenceId.value(), exception);
        }
    }

    private void bindRecord(PreparedStatement statement, PostgresEvidenceRecord record) throws SQLException {
        statement.setObject(1, record.id());
        statement.setTimestamp(2, Timestamp.from(record.timestamp()));
        statement.setString(3, record.source());
        statement.setString(4, record.sourceType());
        statement.setString(5, record.sourceObjectRef());
        statement.setString(6, record.entity());
        statement.setString(7, record.eventType());
        statement.setString(8, record.severity());
        statement.setString(9, record.actor());
        statement.setString(10, record.evidenceType());
        statement.setString(11, record.observedFact());
        statement.setString(12, record.businessMeaning());
        statement.setString(13, record.correlationKey());
        statement.setString(14, record.sensitivity());
        statement.setString(15, record.confidence());
        statement.setString(16, record.reviewStatus());
        statement.setString(17, record.rawPayloadMode());
        statement.setString(18, PostgresFlatMetadataJson.toJson(record.metadata()));
    }

    private PostgresEvidenceRecord recordFrom(ResultSet resultSet) throws SQLException {
        return new PostgresEvidenceRecord(
                resultSet.getObject("id", java.util.UUID.class),
                resultSet.getTimestamp("timestamp").toInstant(),
                resultSet.getString("source"),
                resultSet.getString("source_type"),
                resultSet.getString("source_object_ref"),
                resultSet.getString("entity"),
                resultSet.getString("event_type"),
                resultSet.getString("severity"),
                resultSet.getString("actor"),
                resultSet.getString("evidence_type"),
                resultSet.getString("observed_fact"),
                resultSet.getString("business_meaning"),
                resultSet.getString("correlation_key"),
                resultSet.getString("sensitivity"),
                resultSet.getString("confidence"),
                resultSet.getString("review_status"),
                resultSet.getString("raw_payload_mode"),
                PostgresFlatMetadataJson.fromJson(resultSet.getString("metadata"))
        );
    }
}
