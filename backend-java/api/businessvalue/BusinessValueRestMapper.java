package imperator.api.businessvalue;

import imperator.api.errors.ApiContractException;
import imperator.application.businessvalue.BusinessValueProjection;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.ROIAmount;
import org.springframework.http.HttpStatus;

import java.util.Locale;
import java.util.UUID;

final class BusinessValueRestMapper {
    private BusinessValueRestMapper() {
    }

    static DecisionId decisionId(String raw) {
        if (raw == null || raw.length() != 36 || !raw.equals(raw.toLowerCase(Locale.ROOT))) {
            throw invalidUuid();
        }
        try {
            UUID parsed = UUID.fromString(raw);
            if (!parsed.toString().equals(raw)) {
                throw invalidUuid();
            }
            return new DecisionId(parsed);
        } catch (IllegalArgumentException exception) {
            throw invalidUuid();
        }
    }

    static BusinessValueResponse response(BusinessValueProjection item) {
        return new BusinessValueResponse(
                item.caseId(), item.correlationKey(), item.decisionId().value().toString(),
                item.decisionTitle(), item.businessNeed(), item.decisionStatus().value(),
                item.decisionCreatedAt().value().toString(), item.recommendationId().value().toString(),
                item.recommendationType().value(), item.recommendedAction(), item.deterministicReason(),
                item.explanation().orElse(null), item.recommendationCreatedAt().value().toString(),
                money(item.estimatedSavings()), money(item.realizedSavings()), item.variance().toPlainString(),
                money(item.annualizedBaselineCost()), money(item.annualizedPostActionCost()),
                money(item.actualTransitionCost()), item.confidence().percentage(), item.risk().value(),
                item.policyVersion(), item.evidenceIds().stream().map(id -> id.value().toString()).toList(),
                item.assumptionIds(), item.approvalEntryId().value().toString(),
                item.implementationEntryId().value().toString(), item.validationEntryId().value().toString(),
                item.ledgerHistory().stream().map(BusinessValueRestMapper::response).toList()
        );
    }

    private static BusinessValueResponse.LedgerFactResponse response(
            BusinessValueProjection.LedgerFact item
    ) {
        return new BusinessValueResponse.LedgerFactResponse(
                item.id().value().toString(), item.type().value().toUpperCase(Locale.ROOT),
                item.actorId().value().toString(), item.actorRole(), item.occurredAt().value().toString(),
                item.previousEntryId().map(id -> id.value().toString()).orElse(null),
                item.evidenceIds().stream().map(id -> id.value().toString()).toList()
        );
    }

    private static BusinessValueResponse.MoneyResponse money(ROIAmount value) {
        return new BusinessValueResponse.MoneyResponse(
                value.value().amount().toPlainString(), value.value().currency().code()
        );
    }

    private static ApiContractException invalidUuid() {
        return new ApiContractException(
                HttpStatus.BAD_REQUEST, "INVALID_UUID", "UUID must use canonical lower-case form"
        );
    }
}
