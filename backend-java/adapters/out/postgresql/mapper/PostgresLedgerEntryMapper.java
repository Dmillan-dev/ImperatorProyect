package imperator.adapters.out.postgresql.mapper;

import imperator.adapters.out.postgresql.model.PostgresLedgerEntryRecord;
import imperator.adapters.out.postgresql.model.PostgresLedgerEvidenceSnapshotRecord;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.Currency;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.Money;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class PostgresLedgerEntryMapper {
    public PostgresLedgerEntryRecord toRecord(LedgerEntry ledgerEntry) {
        return new PostgresLedgerEntryRecord(
                ledgerEntry.id().value(),
                ledgerEntry.decisionId().value(),
                ledgerEntry.recommendationId().map(RecommendationId::value).orElse(null),
                ledgerEntry.actorId().value(),
                ledgerEntry.actorRole(),
                ledgerEntry.occurredAt().value(),
                ledgerEntry.entryType().value(),
                ledgerEntry.changeSummary(),
                ledgerEntry.reason(),
                ledgerEntry.estimatedSaving().map(value -> value.value().amount()).orElse(null),
                ledgerEntry.estimatedSaving().map(value -> value.value().currency().code()).orElse(null),
                ledgerEntry.realizedSaving().map(value -> value.value().amount()).orElse(null),
                ledgerEntry.realizedSaving().map(value -> value.value().currency().code()).orElse(null),
                ledgerEntry.confidenceSnapshot().map(ROIConfidence::percentage).orElse(null),
                ledgerEntry.riskSnapshot().map(Severity::value).orElse(null),
                ledgerEntry.previousEntryId().map(LedgerEntryId::value).orElse(null),
                ledgerEntry.metadata()
        );
    }

    public List<PostgresLedgerEvidenceSnapshotRecord> toEvidenceSnapshotRecords(LedgerEntry ledgerEntry) {
        return ledgerEntry.evidenceSnapshotIds().stream()
                .map(evidenceId -> new PostgresLedgerEvidenceSnapshotRecord(ledgerEntry.id().value(), evidenceId.value()))
                .toList();
    }

    public LedgerEntry toDomain(
            PostgresLedgerEntryRecord record,
            Collection<PostgresLedgerEvidenceSnapshotRecord> evidenceSnapshotRecords
    ) {
        return new LedgerEntry(
                new LedgerEntryId(record.id()),
                new DecisionId(record.decisionId()),
                Optional.ofNullable(record.recommendationId()).map(RecommendationId::new),
                new UserId(record.actorId()),
                record.actorRole(),
                new Timestamp(record.occurredAt()),
                new LedgerEntryType(record.entryType()),
                record.changeSummary(),
                record.reason(),
                evidenceSnapshotIds(evidenceSnapshotRecords),
                roiAmount(record.estimatedSavingAmount(), record.estimatedSavingCurrency()),
                roiAmount(record.realizedSavingAmount(), record.realizedSavingCurrency()),
                Optional.ofNullable(record.confidencePercentage()).map(ROIConfidence::new),
                Optional.ofNullable(record.risk()).map(Severity::new),
                Optional.ofNullable(record.previousEntryId()).map(LedgerEntryId::new),
                record.metadata()
        );
    }

    private Set<EvidenceId> evidenceSnapshotIds(
            Collection<PostgresLedgerEvidenceSnapshotRecord> evidenceSnapshotRecords
    ) {
        return records(evidenceSnapshotRecords).stream()
                .map(PostgresLedgerEvidenceSnapshotRecord::evidenceId)
                .map(EvidenceId::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Optional<ROIAmount> roiAmount(BigDecimal amount, String currency) {
        if (amount == null && currency == null) {
            return Optional.empty();
        }
        return Optional.of(new ROIAmount(new Money(amount, new Currency(currency))));
    }

    private Collection<PostgresLedgerEvidenceSnapshotRecord> records(
            Collection<PostgresLedgerEvidenceSnapshotRecord> records
    ) {
        return records == null ? List.of() : List.copyOf(records);
    }
}
