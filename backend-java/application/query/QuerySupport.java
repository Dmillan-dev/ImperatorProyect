package imperator.application.query;

import imperator.application.exceptions.BusinessRuleViolationException;
import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.decision.DrcAoa001RecommendationPolicy;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.shared.Currency;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;
import imperator.ports.out.MvpReadModelQueryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

final class QuerySupport {
    private QuerySupport() {
    }

    static MvpDecisionReadSnapshot snapshot(MvpReadModelQueryPort port, DecisionId id) {
        return port.findDecisionSnapshot(id).orElseThrow(() -> new DecisionNotFoundException(id));
    }

    static DecisionSummary summary(Decision decision) {
        return new DecisionSummary(
                decision.id(), decision.caseId(), decision.title(), decision.businessNeed(),
                decision.status(), decision.ownerId(), decision.requiredApproverId(),
                decision.recommendationId(), decision.createdAt(), decision.updatedAt()
        );
    }

    static DecisionDetail detail(Decision decision) {
        return new DecisionDetail(
                decision.id(), decision.caseId(), decision.title(), decision.businessNeed(),
                decision.status(), decision.ownerId(), decision.requiredApproverId(),
                decision.recommendationId(), decision.createdAt(), decision.updatedAt(),
                decision.originatingEvidenceId(), sortedIds(decision.evidenceIds()),
                decision.reviewedBy(), decision.reviewedAt(), decision.reviewReason()
        );
    }

    static EvidenceSummary evidence(Evidence item) {
        return new EvidenceSummary(
                item.id(), item.timestamp(), item.source(), item.sourceType(), item.sourceObjectRef(),
                item.entity(), item.eventType(), item.severity(), item.actor(), item.evidenceType(),
                item.observedFact(), item.businessMeaning(), item.correlationKey(), item.sensitivity(),
                item.confidence(), item.reviewStatus()
        );
    }

    static RecommendationView recommendation(Recommendation item) {
        return new RecommendationView(
                item.id(), item.decisionId(), item.type(), item.suggestedAction(), item.reason(),
                item.estimatedSavings(), item.confidence(), item.risk(), item.ownerId(),
                item.requiredApproverId(), item.createdAt(), sortedIds(item.evidenceIds())
        );
    }

    static LedgerEntryView ledger(LedgerEntry item) {
        return new LedgerEntryView(
                item.id(), item.decisionId(), item.recommendationId(), item.actorId(), item.actorRole(),
                item.occurredAt(), item.entryType(), item.changeSummary(), item.reason(),
                sortedIds(item.evidenceSnapshotIds()), item.estimatedSaving(), item.realizedSaving(),
                item.confidenceSnapshot(), item.riskSnapshot(), item.previousEntryId(), item.metadata()
        );
    }

    static List<DecisionTimelineItem> timeline(MvpDecisionReadSnapshot snapshot) {
        List<DecisionTimelineItem> items = new ArrayList<>();
        Decision decision = snapshot.decision();
        items.add(new DecisionTimelineItem(
                "DECISION", decision.id().value(), decision.createdAt(), decision.title(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                sortedIds(decision.evidenceIds())
        ));
        for (Evidence evidence : snapshot.evidence()) {
            items.add(new DecisionTimelineItem(
                    "EVIDENCE", evidence.id().value(), evidence.timestamp(), evidence.observedFact(),
                    Optional.of(evidence.source()), Optional.of(evidence.actor()),
                    Optional.of(evidence.confidence()), Optional.empty(), List.of(evidence.id())
            ));
        }
        snapshot.recommendation().ifPresent(recommendation -> items.add(new DecisionTimelineItem(
                "RECOMMENDATION", recommendation.id().value(), recommendation.createdAt(),
                recommendation.reason(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(recommendation.confidence().percentage()), sortedIds(recommendation.evidenceIds())
        )));
        for (LedgerEntry entry : snapshot.ledgerEntries()) {
            items.add(new DecisionTimelineItem(
                    "LEDGER", entry.id().value(), entry.occurredAt(), entry.changeSummary(),
                    Optional.empty(), Optional.of(entry.actorId().value().toString()), Optional.empty(),
                    entry.confidenceSnapshot().map(value -> value.percentage()),
                    sortedIds(entry.evidenceSnapshotIds())
            ));
        }
        items.sort(Comparator
                .comparing((DecisionTimelineItem item) -> item.occurredAt().value())
                .thenComparingInt(item -> typeOrder(item.type()))
                .thenComparing(item -> item.referenceId().toString()));
        return List.copyOf(items);
    }

    static DecisionRoiView roi(MvpDecisionReadSnapshot snapshot) {
        Recommendation recommendation = snapshot.recommendation()
                .orElseThrow(() -> roiNotReady(snapshot.decision().id(), "Recommendation is missing"));
        Map<String, Evidence> references = uniqueByMetadata(snapshot, "evidence_ref");
        Map<String, Evidence> assumptions = uniqueByMetadata(snapshot, "assumption_id");
        Evidence aws = require(references, "E-AWS-001", snapshot.decision().id());
        Evidence ai = require(references, "E-AI-001", snapshot.decision().id());
        Evidence projected = require(assumptions, "A-ROI-001", snapshot.decision().id());
        Evidence transition = require(assumptions, "A-ROI-003", snapshot.decision().id());
        String policyVersion = uniquePolicyVersion(snapshot, recommendation);

        BigDecimal current = amount(aws, "monthly_cost", snapshot.decision().id())
                .add(amount(ai, "monthly_cost", snapshot.decision().id()))
                .setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal projectedAmount = amount(projected, "projected_monthly_cost", snapshot.decision().id());
        BigDecimal transitionAmount = amount(transition, "monthly_transition_cost", snapshot.decision().id());
        BigDecimal monthly = current.subtract(projectedAmount).subtract(transitionAmount)
                .setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal annualized = monthly.multiply(BigDecimal.valueOf(12)).setScale(2, RoundingMode.HALF_EVEN);
        if (annualized.compareTo(recommendation.estimatedSavings().value().amount()) != 0) {
            throw roiNotReady(snapshot.decision().id(), "Persisted annualized recovery is inconsistent");
        }

        List<EvidenceId> assumptionIds = assumptions.values().stream()
                .map(Evidence::id)
                .sorted(Comparator.comparing(id -> id.value().toString()))
                .toList();
        return new DecisionRoiView(
                snapshot.decision().id(), recommendation.id(), eur(current), eur(projectedAmount),
                eur(transitionAmount), eur(monthly), recommendation.estimatedSavings(),
                recommendation.confidence(), recommendation.risk(), policyVersion, assumptionIds
        );
    }

    static void requireSort(PageRequest request, Set<String> allowed, boolean ascendingOnly) {
        if (!allowed.contains(request.sort()) || (ascendingOnly && request.direction() != SortDirection.ASC)) {
            throw new ValidationException("INVALID_PAGINATION", "Unsupported sort contract");
        }
    }

    static <T> PageResult<T> page(List<T> allItems, PageRequest request) {
        long offset = (long) request.page() * request.size();
        int from = offset >= allItems.size() ? allItems.size() : (int) offset;
        int to = Math.min(from + request.size(), allItems.size());
        return PageResult.of(allItems.subList(from, to), request, allItems.size());
    }

    static <S, T> PageResult<T> mapPage(PageResult<S> source, Function<S, T> mapper) {
        return new PageResult<>(
                source.items().stream().map(mapper).toList(), source.page(), source.size(),
                source.totalItems(), source.totalPages()
        );
    }

    static List<EvidenceId> sortedIds(Set<EvidenceId> ids) {
        return ids.stream().sorted(Comparator.comparing(id -> id.value().toString())).toList();
    }

    private static Map<String, Evidence> uniqueByMetadata(MvpDecisionReadSnapshot snapshot, String key) {
        Map<String, Evidence> indexed = new LinkedHashMap<>();
        Set<EvidenceId> recommendationIds = snapshot.recommendation()
                .map(Recommendation::evidenceIds)
                .orElse(Set.of());
        for (Evidence evidence : snapshot.evidence()) {
            if (!recommendationIds.contains(evidence.id())) {
                continue;
            }
            String value = evidence.metadata().get(key);
            if (value != null && indexed.putIfAbsent(value, evidence) != null) {
                throw roiNotReady(snapshot.decision().id(), "ROI provenance is ambiguous");
            }
        }
        return Map.copyOf(indexed);
    }

    private static Evidence require(Map<String, Evidence> index, String key, DecisionId decisionId) {
        Evidence evidence = index.get(key);
        if (evidence == null || !"ACCEPTED".equals(evidence.reviewStatus())) {
            throw roiNotReady(decisionId, "Required ROI Evidence is missing");
        }
        return evidence;
    }

    private static BigDecimal amount(Evidence evidence, String key, DecisionId decisionId) {
        if (!"EUR".equals(evidence.metadata().get("currency"))) {
            throw roiNotReady(decisionId, "ROI currency is inconsistent");
        }
        try {
            return new BigDecimal(evidence.metadata().get(key)).setScale(2, RoundingMode.UNNECESSARY);
        } catch (NumberFormatException | ArithmeticException | NullPointerException exception) {
            throw roiNotReady(decisionId, "ROI amount is invalid");
        }
    }

    private static String uniquePolicyVersion(MvpDecisionReadSnapshot snapshot, Recommendation recommendation) {
        Set<String> versions = snapshot.evidence().stream()
                .filter(evidence -> recommendation.evidenceIds().contains(evidence.id()))
                .map(evidence -> evidence.metadata().get("policy_version"))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (!versions.equals(Set.of(DrcAoa001RecommendationPolicy.POLICY_VERSION))) {
            throw roiNotReady(snapshot.decision().id(), "Policy provenance is incomplete");
        }
        return versions.iterator().next();
    }

    private static ROIAmount eur(BigDecimal amount) {
        return new ROIAmount(new Money(amount, Currency.EUR));
    }

    private static BusinessRuleViolationException roiNotReady(DecisionId decisionId, String reason) {
        return new BusinessRuleViolationException(
                "ROI_NOT_READY", "ROI is not ready for Decision " + decisionId.value() + ": " + reason
        );
    }

    private static int typeOrder(String type) {
        return switch (type) {
            case "DECISION" -> 0;
            case "EVIDENCE" -> 1;
            case "RECOMMENDATION" -> 2;
            case "LEDGER" -> 3;
            default -> throw new IllegalArgumentException("Unsupported timeline type");
        };
    }
}
