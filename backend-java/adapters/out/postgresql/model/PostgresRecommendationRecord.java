package imperator.adapters.out.postgresql.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PostgresRecommendationRecord(
        UUID id,
        UUID decisionId,
        String type,
        String suggestedAction,
        String reason,
        BigDecimal estimatedSavingAmount,
        String estimatedSavingCurrency,
        Integer confidencePercentage,
        String risk,
        UUID ownerId,
        UUID requiredApproverId,
        Instant createdAt
) {
}
