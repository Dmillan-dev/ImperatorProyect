package imperator.adapters.out.postgresql.mapper;

import imperator.adapters.out.postgresql.model.PostgresRecommendationEvidenceRecord;
import imperator.adapters.out.postgresql.model.PostgresRecommendationRecord;
import imperator.domain.decision.Recommendation;
import imperator.domain.shared.Currency;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Money;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.RecommendationType;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class PostgresRecommendationMapper {
    public PostgresRecommendationRecord toRecord(Recommendation recommendation) {
        Objects.requireNonNull(recommendation, "Recommendation is required");

        return new PostgresRecommendationRecord(
                recommendation.id().value(),
                recommendation.decisionId().value(),
                recommendation.type().value(),
                recommendation.suggestedAction(),
                recommendation.reason(),
                recommendation.estimatedSavings().value().amount(),
                recommendation.estimatedSavings().value().currency().code(),
                recommendation.confidence().percentage(),
                recommendation.risk().value(),
                recommendation.ownerId().value(),
                recommendation.requiredApproverId().value(),
                recommendation.createdAt().value()
        );
    }

    public List<PostgresRecommendationEvidenceRecord> toEvidenceRecords(Recommendation recommendation) {
        Objects.requireNonNull(recommendation, "Recommendation is required");

        return recommendation.evidenceIds().stream()
                .map(evidenceId -> new PostgresRecommendationEvidenceRecord(recommendation.id().value(), evidenceId.value()))
                .toList();
    }

    public Recommendation toDomain(
            PostgresRecommendationRecord record,
            Collection<PostgresRecommendationEvidenceRecord> evidenceRecords
    ) {
        Objects.requireNonNull(record, "Persistence recommendation record is required");

        return new Recommendation(
                new RecommendationId(record.id()),
                new DecisionId(record.decisionId()),
                new RecommendationType(record.type()),
                record.suggestedAction(),
                record.reason(),
                evidenceIds(evidenceRecords),
                new ROIAmount(new Money(record.estimatedSavingAmount(), new Currency(record.estimatedSavingCurrency()))),
                new ROIConfidence(record.confidencePercentage()),
                new Severity(record.risk()),
                new UserId(record.ownerId()),
                new UserId(record.requiredApproverId()),
                new Timestamp(record.createdAt())
        );
    }

    private Set<EvidenceId> evidenceIds(Collection<PostgresRecommendationEvidenceRecord> evidenceRecords) {
        return records(evidenceRecords).stream()
                .map(PostgresRecommendationEvidenceRecord::evidenceId)
                .map(EvidenceId::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Collection<PostgresRecommendationEvidenceRecord> records(
            Collection<PostgresRecommendationEvidenceRecord> records
    ) {
        return records == null ? List.of() : List.copyOf(records);
    }
}
