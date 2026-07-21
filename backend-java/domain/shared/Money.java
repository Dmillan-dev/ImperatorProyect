package imperator.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Money amount is required");
        Objects.requireNonNull(currency, "Money currency is required");

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money amount must not be negative");
        }

        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static Money eur(BigDecimal amount) {
        return new Money(amount, Currency.EUR);
    }
}

