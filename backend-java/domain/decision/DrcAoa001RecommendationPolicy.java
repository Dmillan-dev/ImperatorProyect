package imperator.domain.decision;

import imperator.domain.evidence.Evidence;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Money;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Deterministic Recommendation and ROI policy frozen by D082. */
public final class DrcAoa001RecommendationPolicy {
    public static final String CASE_ID = "DRC-AOA-001";
    public static final String POLICY_VERSION = "DRC-AOA-001-v1";
    public static final String SUGGESTED_ACTION =
            "Change the AI Onboarding Assistant to a lower-cost model for standard onboarding requests, "
                    + "with a high-capability fallback for exceptions.";

    private static final String DETERMINISTIC_REASON =
            "Current monthly cost is EUR 2340.00 and projected monthly cost after the model change is EUR "
                    + "720.00, yielding estimated monthly recovery of EUR 1620.00 and estimated annualized "
                    + "recovery of EUR 19440.00 under the accepted assumptions.";
    private static final Set<String> MANDATORY_SUPPORT_REFERENCES = Set.of(
            "E-JIRA-001", "E-JIRA-002", "E-JIRA-003", "E-JIRA-004",
            "E-GH-001", "E-GH-002", "E-GH-003", "E-GH-004",
            "E-AWS-001", "E-AWS-002", "E-AWS-003", "E-AWS-004",
            "E-AI-001", "E-AI-002", "E-AI-003", "E-AI-004",
            "E-USAGE-001", "E-USAGE-002", "E-USAGE-003",
            "E-OWNER-001", "E-OWNER-002", "E-OWNER-003"
    );
    private static final String QUALITY_REFERENCE = "E-AI-005";
    private static final Set<String> REQUIRED_ASSUMPTIONS = Set.of(
            "A-ROI-001", "A-ROI-002", "A-ROI-003", "A-ROI-004"
    );
    private static final BigDecimal EXPECTED_AWS_MONTHLY_COST = new BigDecimal("410.00");
    private static final BigDecimal EXPECTED_AI_MONTHLY_COST = new BigDecimal("1930.00");
    private static final BigDecimal EXPECTED_PROJECTED_MONTHLY_COST = new BigDecimal("720.00");
    private static final BigDecimal EXPECTED_TRANSITION_COST = new BigDecimal("0.00");
    private static final BigDecimal EXPECTED_CURRENT_MONTHLY_COST = new BigDecimal("2340.00");
    private static final BigDecimal EXPECTED_MONTHLY_RECOVERY = new BigDecimal("1620.00");
    private static final BigDecimal EXPECTED_ANNUALIZED_RECOVERY = new BigDecimal("19440.00");

    public Recommendation evaluate(
            RecommendationId recommendationId,
            Decision decision,
            Set<Evidence> selectedEvidence,
            Timestamp generatedAt
    ) {
        Objects.requireNonNull(recommendationId, "Recommendation id is required");
        Decision authoritativeDecision = Objects.requireNonNull(decision, "Decision is required");
        Set<Evidence> evidence = requireEvidence(selectedEvidence);
        Timestamp creationTimestamp = Objects.requireNonNull(generatedAt, "Recommendation timestamp is required");

        requirePolicyDecision(authoritativeDecision, creationTimestamp);
        EvidenceIndex index = indexEvidence(evidence);
        requireCanonicalPack(authoritativeDecision, index);

        BigDecimal awsMonthlyCost = monetaryMetadata(index.support().get("E-AWS-001"), "monthly_cost");
        BigDecimal aiMonthlyCost = monetaryMetadata(index.support().get("E-AI-001"), "monthly_cost");
        BigDecimal projectedMonthlyCost = monetaryMetadata(index.assumptions().get("A-ROI-001"), "projected_monthly_cost");
        BigDecimal transitionCost = monetaryMetadata(index.assumptions().get("A-ROI-003"), "monthly_transition_cost");

        requireAmount(awsMonthlyCost, EXPECTED_AWS_MONTHLY_COST, "AWS monthly cost");
        requireAmount(aiMonthlyCost, EXPECTED_AI_MONTHLY_COST, "AI monthly cost");
        requireAmount(projectedMonthlyCost, EXPECTED_PROJECTED_MONTHLY_COST, "Projected monthly cost");
        requireAmount(transitionCost, EXPECTED_TRANSITION_COST, "Monthly transition cost");

        BigDecimal currentMonthlyCost = normalized(awsMonthlyCost.add(aiMonthlyCost));
        BigDecimal monthlyRecovery = normalized(currentMonthlyCost.subtract(projectedMonthlyCost).subtract(transitionCost));
        BigDecimal annualizedRecovery = normalized(monthlyRecovery.multiply(BigDecimal.valueOf(12)));

        requireAmount(currentMonthlyCost, EXPECTED_CURRENT_MONTHLY_COST, "Current monthly cost");
        requireAmount(monthlyRecovery, EXPECTED_MONTHLY_RECOVERY, "Estimated monthly recovery");
        requireAmount(annualizedRecovery, EXPECTED_ANNUALIZED_RECOVERY, "Estimated annualized recovery");
        if (monthlyRecovery.signum() <= 0) {
            throw new IllegalArgumentException("Estimated monthly recovery must be positive");
        }

        boolean hasQualityEvidence = index.support().containsKey(QUALITY_REFERENCE);
        return new Recommendation(
                recommendationId,
                authoritativeDecision.id(),
                RecommendationType.MODEL_CHANGE,
                SUGGESTED_ACTION,
                DETERMINISTIC_REASON,
                evidence.stream().map(Evidence::id).collect(java.util.stream.Collectors.toUnmodifiableSet()),
                new ROIAmount(Money.eur(annualizedRecovery)),
                new ROIConfidence(hasQualityEvidence ? 92 : 90),
                hasQualityEvidence ? Severity.LOW : Severity.MEDIUM,
                authoritativeDecision.ownerId(),
                authoritativeDecision.requiredApproverId(),
                creationTimestamp
        );
    }

    private Set<Evidence> requireEvidence(Set<Evidence> selectedEvidence) {
        Objects.requireNonNull(selectedEvidence, "Recommendation evidence is required");
        if (selectedEvidence.isEmpty() || selectedEvidence.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Recommendation evidence must contain non-null items");
        }
        return Set.copyOf(selectedEvidence);
    }

    private void requirePolicyDecision(Decision decision, Timestamp generatedAt) {
        if (!CASE_ID.equals(decision.caseId())) {
            throw new IllegalArgumentException("Decision correlation key must be " + CASE_ID);
        }
        if (generatedAt.value().isBefore(decision.createdAt().value())) {
            throw new IllegalArgumentException("Recommendation timestamp cannot be before Decision creation");
        }
        if (!DecisionStatus.CREATED.equals(decision.status()) && !decision.hasRecommendation()) {
            throw new IllegalArgumentException("Decision must be CREATED for first Recommendation generation");
        }
    }

    private EvidenceIndex indexEvidence(Set<Evidence> evidence) {
        Map<String, Evidence> support = new LinkedHashMap<>();
        Map<String, Evidence> assumptions = new LinkedHashMap<>();
        Evidence policyProvenance = null;

        for (Evidence item : evidence) {
            requireCommonEligibility(item);
            String evidenceReference = item.metadata().get("evidence_ref");
            String assumptionId = item.metadata().get("assumption_id");
            String policyVersion = item.metadata().get("policy_version");

            int classifications = (evidenceReference == null ? 0 : 1)
                    + (assumptionId == null ? 0 : 1)
                    + (policyVersion == null ? 0 : 1);
            if (classifications != 1) {
                throw new IllegalArgumentException("Evidence must have exactly one policy classification");
            }

            if (evidenceReference != null) {
                if (!MANDATORY_SUPPORT_REFERENCES.contains(evidenceReference)
                        && !QUALITY_REFERENCE.equals(evidenceReference)) {
                    throw new IllegalArgumentException("Unsupported canonical Evidence reference: " + evidenceReference);
                }
                putUnique(support, evidenceReference, item, "Evidence reference");
            } else if (assumptionId != null) {
                requireAssumptionEvidence(item);
                if (!REQUIRED_ASSUMPTIONS.contains(assumptionId)) {
                    throw new IllegalArgumentException("Unsupported ROI assumption: " + assumptionId);
                }
                putUnique(assumptions, assumptionId, item, "ROI assumption");
            } else {
                requireAssumptionEvidence(item);
                if (!POLICY_VERSION.equals(policyVersion) || policyProvenance != null) {
                    throw new IllegalArgumentException("Policy provenance must uniquely identify " + POLICY_VERSION);
                }
                policyProvenance = item;
            }
        }

        return new EvidenceIndex(Map.copyOf(support), Map.copyOf(assumptions), policyProvenance);
    }

    private void requireCanonicalPack(Decision decision, EvidenceIndex index) {
        if (!index.support().keySet().containsAll(MANDATORY_SUPPORT_REFERENCES)) {
            throw new IllegalArgumentException("Canonical Recommendation support Evidence is incomplete");
        }
        if (!index.assumptions().keySet().equals(REQUIRED_ASSUMPTIONS)) {
            throw new IllegalArgumentException("Canonical ROI assumption Evidence is incomplete");
        }
        if (index.policyProvenance() == null) {
            throw new IllegalArgumentException("Policy provenance Evidence is required");
        }
        Evidence origin = index.support().get("E-JIRA-001");
        if (!decision.originatingEvidenceId().equals(origin.id())) {
            throw new IllegalArgumentException("E-JIRA-001 must be the Decision originating Evidence");
        }

        requireMetadata(index.support().get("E-AWS-001"), "currency", "EUR");
        requireMetadata(index.support().get("E-AI-001"), "currency", "EUR");
        requireMetadata(index.assumptions().get("A-ROI-001"), "currency", "EUR");
        requireMetadata(index.assumptions().get("A-ROI-002"), "fallback", "high_capability_model");
        requireMetadata(index.assumptions().get("A-ROI-003"), "currency", "EUR");
        requireMetadata(index.assumptions().get("A-ROI-004"), "review_period", "2026-06");
    }

    private void requireCommonEligibility(Evidence evidence) {
        if (!CASE_ID.equals(evidence.correlationKey())) {
            throw new IllegalArgumentException("Recommendation Evidence must belong to " + CASE_ID);
        }
        if (!"ACCEPTED".equals(evidence.reviewStatus())) {
            throw new IllegalArgumentException("Recommendation Evidence must be ACCEPTED");
        }
        if ("RESTRICTED".equals(evidence.sensitivity())) {
            throw new IllegalArgumentException("RESTRICTED Evidence cannot support a Recommendation");
        }
        if (!"not_stored".equals(evidence.rawPayloadMode())) {
            throw new IllegalArgumentException("Recommendation Evidence raw payload mode must be not_stored");
        }
        if (!"fresh".equals(evidence.metadata().get("freshness"))) {
            throw new IllegalArgumentException("Recommendation Evidence must be fresh");
        }
    }

    private void requireAssumptionEvidence(Evidence evidence) {
        if (!"roi_assumption".equals(evidence.evidenceType())
                || !"roi_assumption_observed".equals(evidence.eventType())) {
            throw new IllegalArgumentException("ROI assumptions and policy provenance require roi_assumption Evidence");
        }
    }

    private BigDecimal monetaryMetadata(Evidence evidence, String key) {
        requireMetadata(evidence, "currency", "EUR");
        String value = evidence.metadata().get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing monetary Evidence metadata: " + key);
        }
        try {
            return normalized(new BigDecimal(value));
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new IllegalArgumentException("Invalid monetary Evidence metadata: " + key, exception);
        }
    }

    private BigDecimal normalized(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }

    private void requireAmount(BigDecimal actual, BigDecimal expected, String field) {
        if (actual.compareTo(expected) != 0) {
            throw new IllegalArgumentException(field + " does not match the frozen policy input");
        }
    }

    private void requireMetadata(Evidence evidence, String key, String expected) {
        if (!expected.equals(evidence.metadata().get(key))) {
            throw new IllegalArgumentException("Evidence metadata " + key + " must be " + expected);
        }
    }

    private void putUnique(Map<String, Evidence> index, String key, Evidence evidence, String label) {
        if (index.putIfAbsent(key, evidence) != null) {
            throw new IllegalArgumentException(label + " must be unique: " + key);
        }
    }

    private record EvidenceIndex(
            Map<String, Evidence> support,
            Map<String, Evidence> assumptions,
            Evidence policyProvenance
    ) {
    }
}
