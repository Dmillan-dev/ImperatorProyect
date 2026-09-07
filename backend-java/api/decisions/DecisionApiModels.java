package imperator.api.decisions;

import java.util.List;

public final class DecisionApiModels {
    private DecisionApiModels() {
    }

    public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {
        public PageResponse {
            items = List.copyOf(items);
        }
    }

    public record DecisionSummaryResponse(
            String decisionId, String caseId, String title, String businessNeed, String status,
            String ownerId, String requiredApproverId, String recommendationId,
            String createdAt, String updatedAt
    ) {
    }

    public record DecisionDetailResponse(
            String decisionId, String caseId, String title, String businessNeed, String status,
            String ownerId, String requiredApproverId, String recommendationId,
            String createdAt, String updatedAt, String originatingEvidenceId,
            List<String> evidenceIds, String reviewedBy, String reviewedAt, String reviewReason
    ) {
        public DecisionDetailResponse {
            evidenceIds = List.copyOf(evidenceIds);
        }
    }

    public record TimelineItemResponse(
            String type, String referenceId, String occurredAt, String summary,
            String source, String actor, String confidenceLabel, Integer confidencePercentage,
            List<String> evidenceIds
    ) {
        public TimelineItemResponse {
            evidenceIds = List.copyOf(evidenceIds);
        }
    }

    public record EvidenceSummaryResponse(
            String evidenceId, String timestamp, String source, String sourceType,
            String sourceObjectRef, String entity, String eventType, String severity,
            String actor, String evidenceType, String observedFact, String businessMeaning,
            String correlationKey, String sensitivity, String confidence, String reviewStatus
    ) {
    }

    public record MoneyResponse(String amount, String currency) {
    }

    public record ComposeDrcAoa001Request(
            String caseId,
            String decisionId,
            String recommendationId,
            String originatingEvidenceId,
            List<String> evidenceIds,
            String title,
            String businessNeed,
            String ownerId,
            String requiredApproverId,
            String decisionCreatedAt,
            String recommendationGeneratedAt
    ) {
        public ComposeDrcAoa001Request {
            evidenceIds = evidenceIds == null ? null : List.copyOf(evidenceIds);
        }
    }

    public record ComposeDrcAoa001Response(
            String caseId,
            String decisionId,
            String decisionStatus,
            String recommendationId,
            String recommendationType,
            MoneyResponse estimatedAnnualizedSavings,
            int confidence,
            String risk,
            int evidenceCount,
            String workspacePath,
            boolean replayed,
            boolean resumed
    ) {
    }

    public record RoiResponse(
            String decisionId, String recommendationId, MoneyResponse currentMonthlyCost,
            MoneyResponse projectedMonthlyCost, MoneyResponse transitionCost,
            MoneyResponse estimatedMonthlyRecovery, MoneyResponse estimatedAnnualizedRecovery,
            int confidence, String risk, String policyVersion, List<String> assumptionEvidenceIds
    ) {
        public RoiResponse {
            assumptionEvidenceIds = List.copyOf(assumptionEvidenceIds);
        }
    }
}
