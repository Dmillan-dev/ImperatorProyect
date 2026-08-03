package imperator.application.synchronizeevidence;

import imperator.application.exceptions.ValidationException;
import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.ports.out.EvidenceCandidate;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.EvidenceSourceCapture;
import imperator.ports.out.EvidenceSourceOutcome;
import imperator.ports.out.EvidenceSourcePort;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SynchronizeEvidenceUseCaseTest {
    private static final Instant FROM = Instant.parse("2026-03-01T00:00:00Z");
    private static final Instant UNTIL = Instant.parse("2026-07-01T00:00:00Z");
    private static final UUID CORRELATION_ID = UUID.fromString("f6a27eef-829d-4305-9686-f578ad3a4d76");
    private static final UUID EVIDENCE_UUID = UUID.fromString("d52bd5b0-cce7-5b49-91c2-03fc1a4f93d8");

    @Test
    void importsOnceAndReportsStableReplayAsUnchanged() {
        InMemoryEvidenceRepository repository = new InMemoryEvidenceRepository();
        EvidenceSourcePort source = ignored -> capture(candidate("Merged once"));
        SynchronizeEvidenceUseCase useCase = useCase(source, repository);

        SynchronizeEvidenceResult first = useCase.synchronize(command());
        SynchronizeEvidenceResult replay = useCase.synchronize(command());

        assertEquals(SynchronizationStatus.COMPLETE, first.status());
        assertEquals(1, first.acceptedEvidenceCount());
        assertEquals(0, first.unchangedEvidenceCount());
        assertEquals(SynchronizationStatus.COMPLETE, replay.status());
        assertEquals(0, replay.acceptedEvidenceCount());
        assertEquals(1, replay.unchangedEvidenceCount());
        assertEquals(1, repository.size());
        assertEquals(1, repository.saveCalls());
    }

    @Test
    void rejectsSameSourceIdentityWithDifferentNormalizedFactWithoutOverwrite() {
        InMemoryEvidenceRepository repository = new InMemoryEvidenceRepository();
        MutableEvidenceSource source = new MutableEvidenceSource(candidate("Original fact"));
        SynchronizeEvidenceUseCase useCase = useCase(source, repository);
        useCase.synchronize(command());
        source.candidate = candidate("Conflicting fact");

        SynchronizeEvidenceResult conflict = useCase.synchronize(command());

        assertEquals(SynchronizationStatus.SOURCE_IDENTITY_CONFLICT, conflict.status());
        assertEquals("SOURCE_IDENTITY_CONFLICT", conflict.failureCode());
        assertEquals(1, conflict.rejectedEvidenceCount());
        assertEquals("Original fact", repository.findById(new EvidenceId(EVIDENCE_UUID)).orElseThrow().observedFact());
        assertEquals(1, repository.saveCalls());
    }

    @Test
    void preservesExistingEvidenceWhenTheProviderIsUnavailable() {
        InMemoryEvidenceRepository repository = new InMemoryEvidenceRepository();
        MutableEvidenceSource source = new MutableEvidenceSource(candidate("Historical fact"));
        SynchronizeEvidenceUseCase useCase = useCase(source, repository);
        useCase.synchronize(command());
        source.capture = new EvidenceSourceCapture(
                EvidenceSourceOutcome.DEGRADED,
                "acme/imperator-demo",
                "2026-03-10",
                3,
                2,
                0,
                0,
                List.of(),
                List.of(),
                "GITHUB_RETRY_EXHAUSTED",
                Optional.empty()
        );

        SynchronizeEvidenceResult degraded = useCase.synchronize(command());

        assertEquals(SynchronizationStatus.DEGRADED, degraded.status());
        assertEquals(1, repository.size());
        assertEquals(1, repository.saveCalls());
    }

    @Test
    void rejectsInvalidOrOverlongWindowsBeforeCallingTheSource() {
        CountingEvidenceSource source = new CountingEvidenceSource();
        InMemoryEvidenceRepository repository = new InMemoryEvidenceRepository();
        SynchronizeEvidenceUseCase useCase = useCase(source, repository);

        assertThrows(
                ValidationException.class,
                () -> useCase.synchronize(new SynchronizeEvidenceCommand(UNTIL, FROM, CORRELATION_ID))
        );
        assertThrows(
                ValidationException.class,
                () -> useCase.synchronize(new SynchronizeEvidenceCommand(
                        FROM,
                        FROM.plusSeconds(181L * 24 * 60 * 60),
                        CORRELATION_ID
                ))
        );
        assertEquals(0, source.calls);
    }

    @Test
    void rejectsAConcurrentRunInsteadOfQueuingIt() throws Exception {
        BlockingEvidenceSource source = new BlockingEvidenceSource();
        InMemoryEvidenceRepository repository = new InMemoryEvidenceRepository();
        SynchronizeEvidenceUseCase useCase = useCase(source, repository);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<SynchronizeEvidenceResult> first = executor.submit(() -> useCase.synchronize(command()));
            assertTrue(source.entered.await(2, TimeUnit.SECONDS));

            SynchronizeEvidenceResult concurrent = useCase.synchronize(command());
            source.release.countDown();

            assertEquals(SynchronizationStatus.SYNC_ALREADY_RUNNING, concurrent.status());
            assertEquals(SynchronizationStatus.COMPLETE, first.get(2, TimeUnit.SECONDS).status());
        } finally {
            source.release.countDown();
            executor.shutdownNow();
        }
    }

    private static SynchronizeEvidenceUseCase useCase(
            EvidenceSourcePort source,
            InMemoryEvidenceRepository repository
    ) {
        TransactionRunner transactions = new DirectTransactionRunner();
        return new SynchronizeEvidenceUseCase(
                source,
                new ImportEvidenceUseCase(repository, transactions),
                repository
        );
    }

    private static SynchronizeEvidenceCommand command() {
        return new SynchronizeEvidenceCommand(FROM, UNTIL, CORRELATION_ID);
    }

    private static EvidenceSourceCapture capture(EvidenceCandidate candidate) {
        return new EvidenceSourceCapture(
                EvidenceSourceOutcome.COMPLETE,
                "acme/imperator-demo",
                "2026-03-10",
                7,
                0,
                5,
                1,
                List.of(candidate),
                List.of(),
                "",
                Optional.empty()
        );
    }

    private static EvidenceCandidate candidate(String observedFact) {
        return new EvidenceCandidate(
                new EvidenceId(EVIDENCE_UUID),
                "E-GH-001",
                new Timestamp(Instant.parse("2026-04-02T12:00:00Z")),
                "GitHub",
                "code",
                "github:acme/imperator-demo:pull:184",
                "ai-onboarding-assistant",
                "code_change_merged",
                Severity.INFO,
                "platform-engineer",
                "code_deployment",
                observedFact,
                "Establishes implementation lineage",
                "DRC-AOA-001",
                "DRC-AOA-001",
                "CONFIDENTIAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.of("evidence_ref", "E-GH-001", "freshness", "fresh")
        );
    }

    private static final class MutableEvidenceSource implements EvidenceSourcePort {
        private EvidenceCandidate candidate;
        private EvidenceSourceCapture capture;

        private MutableEvidenceSource(EvidenceCandidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public EvidenceSourceCapture capture(imperator.ports.out.EvidenceSourceRequest request) {
            return capture == null ? SynchronizeEvidenceUseCaseTest.capture(candidate) : capture;
        }
    }

    private static final class CountingEvidenceSource implements EvidenceSourcePort {
        private int calls;

        @Override
        public EvidenceSourceCapture capture(imperator.ports.out.EvidenceSourceRequest request) {
            calls++;
            return SynchronizeEvidenceUseCaseTest.capture(candidate("unused"));
        }
    }

    private static final class BlockingEvidenceSource implements EvidenceSourcePort {
        private final CountDownLatch entered = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);

        @Override
        public EvidenceSourceCapture capture(imperator.ports.out.EvidenceSourceRequest request) {
            entered.countDown();
            try {
                if (!release.await(2, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Test synchronization release timed out");
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Test synchronization was interrupted", exception);
            }
            return SynchronizeEvidenceUseCaseTest.capture(candidate("Concurrent fact"));
        }
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

        int size() {
            return evidence.size();
        }

        int saveCalls() {
            return saveCalls;
        }
    }

    private static final class DirectTransactionRunner implements TransactionRunner {
        @Override
        public <T> T execute(Supplier<T> operation) {
            return operation.get();
        }
    }
}
