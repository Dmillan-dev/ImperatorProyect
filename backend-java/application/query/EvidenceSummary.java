package imperator.application.query;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

public record EvidenceSummary(
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
        String reviewStatus
) {
}
