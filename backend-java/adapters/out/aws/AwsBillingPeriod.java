package imperator.adapters.out.aws;

import imperator.ports.out.EvidenceSourceRequest;

import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Optional;

record AwsBillingPeriod(
        YearMonth month,
        Instant startInclusive,
        Instant endExclusive,
        Instant observationTimestamp
) {
    private static final LocalTime LAST_PERSISTABLE_TIME = LocalTime.of(23, 59, 59, 999_999_000);

    static Optional<AwsBillingPeriod> latestComplete(EvidenceSourceRequest request) {
        if (!request.fromInclusive().isBefore(request.untilExclusive())) {
            return Optional.empty();
        }

        YearMonth candidate = YearMonth.from(request.untilExclusive()
                .minusNanos(1)
                .atZone(ZoneOffset.UTC));
        Instant candidateEnd = startOf(candidate.plusMonths(1));
        if (candidateEnd.isAfter(request.untilExclusive())) {
            candidate = candidate.minusMonths(1);
            candidateEnd = startOf(candidate.plusMonths(1));
        }
        Instant candidateStart = startOf(candidate);
        if (candidateStart.isBefore(request.fromInclusive())) {
            return Optional.empty();
        }
        Instant observation = candidate.atEndOfMonth()
                .atTime(LAST_PERSISTABLE_TIME)
                .toInstant(ZoneOffset.UTC);
        return Optional.of(new AwsBillingPeriod(
                candidate,
                candidateStart,
                candidateEnd,
                observation
        ));
    }

    String label() {
        return month.toString();
    }

    private static Instant startOf(YearMonth month) {
        return month.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
