package imperator.api.recommendations;

import imperator.application.query.RecommendationExplanationView;
import imperator.application.query.RecommendationView;
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
import imperator.ports.out.ExplanationStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationRestMapperTest {

    @Test
    void mapsTheExplicitExplanationStateWithoutChangingDeterministicFields() {
        RecommendationExplanationView explanation = new RecommendationExplanationView(
                uuid("70000000-0000-4000-8000-000000000007"),
                ExplanationStatus.GENERATED,
                Optional.of("Generated explanation"),
                "amazon-bedrock",
                "eu.amazon.nova-micro-v1:0",
                "imperator-explanation-v1",
                timestamp("2026-07-01T00:02:00Z"),
                timestamp("2026-07-01T00:02:01Z"),
                Optional.of(100),
                Optional.of(25),
                Optional.of(500L),
                Optional.empty(),
                List.of(new EvidenceId(uuid("20000000-0000-4000-8000-000000000002"))),
                List.of("A-ROI-001")
        );
        RecommendationView view = new RecommendationView(
                new RecommendationId(uuid("50000000-0000-4000-8000-000000000005")),
                new DecisionId(uuid("10000000-0000-4000-8000-000000000001")),
                RecommendationType.MODEL_CHANGE,
                "Use the approved lower-cost model",
                "Deterministic policy result",
                new ROIAmount(Money.eur(new BigDecimal("19440.00"))),
                new ROIConfidence(92),
                Severity.LOW,
                new UserId(uuid("30000000-0000-4000-8000-000000000003")),
                new UserId(uuid("40000000-0000-4000-8000-000000000004")),
                timestamp("2026-07-01T00:01:00Z"),
                explanation.evidenceIds(),
                Optional.of(explanation)
        );

        RecommendationResponse response = RecommendationRestMapper.response(view);

        assertEquals("Deterministic policy result", response.deterministicReason());
        assertEquals("GENERATED", response.explanation().status());
        assertEquals("Generated explanation", response.explanation().text());
        assertEquals("imperator-explanation-v1", response.explanation().promptVersion());
    }

    private static UUID uuid(String value) {
        return UUID.fromString(value);
    }

    private static Timestamp timestamp(String value) {
        return new Timestamp(Instant.parse(value));
    }
}
