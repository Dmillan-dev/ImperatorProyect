package imperator.adapters.out.aws;

import imperator.ports.out.EvidenceSourceOutcome;

import java.time.Instant;
import java.util.Optional;

final class AwsRequestFailure extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final EvidenceSourceOutcome outcome;
    private final String code;
    private final Instant retryAt;

    AwsRequestFailure(EvidenceSourceOutcome outcome, String code) {
        this(outcome, code, Optional.empty());
    }

    AwsRequestFailure(
            EvidenceSourceOutcome outcome,
            String code,
            Optional<Instant> retryAt
    ) {
        super(code, null, false, false);
        this.outcome = outcome;
        this.code = code;
        this.retryAt = retryAt.orElse(null);
    }

    EvidenceSourceOutcome outcome() {
        return outcome;
    }

    String code() {
        return code;
    }

    Optional<Instant> retryAt() {
        return Optional.ofNullable(retryAt);
    }
}
