package imperator.application.importevidence;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

import java.util.Map;

public record ImportEvidenceCommand(
        EvidenceId evidenceId,
        Timestamp timestamp,
        String source,
        String sourceType,
        String sourceObjectRef,
        String entity,
        String eventType,
        Severity severity,
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
