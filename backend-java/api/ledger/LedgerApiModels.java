package imperator.api.ledger;

import java.util.List;
import java.util.Map;

public final class LedgerApiModels {
    private LedgerApiModels() {
    }

    public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {
        public PageResponse {
            items = List.copyOf(items);
        }
    }

    public record Money(String amount, String currency) {
    }

    public record LedgerEntryResponse(
            String ledgerEntryId,
            String decisionId,
            String recommendationId,
            String actorId,
            String actorRole,
            String occurredAt,
            String entryType,
            String changeSummary,
            String reason,
            List<String> evidenceIds,
            Money estimatedSavings,
            Money realizedSavings,
            Integer confidence,
            String risk,
            String previousEntryId,
            Map<String, String> metadata
    ) {
        public LedgerEntryResponse {
            evidenceIds = List.copyOf(evidenceIds);
            metadata = Map.copyOf(metadata);
        }
    }

    public record ReviewRequest(String reviewedAt, String reason, String expectedPreviousEntryId) {
    }

    public record DeferRequest(
            String reviewedAt,
            String reason,
            String expectedPreviousEntryId,
            String requiredEvidence,
            String reviewDate
    ) {
    }

    public record MarkImplementedRequest(
            String occurredAt,
            String reason,
            List<String> evidenceIds,
            String expectedPreviousEntryId,
            String period
    ) {
    }

    public record ValidateResultRequest(
            String occurredAt,
            String reason,
            List<String> evidenceIds,
            String expectedPreviousEntryId,
            String period,
            Money annualizedBaselineCost,
            Money annualizedPostActionCost,
            Money actualTransitionCost
    ) {
    }

    public record ReviewResultResponse(
            String ledgerEntryId,
            String decisionId,
            String recommendationId,
            String status,
            String reviewerId,
            String reviewerRole,
            String reviewReason,
            boolean replayed
    ) {
    }

    public record AppendResultResponse(
            String ledgerEntryId,
            String decisionId,
            String recommendationId,
            String entryType,
            String occurredAt,
            int evidenceSnapshotCount,
            Money realizedSaving,
            boolean replayed
    ) {
    }
}
