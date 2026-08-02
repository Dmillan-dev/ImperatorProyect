package imperator.application.query;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.List;

public record RecommendationView(
        RecommendationId recommendationId,
        DecisionId decisionId,
        RecommendationType type,
        String suggestedAction,
        String deterministicReason,
        ROIAmount estimatedSavings,
        ROIConfidence confidence,
        Severity risk,
        UserId ownerId,
        UserId requiredApproverId,
        Timestamp createdAt,
        List<EvidenceId> evidenceIds
) {
    public RecommendationView {
        evidenceIds = List.copyOf(evidenceIds);
    }
}
