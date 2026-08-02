package imperator.ports.in;

import imperator.application.query.DecisionTimelineItem;
import imperator.application.query.GetDecisionTimelineQuery;
import imperator.application.query.PageResult;

public interface GetDecisionTimelineInputPort {
    PageResult<DecisionTimelineItem> getDecisionTimeline(GetDecisionTimelineQuery query);
}
