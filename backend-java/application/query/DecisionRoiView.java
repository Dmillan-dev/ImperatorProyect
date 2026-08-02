package imperator.application.query;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;

import java.util.List;

public record DecisionRoiView(
        DecisionId decisionId,
        RecommendationId recommendationId,
        ROIAmount currentMonthlyCost,
        ROIAmount projectedMonthlyCost,
        ROIAmount transitionCost,
        ROIAmount estimatedMonthlyRecovery,
        ROIAmount estimatedAnnualizedRecovery,
        ROIConfidence confidence,
        Severity risk,
        String policyVersion,
        List<EvidenceId> assumptionEvidenceIds
) {
    public DecisionRoiView {
        assumptionEvidenceIds = List.copyOf(assumptionEvidenceIds);
    }
}
