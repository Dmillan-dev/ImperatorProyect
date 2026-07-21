package imperator.domain.shared;

import java.util.Objects;

public record Duration(java.time.Duration value) {
    public Duration {
        Objects.requireNonNull(value, "Duration value is required");

        if (value.isNegative()) {
            throw new IllegalArgumentException("Duration must not be negative");
        }
    }

    public static Duration ofSeconds(long seconds) {
        return new Duration(java.time.Duration.ofSeconds(seconds));
    }
}

