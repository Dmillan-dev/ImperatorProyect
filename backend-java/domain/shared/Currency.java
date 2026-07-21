package imperator.domain.shared;

import java.util.Locale;
import java.util.Objects;

public record Currency(String code) {
    public static final Currency EUR = new Currency("EUR");

    public Currency {
        Objects.requireNonNull(code, "Currency code is required");
        code = code.trim().toUpperCase(Locale.ROOT);

        if (!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must be an ISO-style 3-letter code");
        }
    }
}

