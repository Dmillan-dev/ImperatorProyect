package imperator.application.query;

import imperator.domain.shared.DecisionId;

public record GetDecisionEvidenceQuery(DecisionId decisionId, PageRequest pageRequest) {
}
