package imperator.ports.in;

import imperator.application.reviewdecision.ReviewDecisionCommand;
import imperator.application.reviewdecision.ReviewDecisionResult;

public interface ReviewDecisionInputPort {
    ReviewDecisionResult reviewDecision(ReviewDecisionCommand command);
}
