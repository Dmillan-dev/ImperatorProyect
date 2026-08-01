package imperator.application.reviewdecision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.time.LocalDate;
import java.util.Optional;

public record ReviewDecisionCommand(
        LedgerEntryId ledgerEntryId,
        DecisionId decisionId,
        ReviewDecisionAction action,
        UserId reviewerId,
        String reviewerRole,
        Timestamp reviewedAt,
        String reviewReason,
        Optional<LedgerEntryId> expectedPreviousEntryId,
        Optional<String> requiredEvidence,
        Optional<LocalDate> reviewDate
) {
}
