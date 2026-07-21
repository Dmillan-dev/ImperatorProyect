package imperator.domain.shared;

import java.util.Objects;

public record ROIAmount(Money value) {
    public ROIAmount {
        Objects.requireNonNull(value, "ROIAmount value is required");
    }
}

