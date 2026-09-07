package imperator.application.composecase;

import imperator.application.createdecision.CreateDecisionUseCase;
import imperator.application.exceptions.DecisionCreationConflictException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.RecommendationNotReadyException;
import imperator.application.exceptions.ValidationException;
import imperator.application.generaterecommendation.GenerateRecommendationUseCase;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;
import imperator.support.DrcAoa001EvidenceFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComposeDrcAoa001UseCaseTest {
    private static final EvidenceId ORIGIN_ID = id("20000000-0000-4000-8000-000000000001");
    private static final DecisionId DECISION_ID = decisionId("10000000-0000-4000-8000-000000000001");
    private static final RecommendationId RECOMMENDATION_ID =
            recommendationId("50000000-0000-4000-8000-000000000001");
    private static final UserId OWNER_ID = userId("30000000-0000-4000-8000-000000000001");
    private static final UserId APPROVER_ID = userId("40000000-0000-4000-8000-000000000001");
    private static final Timestamp OBSERVED_AT = timestamp("2026-06-30T23:59:59Z");
    private static final Timestamp DECIDED_AT = timestamp("2026-07-01T09:00:00Z");
    private static final Timestamp GENERATED_AT = timestamp("2026-07-01T09:01:00Z");

    @Test
    void composesTheCanonicalCaseThroughTwoIndependentUseCaseTransactions() {
        TestContext context = context();

        ComposeDrcAoa001Result result = context.useCase().compose(command(context.evidenceIds()));

        assertAll(
                () -> assertEquals(ComposeDrcAoa001UseCase.CASE_ID, result.caseId()),
                () -> assertEquals(DECISION_ID, result.decisionId()),
                () -> assertEquals(RECOMMENDATION_ID, result.recommendationId()),
                () -> assertEquals(RecommendationType.MODEL_CHANGE, result.recommendationType()),
                () -> assertEquals(new BigDecimal("19440.00"),
                        result.estimatedAnnualizedSavings().value().amount()),
                () -> assertEquals("EUR", result.estimatedAnnualizedSavings().value().currency().code()),
                () -> assertEquals(92, result.confidence().percentage()),
                () -> assertEquals(Severity.LOW, result.risk()),
                () -> assertEquals(28, result.evidenceCount()),
                () -> assertFalse(result.replayed()),
                () -> assertFalse(result.resumed()),
                () -> assertEquals(1, context.decisions().size()),
                () -> assertEquals(1, context.recommendations().size()),
                () -> assertTrue(context.decisions().findById(DECISION_ID).orElseThrow().hasRecommendation()),
                () -> assertEquals(2, context.transactions().executions())
        );
    }

    @Test
    void equivalentReplayPreservesTheSameDecisionAndRecommendation() {
        TestContext context = context();
        ComposeDrcAoa001Command command = command(context.evidenceIds());
        context.useCase().compose(command);

        ComposeDrcAoa001Result replay = context.useCase().compose(command);

        assertAll(
                () -> assertTrue(replay.replayed()),
                () -> assertFalse(replay.resumed()),
                () -> assertEquals(1, context.decisions().size()),
                () -> assertEquals(1, context.recommendations().size()),
                () -> assertEquals(4, context.transactions().executions())
        );
    }

    @Test
    void retryResumesRecommendationAfterTheDecisionTransactionCommitted() {
        TestContext context = context();
        Evidence omitted = context.evidence().values().stream()
                .filter(item -> !item.id().equals(ORIGIN_ID))
                .findFirst()
                .orElseThrow();
        context.evidence().remove(omitted.id());

        assertThrows(
                EvidenceNotFoundException.class,
                () -> context.useCase().compose(command(context.evidenceIds()))
        );
        assertAll(
                () -> assertEquals(1, context.decisions().size()),
                () -> assertFalse(context.decisions().findById(DECISION_ID).orElseThrow().hasRecommendation()),
                () -> assertEquals(0, context.recommendations().size())
        );

        context.evidence().save(omitted);
        ComposeDrcAoa001Result resumed = context.useCase().compose(command(context.evidenceIds()));

        assertAll(
                () -> assertFalse(resumed.replayed()),
                () -> assertTrue(resumed.resumed()),
                () -> assertEquals(1, context.decisions().size()),
                () -> assertEquals(1, context.recommendations().size()),
                () -> assertEquals(4, context.transactions().executions())
        );
    }

    @Test
    void aDifferentDecisionIdCannotCreateASecondDecisionForTheCase() {
        TestContext context = context();
        ComposeDrcAoa001Command first = command(context.evidenceIds());
        context.useCase().compose(first);
        ComposeDrcAoa001Command conflict = new ComposeDrcAoa001Command(
                first.caseId(),
                decisionId("10000000-0000-4000-8000-000000000099"),
                first.recommendationId(),
                first.originatingEvidenceId(),
                first.evidenceIds(),
                first.title(),
                first.businessNeed(),
                first.ownerId(),
                first.requiredApproverId(),
                first.initiatingActorId(),
                first.decisionCreatedAt(),
                first.recommendationGeneratedAt()
        );

        assertThrows(DecisionCreationConflictException.class, () -> context.useCase().compose(conflict));
        assertEquals(1, context.decisions().size());
    }

    @Test
    void actorMismatchFailsBeforeEitherBusinessUseCaseExecutes() {
        TestContext context = context();
        ComposeDrcAoa001Command valid = command(context.evidenceIds());
        ComposeDrcAoa001Command invalid = new ComposeDrcAoa001Command(
                valid.caseId(),
                valid.decisionId(),
                valid.recommendationId(),
                valid.originatingEvidenceId(),
                valid.evidenceIds(),
                valid.title(),
                valid.businessNeed(),
                valid.ownerId(),
                valid.requiredApproverId(),
                userId("40000000-0000-4000-8000-000000000099"),
                valid.decisionCreatedAt(),
                valid.recommendationGeneratedAt()
        );

        ValidationException failure = assertThrows(
                ValidationException.class,
                () -> context.useCase().compose(invalid)
        );
        assertAll(
                () -> assertEquals("COMPOSITION_APPROVER_MISMATCH", failure.code()),
                () -> assertEquals(0, context.transactions().executions()),
                () -> assertEquals(0, context.decisions().size()),
                () -> assertEquals(0, context.recommendations().size())
        );
    }

    @Test
    void ineligibleOrPostDecisionEvidenceLeavesOnlyTheRecoverableDecision() {
        assertRecommendationNotReady(item -> withMetadata(item, "freshness", "stale"));
        assertRecommendationNotReady(item -> withMetadata(item, "currency", "USD"));
        assertRecommendationNotReady(item -> copyEvidence(
                item,
                item.eventType(),
                item.evidenceType(),
                "RESTRICTED",
                item.reviewStatus(),
                item.rawPayloadMode(),
                item.metadata()
        ));
        assertRecommendationNotReady(item -> copyEvidence(
                item,
                item.eventType(),
                item.evidenceType(),
                item.sensitivity(),
                "REJECTED",
                item.rawPayloadMode(),
                item.metadata()
        ));
        assertRecommendationNotReady(item -> copyEvidence(
                item,
                "deployment_reference_observed",
                "code_deployment",
                item.sensitivity(),
                item.reviewStatus(),
                item.rawPayloadMode(),
                Map.of("deployment_reference", "model-routing-v1", "freshness", "fresh")
        ));

        TestContext context = context();
        Evidence original = context.evidence().values().stream()
                .filter(item -> "E-AWS-001".equals(item.metadata().get("evidence_ref")))
                .findFirst()
                .orElseThrow();
        assertThrows(IllegalArgumentException.class, () -> copyEvidence(
                original,
                original.eventType(),
                original.evidenceType(),
                original.sensitivity(),
                original.reviewStatus(),
                "stored",
                original.metadata()
        ));
    }

    private static void assertRecommendationNotReady(UnaryOperator<Evidence> mutation) {
        TestContext context = context();
        Evidence original = context.evidence().values().stream()
                .filter(item -> "E-AWS-001".equals(item.metadata().get("evidence_ref")))
                .findFirst()
                .orElseThrow();
        context.evidence().save(mutation.apply(original));

        RecommendationNotReadyException failure = assertThrows(
                RecommendationNotReadyException.class,
                () -> context.useCase().compose(command(context.evidenceIds()))
        );

        assertAll(
                () -> assertEquals("RECOMMENDATION_NOT_READY", failure.code()),
                () -> assertEquals(1, context.decisions().size()),
                () -> assertFalse(context.decisions().findById(DECISION_ID).orElseThrow().hasRecommendation()),
                () -> assertEquals(0, context.recommendations().size())
        );
    }

    private static Evidence withMetadata(Evidence evidence, String key, String value) {
        Map<String, String> metadata = new LinkedHashMap<>(evidence.metadata());
        metadata.put(key, value);
        return copyEvidence(
                evidence,
                evidence.eventType(),
                evidence.evidenceType(),
                evidence.sensitivity(),
                evidence.reviewStatus(),
                evidence.rawPayloadMode(),
                metadata
        );
    }

    private static Evidence copyEvidence(
            Evidence evidence,
            String eventType,
            String evidenceType,
            String sensitivity,
            String reviewStatus,
            String rawPayloadMode,
            Map<String, String> metadata
    ) {
        return new Evidence(
                evidence.id(),
                evidence.timestamp(),
                evidence.source(),
                evidence.sourceType(),
                evidence.sourceObjectRef(),
                evidence.entity(),
                eventType,
                evidence.severity(),
                evidence.actor(),
                evidenceType,
                evidence.observedFact(),
                evidence.businessMeaning(),
                evidence.correlationKey(),
                sensitivity,
                evidence.confidence(),
                reviewStatus,
                rawPayloadMode,
                metadata
        );
    }

    private static TestContext context() {
        Set<Evidence> pack = DrcAoa001EvidenceFixture.completePack(ORIGIN_ID, OBSERVED_AT, true);
        InMemoryEvidenceRepository evidence = new InMemoryEvidenceRepository(pack);
        InMemoryDecisionRepository decisions = new InMemoryDecisionRepository();
        InMemoryRecommendationRepository recommendations = new InMemoryRecommendationRepository();
        CountingTransactionRunner transactions = new CountingTransactionRunner();
        var decisionCreator = new CreateDecisionUseCase(evidence, decisions, transactions);
        var recommendationGenerator = new GenerateRecommendationUseCase(
                decisions,
                evidence,
                recommendations,
                transactions
        );
        return new TestContext(
                new ComposeDrcAoa001UseCase(decisionCreator, recommendationGenerator, decisions),
                evidence,
                decisions,
                recommendations,
                transactions,
                DrcAoa001EvidenceFixture.ids(pack)
        );
    }

    private static ComposeDrcAoa001Command command(Set<EvidenceId> evidenceIds) {
        return new ComposeDrcAoa001Command(
                ComposeDrcAoa001UseCase.CASE_ID,
                DECISION_ID,
                RECOMMENDATION_ID,
                ORIGIN_ID,
                evidenceIds,
                ComposeDrcAoa001UseCase.TITLE,
                ComposeDrcAoa001UseCase.BUSINESS_NEED,
                OWNER_ID,
                APPROVER_ID,
                APPROVER_ID,
                DECIDED_AT,
                GENERATED_AT
        );
    }

    private static Timestamp timestamp(String value) {
        return new Timestamp(Instant.parse(value));
    }

    private static EvidenceId id(String value) {
        return new EvidenceId(UUID.fromString(value));
    }

    private static DecisionId decisionId(String value) {
        return new DecisionId(UUID.fromString(value));
    }

    private static RecommendationId recommendationId(String value) {
        return new RecommendationId(UUID.fromString(value));
    }

    private static UserId userId(String value) {
        return new UserId(UUID.fromString(value));
    }

    private record TestContext(
            ComposeDrcAoa001UseCase useCase,
            InMemoryEvidenceRepository evidence,
            InMemoryDecisionRepository decisions,
            InMemoryRecommendationRepository recommendations,
            CountingTransactionRunner transactions,
            Set<EvidenceId> evidenceIds
    ) {
    }

    private static final class InMemoryEvidenceRepository implements EvidenceRepository {
        private final Map<EvidenceId, Evidence> evidence = new LinkedHashMap<>();

        private InMemoryEvidenceRepository(Set<Evidence> items) {
            items.forEach(this::save);
        }

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

        private void remove(EvidenceId id) {
            evidence.remove(id);
        }

        private java.util.Collection<Evidence> values() {
            return evidence.values();
        }
    }

    private static final class InMemoryDecisionRepository implements DecisionRepository {
        private final Map<DecisionId, Decision> decisions = new LinkedHashMap<>();

        @Override
        public synchronized void save(Decision decision) {
            decisions.put(decision.id(), decision);
        }

        @Override
        public synchronized Decision createIfAbsent(Decision decision) {
            return findByCaseId(decision.caseId())
                    .orElseGet(() -> decisions.computeIfAbsent(decision.id(), ignored -> decision));
        }

        @Override
        public synchronized Optional<Decision> findById(DecisionId id) {
            return Optional.ofNullable(decisions.get(id));
        }

        @Override
        public synchronized Optional<Decision> findByCaseId(String caseId) {
            return decisions.values().stream()
                    .filter(decision -> decision.caseId().equals(caseId))
                    .findFirst();
        }

        @Override
        public synchronized Optional<Decision> findByIdForUpdate(DecisionId id) {
            return findById(id);
        }

        @Override
        public synchronized boolean existsById(DecisionId id) {
            return decisions.containsKey(id);
        }

        private synchronized int size() {
            return decisions.size();
        }
    }

    private static final class InMemoryRecommendationRepository implements RecommendationRepository {
        private final Map<RecommendationId, Recommendation> recommendations = new LinkedHashMap<>();

        @Override
        public synchronized void save(Recommendation recommendation) {
            recommendations.put(recommendation.id(), recommendation);
        }

        @Override
        public synchronized Recommendation createIfAbsent(Recommendation recommendation) {
            return recommendations.values().stream()
                    .filter(item -> item.decisionId().equals(recommendation.decisionId()))
                    .findFirst()
                    .orElseGet(() -> recommendations.computeIfAbsent(
                            recommendation.id(),
                            ignored -> recommendation
                    ));
        }

        @Override
        public synchronized Optional<Recommendation> findById(RecommendationId id) {
            return Optional.ofNullable(recommendations.get(id));
        }

        @Override
        public synchronized boolean existsById(RecommendationId id) {
            return recommendations.containsKey(id);
        }

        private synchronized int size() {
            return recommendations.size();
        }
    }

    private static final class CountingTransactionRunner implements TransactionRunner {
        private int executions;

        @Override
        public <T> T execute(Supplier<T> operation) {
            executions++;
            return operation.get();
        }

        private int executions() {
            return executions;
        }
    }
}
