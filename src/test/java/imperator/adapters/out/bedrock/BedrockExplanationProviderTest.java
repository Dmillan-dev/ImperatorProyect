package imperator.adapters.out.bedrock;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.Severity;
import imperator.ports.out.RecommendationExplanation;
import imperator.ports.out.RecommendationExplanationEvidence;
import imperator.ports.out.RecommendationExplanationRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BedrockExplanationProviderTest {
    private static final EvidenceId EVIDENCE_ID = new EvidenceId(
            UUID.fromString("20000000-0000-4000-8000-000000000001")
    );

    @Test
    void createsABoundedExplanationFromValidatedJson() {
        CapturingGateway gateway = new CapturingGateway("""
                {
                  "summary": "The accepted cost Evidence supports the deterministic Recommendation.",
                  "rationale": "The projected annual recovery is consistent with the policy output.",
                  "evidenceReferences": ["E-AWS-001"],
                  "assumptionIds": ["A-ROI-001"],
                  "limitations": ["Savings remain estimates."],
                  "humanReview": "Confirm service quality and approve or reject the Recommendation."
                }
                """);
        BedrockExplanationProvider provider = provider(gateway);

        Optional<RecommendationExplanation> generated = provider.generateExplanation(request("INTERNAL"));

        assertAll(
                () -> assertTrue(generated.isPresent()),
                () -> assertTrue(generated.orElseThrow().text().contains("Evidence: E-AWS-001")),
                () -> assertEquals(BedrockExplanationProvider.PROVIDER_NAME,
                        generated.orElseThrow().provider()),
                () -> assertEquals(321, generated.orElseThrow().inputTokens()),
                () -> assertTrue(gateway.userPrompt().contains("\"deterministicReason\"")),
                () -> assertFalse(gateway.userPrompt().contains("raw_payload")),
                () -> assertEquals(1, gateway.calls())
        );
    }

    @Test
    void rejectsReferencesThatWereNotSuppliedByImperator() {
        CapturingGateway gateway = new CapturingGateway("""
                {
                  "summary": "Summary",
                  "rationale": "Rationale",
                  "evidenceReferences": ["E-INVENTED-999"],
                  "assumptionIds": ["A-ROI-001"],
                  "limitations": ["Savings remain estimates."],
                  "humanReview": "Confirm service quality before deciding."
                }
                """);

        assertThrows(
                IllegalArgumentException.class,
                () -> provider(gateway).generateExplanation(request("INTERNAL"))
        );
        assertEquals(1, gateway.calls());
    }

    @Test
    void rejectsAnExplanationThatOmitsASuppliedAssumption() {
        CapturingGateway gateway = new CapturingGateway("""
                {
                  "summary": "Summary",
                  "rationale": "Rationale",
                  "evidenceReferences": ["E-AWS-001"],
                  "assumptionIds": ["A-ROI-001"],
                  "limitations": ["Savings remain estimates."],
                  "humanReview": "Confirm service quality before deciding."
                }
                """);

        assertThrows(
                IllegalArgumentException.class,
                () -> provider(gateway).generateExplanation(request(
                        "INTERNAL",
                        "Monthly cost is EUR 2340.00",
                        List.of("A-ROI-001", "A-ROI-002")
                ))
        );
        assertEquals(1, gateway.calls());
    }

    @Test
    void refusesRestrictedEvidenceBeforeAnyProviderCall() {
        CapturingGateway gateway = new CapturingGateway("{}");

        assertThrows(
                IllegalArgumentException.class,
                () -> provider(gateway).generateExplanation(request("RESTRICTED"))
        );
        assertEquals(0, gateway.calls());
    }

    @Test
    void refusesSecretLikeMaterialBeforeAnyProviderCall() {
        CapturingGateway gateway = new CapturingGateway("{}");

        assertThrows(
                IllegalArgumentException.class,
                () -> provider(gateway).generateExplanation(request(
                        "INTERNAL",
                        "Authorization used Bearer abcdefghijklmnopqrstuvwxyz012345"
                ))
        );
        assertEquals(0, gateway.calls());
    }

    private static BedrockExplanationProvider provider(CapturingGateway gateway) {
        return new BedrockExplanationProvider(
                new BedrockExplanationSettings(
                        "eu-west-1",
                        "eu.amazon.nova-micro-v1:0",
                        700,
                        0.0F
                ),
                gateway
        );
    }

    private static RecommendationExplanationRequest request(String sensitivity) {
        return request(sensitivity, "Monthly cost is EUR 2340.00");
    }

    private static RecommendationExplanationRequest request(String sensitivity, String observedFact) {
        return request(sensitivity, observedFact, List.of("A-ROI-001"));
    }

    private static RecommendationExplanationRequest request(
            String sensitivity,
            String observedFact,
            List<String> assumptionIds
    ) {
        return new RecommendationExplanationRequest(
                new RecommendationId(UUID.fromString("50000000-0000-4000-8000-000000000001")),
                new DecisionId(UUID.fromString("10000000-0000-4000-8000-000000000001")),
                "DRC-AOA-001",
                "Reduce recurring AI operating cost",
                RecommendationType.MODEL_CHANGE,
                "Use the approved lower-cost model",
                "Accepted Evidence satisfies DRC-AOA-001-v1",
                new ROIAmount(Money.eur(new BigDecimal("19440.00"))),
                new ROIConfidence(92),
                Severity.LOW,
                List.of(EVIDENCE_ID),
                assumptionIds,
                "DRC-AOA-001-v1",
                List.of(new RecommendationExplanationEvidence(
                        EVIDENCE_ID,
                        "E-AWS-001",
                        observedFact,
                        "The measured baseline supports a lower-cost model evaluation",
                        sensitivity,
                        "HIGH"
                ))
        );
    }

    private static final class CapturingGateway implements BedrockConverseGateway {
        private final String response;
        private String userPrompt;
        private int calls;

        private CapturingGateway(String response) {
            this.response = response;
        }

        @Override
        public BedrockConverseResult converse(String systemPrompt, String prompt) {
            calls++;
            userPrompt = prompt;
            return new BedrockConverseResult(response, 321, 87, 640L);
        }

        private String userPrompt() {
            return userPrompt;
        }

        private int calls() {
            return calls;
        }
    }
}
