package imperator.api.ledger;

import imperator.api.errors.ApiContractException;
import imperator.application.appendledgerentry.AppendLedgerEntryCommand;
import imperator.application.appendledgerentry.AppendLedgerEntryResult;
import imperator.application.query.GetDecisionLedgerQuery;
import imperator.application.query.LedgerEntryView;
import imperator.application.query.ListLedgerEntriesQuery;
import imperator.application.query.PageRequest;
import imperator.application.query.PageResult;
import imperator.application.query.SortDirection;
import imperator.application.reviewdecision.ReviewDecisionAction;
import imperator.application.reviewdecision.ReviewDecisionCommand;
import imperator.application.reviewdecision.ReviewDecisionResult;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.Currency;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.Timestamp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class LedgerRestMapper {
    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    private LedgerRestMapper() {
    }

    static DecisionId decisionId(String value) {
        return new DecisionId(canonicalUuid(value, "INVALID_UUID"));
    }

    static PageRequest pageRequest(
            String page,
            String size,
            String sort,
            String direction,
            SortDirection defaultDirection
    ) {
        try {
            return new PageRequest(
                    page == null ? 0 : Integer.parseInt(page),
                    size == null ? 20 : Integer.parseInt(size),
                    sort == null ? "occurredAt" : sort,
                    direction == null ? defaultDirection : SortDirection.valueOf(direction.toUpperCase(Locale.ROOT))
            );
        } catch (IllegalArgumentException exception) {
            throw badRequest("INVALID_PAGINATION", "Pagination parameters are invalid");
        }
    }

    static GetDecisionLedgerQuery decisionLedgerQuery(
            String decisionId,
            PageRequest request
    ) {
        return new GetDecisionLedgerQuery(decisionId(decisionId), request);
    }

    static ListLedgerEntriesQuery ledgerQuery(PageRequest request) {
        return new ListLedgerEntriesQuery(request);
    }

    static ReviewDecisionCommand reviewCommand(
            String decisionId,
            HttpServletRequest request,
            TrustedActorContextResolver.ActorContext actor,
            ReviewDecisionAction action,
            LedgerApiModels.ReviewRequest body
    ) {
        require(body, "Review request is required");
        return new ReviewDecisionCommand(
                idempotencyKey(request), decisionId(decisionId), action, actor.actorId(), actor.actorRole(),
                timestamp(body.reviewedAt()), requireText(body.reason(), "Review reason is required"),
                optionalLedgerId(body.expectedPreviousEntryId()), Optional.empty(), Optional.empty()
        );
    }

    static ReviewDecisionCommand deferCommand(
            String decisionId,
            HttpServletRequest request,
            TrustedActorContextResolver.ActorContext actor,
            LedgerApiModels.DeferRequest body
    ) {
        require(body, "Deferral request is required");
        Optional<String> requiredEvidence = optionalText(body.requiredEvidence());
        Optional<LocalDate> reviewDate = optionalText(body.reviewDate()).map(LedgerRestMapper::localDate);
        if (requiredEvidence.isPresent() == reviewDate.isPresent()) {
            throw badRequest(
                    "INVALID_DEFER_REQUEST", "Exactly one of requiredEvidence and reviewDate is required"
            );
        }
        return new ReviewDecisionCommand(
                idempotencyKey(request), decisionId(decisionId), ReviewDecisionAction.DEFER,
                actor.actorId(), actor.actorRole(), timestamp(body.reviewedAt()),
                requireText(body.reason(), "Review reason is required"),
                optionalLedgerId(body.expectedPreviousEntryId()), requiredEvidence, reviewDate
        );
    }

    static AppendLedgerEntryCommand markImplementedCommand(
            String decisionId,
            HttpServletRequest request,
            TrustedActorContextResolver.ActorContext actor,
            LedgerApiModels.MarkImplementedRequest body
    ) {
        require(body, "Implementation request is required");
        return new AppendLedgerEntryCommand(
                idempotencyKey(request), decisionId(decisionId), actor.actorId(), actor.actorRole(),
                timestamp(body.occurredAt()), LedgerEntryType.IMPLEMENTATION_MARKED,
                requireText(body.reason(), "Ledger reason is required"), evidenceIds(body.evidenceIds()),
                Optional.of(requiredLedgerId(body.expectedPreviousEntryId())),
                requireText(body.period(), "Period is required"), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    static AppendLedgerEntryCommand validateResultCommand(
            String decisionId,
            HttpServletRequest request,
            TrustedActorContextResolver.ActorContext actor,
            LedgerApiModels.ValidateResultRequest body
    ) {
        require(body, "Result validation request is required");
        return new AppendLedgerEntryCommand(
                idempotencyKey(request), decisionId(decisionId), actor.actorId(), actor.actorRole(),
                timestamp(body.occurredAt()), LedgerEntryType.RESULT_VALIDATED,
                requireText(body.reason(), "Ledger reason is required"), evidenceIds(body.evidenceIds()),
                Optional.of(requiredLedgerId(body.expectedPreviousEntryId())),
                requireText(body.period(), "Period is required"),
                Optional.of(money(body.annualizedBaselineCost())),
                Optional.of(money(body.annualizedPostActionCost())),
                Optional.of(money(body.actualTransitionCost()))
        );
    }

    static LedgerApiModels.PageResponse<LedgerApiModels.LedgerEntryResponse> response(
            PageResult<LedgerEntryView> page
    ) {
        return new LedgerApiModels.PageResponse<>(
                page.items().stream().map(LedgerRestMapper::response).toList(),
                page.page(), page.size(), page.totalItems(), page.totalPages()
        );
    }

    static LedgerApiModels.LedgerEntryResponse response(LedgerEntryView item) {
        return new LedgerApiModels.LedgerEntryResponse(
                item.ledgerEntryId().value().toString(), item.decisionId().value().toString(),
                item.recommendationId().map(id -> id.value().toString()).orElse(null),
                item.actorId().value().toString(), item.actorRole(), item.occurredAt().value().toString(),
                item.entryType().value().toUpperCase(Locale.ROOT), item.changeSummary(), item.reason(),
                item.evidenceIds().stream().map(id -> id.value().toString()).toList(),
                item.estimatedSavings().map(LedgerRestMapper::money).orElse(null),
                item.realizedSavings().map(LedgerRestMapper::money).orElse(null),
                item.confidence().map(value -> value.percentage()).orElse(null),
                item.risk().map(value -> value.value()).orElse(null),
                item.previousEntryId().map(id -> id.value().toString()).orElse(null), item.metadata()
        );
    }

    static LedgerApiModels.ReviewResultResponse response(ReviewDecisionResult item) {
        return new LedgerApiModels.ReviewResultResponse(
                item.ledgerEntryId().value().toString(), item.decisionId().value().toString(),
                item.recommendationId().value().toString(), item.status().value(),
                item.reviewerId().value().toString(), item.reviewerRole(), item.reviewReason(), item.replayed()
        );
    }

    static LedgerApiModels.AppendResultResponse response(AppendLedgerEntryResult item) {
        return new LedgerApiModels.AppendResultResponse(
                item.ledgerEntryId().value().toString(), item.decisionId().value().toString(),
                item.recommendationId().map(id -> id.value().toString()).orElse(null),
                item.entryType().value().toUpperCase(Locale.ROOT), item.occurredAt().value().toString(),
                item.evidenceSnapshotCount(), item.realizedSaving().map(LedgerRestMapper::money).orElse(null),
                item.replayed()
        );
    }

    private static LedgerEntryId idempotencyKey(HttpServletRequest request) {
        Enumeration<String> values = request.getHeaders(IDEMPOTENCY_HEADER);
        if (values == null || !values.hasMoreElements()) {
            throw badRequest("IDEMPOTENCY_KEY_REQUIRED", "Idempotency-Key is required");
        }
        String value = values.nextElement();
        if (values.hasMoreElements() || value == null || value.isBlank()) {
            throw badRequest("IDEMPOTENCY_KEY_INVALID", "Idempotency-Key is invalid");
        }
        return new LedgerEntryId(canonicalUuid(value.trim(), "IDEMPOTENCY_KEY_INVALID"));
    }

    private static Timestamp timestamp(String value) {
        try {
            String timestamp = requireText(value, "Timestamp is required");
            if (!timestamp.endsWith("Z")) {
                throw badRequest("INVALID_TIMESTAMP", "Timestamp must use UTC RFC 3339 form");
            }
            return new Timestamp(Instant.parse(timestamp));
        } catch (DateTimeParseException exception) {
            throw badRequest("INVALID_TIMESTAMP", "Timestamp must use UTC RFC 3339 form");
        }
    }

    private static LocalDate localDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw badRequest("INVALID_DATE", "Review date is invalid");
        }
    }

    private static Set<EvidenceId> evidenceIds(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw badRequest("EVIDENCE_IDS_REQUIRED", "Evidence ids are required");
        }
        Set<EvidenceId> ids = new LinkedHashSet<>();
        for (String value : values) {
            if (!ids.add(new EvidenceId(canonicalUuid(value, "INVALID_UUID")))) {
                throw badRequest("DUPLICATE_EVIDENCE_ID", "Evidence ids must be unique");
            }
        }
        return Set.copyOf(ids);
    }

    private static ROIAmount money(LedgerApiModels.Money value) {
        require(value, "Money is required");
        if (!Currency.EUR.code().equals(value.currency())) {
            throw badRequest("INVALID_MONEY", "Money currency must be EUR");
        }
        try {
            String rawAmount = requireText(value.amount(), "Money amount is required");
            if (!rawAmount.matches("(?:0|[1-9][0-9]*)\\.[0-9]{2}")) {
                throw badRequest("INVALID_MONEY", "Money amount must use scale two");
            }
            BigDecimal amount = new BigDecimal(rawAmount)
                    .setScale(2, RoundingMode.UNNECESSARY);
            return new ROIAmount(new Money(amount, Currency.EUR));
        } catch (NumberFormatException | ArithmeticException exception) {
            throw badRequest("INVALID_MONEY", "Money amount must use scale two");
        }
    }

    private static LedgerApiModels.Money money(ROIAmount value) {
        return new LedgerApiModels.Money(value.value().amount().toPlainString(), value.value().currency().code());
    }

    private static Optional<LedgerEntryId> optionalLedgerId(String value) {
        return optionalText(value).map(LedgerRestMapper::requiredLedgerId);
    }

    private static LedgerEntryId requiredLedgerId(String value) {
        return new LedgerEntryId(canonicalUuid(requireText(value, "Ledger entry id is required"), "INVALID_UUID"));
    }

    private static UUID canonicalUuid(String value, String code) {
        if (value == null || value.length() != 36 || !value.equals(value.toLowerCase(Locale.ROOT))) {
            throw badRequest(code, "UUID must use canonical lower-case form");
        }
        try {
            UUID parsed = UUID.fromString(value);
            if (!parsed.toString().equals(value)) {
                throw badRequest(code, "UUID must use canonical lower-case form");
            }
            return parsed;
        } catch (IllegalArgumentException exception) {
            throw badRequest(code, "UUID must use canonical lower-case form");
        }
    }

    private static Optional<String> optionalText(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.trim());
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw badRequest("INVALID_REQUEST", message);
        }
        return value.trim();
    }

    private static <T> T require(T value, String message) {
        if (value == null) {
            throw badRequest("INVALID_REQUEST", message);
        }
        return value;
    }

    private static ApiContractException badRequest(String code, String message) {
        return new ApiContractException(HttpStatus.BAD_REQUEST, code, message);
    }
}
