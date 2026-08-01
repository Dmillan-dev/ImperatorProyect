package imperator.domain.decision;

import imperator.domain.shared.Currency;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Deterministic realized-recovery policy frozen by D083. */
public final class DrcAoa001ResultValidationPolicy {
    private static final int MONEY_SCALE = 2;

    public ValidationResult validate(
            ROIAmount annualizedBaselineCost,
            ROIAmount annualizedPostActionCost,
            ROIAmount actualTransitionCost,
            ROIAmount estimatedSavings
    ) {
        BigDecimal baseline = eurAmount(annualizedBaselineCost, "Annualized baseline cost");
        BigDecimal postAction = eurAmount(annualizedPostActionCost, "Annualized post-action cost");
        BigDecimal transition = eurAmount(actualTransitionCost, "Actual transition cost");
        BigDecimal estimate = eurAmount(estimatedSavings, "Estimated savings");

        BigDecimal realized = normalized(baseline.subtract(postAction).subtract(transition));
        if (realized.signum() < 0) {
            throw new IllegalArgumentException("Annualized realized recovery must not be negative");
        }

        BigDecimal variance = normalized(realized.subtract(estimate));
        return new ValidationResult(new ROIAmount(Money.eur(realized)), variance);
    }

    private BigDecimal eurAmount(ROIAmount amount, String fieldName) {
        ROIAmount value = Objects.requireNonNull(amount, fieldName + " is required");
        if (!Currency.EUR.equals(value.value().currency())) {
            throw new IllegalArgumentException(fieldName + " must use EUR");
        }
        return normalized(value.value().amount());
    }

    private BigDecimal normalized(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_EVEN);
    }

    public record ValidationResult(ROIAmount realizedRecovery, BigDecimal variance) {
        public ValidationResult {
            realizedRecovery = Objects.requireNonNull(realizedRecovery, "Realized recovery is required");
            variance = Objects.requireNonNull(variance, "Realized recovery variance is required")
                    .setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
        }
    }
}
