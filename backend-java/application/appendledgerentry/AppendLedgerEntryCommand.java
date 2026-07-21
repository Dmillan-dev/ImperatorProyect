package imperator.application.appendledgerentry;

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

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record AppendLedgerEntryCommand(
        LedgerEntryId ledgerEntryId,
        DecisionId decisionId,
        Optional<RecommendationId> recommendationId,
        UserId actorId,
        String actorRole,
        Timestamp occurredAt,
        LedgerEntryType entryType,
        String changeSummary,
        String reason,
        Set<EvidenceId> evidenceSnapshotIds,
        Optional<ROIAmount> estimatedSaving,
        Optional<ROIAmount> realizedSaving,
        Optional<ROIConfidence> confidenceSnapshot,
        Optional<Severity> riskSnapshot,
        Optional<LedgerEntryId> previousEntryId,
        Map<String, String> metadata
) {
}
