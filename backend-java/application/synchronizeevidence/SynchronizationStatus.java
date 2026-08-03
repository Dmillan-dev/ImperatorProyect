package imperator.application.synchronizeevidence;

import imperator.ports.out.EvidenceSourceOutcome;

public enum SynchronizationStatus {
    COMPLETE,
    PARTIAL,
    DISABLED,
    MISCONFIGURED,
    UNAUTHORIZED,
    FORBIDDEN,
    RATE_LIMITED,
    REPOSITORY_UNAVAILABLE,
    API_VERSION_UNSUPPORTED,
    DEGRADED,
    INCOMPLETE,
    NO_MATCH,
    AMBIGUOUS_CORRELATION,
    SOURCE_IDENTITY_CONFLICT,
    SYNC_ALREADY_RUNNING;

    static SynchronizationStatus from(EvidenceSourceOutcome outcome) {
        return valueOf(outcome.name());
    }
}
