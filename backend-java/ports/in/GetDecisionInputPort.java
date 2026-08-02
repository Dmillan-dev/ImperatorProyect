package imperator.ports.in;

import imperator.application.query.DecisionDetail;
import imperator.application.query.GetDecisionQuery;

public interface GetDecisionInputPort {
    DecisionDetail getDecision(GetDecisionQuery query);
}
