package imperator.adapters.out.postgresql;

import imperator.application.query.MvpDecisionReadSnapshot;
import imperator.application.query.PageRequest;
import imperator.application.query.PageResult;
import imperator.application.query.SortDirection;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.MvpReadModelQueryPort;
import imperator.ports.out.RecommendationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Read-only PostgreSQL composition for the D086 MVP query boundary. */
public final class PostgresMvpReadModelQueryAdapter implements MvpReadModelQueryPort {
    private static final String COUNT_DECISIONS_SQL = "SELECT COUNT(*) FROM decisions";
    private static final String COUNT_LEDGER_SQL = "SELECT COUNT(*) FROM ledger_entries";

    private final PostgresConnectionProvider connectionProvider;
    private final DecisionRepository decisionRepository;
    private final EvidenceRepository evidenceRepository;
    private final RecommendationRepository recommendationRepository;
    private final LedgerRepository ledgerRepository;

    public PostgresMvpReadModelQueryAdapter(
            PostgresConnectionProvider connectionProvider,
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            LedgerRepository ledgerRepository
    ) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "Connection provider is required");
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.recommendationRepository = Objects.requireNonNull(
                recommendationRepository, "Recommendation repository is required"
        );
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "Ledger repository is required");
    }

    @Override
    public PageResult<Decision> findDecisions(PageRequest pageRequest) {
        PageRequest request = Objects.requireNonNull(pageRequest, "Page request is required");
        String column = switch (request.sort()) {
            case "createdAt" -> "created_at";
            case "updatedAt" -> "updated_at";
            default -> throw new IllegalArgumentException("Unsupported Decision sort field");
        };
        String sql = "SELECT id FROM decisions ORDER BY " + column + " "
                + direction(request.direction()) + ", id " + direction(request.direction())
                + " LIMIT ? OFFSET ?";
        try (PostgresConnectionProvider.ConnectionLease lease = connectionProvider.acquire()) {
            Connection connection = lease.connection();
            long total = count(connection, COUNT_DECISIONS_SQL);
            List<Decision> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, request.size());
                statement.setLong(2, (long) request.page() * request.size());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        DecisionId id = new DecisionId(rows.getObject("id", UUID.class));
                        items.add(decisionRepository.findById(id).orElseThrow(() -> missing("Decision", id.value())));
                    }
                }
            }
            return PageResult.of(items, request, total);
        } catch (SQLException exception) {
            throw unavailable("Could not query Decisions", exception);
        }
    }

    @Override
    public Optional<MvpDecisionReadSnapshot> findDecisionSnapshot(DecisionId decisionId) {
        DecisionId id = Objects.requireNonNull(decisionId, "Decision id is required");
        Optional<Decision> found = decisionRepository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }

        Decision decision = found.get();
        Optional<Recommendation> recommendation = decision.recommendationId()
                .map(recommendationId -> recommendationRepository.findById(recommendationId)
                        .orElseThrow(() -> missing("Recommendation", recommendationId.value())));
        List<LedgerEntry> ledgerEntries = ledgerRepository.findByDecisionId(id);
        Set<EvidenceId> evidenceIds = new LinkedHashSet<>(decision.evidenceIds());
        recommendation.ifPresent(item -> evidenceIds.addAll(item.evidenceIds()));
        ledgerEntries.forEach(item -> evidenceIds.addAll(item.evidenceSnapshotIds()));

        List<Evidence> evidence = evidenceIds.stream()
                .sorted(Comparator.comparing(item -> item.value().toString()))
                .map(evidenceId -> evidenceRepository.findById(evidenceId)
                        .orElseThrow(() -> missing("Evidence", evidenceId.value())))
                .toList();
        return Optional.of(new MvpDecisionReadSnapshot(decision, evidence, recommendation, ledgerEntries));
    }

    @Override
    public Optional<Recommendation> findRecommendation(RecommendationId recommendationId) {
        return recommendationRepository.findById(
                Objects.requireNonNull(recommendationId, "Recommendation id is required")
        );
    }

    @Override
    public PageResult<LedgerEntry> findLedgerEntries(PageRequest pageRequest) {
        PageRequest request = Objects.requireNonNull(pageRequest, "Page request is required");
        if (!"occurredAt".equals(request.sort())) {
            throw new IllegalArgumentException("Unsupported Ledger sort field");
        }
        String order = direction(request.direction());
        String sql = "SELECT id FROM ledger_entries ORDER BY occurred_at " + order
                + ", id " + order + " LIMIT ? OFFSET ?";
        try (PostgresConnectionProvider.ConnectionLease lease = connectionProvider.acquire()) {
            Connection connection = lease.connection();
            long total = count(connection, COUNT_LEDGER_SQL);
            List<LedgerEntry> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, request.size());
                statement.setLong(2, (long) request.page() * request.size());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) {
                        LedgerEntryId id = new LedgerEntryId(rows.getObject("id", UUID.class));
                        items.add(ledgerRepository.findById(id).orElseThrow(() -> missing("Ledger entry", id.value())));
                    }
                }
            }
            return PageResult.of(items, request, total);
        } catch (SQLException exception) {
            throw unavailable("Could not query Ledger entries", exception);
        }
    }

    private long count(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (!result.next()) {
                throw new SQLException("Count query returned no row");
            }
            return result.getLong(1);
        }
    }

    private String direction(SortDirection direction) {
        return direction == SortDirection.ASC ? "ASC" : "DESC";
    }

    private IllegalStateException missing(String aggregate, UUID id) {
        return new IllegalStateException(aggregate + " read model source is missing: " + id);
    }

    private IllegalStateException unavailable(String message, SQLException cause) {
        return new IllegalStateException(message, cause);
    }
}
