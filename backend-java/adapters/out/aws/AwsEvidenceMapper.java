package imperator.adapters.out.aws;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.ports.out.EvidenceCandidate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

final class AwsEvidenceMapper {
    private static final UUID EVIDENCE_NAMESPACE = UUID.fromString(
            "c3747a5f-2576-45e2-a3ac-cbed72e7cf5c"
    );
    private static final String SOURCE = "AWS";
    private static final String SOURCE_TYPE = "cloud_cost";
    private static final String ENTITY = "ai-onboarding-assistant";
    private static final String CASE = "DRC-AOA-001";
    private static final String RESOURCE_GROUP = "onboarding-assistant-prod";

    private final AwsConnectorSettings settings;

    AwsEvidenceMapper(AwsConnectorSettings settings) {
        this.settings = settings;
    }

    EvidenceCandidate cost(
            AwsBillingPeriod period,
            AwsCostObservation cost,
            String freshness
    ) {
        String reference = "E-AWS-001";
        Map<String, String> metadata = commonMetadata(reference, freshness);
        metadata.put("billing_start", period.startInclusive().toString());
        metadata.put("billing_end", period.endExclusive().toString());
        metadata.put("metric", "UnblendedCost");
        metadata.put("monthly_cost", cost.monthlyCost().toPlainString());
        metadata.put("source_amount", canonicalDecimal(cost.sourceAmount()));
        metadata.put("currency", cost.currency());
        metadata.put("estimated", "false");
        putScopeTags(metadata);
        return candidate(
                reference,
                canonicalName(reference, period),
                period,
                "aws:ce:" + settings.expectedAccountId() + ":" + period.label()
                        + ":" + RESOURCE_GROUP,
                "cloud_cost_observed",
                "cloud_cost",
                "Finalized scoped AWS cost for " + period.label() + " was "
                        + cost.monthlyCost().toPlainString() + " " + cost.currency(),
                "Provides the infrastructure-cost input for DRC-AOA-001",
                metadata
        );
    }

    EvidenceCandidate resources(
            AwsBillingPeriod period,
            AwsResourceSnapshot resources,
            String freshness
    ) {
        String reference = "E-AWS-002";
        Map<String, String> metadata = commonMetadata(reference, freshness);
        metadata.put("region", settings.region());
        metadata.put("period", period.label());
        putScopeTags(metadata);
        metadata.put("resource_count", Integer.toString(resources.supportedResources().size()));
        metadata.put("services", resources.services().stream()
                .sorted()
                .collect(java.util.stream.Collectors.joining(",")));
        metadata.put("resource_set_sha256", resources.fingerprint());
        int count = resources.supportedResources().size();
        return candidate(
                reference,
                canonicalName(reference, period),
                period,
                "aws:tag:" + settings.expectedAccountId() + ":" + settings.region()
                        + ":" + period.label() + ":" + RESOURCE_GROUP,
                "cloud_resource_tag_observed",
                "cloud_cost",
                count + " scoped AWS resource" + (count == 1 ? "" : "s")
                        + " carried the exact DRC-AOA-001 correlation tags",
                "Connects AWS resources and attributed spend to IMP-214 and DRC-AOA-001",
                metadata
        );
    }

    EvidenceCandidate utilization(
            AwsBillingPeriod period,
            AwsResourceSnapshot resources,
            AwsUtilizationObservation utilization,
            String freshness
    ) {
        String reference = "E-AWS-003";
        Map<String, String> metadata = commonMetadata(reference, freshness);
        metadata.put("region", settings.region());
        metadata.put("period", period.label());
        metadata.put("lambda_count", Integer.toString(utilization.lambdaCount()));
        metadata.put("invocation_sum", utilization.invocationSum().toPlainString());
        metadata.put("error_sum", utilization.errorSum().toPlainString());
        metadata.put("duration_ms_sum", utilization.durationMillisecondsSum().toPlainString());
        metadata.put("metric_namespace", "AWS/Lambda");
        metadata.put("period_seconds", "86400");
        metadata.put("metric_set_version", "lambda-metrics-v1");
        metadata.put("resource_set_sha256", resources.fingerprint());
        return candidate(
                reference,
                canonicalName(reference, period),
                period,
                "aws:cloudwatch:" + settings.expectedAccountId() + ":" + settings.region()
                        + ":" + period.label() + ":" + RESOURCE_GROUP,
                "cloud_utilization_observed",
                "cloud_utilization",
                "Scoped Lambda utilization totalled "
                        + utilization.invocationSum().toPlainString() + " invocations, "
                        + utilization.errorSum().toPlainString() + " errors and "
                        + utilization.durationMillisecondsSum().toPlainString()
                        + " milliseconds duration",
                "Supplies bounded workload context without making an optimization decision",
                metadata
        );
    }

    EvidenceCandidate owner(
            AwsBillingPeriod period,
            AwsResourceSnapshot resources,
            String freshness
    ) {
        String reference = "E-AWS-004";
        Map<String, String> metadata = commonMetadata(reference, freshness);
        metadata.put("region", settings.region());
        metadata.put("period", period.label());
        metadata.put("owner", "platform-team");
        metadata.put("resource_count", Integer.toString(resources.supportedResources().size()));
        metadata.put("resource_set_sha256", resources.fingerprint());
        return candidate(
                reference,
                canonicalName(reference, period),
                period,
                "aws:tag:" + settings.expectedAccountId() + ":" + settings.region()
                        + ":" + period.label() + ":owner:platform-team",
                "cloud_owner_observed",
                "cloud_cost",
                "All " + resources.supportedResources().size()
                        + " scoped AWS resources were owned by platform-team",
                "Establishes technical AWS cost accountability for the resource group",
                metadata
        );
    }

    private EvidenceCandidate candidate(
            String reference,
            String canonicalName,
            AwsBillingPeriod period,
            String sourceObjectReference,
            String eventType,
            String evidenceType,
            String observedFact,
            String businessMeaning,
            Map<String, String> metadata
    ) {
        return new EvidenceCandidate(
                new EvidenceId(UuidV5.from(EVIDENCE_NAMESPACE, canonicalName)),
                reference,
                new Timestamp(period.observationTimestamp()),
                SOURCE,
                SOURCE_TYPE,
                sourceObjectReference,
                ENTITY,
                eventType,
                Severity.INFO,
                "aws-account:" + settings.expectedAccountId(),
                evidenceType,
                observedFact,
                businessMeaning,
                CASE,
                CASE,
                "CONFIDENTIAL",
                "HIGH",
                "ACCEPTED",
                "not_stored",
                Map.copyOf(metadata)
        );
    }

    private Map<String, String> commonMetadata(
            String reference,
            String freshness
    ) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("evidence_ref", reference);
        metadata.put("freshness", freshness);
        metadata.put("account_id", settings.expectedAccountId());
        return metadata;
    }

    private String canonicalName(String reference, AwsBillingPeriod period) {
        String prefix = reference + "|" + settings.expectedAccountId() + "|";
        return switch (reference) {
            case "E-AWS-001" -> prefix + period.label() + "|" + RESOURCE_GROUP;
            case "E-AWS-002" -> prefix + settings.region() + "|" + period.label()
                    + "|" + RESOURCE_GROUP;
            case "E-AWS-003" -> prefix + settings.region() + "|" + period.label()
                    + "|" + RESOURCE_GROUP + "|lambda-metrics-v1";
            case "E-AWS-004" -> prefix + settings.region() + "|" + period.label()
                    + "|" + RESOURCE_GROUP + "|platform-team";
            default -> throw new IllegalArgumentException("Unsupported AWS Evidence reference");
        };
    }

    private static void putScopeTags(Map<String, String> metadata) {
        metadata.put("project", "customer-onboarding");
        metadata.put("jira_ticket", "IMP-214");
        metadata.put("resource_group", RESOURCE_GROUP);
    }

    private static String canonicalDecimal(java.math.BigDecimal value) {
        return value.signum() == 0 ? "0" : value.stripTrailingZeros().toPlainString();
    }
}
