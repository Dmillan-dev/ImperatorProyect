package imperator.domain.shared;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public record DecisionStatus(String value) {
    private static final Set<String> ALLOWED_VALUES = Set.of(
            "CREATED",
            "UNDER_REVIEW",
            "APPROVED",
            "REJECTED",
            "DEFERRED"
    );

    public static final DecisionStatus CREATED = new DecisionStatus("CREATED");
    public static final DecisionStatus UNDER_REVIEW = new DecisionStatus("UNDER_REVIEW");
    public static final DecisionStatus APPROVED = new DecisionStatus("APPROVED");
    public static final DecisionStatus REJECTED = new DecisionStatus("REJECTED");
    public static final DecisionStatus DEFERRED = new DecisionStatus("DEFERRED");

    public DecisionStatus {
        Objects.requireNonNull(value, "DecisionStatus value is required");
        value = value.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_VALUES.contains(value)) {
            throw new IllegalArgumentException("Unsupported decision status");
        }
    }
}

