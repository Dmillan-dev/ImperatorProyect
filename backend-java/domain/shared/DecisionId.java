package imperator.domain.shared;

import java.util.Objects;
import java.util.UUID;

public record DecisionId(UUID value) {
    public DecisionId {
        Objects.requireNonNull(value, "DecisionId value is required");
    }

    public static DecisionId fromString(String value) {
        return new DecisionId(UUID.fromString(Objects.requireNonNull(value, "DecisionId value is required")));
    }
}

