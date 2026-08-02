package imperator.api.recommendations;

import java.util.List;

public record RecommendationResponse(
        String recommendationId,
        String decisionId,
        String type,
        String suggestedAction,
        String deterministicReason,
        MoneyResponse estimatedSavings,
        int confidence,
        String risk,
        String ownerId,
        String requiredApproverId,
        String createdAt,
        List<String> evidenceIds
) {
    public RecommendationResponse {
        evidenceIds = List.copyOf(evidenceIds);
    }

    public record MoneyResponse(String amount, String currency) {
    }
}
