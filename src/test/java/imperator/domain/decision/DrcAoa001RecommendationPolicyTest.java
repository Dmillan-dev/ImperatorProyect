package imperator.domain.decision;

import imperator.domain.evidence.Evidence;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Money;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import imperator.support.DrcAoa001EvidenceFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DrcAoa001RecommendationPolicyTest {
    private static final Timestamp OBSERVED_AT = new Timestamp(Instant.parse("2026-06-30T10:00:00Z"));
    private static final Timestamp DECIDED_AT = new Timestamp(Instant.parse("2026-07-01T10:00:00Z"));
    private static final Timestamp GENERATED_AT = new Timestamp(Instant.parse("2026-07-02T10:00:00Z"));
    private static final String EXPECTED_REASON =
            "Current monthly cost is EUR 2340.00 and projected monthly cost after the model change is EUR "
                    + "720.00, yielding estimated monthly recovery of EUR 1620.00 and estimated annualized "
                    + "recovery of EUR 19440.00 under the accepted assumptions.";

    private final DrcAoa001RecommendationPolicy policy = new DrcAoa001RecommendationPolicy();

    @Test
    void completeCanonicalPackProducesTheFrozenRecommendationAndAnnualizedSavings() {
        EvidenceId originId = evidenceId();
        Set<Evidence> evidence = DrcAoa001EvidenceFixture.completePack(originId, OBSERVED_AT, true);
        Decision decision = decision(originId);
        RecommendationId recommendationId = recommendationId();

        Recommendation recommendation = policy.evaluate(
                recommendationId,
                decision,
                evidence,
                GENERATED_AT
        );

        assertAll(
                () -> assertEquals(recommendationId, recommendation.id()),
                () -> assertEquals(decision.id(), recommendation.decisionId()),
                () -> assertEquals(RecommendationType.MODEL_CHANGE, recommendation.type()),
                () -> assertEquals(DrcAoa001RecommendationPolicy.SUGGESTED_ACTION, recommendation.suggestedAction()),
                () -> assertEquals(EXPECTED_REASON, recommendation.reason()),
                () -> assertEquals(DrcAoa001EvidenceFixture.ids(evidence), recommendation.evidenceIds()),
                () -> assertEquals(
                        new ROIAmount(Money.eur(new BigDecimal("19440.00"))),
                        recommendation.estimatedSavings()
                ),
                () -> assertEquals(new ROIConfidence(92), recommendation.confidence()),
                () -> assertEquals(Severity.LOW, recommendation.risk()),
                () -> assertEquals(decision.ownerId(), recommendation.ownerId()),
                () -> assertEquals(decision.requiredApproverId(), recommendation.requiredApproverId()),
                () -> assertEquals(GENERATED_AT, recommendation.createdAt())
        );
    }

    @Test
    void missingOnlyQualityEvidenceProducesTheSingleMediumRiskBranch() {
        EvidenceId originId = evidenceId();
        Set<Evidence> evidence = DrcAoa001EvidenceFixture.completePack(originId, OBSERVED_AT, false);

        Recommendation recommendation = policy.evaluate(
                recommendationId(),
                decision(originId),
                evidence,
                GENERATED_AT
        );

        assertAll(
                () -> assertEquals(new ROIConfidence(90), recommendation.confidence()),
                () -> assertEquals(Severity.MEDIUM, recommendation.risk()),
                () -> assertEquals(
                        new ROIAmount(Money.eur(new BigDecimal("19440.00"))),
                        recommendation.estimatedSavings()
                )
        );
    }

    @Test
    void missingMandatoryEvidenceMakesThePolicyNotReady() {
        EvidenceId originId = evidenceId();
        Set<Evidence> complete = DrcAoa001EvidenceFixture.completePack(originId, OBSERVED_AT, true);
        Evidence omitted = complete.stream()
                .filter(item -> "E-GH-004".equals(item.metadata().get("evidence_ref")))
                .findFirst()
                .orElseThrow();
        Set<Evidence> incomplete = complete.stream()
                .filter(item -> !item.id().equals(omitted.id()))
                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        assertThrows(
                IllegalArgumentException.class,
                () -> policy.evaluate(recommendationId(), decision(originId), incomplete, GENERATED_AT)
        );
    }

    private Decision decision(EvidenceId originId) {
        return Decision.create(
                new DecisionId(UUID.randomUUID()),
                DrcAoa001RecommendationPolicy.CASE_ID,
                "Optimize AI Onboarding Assistant cost",
                "Reduce recurring AI spend without unacceptable quality loss",
                originId,
                new UserId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                DECIDED_AT
        );
    }

    private EvidenceId evidenceId() {
        return new EvidenceId(UUID.randomUUID());
    }

    private RecommendationId recommendationId() {
        return new RecommendationId(UUID.randomUUID());
    }
}
