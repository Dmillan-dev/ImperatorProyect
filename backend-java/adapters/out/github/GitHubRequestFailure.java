package imperator.adapters.out.github;

import imperator.ports.out.EvidenceSourceOutcome;

import java.time.Instant;
import java.util.Optional;

final class GitHubRequestFailure extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final EvidenceSourceOutcome outcome;
    private final String code;
    private final Instant retryAt;

    GitHubRequestFailure(
            EvidenceSourceOutcome outcome,
            String code,
            Optional<Instant> retryAt
    ) {
        super(code);
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
