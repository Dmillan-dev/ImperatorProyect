package imperator.ports.out;

import imperator.domain.decision.Decision;
import imperator.domain.shared.DecisionId;

import java.util.Optional;

public interface DecisionRepository {
    void save(Decision decision);

    Optional<Decision> findById(DecisionId id);

    Optional<Decision> findByCaseId(String caseId);
}

