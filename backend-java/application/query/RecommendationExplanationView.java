package imperator.application.query;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Timestamp;
import imperator.ports.out.ExplanationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record RecommendationExplanationView(
        UUID explanationId,
        ExplanationStatus status,
        Optional<String> text,
        String provider,
        String modelId,
        String promptVersion,
        Timestamp requestedAt,
        Timestamp completedAt,
        Optional<Integer> inputTokens,
        Optional<Integer> outputTokens,
        Optional<Long> latencyMillis,
        Optional<String> failureCode,
        List<EvidenceId> evidenceIds,
        List<String> assumptionIds
) {
    public RecommendationExplanationView {
        text = text.map(String::trim);
        failureCode = failureCode.map(String::trim);
        evidenceIds = List.copyOf(evidenceIds);
        assumptionIds = List.copyOf(assumptionIds);
    }
}
