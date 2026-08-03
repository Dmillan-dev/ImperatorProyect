package imperator.api.decisions;

import imperator.application.query.DecisionTimelineItem;
import imperator.application.query.EvidenceSummary;
import imperator.application.query.PageResult;
import imperator.domain.shared.EvidenceId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
final class DecisionEvidenceAuthorizationPolicy {
    private static final String ADMIN = "ADMIN";
    private static final String PLATFORM_ENGINEER = "PLATFORM_ENGINEER";
    private static final String FINANCE = "FINANCE";
    private static final String AUDITOR = "AUDITOR";
    private static final String RESTRICTED = "RESTRICTED";
    private static final String REDACTED = "[REDACTED]";
    private static final Set<String> FINANCE_VISIBLE_TYPES = Set.of(
            "business_context",
            "cloud_cost",
            "cloud_utilization",
            "ai_consumption",
            "ai_quality_review",
            "usage_value_signal",
            "owner_approval",
            "roi_assumption"
    );

    PageResult<EvidenceSummary> authorizeEvidence(
            PageResult<EvidenceSummary> page,
            String role
    ) {
        return new PageResult<>(
                page.items().stream().map(item -> authorize(item, role)).toList(),
                page.page(), page.size(), page.totalItems(), page.totalPages()
        );
    }

    PageResult<DecisionTimelineItem> authorizeTimeline(
            PageResult<DecisionTimelineItem> page,
            String role,
            Map<EvidenceId, EvidenceSummary> evidenceById
    ) {
        return new PageResult<>(
                page.items().stream()
                        .map(item -> authorize(item, role, evidenceById))
                        .toList(),
                page.page(), page.size(), page.totalItems(), page.totalPages()
        );
    }

    private EvidenceSummary authorize(EvidenceSummary item, String role) {
        if (RESTRICTED.equals(item.sensitivity())) {
            return redactRestricted(item);
        }
        if ("PUBLIC".equals(item.sensitivity()) || "INTERNAL".equals(item.sensitivity())) {
            return item;
        }
        if (Set.of(ADMIN, PLATFORM_ENGINEER, AUDITOR).contains(role)) {
            return item;
        }
        if (FINANCE.equals(role)
                && FINANCE_VISIBLE_TYPES.contains(item.evidenceType())
                && !"ai_sensitive_payload_rejected".equals(item.eventType())) {
            return item;
        }
        return redactConfidential(item);
    }

    private DecisionTimelineItem authorize(
            DecisionTimelineItem item,
            String role,
            Map<EvidenceId, EvidenceSummary> evidenceById
    ) {
        if (!"EVIDENCE".equals(item.type())) {
            return item;
        }

        EvidenceSummary evidence = evidenceById.get(new EvidenceId(item.referenceId()));
        if (evidence == null || isRedacted(authorize(evidence, role))) {
            return new DecisionTimelineItem(
                    item.type(), item.referenceId(), item.occurredAt(), REDACTED,
                    Optional.empty(), Optional.empty(), item.confidenceLabel(),
                    item.confidencePercentage(), item.evidenceIds()
            );
        }
        return item;
    }

    private boolean isRedacted(EvidenceSummary item) {
        return REDACTED.equals(item.observedFact());
    }

    private EvidenceSummary redactConfidential(EvidenceSummary item) {
        return new EvidenceSummary(
                item.evidenceId(), item.timestamp(), item.source(), item.sourceType(), REDACTED,
                REDACTED, item.eventType(), item.severity(), REDACTED, item.evidenceType(),
                REDACTED, REDACTED, REDACTED, item.sensitivity(), item.confidence(),
                item.reviewStatus()
        );
    }

    private EvidenceSummary redactRestricted(EvidenceSummary item) {
        return new EvidenceSummary(
                item.evidenceId(), item.timestamp(), REDACTED, REDACTED, REDACTED, REDACTED,
                REDACTED, item.severity(), REDACTED, REDACTED, REDACTED, REDACTED, REDACTED,
                item.sensitivity(), item.confidence(), item.reviewStatus()
        );
    }
}
