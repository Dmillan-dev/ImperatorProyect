package imperator.application.createdecision;

import imperator.application.exceptions.DecisionCreationConflictException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.EvidenceTraceabilityViolationException;
import imperator.domain.decision.Decision;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateDecisionUseCaseTest {
    private static final Timestamp CREATED_AT = timestamp("2026-07-01T09:00:00Z");
    private static final EvidenceId EVIDENCE_ID = evidenceId("20000000-0000-4000-8000-000000000001");
    private static final DecisionId DECISION_ID = decisionId("10000000-0000-4000-8000-000000000001");
    private static final UserId OWNER_ID = userId("30000000-0000-4000-8000-000000000001");
    private static final UserId APPROVER_ID = userId("40000000-0000-4000-8000-000000000001");

    @Test
    void createsOneDecisionInTheFrozenInitialState() {
        TestContext context = context(eligibleEvidence(EVIDENCE_ID));

        CreateDecisionResult result = context.useCase().createDecision(command("Evaluate AI model cost"));
        Decision stored = context.decisions().findById(DECISION_ID).orElseThrow();

        assertAll(
                () -> assertEquals(DECISION_ID, result.decisionId()),
                () -> assertEquals("DRC-AOA-001", result.caseId()),
                () -> assertEquals(EVIDENCE_ID, result.originatingEvidenceId()),
                () -> assertEquals(DecisionStatus.CREATED, result.status()),
                () -> assertTrue(result.originatingEvidenceCanSupportApproval()),
                () -> assertFalse(result.originatingEvidenceRequiresReview()),
                () -> assertEquals(DecisionStatus.CREATED, stored.status()),
                () -> assertEquals(CREATED_AT, stored.createdAt()),
                () -> assertEquals(CREATED_AT, stored.updatedAt()),
                () -> assertEquals(1, stored.evidenceIds().size()),
                () -> assertTrue(stored.evidenceIds().contains(EVIDENCE_ID)),
                () -> assertTrue(stored.recommendationId().isEmpty()),
                () -> assertTrue(stored.reviewedBy().isEmpty()),
                () -> assertTrue(stored.reviewedAt().isEmpty()),
                () -> assertTrue(stored.reviewReason().isEmpty()),
                () -> assertEquals(1, context.decisions().createIfAbsentCalls()),
                () -> assertEquals(1, context.transactions().executions())
        );
    }

    @Test
    void identicalRetryReturnsThePersistedDecisionWithoutMutation() {
        TestContext context = context(eligibleEvidence(EVIDENCE_ID));
        CreateDecisionCommand firstAttempt = command("  Evaluate AI model cost  ");
        CreateDecisionCommand retry = command("Evaluate AI model cost");

        CreateDecisionResult firstResult = context.useCase().createDecision(firstAttempt);
        CreateDecisionResult retryResult = context.useCase().createDecision(retry);
        Decision stored = context.decisions().findById(DECISION_ID).orElseThrow();

        assertAll(
                () -> assertEquals(firstResult, retryResult),
                () -> assertEquals("Evaluate AI model cost", stored.title()),
                () -> assertEquals(DecisionStatus.CREATED, stored.status()),
                () -> assertEquals(CREATED_AT, stored.updatedAt()),
                () -> assertEquals(2, context.decisions().createIfAbsentCalls()),
                () -> assertEquals(0, context.decisions().saveCalls()),
                () -> assertEquals(2, context.transactions().executions())
        );
    }

    @Test
    void conflictingRetryCannotOverwriteImmutableCreationAttributes() {
        TestContext context = context(eligibleEvidence(EVIDENCE_ID));
        context.useCase().createDecision(command("Evaluate AI model cost"));

        DecisionCreationConflictException conflict = assertThrows(
                DecisionCreationConflictException.class,
                () -> context.useCase().createDecision(command("Replace the entire AI platform"))
        );
        Decision stored = context.decisions().findById(DECISION_ID).orElseThrow();

        assertAll(
                () -> assertEquals("DECISION_CREATION_CONFLICT", conflict.code()),
                () -> assertEquals("Evaluate AI model cost", stored.title()),
                () -> assertEquals(DecisionStatus.CREATED, stored.status()),
                () -> assertEquals(0, context.decisions().saveCalls())
        );
    }

    @Test
    void replayCannotResetAProgressedDecision() {
        TestContext context = context(eligibleEvidence(EVIDENCE_ID));
        CreateDecisionCommand command = command("Evaluate AI model cost");
        context.useCase().createDecision(command);

        Decision progressed = context.decisions().findById(DECISION_ID).orElseThrow();
        Timestamp reviewedAt = timestamp("2026-07-01T10:00:00Z");
        progressed.defer(APPROVER_ID, reviewedAt, "More usage evidence is required");
        context.decisions().save(progressed);

        CreateDecisionResult retryResult = context.useCase().createDecision(command);
        Decision stored = context.decisions().findById(DECISION_ID).orElseThrow();

        assertAll(
                () -> assertEquals(DecisionStatus.DEFERRED, retryResult.status()),
                () -> assertEquals(DecisionStatus.DEFERRED, stored.status()),
                () -> assertEquals(reviewedAt, stored.updatedAt()),
                () -> assertEquals(Optional.of(reviewedAt), stored.reviewedAt()),
                () -> assertEquals(
                        Optional.of("More usage evidence is required"),
                        stored.reviewReason()
                ),
                () -> assertEquals(1, context.decisions().saveCalls())
        );
    }

    @Test
    void missingEvidenceNeverCreatesADecision() {
        TestContext missingContext = context();

        assertThrows(
                EvidenceNotFoundException.class,
                () -> missingContext.useCase().createDecision(command("Evaluate AI model cost"))
        );
        assertFalse(missingContext.decisions().existsById(DECISION_ID));
    }

    @Test
    void everyApplicationEligibilityFailurePreventsDecisionCreation() {
        assertIneligible(evidence(
                EVIDENCE_ID,
                "OTHER-CASE",
                "business_context",
                "business_context_requested",
                "ACCEPTED",
                "INTERNAL"
        ));
        assertIneligible(evidence(
                EVIDENCE_ID,
                "DRC-AOA-001",
                "cloud_cost",
                "business_context_requested",
                "ACCEPTED",
                "INTERNAL"
        ));
        assertIneligible(evidence(
                EVIDENCE_ID,
                "DRC-AOA-001",
                "business_context",
                "decision_created",
                "ACCEPTED",
                "INTERNAL"
        ));
        assertIneligible(evidence(
                EVIDENCE_ID,
                "DRC-AOA-001",
                "business_context",
                "business_context_requested",
                "NEEDS_REVIEW",
                "INTERNAL"
        ));
        assertIneligible(evidence(
                EVIDENCE_ID,
                "DRC-AOA-001",
                "business_context",
                "business_context_requested",
                "ACCEPTED",
                "RESTRICTED"
        ));
    }

    private static void assertIneligible(Evidence ineligible) {
        TestContext ineligibleContext = context(ineligible);

        EvidenceTraceabilityViolationException rejection = assertThrows(
                EvidenceTraceabilityViolationException.class,
                () -> ineligibleContext.useCase().createDecision(command("Evaluate AI model cost"))
        );

        assertAll(
                () -> assertEquals("EVIDENCE_TRACEABILITY_VIOLATION", rejection.code()),
                () -> assertFalse(ineligibleContext.decisions().existsById(DECISION_ID)),
                () -> assertEquals(0, ineligibleContext.decisions().createIfAbsentCalls())
        );
    }

    private static TestContext context(Evidence... evidenceItems) {
        InMemoryEvidenceRepository evidenceRepository = new InMemoryEvidenceRepository();
        for (Evidence evidence : evidenceItems) {
            evidenceRepository.save(evidence);
        }
        InMemoryDecisionRepository decisionRepository = new InMemoryDecisionRepository();
        CountingTransactionRunner transactions = new CountingTransactionRunner();
        return new TestContext(
                new CreateDecisionUseCase(evidenceRepository, decisionRepository, transactions),
                decisionRepository,
                transactions
        );
    }

    private static CreateDecisionCommand command(String title) {
        return new CreateDecisionCommand(
                DECISION_ID,
                EVIDENCE_ID,
                title,
                "Reduce recurring AI operating cost",
                OWNER_ID,
                APPROVER_ID,
                CREATED_AT
        );
    }

    private static Evidence eligibleEvidence(EvidenceId id) {
        return evidence(
                id,
                "DRC-AOA-001",
                "business_context",
                "business_context_requested",
                "ACCEPTED",
                "INTERNAL"
        );
    }

    private static Evidence evidence(
            EvidenceId id,
            String correlationKey,
            String evidenceType,
            String eventType,
            String reviewStatus,
            String sensitivity
    ) {
        return new Evidence(
                id,
                timestamp("2026-06-30T23:59:59Z"),
                "Jira",
                "business_context",
                "IMP-214",
                "ai-onboarding-assistant",
                eventType,
                Severity.INFO,
                "head-customer-success",
                evidenceType,
                "AI operating cost requires a model review",
                "Establishes the business need for DRC-AOA-001",
                correlationKey,
                sensitivity,
                "HIGH",
                reviewStatus,
                "not_stored",
                Map.of("evidence_ref", "E-JIRA-001")
        );
    }

    private static Timestamp timestamp(String value) {
        return new Timestamp(Instant.parse(value));
    }

    private static EvidenceId evidenceId(String value) {
        return new EvidenceId(UUID.fromString(value));
    }

    private static DecisionId decisionId(String value) {
        return new DecisionId(UUID.fromString(value));
    }

    private static UserId userId(String value) {
        return new UserId(UUID.fromString(value));
    }

    private record TestContext(
            CreateDecisionUseCase useCase,
            InMemoryDecisionRepository decisions,
            CountingTransactionRunner transactions
    ) {
    }

    private static final class InMemoryEvidenceRepository implements EvidenceRepository {
        private final Map<EvidenceId, Evidence> evidence = new LinkedHashMap<>();

        @Override
        public void save(Evidence item) {
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
    }

    private static final class InMemoryDecisionRepository implements DecisionRepository {
        private final Map<DecisionId, Decision> decisions = new LinkedHashMap<>();
        private int createIfAbsentCalls;
        private int saveCalls;

        @Override
        public synchronized void save(Decision decision) {
            saveCalls++;
            decisions.put(decision.id(), decision);
        }

        @Override
        public synchronized Decision createIfAbsent(Decision decision) {
            createIfAbsentCalls++;
            return decisions.computeIfAbsent(decision.id(), ignored -> decision);
        }

        @Override
        public synchronized Optional<Decision> findById(DecisionId id) {
            return Optional.ofNullable(decisions.get(id));
        }

        @Override
        public synchronized Optional<Decision> findByIdForUpdate(DecisionId id) {
            return findById(id);
        }

        @Override
        public synchronized boolean existsById(DecisionId id) {
            return decisions.containsKey(id);
        }

        synchronized int createIfAbsentCalls() {
            return createIfAbsentCalls;
        }

        synchronized int saveCalls() {
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
