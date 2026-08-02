package imperator.ports.out;

import imperator.application.query.MvpDecisionReadSnapshot;
import imperator.application.query.PageRequest;
import imperator.application.query.PageResult;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.RecommendationId;

import java.util.Optional;

/** The single read-only output boundary authorized for the frozen MVP routes. */
public interface MvpReadModelQueryPort {
    PageResult<Decision> findDecisions(PageRequest pageRequest);

    Optional<MvpDecisionReadSnapshot> findDecisionSnapshot(DecisionId decisionId);

    Optional<Recommendation> findRecommendation(RecommendationId recommendationId);

    PageResult<LedgerEntry> findLedgerEntries(PageRequest pageRequest);
}
