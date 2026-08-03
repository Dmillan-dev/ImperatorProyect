package imperator.application.synchronizeevidence;

import java.time.Instant;
import java.util.UUID;

public record SynchronizeEvidenceCommand(
        Instant fromInclusive,
        Instant untilExclusive,
        UUID correlationId
) {
}
