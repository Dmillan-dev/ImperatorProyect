package imperator.ports.in;

import imperator.application.query.DecisionSummary;
import imperator.application.query.ListDecisionsQuery;
import imperator.application.query.PageResult;

public interface ListDecisionsInputPort {
    PageResult<DecisionSummary> listDecisions(ListDecisionsQuery query);
}
