package imperator.application.query;

import imperator.domain.shared.DecisionId;

public record GetDecisionTimelineQuery(DecisionId decisionId, PageRequest pageRequest) {
}
