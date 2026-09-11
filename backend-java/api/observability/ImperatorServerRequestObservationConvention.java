package imperator.api.observability;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;

public final class ImperatorServerRequestObservationConvention
        extends DefaultServerRequestObservationConvention {

    @Override
    public KeyValues getLowCardinalityKeyValues(ServerRequestObservationContext context) {
        return KeyValues.of(
                method(context),
                KeyValue.of(
                        "uri",
                        ImperatorRouteCatalog.metricTemplate(
                                context.getCarrier().getMethod(),
                                context.getPathPattern()
                        )
                ),
                status(context),
                outcome(context)
        );
    }

    @Override
    public KeyValues getHighCardinalityKeyValues(ServerRequestObservationContext context) {
        return KeyValues.empty();
    }
}
