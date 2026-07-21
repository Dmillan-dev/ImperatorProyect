package imperator.domain.shared;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public record RecommendationType(String value) {
    private static final Set<String> ALLOWED_VALUES = Set.of(
            "MODEL_DOWNGRADE",
            "MODEL_CHANGE",
            "RIGHTSIZE_INSTANCE",
            "REMOVE_UNUSED_RESOURCE",
            "OPTIMIZE_PIPELINE"
    );

    public static final RecommendationType MODEL_DOWNGRADE = new RecommendationType("MODEL_DOWNGRADE");
    public static final RecommendationType MODEL_CHANGE = new RecommendationType("MODEL_CHANGE");
    public static final RecommendationType RIGHTSIZE_INSTANCE = new RecommendationType("RIGHTSIZE_INSTANCE");
    public static final RecommendationType REMOVE_UNUSED_RESOURCE = new RecommendationType("REMOVE_UNUSED_RESOURCE");
    public static final RecommendationType OPTIMIZE_PIPELINE = new RecommendationType("OPTIMIZE_PIPELINE");

    public RecommendationType {
        Objects.requireNonNull(value, "RecommendationType value is required");
        value = value.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_VALUES.contains(value)) {
            throw new IllegalArgumentException("Unsupported recommendation type");
        }
    }
}

