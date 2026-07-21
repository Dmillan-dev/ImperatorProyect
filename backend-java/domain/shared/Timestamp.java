package imperator.domain.shared;

import java.time.Instant;
import java.util.Objects;

public record Timestamp(Instant value) {
    public Timestamp {
        Objects.requireNonNull(value, "Timestamp value is required");
    }

    public static Timestamp fromIso8601(String value) {
        return new Timestamp(Instant.parse(Objects.requireNonNull(value, "Timestamp value is required")));
    }
}

