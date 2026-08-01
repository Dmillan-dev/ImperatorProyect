package imperator.ports.in;

import imperator.application.businessvalue.BusinessValueProjection;
import imperator.domain.shared.DecisionId;

import java.util.Optional;

/** Inbound query contract for the non-persisted Business Value projection. */
public interface ProjectBusinessValueInputPort {
    BusinessValueProjection projectBusinessValue(DecisionId decisionId, Optional<String> explanation);
}
