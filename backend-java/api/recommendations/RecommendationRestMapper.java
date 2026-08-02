package imperator.api.recommendations;

import imperator.api.errors.ApiContractException;
import imperator.application.query.RecommendationView;
import imperator.domain.shared.RecommendationId;
import org.springframework.http.HttpStatus;

import java.util.Locale;
import java.util.UUID;

final class RecommendationRestMapper {
    private RecommendationRestMapper() {
    }

    static RecommendationId recommendationId(String raw) {
        if (raw == null || raw.length() != 36 || !raw.equals(raw.toLowerCase(Locale.ROOT))) {
            throw invalidUuid();
        }
        try {
            UUID parsed = UUID.fromString(raw);
            if (!parsed.toString().equals(raw)) {
                throw invalidUuid();
            }
            return new RecommendationId(parsed);
        } catch (IllegalArgumentException exception) {
            throw invalidUuid();
        }
    }

    static RecommendationResponse response(RecommendationView item) {
        return new RecommendationResponse(
                item.recommendationId().value().toString(), item.decisionId().value().toString(),
                item.type().value(), item.suggestedAction(), item.deterministicReason(),
                new RecommendationResponse.MoneyResponse(
                        item.estimatedSavings().value().amount().toPlainString(),
                        item.estimatedSavings().value().currency().code()
                ), item.confidence().percentage(), item.risk().value(), item.ownerId().value().toString(),
                item.requiredApproverId().value().toString(), item.createdAt().value().toString(),
                item.evidenceIds().stream().map(id -> id.value().toString()).toList()
        );
    }

    private static ApiContractException invalidUuid() {
        return new ApiContractException(
                HttpStatus.BAD_REQUEST, "INVALID_UUID", "UUID must use canonical lower-case form"
        );
    }
}
