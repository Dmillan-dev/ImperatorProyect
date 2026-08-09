package imperator.adapters.out.aws;

import java.math.BigDecimal;

record AwsUtilizationObservation(
        int lambdaCount,
        BigDecimal invocationSum,
        BigDecimal errorSum,
        BigDecimal durationMillisecondsSum
) {
}
