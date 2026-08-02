package imperator.api.decisions;

import imperator.api.errors.ApiContractException;
import imperator.application.query.DecisionDetail;
import imperator.application.query.DecisionRoiView;
import imperator.application.query.DecisionSummary;
import imperator.application.query.DecisionTimelineItem;
import imperator.application.query.EvidenceSummary;
import imperator.application.query.PageRequest;
import imperator.application.query.PageResult;
import imperator.application.query.SortDirection;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.ROIAmount;
import org.springframework.http.HttpStatus;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

final class DecisionRestMapper {
    private DecisionRestMapper() {
    }

    static DecisionId decisionId(String raw) {
        return new DecisionId(canonicalUuid(raw));
    }

    static PageRequest pageRequest(
            String page,
            String size,
            String sort,
            String direction,
            String defaultSort,
            SortDirection defaultDirection
    ) {
        try {
            int parsedPage = page == null ? 0 : Integer.parseInt(page);
            int parsedSize = size == null ? 20 : Integer.parseInt(size);
            String parsedSort = sort == null ? defaultSort : sort;
            SortDirection parsedDirection = direction == null
                    ? defaultDirection
                    : SortDirection.valueOf(direction.toUpperCase(Locale.ROOT));
            return new PageRequest(parsedPage, parsedSize, parsedSort, parsedDirection);
        } catch (IllegalArgumentException exception) {
            throw badRequest("INVALID_PAGINATION", "Pagination parameters are invalid");
        }
    }

    static DecisionApiModels.DecisionSummaryResponse response(DecisionSummary item) {
        return new DecisionApiModels.DecisionSummaryResponse(
                uuid(item.decisionId().value()), item.caseId(), item.title(), item.businessNeed(),
                item.status().value(), uuid(item.ownerId().value()), uuid(item.requiredApproverId().value()),
                item.recommendationId().map(value -> uuid(value.value())).orElse(null),
                item.createdAt().value().toString(), item.updatedAt().value().toString()
        );
    }

    static DecisionApiModels.DecisionDetailResponse response(DecisionDetail item) {
        return new DecisionApiModels.DecisionDetailResponse(
                uuid(item.decisionId().value()), item.caseId(), item.title(), item.businessNeed(),
                item.status().value(), uuid(item.ownerId().value()), uuid(item.requiredApproverId().value()),
                item.recommendationId().map(value -> uuid(value.value())).orElse(null),
                item.createdAt().value().toString(), item.updatedAt().value().toString(),
                uuid(item.originatingEvidenceId().value()), item.evidenceIds().stream()
                        .map(EvidenceId::value).map(DecisionRestMapper::uuid).toList(),
                item.reviewedBy().map(value -> uuid(value.value())).orElse(null),
                item.reviewedAt().map(value -> value.value().toString()).orElse(null),
                item.reviewReason().orElse(null)
        );
    }

    static DecisionApiModels.TimelineItemResponse response(DecisionTimelineItem item) {
        return new DecisionApiModels.TimelineItemResponse(
                item.type(), uuid(item.referenceId()), item.occurredAt().value().toString(), item.summary(),
                item.source().orElse(null), item.actor().orElse(null), item.confidenceLabel().orElse(null),
                item.confidencePercentage().orElse(null), item.evidenceIds().stream()
                        .map(EvidenceId::value).map(DecisionRestMapper::uuid).toList()
        );
    }

    static DecisionApiModels.EvidenceSummaryResponse response(EvidenceSummary item) {
        return new DecisionApiModels.EvidenceSummaryResponse(
                uuid(item.evidenceId().value()), item.timestamp().value().toString(), item.source(),
                item.sourceType(), item.sourceObjectRef(), item.entity(), item.eventType(),
                item.severity().value(), item.actor(), item.evidenceType(), item.observedFact(),
                item.businessMeaning(), item.correlationKey(), item.sensitivity(), item.confidence(),
                item.reviewStatus()
        );
    }

    static DecisionApiModels.RoiResponse response(DecisionRoiView item) {
        return new DecisionApiModels.RoiResponse(
                uuid(item.decisionId().value()), uuid(item.recommendationId().value()),
                money(item.currentMonthlyCost()), money(item.projectedMonthlyCost()),
                money(item.transitionCost()), money(item.estimatedMonthlyRecovery()),
                money(item.estimatedAnnualizedRecovery()), item.confidence().percentage(),
                item.risk().value(), item.policyVersion(), item.assumptionEvidenceIds().stream()
                        .map(EvidenceId::value).map(DecisionRestMapper::uuid).toList()
        );
    }

    static <S, T> DecisionApiModels.PageResponse<T> response(
            PageResult<S> page,
            Function<S, T> mapper
    ) {
        return new DecisionApiModels.PageResponse<>(
                page.items().stream().map(mapper).toList(), page.page(), page.size(),
                page.totalItems(), page.totalPages()
        );
    }

    private static DecisionApiModels.MoneyResponse money(ROIAmount value) {
        return new DecisionApiModels.MoneyResponse(
                value.value().amount().toPlainString(), value.value().currency().code()
        );
    }

    private static UUID canonicalUuid(String raw) {
        if (raw == null || raw.length() != 36 || !raw.equals(raw.toLowerCase(Locale.ROOT))) {
            throw badRequest("INVALID_UUID", "UUID must use canonical lower-case form");
        }
        try {
            UUID parsed = UUID.fromString(raw);
            if (!parsed.toString().equals(raw)) {
                throw badRequest("INVALID_UUID", "UUID must use canonical lower-case form");
            }
            return parsed;
        } catch (IllegalArgumentException exception) {
            throw badRequest("INVALID_UUID", "UUID must use canonical lower-case form");
        }
    }

    private static String uuid(UUID value) {
        return value.toString();
    }

    private static ApiContractException badRequest(String code, String message) {
        return new ApiContractException(HttpStatus.BAD_REQUEST, code, message);
    }
}
