package imperator.domain.shared;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public record Severity(String value) {
    private static final Set<String> ALLOWED_VALUES = Set.of("INFO", "LOW", "MEDIUM", "HIGH", "CRITICAL");

    public static final Severity INFO = new Severity("INFO");
    public static final Severity LOW = new Severity("LOW");
    public static final Severity MEDIUM = new Severity("MEDIUM");
    public static final Severity HIGH = new Severity("HIGH");
    public static final Severity CRITICAL = new Severity("CRITICAL");

    public Severity {
        Objects.requireNonNull(value, "Severity value is required");
        value = value.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_VALUES.contains(value)) {
            throw new IllegalArgumentException("Severity must be INFO, LOW, MEDIUM, HIGH or CRITICAL");
        }
    }
}

