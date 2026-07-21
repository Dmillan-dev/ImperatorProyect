package imperator.domain.shared;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        Objects.requireNonNull(value, "UserId value is required");
    }

    public static UserId fromString(String value) {
        return new UserId(UUID.fromString(Objects.requireNonNull(value, "UserId value is required")));
    }
}

