package imperator.ports.out;

import imperator.domain.shared.EvidenceId;

import java.util.Objects;
import java.util.Set;

public record RecommendationExplanationEvidence(
        EvidenceId evidenceId,
        String reference,
        String observedFact,
        String businessMeaning,
        String sensitivity,
        String confidence
) {
    private static final int MAX_TEXT_LENGTH = 500;
    private static final Set<String> SENSITIVITIES = Set.of(
            "PUBLIC", "INTERNAL", "CONFIDENTIAL", "RESTRICTED"
    );
    private static final Set<String> CONFIDENCE_VALUES = Set.of("LOW", "MEDIUM", "HIGH");

    public RecommendationExplanationEvidence {
        Objects.requireNonNull(evidenceId, "Explanation Evidence id is required");
        reference = requireText(reference, "Explanation Evidence reference", 80);
        observedFact = requireText(observedFact, "Explanation observed fact", MAX_TEXT_LENGTH);
        businessMeaning = requireText(businessMeaning, "Explanation business meaning", MAX_TEXT_LENGTH);
        sensitivity = requireControlled(
                sensitivity, "Explanation Evidence sensitivity", SENSITIVITIES
        );
        confidence = requireControlled(
                confidence, "Explanation Evidence confidence", CONFIDENCE_VALUES
        );
    }

    private static String requireText(String value, String field, int maxLength) {
        Objects.requireNonNull(value, field + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " must contain 1 to " + maxLength + " characters");
        }
        return normalized;
    }

    private static String requireControlled(String value, String field, Set<String> allowed) {
        String normalized = requireText(value, field, 20).toUpperCase(java.util.Locale.ROOT);
        if (!allowed.contains(normalized)) {
            throw new IllegalArgumentException(field + " has an unsupported value");
        }
        return normalized;
    }
}
