package imperator.ports.out;

import imperator.domain.ledger.LedgerEntry;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.LedgerEntryId;

import java.util.List;
import java.util.Optional;

public interface LedgerRepository {
    void append(LedgerEntry entry);

    Optional<LedgerEntry> findById(LedgerEntryId id);

    List<LedgerEntry> findByDecisionId(DecisionId decisionId);
}

