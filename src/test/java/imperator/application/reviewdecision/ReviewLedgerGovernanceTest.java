package imperator.application.reviewdecision;

import imperator.application.appendledgerentry.AppendLedgerEntryCommand;
import imperator.application.appendledgerentry.AppendLedgerEntryResult;
import imperator.application.appendledgerentry.AppendLedgerEntryUseCase;
import imperator.application.exceptions.AuthorizationException;
import imperator.application.exceptions.ConflictException;
import imperator.application.ledger.LedgerChain;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.Money;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ReviewLedgerGovernanceTest {
    private static final Instant BASE_TIME = Instant.parse("2026-08-01T10:00:00Z");
    private static final ROIAmount ESTIMATED_SAVINGS = eur("19440.00");

    private final AtomicLong identifiers = new AtomicLong(100);
    private InMemoryDecisionRepository decisionRepository;
    private InMemoryRecommendationRepository recommendationRepository;
    private InMemoryEvidenceRepository evidenceRepository;
    private InMemoryLedgerRepository ledgerRepository;
    private TransactionRunner transactionRunner;

    @BeforeEach
    void setUp() {
        decisionRepository = new InMemoryDecisionRepository();
        recommendationRepository = new InMemoryRecommendationRepository();
        evidenceRepository = new InMemoryEvidenceRepository();
        ledgerRepository = new InMemoryLedgerRepository();
        transactionRunner = Supplier::get;
    }

    @Test
    void approveAtomicallyCreatesTheAuthoritativeLedgerEntryAndReplaysIt() {
        Graph graph = graph();
        ReviewDecisionUseCase useCase = reviewUseCase();
        LedgerEntryId operationId = ledgerEntryId();
        ReviewDecisionCommand command = approveCommand(graph, operationId, Optional.empty(), "Approved for rollout");

        ReviewDecisionResult created = useCase.reviewDecision(command);
        ReviewDecisionResult replay = useCase.reviewDecision(command);

        LedgerEntry entry = ledgerRepository.findById(operationId).orElseThrow();
        assertEquals(DecisionStatus.APPROVED, decisionRepository.findById(graph.decision().id()).orElseThrow().status());
        assertEquals(LedgerEntryType.APPROVED, entry.entryType());
        assertEquals(graph.recommendation().evidenceIds(), entry.evidenceSnapshotIds());
        assertEquals(Optional.of(ESTIMATED_SAVINGS), entry.estimatedSaving());
        assertFalse(created.replayed());
        assertTrue(replay.replayed());
        assertEquals(1, ledgerRepository.findByDecisionId(graph.decision().id()).size());

        ReviewDecisionCommand conflict = approveCommand(
                graph,
                operationId,
                Optional.empty(),
                "A different immutable approval reason"
        );
        assertThrows(ConflictException.class, () -> useCase.reviewDecision(conflict));
        assertEquals(1, ledgerRepository.findByDecisionId(graph.decision().id()).size());
    }

    @Test
    void rejectAtomicallyCreatesTheMatchingAuthoritativeLedgerEntry() {
        Graph graph = graph();
        LedgerEntryId operationId = ledgerEntryId();
        ReviewDecisionCommand command = new ReviewDecisionCommand(
                operationId,
                graph.decision().id(),
                ReviewDecisionAction.REJECT,
                graph.approverId(),
                "ADMIN",
                timestamp(3),
                "Risk is not acceptable for this rollout",
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        ReviewDecisionResult result = reviewUseCase().reviewDecision(command);
        LedgerEntry entry = ledgerRepository.findById(operationId).orElseThrow();

        assertEquals(DecisionStatus.REJECTED, graph.decision().status());
        assertEquals(DecisionStatus.REJECTED, result.status());
        assertEquals(LedgerEntryType.REJECTED, entry.entryType());
        assertEquals(graph.recommendation().evidenceIds(), entry.evidenceSnapshotIds());
        assertEquals(Optional.of(ESTIMATED_SAVINGS), entry.estimatedSaving());
        assertFalse(result.replayed());
        assertEquals(1, ledgerRepository.findByDecisionId(graph.decision().id()).size());
    }

    @Test
    void sameReviewActionWithAnotherOperationIdentifierConflictsWithoutMutation() {
        Graph graph = graph();
        ReviewDecisionUseCase useCase = reviewUseCase();
        useCase.reviewDecision(approveCommand(
                graph,
                ledgerEntryId(),
                Optional.empty(),
                "Approved for rollout"
        ));

        ReviewDecisionCommand duplicateBusinessAction = approveCommand(
                graph,
                ledgerEntryId(),
                Optional.empty(),
                "Approved for rollout"
        );

        assertThrows(ConflictException.class, () -> useCase.reviewDecision(duplicateBusinessAction));
        assertEquals(DecisionStatus.APPROVED, graph.decision().status());
        assertEquals(1, ledgerRepository.findByDecisionId(graph.decision().id()).size());
    }

    @Test
    void deferThenApproveProducesOneLinearTwoEntryHistory() {
        Graph graph = graph();
        ReviewDecisionUseCase useCase = reviewUseCase();
        LedgerEntryId deferredId = ledgerEntryId();
        ReviewDecisionCommand defer = new ReviewDecisionCommand(
                deferredId,
                graph.decision().id(),
                ReviewDecisionAction.DEFER,
                userId(),
                "FINANCE",
                timestamp(3),
                "Cost validation requires one more source",
                Optional.empty(),
                Optional.of("Post-action cost Evidence"),
                Optional.empty()
        );

        useCase.reviewDecision(defer);
        useCase.reviewDecision(approveCommand(
                graph,
                ledgerEntryId(),
                Optional.of(deferredId),
                "Approved after resolving the Evidence gap"
        ));

        List<LedgerEntry> history = ledgerRepository.findByDecisionId(graph.decision().id());
        LedgerChain chain = LedgerChain.from(graph.decision().id(), history);
        assertEquals(2, history.size());
        assertEquals(LedgerEntryType.APPROVED, chain.head().orElseThrow().entryType());
        assertEquals(Optional.of(deferredId), chain.head().orElseThrow().previousEntryId());
        assertEquals(DecisionStatus.APPROVED, graph.decision().status());
    }

    @Test
    void implementationAndValidationRemainLedgerFactsAndPreserveLinearHistory() {
        Graph graph = graph();
        ReviewDecisionUseCase reviewUseCase = reviewUseCase();
        LedgerEntryId approvedId = ledgerEntryId();
        reviewUseCase.reviewDecision(approveCommand(graph, approvedId, Optional.empty(), "Approved for implementation"));

        Evidence implementationEvidence = evidence("implementation_marked", timestamp(4));
        Evidence validationEvidence = evidence("result_validated", timestamp(5));
        evidenceRepository.save(implementationEvidence);
        evidenceRepository.save(validationEvidence);

        AppendLedgerEntryUseCase ledgerUseCase = appendUseCase();
        LedgerEntryId implementationId = ledgerEntryId();
        AppendLedgerEntryCommand implementation = new AppendLedgerEntryCommand(
                implementationId,
                graph.decision().id(),
                userId(),
                "PLATFORM_ENGINEER",
                timestamp(4),
                LedgerEntryType.IMPLEMENTATION_MARKED,
                "Lower-cost model deployed outside IMPERATOR",
                Set.of(implementationEvidence.id()),
                Optional.of(approvedId),
                "2026-Q3",
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
        ledgerUseCase.appendLedgerEntry(implementation);

        LedgerEntryId validationId = ledgerEntryId();
        AppendLedgerEntryResult validation = ledgerUseCase.appendLedgerEntry(new AppendLedgerEntryCommand(
                validationId,
                graph.decision().id(),
                userId(),
                "FINANCE",
                timestamp(5),
                LedgerEntryType.RESULT_VALIDATED,
                "Observed recovery validated by Finance",
                Set.of(validationEvidence.id()),
                Optional.of(implementationId),
                "2026 annualized validation",
                Optional.of(eur("24000.00")),
                Optional.of(eur("4000.00")),
                Optional.of(eur("100.00"))
        ));

        List<LedgerEntry> history = ledgerRepository.findByDecisionId(graph.decision().id());
        LedgerChain chain = LedgerChain.from(graph.decision().id(), history);
        assertEquals(3, history.size());
        assertEquals(validationId, chain.head().orElseThrow().id());
        assertEquals(Optional.of(implementationId), chain.head().orElseThrow().previousEntryId());
        assertEquals(new BigDecimal("19900.00"), validation.realizedSaving().orElseThrow().value().amount());
        assertEquals(DecisionStatus.APPROVED, graph.decision().status());

        AppendLedgerEntryResult implementationReplay = ledgerUseCase.appendLedgerEntry(implementation);
        assertTrue(implementationReplay.replayed());
        assertEquals(3, ledgerRepository.findByDecisionId(graph.decision().id()).size());

        AppendLedgerEntryCommand duplicateImplementation = new AppendLedgerEntryCommand(
                ledgerEntryId(),
                graph.decision().id(),
                userId(),
                "PLATFORM_ENGINEER",
                timestamp(6),
                LedgerEntryType.IMPLEMENTATION_MARKED,
                "Duplicate implementation",
                Set.of(implementationEvidence.id()),
                Optional.of(approvedId),
                "2026-Q3",
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
        assertThrows(ConflictException.class, () -> ledgerUseCase.appendLedgerEntry(duplicateImplementation));
        assertEquals(3, ledgerRepository.findByDecisionId(graph.decision().id()).size());
    }

    @Test
    void unauthorizedReviewChangesNeitherDecisionNorLedger() {
        Graph graph = graph();
        ReviewDecisionCommand command = new ReviewDecisionCommand(
                ledgerEntryId(),
                graph.decision().id(),
                ReviewDecisionAction.APPROVE,
                graph.approverId(),
                "PLATFORM_ENGINEER",
                timestamp(3),
                "Unauthorized approval",
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        assertThrows(AuthorizationException.class, () -> reviewUseCase().reviewDecision(command));
        assertEquals(DecisionStatus.CREATED, graph.decision().status());
        assertTrue(ledgerRepository.findByDecisionId(graph.decision().id()).isEmpty());
    }

    private ReviewDecisionUseCase reviewUseCase() {
        return new ReviewDecisionUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
    }

    private AppendLedgerEntryUseCase appendUseCase() {
        return new AppendLedgerEntryUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
    }

    private ReviewDecisionCommand approveCommand(
            Graph graph,
            LedgerEntryId operationId,
            Optional<LedgerEntryId> expectedPrevious,
            String reason
    ) {
        return new ReviewDecisionCommand(
                operationId,
                graph.decision().id(),
                ReviewDecisionAction.APPROVE,
                graph.approverId(),
                "ADMIN",
                expectedPrevious.isEmpty() ? timestamp(3) : timestamp(4),
                reason,
                expectedPrevious,
                Optional.empty(),
                Optional.empty()
        );
    }

    private Graph graph() {
        Evidence evidence = evidence("recommendation_support", timestamp(0));
        evidenceRepository.save(evidence);
        UserId ownerId = userId();
        UserId approverId = userId();
        Decision decision = Decision.create(
                decisionId(),
                "DRC-AOA-001",
                "Optimize AI onboarding cost",
                "Reduce recurring AI expenditure",
                evidence.id(),
                ownerId,
                approverId,
                timestamp(1)
        );
        Recommendation recommendation = new Recommendation(
                recommendationId(),
                decision.id(),
                RecommendationType.MODEL_CHANGE,
                "Use a lower-cost model with fallback",
                "Deterministic cost recovery policy",
                Set.of(evidence.id()),
                ESTIMATED_SAVINGS,
                new ROIConfidence(92),
                Severity.LOW,
                ownerId,
                approverId,
                timestamp(2)
        );
        decision.attachRecommendation(recommendation.id(), timestamp(2));
        decisionRepository.save(decision);
        recommendationRepository.save(recommendation);
        return new Graph(decision, recommendation, approverId);
    }

    private Evidence evidence(String eventType, Timestamp timestamp) {
        return new Evidence(
                evidenceId(),
                timestamp,
                "test",
                "manual",
                "ref-" + identifiers.incrementAndGet(),
                "ai-onboarding-assistant",
                eventType,
                Severity.INFO,
                "human",
                "governance",
                "Observed governance fact",
                "Supports the Decision governance lifecycle",
                "DRC-AOA-001",
                "INTERNAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.of()
        );
    }

    private Timestamp timestamp(long seconds) {
        return new Timestamp(BASE_TIME.plusSeconds(seconds));
    }

    private DecisionId decisionId() {
        return new DecisionId(uuid());
    }

    private RecommendationId recommendationId() {
        return new RecommendationId(uuid());
    }

    private EvidenceId evidenceId() {
        return new EvidenceId(uuid());
    }

    private LedgerEntryId ledgerEntryId() {
        return new LedgerEntryId(uuid());
    }

    private UserId userId() {
        return new UserId(uuid());
    }

    private UUID uuid() {
        return new UUID(10, identifiers.incrementAndGet());
    }

    private static ROIAmount eur(String value) {
        return new ROIAmount(Money.eur(new BigDecimal(value)));
    }

    private record Graph(Decision decision, Recommendation recommendation, UserId approverId) {
    }

    private static final class InMemoryDecisionRepository implements DecisionRepository {
        private final Map<DecisionId, Decision> decisions = new LinkedHashMap<>();

        @Override
        public void save(Decision decision) {
            decisions.put(decision.id(), decision);
        }

        @Override
        public Decision createIfAbsent(Decision decision) {
            return decisions.computeIfAbsent(decision.id(), ignored -> decision);
        }

        @Override
        public Optional<Decision> findById(DecisionId id) {
            return Optional.ofNullable(decisions.get(id));
        }

        @Override
        public Optional<Decision> findByCaseId(String caseId) {
            return decisions.values().stream()
                    .filter(decision -> decision.caseId().equals(caseId))
                    .findFirst();
        }

        @Override
        public Optional<Decision> findByIdForUpdate(DecisionId id) {
            return findById(id);
        }

        @Override
        public boolean existsById(DecisionId id) {
            return decisions.containsKey(id);
        }
    }

    private static final class InMemoryRecommendationRepository implements RecommendationRepository {
        private final Map<RecommendationId, Recommendation> recommendations = new LinkedHashMap<>();

        @Override
        public void save(Recommendation recommendation) {
            recommendations.put(recommendation.id(), recommendation);
        }

        @Override
        public Recommendation createIfAbsent(Recommendation recommendation) {
            return recommendations.computeIfAbsent(recommendation.id(), ignored -> recommendation);
        }

        @Override
        public Optional<Recommendation> findById(RecommendationId id) {
            return Optional.ofNullable(recommendations.get(id));
        }

        @Override
        public boolean existsById(RecommendationId id) {
            return recommendations.containsKey(id);
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

    private static final class InMemoryLedgerRepository implements LedgerRepository {
        private final Map<LedgerEntryId, LedgerEntry> entries = new LinkedHashMap<>();

        @Override
        public LedgerEntry append(LedgerEntry entry) {
            return entries.computeIfAbsent(entry.id(), ignored -> entry);
        }

        @Override
        public Optional<LedgerEntry> findById(LedgerEntryId id) {
            return Optional.ofNullable(entries.get(id));
        }

        @Override
        public List<LedgerEntry> findByDecisionId(DecisionId decisionId) {
            return entries.values().stream()
                    .filter(entry -> entry.belongsTo(decisionId))
                    .toList();
        }
    }
}
