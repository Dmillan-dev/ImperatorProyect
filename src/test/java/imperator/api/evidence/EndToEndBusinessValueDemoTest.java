package imperator.api.evidence;

import imperator.application.appendledgerentry.AppendLedgerEntryCommand;
import imperator.application.appendledgerentry.AppendLedgerEntryUseCase;
import imperator.application.businessvalue.BusinessValueProjection;
import imperator.application.businessvalue.ProjectBusinessValueUseCase;
import imperator.application.createdecision.CreateDecisionCommand;
import imperator.application.createdecision.CreateDecisionUseCase;
import imperator.application.exceptions.BusinessRuleViolationException;
import imperator.application.generaterecommendation.GenerateRecommendationCommand;
import imperator.application.generaterecommendation.GenerateRecommendationResult;
import imperator.application.generaterecommendation.GenerateRecommendationUseCase;
import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.application.reviewdecision.ReviewDecisionAction;
import imperator.application.reviewdecision.ReviewDecisionCommand;
import imperator.application.reviewdecision.ReviewDecisionUseCase;
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
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.RecommendationExplanation;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Executable local certification harness for the Sprint 3.5 vertical slice. */
final class EndToEndBusinessValueDemoTest {
    private static final String EXPLANATION =
            "The validated model-routing change lowers annualized AI operating cost while retaining a fallback.";
    private static final Instant IMPORTED_AT = Instant.parse("2026-07-01T08:00:00Z");
    private static final DecisionId DECISION_ID = decisionId(301);
    private static final RecommendationId RECOMMENDATION_ID = recommendationId(302);
    private static final UserId OWNER_ID = userId(401);
    private static final UserId APPROVER_ID = userId(402);
    private static final UserId IMPLEMENTER_ID = userId(403);
    private static final UserId VALIDATOR_ID = userId(404);
    private static final LedgerEntryId APPROVAL_ID = ledgerEntryId(501);
    private static final LedgerEntryId IMPLEMENTATION_ID = ledgerEntryId(502);
    private static final LedgerEntryId VALIDATION_ID = ledgerEntryId(503);

    @Test
    void deterministicWorkflowProducesOneAuditableBusinessValueProjection() throws IOException {
        InMemoryEvidenceRepository evidenceRepository = new InMemoryEvidenceRepository();
        InMemoryDecisionRepository decisionRepository = new InMemoryDecisionRepository();
        InMemoryRecommendationRepository recommendationRepository = new InMemoryRecommendationRepository();
        InMemoryLedgerRepository ledgerRepository = new InMemoryLedgerRepository();
        TransactionRunner transactionRunner = Supplier::get;

        ImportEvidenceUseCase importUseCase = new ImportEvidenceUseCase(evidenceRepository, transactionRunner);
        EvidenceNdjsonImporter importer = new EvidenceNdjsonImporter(
                () -> importUseCase,
                Clock.fixed(IMPORTED_AT, ZoneOffset.UTC)
        );
        EvidenceImportResponse importResponse = importer.importPayload(demoDataset());
        assertEquals("ACCEPTED", importResponse.status());
        assertEquals(30, importResponse.accepted());
        assertEquals(0, importResponse.rejected());

        new CreateDecisionUseCase(evidenceRepository, decisionRepository, transactionRunner).createDecision(
                new CreateDecisionCommand(
                        DECISION_ID,
                        evidenceId(201),
                        "Optimize AI onboarding assistant cost",
                        "Reduce recurring AI expenditure without losing exception-handling quality",
                        OWNER_ID,
                        APPROVER_ID,
                        timestamp("2026-07-01T09:00:00Z")
                )
        );

        GenerateRecommendationUseCase recommendationUseCase = new GenerateRecommendationUseCase(
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                transactionRunner,
                ignored -> Optional.of(new RecommendationExplanation(EXPLANATION))
        );
        GenerateRecommendationResult recommendationResult = recommendationUseCase.generateRecommendation(
                new GenerateRecommendationCommand(
                        RECOMMENDATION_ID,
                        DECISION_ID,
                        recommendationEvidenceIds(),
                        timestamp("2026-07-01T10:00:00Z")
                )
        );

        ProjectBusinessValueUseCase projectionUseCase = new ProjectBusinessValueUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
        assertThrows(
                BusinessRuleViolationException.class,
                () -> projectionUseCase.projectBusinessValue(DECISION_ID, recommendationResult.explanation())
        );

        ReviewDecisionUseCase reviewUseCase = new ReviewDecisionUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
        ReviewDecisionCommand approval = approvalCommand();
        reviewUseCase.reviewDecision(approval);

        AppendLedgerEntryUseCase ledgerUseCase = new AppendLedgerEntryUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
        AppendLedgerEntryCommand implementation = implementationCommand();
        AppendLedgerEntryCommand validation = validationCommand();
        ledgerUseCase.appendLedgerEntry(implementation);
        ledgerUseCase.appendLedgerEntry(validation);

        BusinessValueProjection first = projectionUseCase.projectBusinessValue(
                DECISION_ID,
                recommendationResult.explanation()
        );
        assertProjection(first);

        assertTrue(reviewUseCase.reviewDecision(approval).replayed());
        assertTrue(ledgerUseCase.appendLedgerEntry(implementation).replayed());
        assertTrue(ledgerUseCase.appendLedgerEntry(validation).replayed());
        BusinessValueProjection replay = projectionUseCase.projectBusinessValue(
                DECISION_ID,
                recommendationResult.explanation()
        );

        assertEquals(first, replay);
        assertEquals(3, ledgerRepository.findByDecisionId(DECISION_ID).size());
    }

    private void assertProjection(BusinessValueProjection projection) {
        assertEquals("DRC-AOA-001", projection.caseId());
        assertEquals(projection.caseId(), projection.correlationKey());
        assertEquals(DECISION_ID, projection.decisionId());
        assertEquals(DecisionStatus.APPROVED, projection.decisionStatus());
        assertEquals(RECOMMENDATION_ID, projection.recommendationId());
        assertEquals("MODEL_CHANGE", projection.recommendationType().value());
        assertEquals(Optional.of(EXPLANATION), projection.explanation());
        assertEquals(eur("19440.00"), projection.estimatedSavings());
        assertEquals(eur("18960.00"), projection.realizedSavings());
        assertEquals(new BigDecimal("-480.00"), projection.variance());
        assertEquals(eur("28080.00"), projection.annualizedBaselineCost());
        assertEquals(eur("9000.00"), projection.annualizedPostActionCost());
        assertEquals(eur("120.00"), projection.actualTransitionCost());
        assertEquals(92, projection.confidence().percentage());
        assertEquals(Severity.LOW, projection.risk());
        assertEquals("DRC-AOA-001-v1", projection.policyVersion());
        assertEquals(List.of("A-ROI-001", "A-ROI-002", "A-ROI-003", "A-ROI-004"), projection.assumptionIds());
        assertEquals(30, projection.evidenceIds().size());
        assertEquals(APPROVAL_ID, projection.approvalEntryId());
        assertEquals(IMPLEMENTATION_ID, projection.implementationEntryId());
        assertEquals(VALIDATION_ID, projection.validationEntryId());
        assertEquals(
                List.of(LedgerEntryType.APPROVED, LedgerEntryType.IMPLEMENTATION_MARKED, LedgerEntryType.RESULT_VALIDATED),
                projection.ledgerHistory().stream().map(BusinessValueProjection.LedgerFact::type).toList()
        );
        assertEquals(List.of(APPROVER_ID, IMPLEMENTER_ID, VALIDATOR_ID),
                projection.ledgerHistory().stream().map(BusinessValueProjection.LedgerFact::actorId).toList());
        assertFalse(projection.recommendedAction().isBlank());
        assertFalse(projection.deterministicReason().isBlank());
    }

    private ReviewDecisionCommand approvalCommand() {
        return new ReviewDecisionCommand(
                APPROVAL_ID,
                DECISION_ID,
                ReviewDecisionAction.APPROVE,
                APPROVER_ID,
                "ADMIN",
                timestamp("2026-07-01T11:00:00Z"),
                "Approved for controlled model-routing rollout",
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    private AppendLedgerEntryCommand implementationCommand() {
        return new AppendLedgerEntryCommand(
                IMPLEMENTATION_ID,
                DECISION_ID,
                IMPLEMENTER_ID,
                "PLATFORM_ENGINEER",
                timestamp("2026-07-15T10:30:00Z"),
                LedgerEntryType.IMPLEMENTATION_MARKED,
                "Lower-cost routing deployed with high-capability fallback",
                Set.of(evidenceId(229)),
                Optional.of(APPROVAL_ID),
                "2026-07",
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    private AppendLedgerEntryCommand validationCommand() {
        return new AppendLedgerEntryCommand(
                VALIDATION_ID,
                DECISION_ID,
                VALIDATOR_ID,
                "FINANCE",
                timestamp("2026-08-01T00:00:00Z"),
                LedgerEntryType.RESULT_VALIDATED,
                "Finance validated annualized recovery from post-action evidence",
                Set.of(evidenceId(230)),
                Optional.of(IMPLEMENTATION_ID),
                "2026-07",
                Optional.of(eur("28080.00")),
                Optional.of(eur("9000.00")),
                Optional.of(eur("120.00"))
        );
    }

    private byte[] demoDataset() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(
                "/evidence/drc-aoa-001-business-value-demo.jsonl"
        )) {
            if (stream == null) {
                throw new IOException("Business Value demo dataset is missing");
            }
            return stream.readAllBytes();
        }
    }

    private static Set<EvidenceId> recommendationEvidenceIds() {
        Set<EvidenceId> ids = new LinkedHashSet<>();
        IntStream.rangeClosed(201, 228).mapToObj(EndToEndBusinessValueDemoTest::evidenceId).forEach(ids::add);
        return Set.copyOf(ids);
    }

    private static Timestamp timestamp(String value) {
        return new Timestamp(Instant.parse(value));
    }

    private static ROIAmount eur(String value) {
        return new ROIAmount(Money.eur(new BigDecimal(value)));
    }

    private static EvidenceId evidenceId(int suffix) {
        return new EvidenceId(uuid(suffix));
    }

    private static DecisionId decisionId(int suffix) {
        return new DecisionId(uuid(suffix));
    }

    private static RecommendationId recommendationId(int suffix) {
        return new RecommendationId(uuid(suffix));
    }

    private static LedgerEntryId ledgerEntryId(int suffix) {
        return new LedgerEntryId(uuid(suffix));
    }

    private static UserId userId(int suffix) {
        return new UserId(uuid(suffix));
    }

    private static UUID uuid(int suffix) {
        return UUID.fromString("00000000-0000-4000-8000-" + String.format("%012d", suffix));
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
            return entries.values().stream().filter(entry -> entry.belongsTo(decisionId)).toList();
        }
    }
}
