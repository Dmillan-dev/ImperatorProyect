package imperator.adapters.out.postgresql.model;

import java.util.UUID;

public record PostgresRecommendationEvidenceRecord(
        UUID recommendationId,
        UUID evidenceId
) {
}
