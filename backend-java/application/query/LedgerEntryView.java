package imperator.application.query;

import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record LedgerEntryView(
        LedgerEntryId ledgerEntryId,
        DecisionId decisionId,
        Optional<RecommendationId> recommendationId,
        UserId actorId,
        String actorRole,
        Timestamp occurredAt,
        LedgerEntryType entryType,
        String changeSummary,
        String reason,
        List<EvidenceId> evidenceIds,
        Optional<ROIAmount> estimatedSavings,
        Optional<ROIAmount> realizedSavings,
        Optional<ROIConfidence> confidence,
        Optional<Severity> risk,
        Optional<LedgerEntryId> previousEntryId,
        Map<String, String> metadata
) {
    public LedgerEntryView {
        evidenceIds = List.copyOf(evidenceIds);
        metadata = Map.copyOf(metadata);
    }
}
