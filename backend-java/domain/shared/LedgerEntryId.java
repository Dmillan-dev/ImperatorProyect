package imperator.domain.shared;

import java.util.Objects;
import java.util.UUID;

public record LedgerEntryId(UUID value) {
    public LedgerEntryId {
        Objects.requireNonNull(value, "LedgerEntryId value is required");
    }

    public static LedgerEntryId fromString(String value) {
        return new LedgerEntryId(UUID.fromString(Objects.requireNonNull(value, "LedgerEntryId value is required")));
    }
}

