package imperator.ports.out;

import imperator.domain.decision.Decision;
import imperator.domain.shared.DecisionId;

import java.util.Optional;

public interface DecisionRepository {
    void save(Decision decision);

    Decision createIfAbsent(Decision decision);

    Optional<Decision> findById(DecisionId id);

    boolean existsById(DecisionId id);
}
