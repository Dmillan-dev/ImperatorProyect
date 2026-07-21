package imperator.adapters.out.postgresql.model;

import java.time.Instant;
import java.util.UUID;

public record PostgresDecisionRecord(
        UUID id,
        String caseId,
        String title,
        String businessNeed,
        UUID originatingEvidenceId,
        UUID ownerId,
        UUID requiredApproverId,
        Instant createdAt,
        String status,
        UUID recommendationId,
        UUID reviewedBy,
        Instant reviewedAt,
        String reviewReason,
        Instant updatedAt
) {
}
