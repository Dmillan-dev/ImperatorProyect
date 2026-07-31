package imperator.application.importevidence;

import imperator.application.exceptions.DuplicateEvidenceException;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportEvidenceUseCaseTest {

    @Test
    void persistsOneEvidenceInsideOneTransaction() {
        InMemoryEvidenceRepository repository = new InMemoryEvidenceRepository();
        CountingTransactionRunner transactions = new CountingTransactionRunner();
        ImportEvidenceUseCase useCase = new ImportEvidenceUseCase(repository, transactions);
        ImportEvidenceCommand command = command(UUID.randomUUID());

        ImportEvidenceResult result = useCase.importEvidence(command);

        assertTrue(repository.existsById(result.evidenceId()));
        assertEquals(1, transactions.executions());
        assertEquals(1, repository.saveCalls());
    }

    @Test
    void rejectsAStableDuplicateIdentifierWithoutSavingAgain() {
        InMemoryEvidenceRepository repository = new InMemoryEvidenceRepository();
        CountingTransactionRunner transactions = new CountingTransactionRunner();
        ImportEvidenceUseCase useCase = new ImportEvidenceUseCase(repository, transactions);
        ImportEvidenceCommand command = command(UUID.randomUUID());
        useCase.importEvidence(command);

        assertThrows(
                DuplicateEvidenceException.class,
                () -> useCase.importEvidence(command)
        );
        assertEquals(2, transactions.executions());
        assertEquals(1, repository.saveCalls());
    }

    private static ImportEvidenceCommand command(UUID id) {
        return new ImportEvidenceCommand(
                new EvidenceId(id),
                new Timestamp(Instant.parse("2026-06-30T23:59:59Z")),
                "AWS",
                "cloud_cost",
                "cost-export/2026-06",
                "onboarding-assistant",
                "cloud_cost_observed",
                Severity.INFO,
                "aws-cost-export",
                "cloud_cost",
                "AWS monthly cost is EUR410",
                "Provides infrastructure cost input",
                "DRC-AOA-001",
                "CONFIDENTIAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.of("evidence_ref", "E-AWS-001")
        );
    }

    private static final class InMemoryEvidenceRepository implements EvidenceRepository {
        private final Map<EvidenceId, Evidence> evidence = new LinkedHashMap<>();
        private int saveCalls;

        @Override
        public void save(Evidence item) {
            saveCalls++;
            evidence.put(item.id(), item);
        }

        @Override
        public Optional<Evidence> findById(EvidenceId id) {
            return Optional.ofNullable(evidence.get(id));
        }

        @Override
        public boolean existsById(EvidenceId id) {
            return evidence.containsKey(id);
        }

        int saveCalls() {
            return saveCalls;
        }
    }

    private static final class CountingTransactionRunner implements TransactionRunner {
        private int executions;

        @Override
        public <T> T execute(Supplier<T> operation) {
            executions++;
            return operation.get();
        }

        int executions() {
            return executions;
        }
    }
}
