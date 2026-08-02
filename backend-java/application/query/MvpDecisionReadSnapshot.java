package imperator.application.query;

import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;

import java.util.List;
import java.util.Optional;

public record MvpDecisionReadSnapshot(
        Decision decision,
        List<Evidence> evidence,
        Optional<Recommendation> recommendation,
        List<LedgerEntry> ledgerEntries
) {
    public MvpDecisionReadSnapshot {
        evidence = List.copyOf(evidence);
        recommendation = recommendation == null ? Optional.empty() : recommendation;
        ledgerEntries = List.copyOf(ledgerEntries);
    }
}
