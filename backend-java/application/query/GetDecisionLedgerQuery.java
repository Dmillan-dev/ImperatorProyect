package imperator.application.query;

import imperator.domain.shared.DecisionId;

public record GetDecisionLedgerQuery(DecisionId decisionId, PageRequest pageRequest) {
}
