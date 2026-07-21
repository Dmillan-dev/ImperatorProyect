package imperator.ports.in;

import imperator.application.createdecision.CreateDecisionCommand;
import imperator.application.createdecision.CreateDecisionResult;

public interface CreateDecisionInputPort {
    CreateDecisionResult createDecision(CreateDecisionCommand command);
}
