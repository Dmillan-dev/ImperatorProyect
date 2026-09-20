package imperator.ports.out;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record RecommendationExplanationRecord(
        UUID explanationId,
        RecommendationId recommendationId,
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
    public RecommendationExplanationRecord {
        Objects.requireNonNull(explanationId, "Explanation id is required");
        Objects.requireNonNull(recommendationId, "Explanation Recommendation id is required");
        Objects.requireNonNull(status, "Explanation status is required");
        text = Objects.requireNonNull(text, "Explanation text is required")
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(value -> requireText(value, "Explanation text", 6000));
        provider = requireText(provider, "Explanation provider", 80);
        modelId = requireText(modelId, "Explanation model id", 300);
        promptVersion = requireText(promptVersion, "Explanation prompt version", 80);
        Objects.requireNonNull(requestedAt, "Explanation requested timestamp is required");
        Objects.requireNonNull(completedAt, "Explanation completed timestamp is required");
        if (completedAt.value().isBefore(requestedAt.value())) {
            throw new IllegalArgumentException("Explanation completion cannot precede request");
        }
        inputTokens = nonNegative(inputTokens, "Explanation input tokens");
        outputTokens = nonNegative(outputTokens, "Explanation output tokens");
        latencyMillis = nonNegativeLong(latencyMillis, "Explanation latency");
        failureCode = Objects.requireNonNull(failureCode, "Explanation failure code is required")
                .map(value -> requireText(value, "Explanation failure code", 80));
        evidenceIds = List.copyOf(Objects.requireNonNull(evidenceIds, "Explanation Evidence ids are required"));
        if (evidenceIds.isEmpty()
                || evidenceIds.stream().anyMatch(Objects::isNull)
                || Set.copyOf(evidenceIds).size() != evidenceIds.size()) {
            throw new IllegalArgumentException("Explanation Evidence ids must be non-empty, non-null and unique");
        }
        assumptionIds = Objects.requireNonNull(assumptionIds, "Explanation assumption ids are required").stream()
                .map(value -> requireText(value, "Explanation assumption id", 80))
                .toList();
        if (assumptionIds.isEmpty() || Set.copyOf(assumptionIds).size() != assumptionIds.size()) {
            throw new IllegalArgumentException("Explanation assumption ids must be non-empty and unique");
        }
        if (status == ExplanationStatus.GENERATED && text.isEmpty()) {
            throw new IllegalArgumentException("Generated explanation text is required");
        }
        if (status != ExplanationStatus.GENERATED && text.isPresent()) {
            throw new IllegalArgumentException("Non-generated explanation cannot contain text");
        }
    }

    public boolean matches(String candidateProvider, String candidateModel, String candidatePromptVersion) {
        return provider.equals(candidateProvider)
                && modelId.equals(candidateModel)
                && promptVersion.equals(candidatePromptVersion);
    }

    private static Optional<Integer> nonNegative(Optional<Integer> value, String field) {
        Optional<Integer> normalized = Objects.requireNonNull(value, field + " is required");
        normalized.ifPresent(item -> {
            if (item < 0) {
                throw new IllegalArgumentException(field + " cannot be negative");
            }
        });
        return normalized;
    }

    private static Optional<Long> nonNegativeLong(Optional<Long> value, String field) {
        Optional<Long> normalized = Objects.requireNonNull(value, field + " is required");
        normalized.ifPresent(item -> {
            if (item < 0) {
                throw new IllegalArgumentException(field + " cannot be negative");
            }
        });
        return normalized;
    }

    private static String requireText(String value, String field, int maxLength) {
        Objects.requireNonNull(value, field + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " must contain 1 to " + maxLength + " characters");
        }
        return normalized;
    }
}
