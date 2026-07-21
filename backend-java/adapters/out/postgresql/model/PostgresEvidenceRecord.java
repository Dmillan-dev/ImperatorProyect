package imperator.adapters.out.postgresql.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PostgresEvidenceRecord(
        UUID id,
        Instant timestamp,
        String source,
        String sourceType,
        String sourceObjectRef,
        String entity,
        String eventType,
        String severity,
        String actor,
        String evidenceType,
        String observedFact,
        String businessMeaning,
        String correlationKey,
        String sensitivity,
        String confidence,
        String reviewStatus,
        String rawPayloadMode,
        Map<String, String> metadata
) {
}
