package imperator.support;

import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DrcAoa001EvidenceFixture {
    private static final String CASE_ID = "DRC-AOA-001";

    private DrcAoa001EvidenceFixture() {
    }

    public static Set<Evidence> completePack(
            EvidenceId originatingEvidenceId,
            Timestamp observedAt,
            boolean includeQualityEvidence
    ) {
        Set<Evidence> evidence = new LinkedHashSet<>();
        evidence.add(support(
                originatingEvidenceId,
                observedAt,
                "E-JIRA-001",
                "Jira",
                "business_context",
                "business_context_requested",
                Map.of()
        ));
        evidence.add(support(observedAt, "E-JIRA-002", "Jira", "business_context", "decision_status_observed"));
        evidence.add(support(observedAt, "E-JIRA-003", "Jira", "business_context", "business_owner_observed"));
        evidence.add(support(observedAt, "E-JIRA-004", "Manual", "business_context", "business_value_proxy_observed"));

        evidence.add(support(observedAt, "E-GH-001", "GitHub", "code_deployment", "code_change_merged"));
        evidence.add(support(observedAt, "E-GH-002", "GitHub", "code_deployment", "code_review_observed"));
        evidence.add(support(observedAt, "E-GH-003", "GitHub", "code_deployment", "deployment_reference_observed"));
        evidence.add(support(observedAt, "E-GH-004", "Manual", "code_deployment", "implementation_feasibility_observed"));

        evidence.add(support(
                observedAt,
                "E-AWS-001",
                "AWS",
                "cloud_cost",
                "cloud_cost_observed",
                Map.of("currency", "EUR", "monthly_cost", "410.00")
        ));
        evidence.add(support(observedAt, "E-AWS-002", "AWS", "cloud_cost", "cloud_resource_tag_observed"));
        evidence.add(support(observedAt, "E-AWS-003", "AWS", "cloud_utilization", "cloud_utilization_observed"));
        evidence.add(support(observedAt, "E-AWS-004", "AWS", "cloud_cost", "cloud_owner_observed"));

        evidence.add(support(
                observedAt,
                "E-AI-001",
                "OpenAI + Anthropic Claude",
                "ai_consumption",
                "ai_cost_observed",
                Map.of("currency", "EUR", "monthly_cost", "1930.00")
        ));
        evidence.add(support(observedAt, "E-AI-002", "OpenAI + Anthropic Claude", "ai_consumption", "ai_model_observed"));
        evidence.add(support(observedAt, "E-AI-003", "OpenAI + Anthropic Claude", "ai_consumption", "ai_usage_observed"));
        evidence.add(support(observedAt, "E-AI-004", "OpenAI + Anthropic Claude", "ai_consumption", "ai_application_observed"));
        if (includeQualityEvidence) {
            evidence.add(support(observedAt, "E-AI-005", "Manual", "ai_quality_review", "ai_quality_review_observed"));
        }

        evidence.add(support(observedAt, "E-USAGE-001", "Product Analytics", "usage_value_signal", "active_user_count_observed"));
        evidence.add(support(observedAt, "E-USAGE-002", "Product Analytics", "usage_value_signal", "usage_signal_observed"));
        evidence.add(support(observedAt, "E-USAGE-003", "Manual", "usage_value_signal", "value_proxy_observed"));

        evidence.add(support(observedAt, "E-OWNER-001", "Manual", "owner_approval", "business_owner_observed"));
        evidence.add(support(observedAt, "E-OWNER-002", "Manual", "owner_approval", "technical_owner_observed"));
        evidence.add(support(observedAt, "E-OWNER-003", "Manual", "owner_approval", "approval_path_observed"));

        evidence.add(assumption(observedAt, "A-ROI-001", Map.of(
                "currency", "EUR",
                "projected_monthly_cost", "720.00"
        )));
        evidence.add(assumption(observedAt, "A-ROI-002", Map.of(
                "fallback", "high_capability_model"
        )));
        evidence.add(assumption(observedAt, "A-ROI-003", Map.of(
                "currency", "EUR",
                "monthly_transition_cost", "0.00"
        )));
        evidence.add(assumption(observedAt, "A-ROI-004", Map.of(
                "review_period", "2026-06"
        )));
        evidence.add(policyProvenance(observedAt));
        return Set.copyOf(evidence);
    }

    public static Set<EvidenceId> ids(Set<Evidence> evidence) {
        return evidence.stream().map(Evidence::id).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public static Evidence origin(Set<Evidence> evidence) {
        return evidence.stream()
                .filter(item -> "E-JIRA-001".equals(item.metadata().get("evidence_ref")))
                .findFirst()
                .orElseThrow();
    }

    private static Evidence support(
            Timestamp observedAt,
            String reference,
            String source,
            String evidenceType,
            String eventType
    ) {
        return support(new EvidenceId(UUID.randomUUID()), observedAt, reference, source, evidenceType, eventType, Map.of());
    }

    private static Evidence support(
            Timestamp observedAt,
            String reference,
            String source,
            String evidenceType,
            String eventType,
            Map<String, String> additionalMetadata
    ) {
        return support(new EvidenceId(UUID.randomUUID()), observedAt, reference, source, evidenceType, eventType, additionalMetadata);
    }

    private static Evidence support(
            EvidenceId id,
            Timestamp observedAt,
            String reference,
            String source,
            String evidenceType,
            String eventType,
            Map<String, String> additionalMetadata
    ) {
        Map<String, String> metadata = new java.util.LinkedHashMap<>(additionalMetadata);
        metadata.put("evidence_ref", reference);
        metadata.put("freshness", "fresh");
        return evidence(id, observedAt, source, evidenceType, eventType, Map.copyOf(metadata));
    }

    private static Evidence assumption(
            Timestamp observedAt,
            String assumptionId,
            Map<String, String> additionalMetadata
    ) {
        Map<String, String> metadata = new java.util.LinkedHashMap<>(additionalMetadata);
        metadata.put("assumption_id", assumptionId);
        metadata.put("freshness", "fresh");
        return evidence(
                new EvidenceId(UUID.randomUUID()),
                observedAt,
                "Manual",
                "roi_assumption",
                "roi_assumption_observed",
                Map.copyOf(metadata)
        );
    }

    private static Evidence policyProvenance(Timestamp observedAt) {
        return evidence(
                new EvidenceId(UUID.randomUUID()),
                observedAt,
                "Manual",
                "roi_assumption",
                "roi_assumption_observed",
                Map.of("freshness", "fresh", "policy_version", "DRC-AOA-001-v1")
        );
    }

    private static Evidence evidence(
            EvidenceId id,
            Timestamp observedAt,
            String source,
            String evidenceType,
            String eventType,
            Map<String, String> metadata
    ) {
        return new Evidence(
                id,
                observedAt,
                source,
                "manual",
                metadata.getOrDefault("evidence_ref", metadata.getOrDefault("assumption_id", "policy-version")),
                "ai-onboarding-assistant",
                eventType,
                Severity.INFO,
                "integration-fixture",
                evidenceType,
                "Accepted normalized Evidence for " + CASE_ID,
                "Supports the deterministic Recommendation policy",
                CASE_ID,
                "INTERNAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                metadata
        );
    }
}
