package imperator.api.observability;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ImperatorMeterContractFilter implements MeterFilter {
    private static final Map<String, Set<String>> ALLOWED_TAGS = Map.of(
            "imperator.evidence.import.lines", Set.of("outcome"),
            "imperator.connector.sync", Set.of("source", "outcome"),
            "imperator.decision.composition", Set.of("outcome"),
            "imperator.ledger.command", Set.of("command", "outcome"),
            "imperator.business.value.projection", Set.of("outcome"),
            "imperator.security.authentication.failure", Set.of("reason"),
            "imperator.security.authorization.denial", Set.of("role", "route_id"),
            "imperator.database.operation.failure", Set.of("operation")
    );

    @Override
    public MeterFilterReply accept(Meter.Id id) {
        if (!id.getName().startsWith("imperator.")) {
            return MeterFilterReply.NEUTRAL;
        }
        Set<String> allowed = ALLOWED_TAGS.get(id.getName());
        if (allowed == null) {
            return MeterFilterReply.DENY;
        }
        Set<String> actual = id.getTags().stream()
                .map(tag -> tag.getKey())
                .collect(Collectors.toUnmodifiableSet());
        return allowed.equals(actual) ? MeterFilterReply.NEUTRAL : MeterFilterReply.DENY;
    }
}
