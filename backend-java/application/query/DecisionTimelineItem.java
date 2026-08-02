package imperator.application.query;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Timestamp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record DecisionTimelineItem(
        String type,
        UUID referenceId,
        Timestamp occurredAt,
        String summary,
        Optional<String> source,
        Optional<String> actor,
        Optional<String> confidenceLabel,
        Optional<Integer> confidencePercentage,
        List<EvidenceId> evidenceIds
) {
    public DecisionTimelineItem {
        evidenceIds = List.copyOf(evidenceIds);
    }
}
