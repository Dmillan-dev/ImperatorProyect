package imperator.application.appendledgerentry;

import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.Timestamp;

import java.util.Optional;

public record AppendLedgerEntryResult(
        LedgerEntryId ledgerEntryId,
        DecisionId decisionId,
        Optional<RecommendationId> recommendationId,
        LedgerEntryType entryType,
        Timestamp occurredAt,
        int evidenceSnapshotCount,
        Optional<ROIAmount> realizedSaving,
        boolean replayed
) {
}
