package imperator.domain.decision;

import imperator.domain.shared.Currency;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class DrcAoa001ResultValidationPolicyTest {
    private final DrcAoa001ResultValidationPolicy policy = new DrcAoa001ResultValidationPolicy();

    @Test
    void calculatesAnnualizedRealizedRecoveryAndVarianceInEur() {
        DrcAoa001ResultValidationPolicy.ValidationResult result = policy.validate(
                eur("24000.00"),
                eur("4000.00"),
                eur("100.00"),
                eur("19440.00")
        );

        assertEquals(new BigDecimal("19900.00"), result.realizedRecovery().value().amount());
        assertEquals(Currency.EUR, result.realizedRecovery().value().currency());
        assertEquals(new BigDecimal("460.00"), result.variance());
    }

    @Test
    void preservesNegativeVarianceWithoutCreatingNegativeMoney() {
        DrcAoa001ResultValidationPolicy.ValidationResult result = policy.validate(
                eur("18000.00"),
                eur("3000.00"),
                eur("0.00"),
                eur("19440.00")
        );

        assertEquals(new BigDecimal("15000.00"), result.realizedRecovery().value().amount());
        assertEquals(new BigDecimal("-4440.00"), result.variance());
    }

    @Test
    void rejectsNegativeRealizedRecovery() {
        assertThrows(
                IllegalArgumentException.class,
                () -> policy.validate(eur("100.00"), eur("200.00"), eur("0.00"), eur("19440.00"))
        );
    }

    @Test
    void rejectsNonEurInputs() {
        ROIAmount usd = new ROIAmount(new Money(new BigDecimal("24000.00"), new Currency("USD")));

        assertThrows(
                IllegalArgumentException.class,
                () -> policy.validate(usd, eur("4000.00"), eur("0.00"), eur("19440.00"))
        );
    }

    private ROIAmount eur(String value) {
        return new ROIAmount(Money.eur(new BigDecimal(value)));
    }
}
