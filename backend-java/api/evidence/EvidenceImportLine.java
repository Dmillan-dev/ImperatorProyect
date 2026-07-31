package imperator.api.evidence;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

record EvidenceImportLine(
        UUID id,
        String schemaVersion,
        Instant timestamp,
        Instant sourceIngestedAt,
        String source,
        String sourceType,
        String sourceObjectRef,
        String entity,
        String eventType,
        String severity,
        String actor,
        String evidenceType,
        String caseHint,
        String observedFact,
        String businessMeaning,
        String correlationKey,
        String sensitivity,
        String confidence,
        String freshness,
        String reviewStatus,
        String rawPayloadMode,
        Map<String, String> metadata
) {
}
