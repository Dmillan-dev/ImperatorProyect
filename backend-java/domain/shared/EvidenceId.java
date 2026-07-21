package imperator.domain.shared;

import java.util.Objects;
import java.util.UUID;

public record EvidenceId(UUID value) {
    public EvidenceId {
        Objects.requireNonNull(value, "EvidenceId value is required");
    }

    public static EvidenceId fromString(String value) {
        return new EvidenceId(UUID.fromString(Objects.requireNonNull(value, "EvidenceId value is required")));
    }
}

