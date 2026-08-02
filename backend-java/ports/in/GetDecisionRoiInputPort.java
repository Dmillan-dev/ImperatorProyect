package imperator.ports.in;

import imperator.application.query.DecisionRoiView;
import imperator.application.query.GetDecisionRoiQuery;

public interface GetDecisionRoiInputPort {
    DecisionRoiView getDecisionRoi(GetDecisionRoiQuery query);
}
