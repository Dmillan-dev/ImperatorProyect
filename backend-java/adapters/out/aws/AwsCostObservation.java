package imperator.adapters.out.aws;

import java.math.BigDecimal;

record AwsCostObservation(
        BigDecimal sourceAmount,
        BigDecimal monthlyCost,
        String currency
) {
}
