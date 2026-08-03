package imperator.ports.out;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record EvidenceSourceRequest(
        Instant fromInclusive,
        Instant untilExclusive,
        UUID correlationId
) {
    public EvidenceSourceRequest {
        fromInclusive = Objects.requireNonNull(fromInclusive, "Source window start is required");
        untilExclusive = Objects.requireNonNull(untilExclusive, "Source window end is required");
        correlationId = Objects.requireNonNull(correlationId, "Source correlation ID is required");
    }
}
