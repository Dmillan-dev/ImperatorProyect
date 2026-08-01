package imperator.application.businessvalue;

import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Immutable, non-persisted Application read model for one validated business outcome. */
public record BusinessValueProjection(
        String caseId,
        String correlationKey,
        DecisionId decisionId,
        String decisionTitle,
        String businessNeed,
        DecisionStatus decisionStatus,
        Timestamp decisionCreatedAt,
        RecommendationId recommendationId,
        RecommendationType recommendationType,
        String recommendedAction,
        String deterministicReason,
        Optional<String> explanation,
        Timestamp recommendationCreatedAt,
        ROIAmount estimatedSavings,
        ROIAmount realizedSavings,
        BigDecimal variance,
        ROIAmount annualizedBaselineCost,
        ROIAmount annualizedPostActionCost,
        ROIAmount actualTransitionCost,
        ROIConfidence confidence,
        Severity risk,
        String policyVersion,
        List<EvidenceId> evidenceIds,
        List<String> assumptionIds,
        LedgerEntryId approvalEntryId,
        LedgerEntryId implementationEntryId,
        LedgerEntryId validationEntryId,
        List<LedgerFact> ledgerHistory
) {
    public BusinessValueProjection {
        caseId = requireText(caseId, "Case id");
        correlationKey = requireText(correlationKey, "Correlation key");
        Objects.requireNonNull(decisionId, "Decision id is required");
        decisionTitle = requireText(decisionTitle, "Decision title");
        businessNeed = requireText(businessNeed, "Business need");
        Objects.requireNonNull(decisionStatus, "Decision status is required");
        Objects.requireNonNull(decisionCreatedAt, "Decision creation timestamp is required");
        Objects.requireNonNull(recommendationId, "Recommendation id is required");
        Objects.requireNonNull(recommendationType, "Recommendation type is required");
        recommendedAction = requireText(recommendedAction, "Recommended action");
        deterministicReason = requireText(deterministicReason, "Deterministic reason");
        explanation = explanation == null ? Optional.empty() : explanation.map(String::trim).filter(value -> !value.isEmpty());
        Objects.requireNonNull(recommendationCreatedAt, "Recommendation creation timestamp is required");
        Objects.requireNonNull(estimatedSavings, "Estimated savings are required");
        Objects.requireNonNull(realizedSavings, "Realized savings are required");
        variance = Objects.requireNonNull(variance, "Savings variance is required");
        Objects.requireNonNull(annualizedBaselineCost, "Annualized baseline cost is required");
        Objects.requireNonNull(annualizedPostActionCost, "Annualized post-action cost is required");
        Objects.requireNonNull(actualTransitionCost, "Actual transition cost is required");
        Objects.requireNonNull(confidence, "Confidence is required");
        Objects.requireNonNull(risk, "Risk is required");
        policyVersion = requireText(policyVersion, "Policy version");
        evidenceIds = List.copyOf(Objects.requireNonNull(evidenceIds, "Evidence ids are required"));
        assumptionIds = List.copyOf(Objects.requireNonNull(assumptionIds, "Assumption ids are required"));
        Objects.requireNonNull(approvalEntryId, "Approval entry id is required");
        Objects.requireNonNull(implementationEntryId, "Implementation entry id is required");
        Objects.requireNonNull(validationEntryId, "Validation entry id is required");
        ledgerHistory = List.copyOf(Objects.requireNonNull(ledgerHistory, "Ledger history is required"));
    }

    /** One ordered, immutable audit fact exposed by the projection. */
    public record LedgerFact(
            LedgerEntryId id,
            LedgerEntryType type,
            UserId actorId,
            String actorRole,
            Timestamp occurredAt,
            Optional<LedgerEntryId> previousEntryId,
            List<EvidenceId> evidenceIds
    ) {
        public LedgerFact {
            Objects.requireNonNull(id, "Ledger fact id is required");
            Objects.requireNonNull(type, "Ledger fact type is required");
            Objects.requireNonNull(actorId, "Ledger actor id is required");
            actorRole = requireText(actorRole, "Ledger actor role");
            Objects.requireNonNull(occurredAt, "Ledger occurrence timestamp is required");
            previousEntryId = previousEntryId == null ? Optional.empty() : previousEntryId;
            evidenceIds = List.copyOf(Objects.requireNonNull(evidenceIds, "Ledger Evidence ids are required"));
        }
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
