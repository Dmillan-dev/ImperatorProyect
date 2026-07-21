package imperator.domain.shared;

import java.util.Objects;
import java.util.UUID;

public record RecommendationId(UUID value) {
    public RecommendationId {
        Objects.requireNonNull(value, "RecommendationId value is required");
    }

    public static RecommendationId fromString(String value) {
        return new RecommendationId(UUID.fromString(Objects.requireNonNull(value, "RecommendationId value is required")));
    }
}

