package imperator.application.appendledgerentry;

import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.Optional;
import java.util.Set;

public record AppendLedgerEntryCommand(
        LedgerEntryId ledgerEntryId,
        DecisionId decisionId,
        UserId actorId,
        String actorRole,
        Timestamp occurredAt,
        LedgerEntryType entryType,
        String reason,
        Set<EvidenceId> evidenceSnapshotIds,
        Optional<LedgerEntryId> expectedPreviousEntryId,
        String period,
        Optional<ROIAmount> annualizedBaselineCost,
        Optional<ROIAmount> annualizedPostActionCost,
        Optional<ROIAmount> actualTransitionCost
) {
}
