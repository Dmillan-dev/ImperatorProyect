package imperator.ports.out;

import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;

import java.util.List;
import java.util.Optional;

public interface EvidenceRepository {
    void save(Evidence evidence);

    Optional<Evidence> findById(EvidenceId id);

    List<Evidence> findByCorrelationKey(String correlationKey);
}

