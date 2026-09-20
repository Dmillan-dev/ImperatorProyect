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
        List<String> evidenceIds,
        ExplanationResponse explanation
) {
    public RecommendationResponse {
        evidenceIds = List.copyOf(evidenceIds);
    }

    public record MoneyResponse(String amount, String currency) {
    }

    public record ExplanationResponse(
            String explanationId,
            String status,
            String text,
            String provider,
            String modelId,
            String promptVersion,
            String requestedAt,
            String completedAt,
            Integer inputTokens,
            Integer outputTokens,
            Long latencyMillis,
            String failureCode,
            List<String> evidenceIds,
            List<String> assumptionIds
    ) {
        public ExplanationResponse {
            evidenceIds = List.copyOf(evidenceIds);
            assumptionIds = List.copyOf(assumptionIds);
        }
    }
}
