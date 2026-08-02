package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.decision.DrcAoa001RecommendationPolicy;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.ports.out.MvpReadModelQueryPort;
import imperator.support.DrcAoa001EvidenceFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MvpQueryUseCasesTest {
    private Decision decision;
    private Recommendation recommendation;
    private Set<Evidence> evidence;
    private MvpReadModelQueryPort readModel;

    @BeforeEach
    void setUp() {
        Timestamp observedAt = new Timestamp(Instant.parse("2026-06-30T23:00:00Z"));
        EvidenceId originId = new EvidenceId(UUID.fromString("10000000-0000-4000-8000-000000000001"));
        evidence = DrcAoa001EvidenceFixture.completePack(originId, observedAt, true);
        decision = Decision.create(
                new DecisionId(UUID.fromString("20000000-0000-4000-8000-000000000002")),
                DrcAoa001RecommendationPolicy.CASE_ID,
                "Reduce AI onboarding cost",
                "Recover avoidable AI spend",
                originId,
                new UserId(UUID.fromString("30000000-0000-4000-8000-000000000003")),
                new UserId(UUID.fromString("40000000-0000-4000-8000-000000000004")),
                new Timestamp(Instant.parse("2026-07-01T00:00:00Z"))
        );
        recommendation = new DrcAoa001RecommendationPolicy().evaluate(
                new RecommendationId(UUID.fromString("50000000-0000-4000-8000-000000000005")),
                decision,
                evidence,
                new Timestamp(Instant.parse("2026-07-01T00:01:00Z"))
        );
        decision.attachRecommendation(recommendation.id(), recommendation.createdAt());
        readModel = new FixedReadModel();
    }

    @Test
    void reconstructsTheFrozenRoiOnlyFromPersistedRecommendationAndEvidence() {
        DecisionRoiView result = new GetDecisionRoiUseCase(readModel).getDecisionRoi(
                new GetDecisionRoiQuery(decision.id())
        );

        assertEquals(new BigDecimal("2340.00"), result.currentMonthlyCost().value().amount());
        assertEquals(new BigDecimal("720.00"), result.projectedMonthlyCost().value().amount());
        assertEquals(new BigDecimal("1620.00"), result.estimatedMonthlyRecovery().value().amount());
        assertEquals(recommendation.estimatedSavings(), result.estimatedAnnualizedRecovery());
        assertEquals(DrcAoa001RecommendationPolicy.POLICY_VERSION, result.policyVersion());
        assertEquals(4, result.assumptionEvidenceIds().size());
    }

    @Test
    void appliesFrozenPaginationAndCausalDirectionRules() {
        PageResult<DecisionTimelineItem> timeline = new GetDecisionTimelineUseCase(readModel)
                .getDecisionTimeline(new GetDecisionTimelineQuery(
                        decision.id(), new PageRequest(0, 5, "occurredAt", SortDirection.ASC)
                ));

        assertEquals(5, timeline.items().size());
        assertEquals("EVIDENCE", timeline.items().getFirst().type());
        assertThrows(ValidationException.class, () -> new GetDecisionTimelineUseCase(readModel)
                .getDecisionTimeline(new GetDecisionTimelineQuery(
                        decision.id(), new PageRequest(0, 20, "occurredAt", SortDirection.DESC)
                )));
    }

    @Test
    void exposesOnlyImmutableApplicationProjections() {
        DecisionDetail detail = new GetDecisionUseCase(readModel).getDecision(new GetDecisionQuery(decision.id()));
        RecommendationView view = new GetRecommendationUseCase(readModel)
                .getRecommendation(new GetRecommendationQuery(recommendation.id()));

        assertEquals(decision.id(), detail.decisionId());
        assertEquals(recommendation.reason(), view.deterministicReason());
        assertEquals(recommendation.evidenceIds().size(), view.evidenceIds().size());
    }

    private final class FixedReadModel implements MvpReadModelQueryPort {
        @Override
        public PageResult<Decision> findDecisions(PageRequest pageRequest) {
            return PageResult.of(List.of(decision), pageRequest, 1);
        }

        @Override
        public Optional<MvpDecisionReadSnapshot> findDecisionSnapshot(DecisionId decisionId) {
            if (!decision.id().equals(decisionId)) {
                return Optional.empty();
            }
            return Optional.of(new MvpDecisionReadSnapshot(
                    decision, evidence.stream().toList(), Optional.of(recommendation), List.of()
            ));
        }

        @Override
        public Optional<Recommendation> findRecommendation(RecommendationId recommendationId) {
            return recommendation.id().equals(recommendationId)
                    ? Optional.of(recommendation)
                    : Optional.empty();
        }

        @Override
        public PageResult<LedgerEntry> findLedgerEntries(PageRequest pageRequest) {
            return PageResult.of(List.of(), pageRequest, 0);
        }
    }
}
