package imperator.adapters.out.postgresql.model;

import java.util.UUID;

public record PostgresDecisionEvidenceRecord(
        UUID decisionId,
        UUID evidenceId
) {
}
