package imperator.adapters.out.postgresql.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PostgresLedgerEntryRecord(
        UUID id,
        UUID decisionId,
        UUID recommendationId,
        UUID actorId,
        String actorRole,
        Instant occurredAt,
        String entryType,
        String changeSummary,
        String reason,
        BigDecimal estimatedSavingAmount,
        String estimatedSavingCurrency,
        BigDecimal realizedSavingAmount,
        String realizedSavingCurrency,
        Integer confidencePercentage,
        String risk,
        UUID previousEntryId,
        Map<String, String> metadata
) {
}
