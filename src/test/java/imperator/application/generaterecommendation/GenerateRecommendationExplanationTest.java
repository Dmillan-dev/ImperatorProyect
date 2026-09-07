package imperator.application.generaterecommendation;

import imperator.domain.decision.Decision;
import imperator.domain.decision.DrcAoa001RecommendationPolicy;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.ExplanationProvider;
import imperator.ports.out.RecommendationExplanation;
import imperator.ports.out.RecommendationExplanationRequest;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateRecommendationExplanationTest {
    private static final Timestamp OBSERVED_AT = timestamp("2026-06-30T23:59:59Z");
    private static final Timestamp DECIDED_AT = timestamp("2026-07-01T09:00:00Z");
    private static final Timestamp GENERATED_AT = timestamp("2026-07-01T09:01:00Z");
    private static final EvidenceId ORIGINATING_EVIDENCE_ID =
            evidenceId("20000000-0000-4000-8000-000000000001");
    private static final DecisionId DECISION_ID =
            decisionId("10000000-0000-4000-8000-000000000001");
    private static final RecommendationId RECOMMENDATION_ID =
            recommendationId("50000000-0000-4000-8000-000000000001");
    private static final UserId OWNER_ID = userId("30000000-0000-4000-8000-000000000001");
    private static final UserId APPROVER_ID = userId("40000000-0000-4000-8000-000000000001");
    private static final String EXPLANATION =
            "Change the model because the accepted Evidence supports EUR 19440.00 in annualized recovery.";

    @Test
    void explainsOnlyThePersistedDeterministicRecommendationAfterCommit() {
        TestContext context = context(request -> Optional.of(new RecommendationExplanation(EXPLANATION)));

        GenerateRecommendationResult result = context.useCase().generateRecommendation(command(context.evidence()));
        Recommendation stored = context.recommendations().findById(RECOMMENDATION_ID).orElseThrow();
        RecommendationExplanationRequest request = context.provider().lastRequest();

        assertAll(
                () -> assertEquals(Optional.of(EXPLANATION), result.explanation()),
                () -> assertEquals(RECOMMENDATION_ID, request.recommendationId()),
                () -> assertEquals(DECISION_ID, request.decisionId()),
                () -> assertEquals("DRC-AOA-001", request.caseId()),
                () -> assertEquals("Reduce recurring AI operating cost", request.businessNeed()),
                () -> assertEquals(stored.type(), request.recommendationType()),
                () -> assertEquals(stored.suggestedAction(), request.suggestedAction()),
                () -> assertEquals(stored.reason(), request.deterministicReason()),
                () -> assertEquals(stored.estimatedSavings(), request.estimatedSavings()),
                () -> assertEquals(stored.confidence(), request.confidence()),
                () -> assertEquals(stored.risk(), request.risk()),
                () -> assertEquals(stored.evidenceIds(), Set.copyOf(request.evidenceIds())),
                () -> assertEquals(
                        Set.of("A-ROI-001", "A-ROI-002", "A-ROI-003", "A-ROI-004"),
                        Set.copyOf(request.assumptionIds())
                ),
                () -> assertEquals(DrcAoa001RecommendationPolicy.POLICY_VERSION, request.policyVersion()),
                () -> assertEquals(1, context.provider().calls()),
                () -> assertFalse(context.provider().invokedWhileTransactionActive()),
                () -> assertEquals(1, context.transactions().executions()),
                () -> assertFalse(context.transactions().active())
        );
        assertDeterministicFieldsRemainFrozen(stored, result);
    }

    @Test
    void unavailableProviderPreservesThePersistedRecommendation() {
        TestContext context = context(ignored -> Optional.empty());

        GenerateRecommendationResult result = context.useCase().generateRecommendation(command(context.evidence()));
        Recommendation stored = context.recommendations().findById(RECOMMENDATION_ID).orElseThrow();

        assertAll(
                () -> assertTrue(result.explanation().isEmpty()),
                () -> assertTrue(context.decisions().findById(DECISION_ID).orElseThrow().hasRecommendation()),
                () -> assertEquals(1, context.provider().calls()),
                () -> assertEquals(1, context.transactions().executions())
        );
        assertDeterministicFieldsRemainFrozen(stored, result);
    }

    @Test
    void providerFailureCannotRollBackOrAlterThePersistedRecommendation() {
        TestContext context = context(ignored -> {
            throw new IllegalStateException("Simulated provider outage");
        });

        GenerateRecommendationResult result = context.useCase().generateRecommendation(command(context.evidence()));
        Recommendation stored = context.recommendations().findById(RECOMMENDATION_ID).orElseThrow();

        assertAll(
                () -> assertTrue(result.explanation().isEmpty()),
                () -> assertTrue(context.recommendations().existsById(RECOMMENDATION_ID)),
                () -> assertEquals(Optional.of(RECOMMENDATION_ID),
                        context.decisions().findById(DECISION_ID).orElseThrow().recommendationId()),
                () -> assertEquals(1, context.provider().calls()),
                () -> assertEquals(1, context.transactions().executions())
        );
        assertDeterministicFieldsRemainFrozen(stored, result);
    }

    private static TestContext context(ExplanationProvider delegate) {
        InMemoryEvidenceRepository evidenceRepository = new InMemoryEvidenceRepository();
        Set<Evidence> evidence = DrcAoa001EvidenceFixture.completePack(
                ORIGINATING_EVIDENCE_ID,
                OBSERVED_AT,
                true
        );
        evidence.forEach(evidenceRepository::save);

        InMemoryDecisionRepository decisionRepository = new InMemoryDecisionRepository();
        decisionRepository.save(Decision.create(
                DECISION_ID,
                "DRC-AOA-001",
                "Evaluate AI model cost",
                "Reduce recurring AI operating cost",
                ORIGINATING_EVIDENCE_ID,
                OWNER_ID,
                APPROVER_ID,
                DECIDED_AT
        ));

        InMemoryRecommendationRepository recommendationRepository = new InMemoryRecommendationRepository();
        CountingTransactionRunner transactions = new CountingTransactionRunner();
        CapturingExplanationProvider provider = new CapturingExplanationProvider(delegate, transactions);
        GenerateRecommendationUseCase useCase = new GenerateRecommendationUseCase(
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                transactions,
                provider
        );
        return new TestContext(
                useCase,
                evidence,
                decisionRepository,
                recommendationRepository,
                transactions,
                provider
        );
    }

    private static GenerateRecommendationCommand command(Set<Evidence> evidence) {
        return new GenerateRecommendationCommand(
                RECOMMENDATION_ID,
                DECISION_ID,
                DrcAoa001EvidenceFixture.ids(evidence),
                GENERATED_AT
        );
    }

    private static void assertDeterministicFieldsRemainFrozen(
            Recommendation stored,
            GenerateRecommendationResult result
    ) {
        assertAll(
                () -> assertEquals(RecommendationType.MODEL_CHANGE, stored.type()),
                () -> assertEquals(DrcAoa001RecommendationPolicy.SUGGESTED_ACTION, stored.suggestedAction()),
                () -> assertEquals(new ROIAmount(Money.eur(new BigDecimal("19440.00"))), stored.estimatedSavings()),
                () -> assertEquals(new ROIConfidence(92), stored.confidence()),
                () -> assertEquals(Severity.LOW, stored.risk()),
                () -> assertEquals(stored.estimatedSavings(), result.estimatedSavings()),
                () -> assertEquals(stored.confidence(), result.confidence()),
                () -> assertEquals(stored.risk(), result.risk())
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

    private static RecommendationId recommendationId(String value) {
        return new RecommendationId(UUID.fromString(value));
    }

    private static UserId userId(String value) {
        return new UserId(UUID.fromString(value));
    }

    private record TestContext(
            GenerateRecommendationUseCase useCase,
            Set<Evidence> evidence,
            InMemoryDecisionRepository decisions,
            InMemoryRecommendationRepository recommendations,
            CountingTransactionRunner transactions,
            CapturingExplanationProvider provider
    ) {
    }

    private static final class CapturingExplanationProvider implements ExplanationProvider {
        private final ExplanationProvider delegate;
        private final CountingTransactionRunner transactions;
        private RecommendationExplanationRequest lastRequest;
        private int calls;
        private boolean invokedWhileTransactionActive;

        private CapturingExplanationProvider(
                ExplanationProvider delegate,
                CountingTransactionRunner transactions
        ) {
            this.delegate = delegate;
            this.transactions = transactions;
        }

        @Override
        public Optional<RecommendationExplanation> generateExplanation(RecommendationExplanationRequest request) {
            calls++;
            lastRequest = request;
            invokedWhileTransactionActive = transactions.active();
            return delegate.generateExplanation(request);
        }

        private RecommendationExplanationRequest lastRequest() {
            assertNotNull(lastRequest);
            return lastRequest;
        }

        private int calls() {
            return calls;
        }

        private boolean invokedWhileTransactionActive() {
            return invokedWhileTransactionActive;
        }
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

        @Override
        public synchronized void save(Decision decision) {
            decisions.put(decision.id(), decision);
        }

        @Override
        public synchronized Decision createIfAbsent(Decision decision) {
            return decisions.computeIfAbsent(decision.id(), ignored -> decision);
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
    }

    private static final class InMemoryRecommendationRepository implements RecommendationRepository {
        private final Map<RecommendationId, Recommendation> recommendations = new LinkedHashMap<>();

        @Override
        public synchronized void save(Recommendation recommendation) {
            recommendations.put(recommendation.id(), recommendation);
        }

        @Override
        public synchronized Recommendation createIfAbsent(Recommendation recommendation) {
            return recommendations.computeIfAbsent(recommendation.id(), ignored -> recommendation);
        }

        @Override
        public synchronized Optional<Recommendation> findById(RecommendationId id) {
            return Optional.ofNullable(recommendations.get(id));
        }

        @Override
        public synchronized boolean existsById(RecommendationId id) {
            return recommendations.containsKey(id);
        }
    }

    private static final class CountingTransactionRunner implements TransactionRunner {
        private int executions;
        private boolean active;

        @Override
        public <T> T execute(Supplier<T> operation) {
            executions++;
            active = true;
            try {
                return operation.get();
            } finally {
                active = false;
            }
        }

        private int executions() {
            return executions;
        }

        private boolean active() {
            return active;
        }
    }
}
