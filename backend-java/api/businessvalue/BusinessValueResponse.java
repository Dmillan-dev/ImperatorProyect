package imperator.api.businessvalue;

import java.util.List;

public record BusinessValueResponse(
        String caseId,
        String correlationKey,
        String decisionId,
        String decisionTitle,
        String businessNeed,
        String decisionStatus,
        String decisionCreatedAt,
        String recommendationId,
        String recommendationType,
        String recommendedAction,
        String deterministicReason,
        String explanation,
        String recommendationCreatedAt,
        MoneyResponse estimatedSavings,
        MoneyResponse realizedSavings,
        String variance,
        MoneyResponse annualizedBaselineCost,
        MoneyResponse annualizedPostActionCost,
        MoneyResponse actualTransitionCost,
        int confidence,
        String risk,
        String policyVersion,
        List<String> evidenceIds,
        List<String> assumptionIds,
        String approvalEntryId,
        String implementationEntryId,
        String validationEntryId,
        List<LedgerFactResponse> ledgerHistory
) {
    public BusinessValueResponse {
        evidenceIds = List.copyOf(evidenceIds);
        assumptionIds = List.copyOf(assumptionIds);
        ledgerHistory = List.copyOf(ledgerHistory);
    }

    public record MoneyResponse(String amount, String currency) {
    }

    public record LedgerFactResponse(
            String id,
            String type,
            String actorId,
            String actorRole,
            String occurredAt,
            String previousEntryId,
            List<String> evidenceIds
    ) {
        public LedgerFactResponse {
            evidenceIds = List.copyOf(evidenceIds);
        }
    }
}
