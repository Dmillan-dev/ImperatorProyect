package imperator.adapters.out.postgresql.mapper;

import imperator.adapters.out.postgresql.model.PostgresDecisionEvidenceRecord;
import imperator.adapters.out.postgresql.model.PostgresDecisionRecord;
import imperator.domain.decision.Decision;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PostgresDecisionMapper {
    public PostgresDecisionRecord toRecord(Decision decision) {
        Objects.requireNonNull(decision, "Decision is required");

        return new PostgresDecisionRecord(
                decision.id().value(),
                decision.caseId(),
                decision.title(),
                decision.businessNeed(),
                decision.originatingEvidenceId().value(),
                decision.ownerId().value(),
                decision.requiredApproverId().value(),
                decision.createdAt().value(),
                decision.status().value(),
                decision.recommendationId().map(RecommendationId::value).orElse(null),
                decision.reviewedBy().map(UserId::value).orElse(null),
                decision.reviewedAt().map(Timestamp::value).orElse(null),
                decision.reviewReason().orElse(null),
                decision.updatedAt().value()
        );
    }

    public List<PostgresDecisionEvidenceRecord> toEvidenceRecords(Decision decision) {
        Objects.requireNonNull(decision, "Decision is required");

        return decision.evidenceIds().stream()
                .map(evidenceId -> new PostgresDecisionEvidenceRecord(decision.id().value(), evidenceId.value()))
                .toList();
    }

    public Decision toDomain(
            PostgresDecisionRecord record,
            Collection<PostgresDecisionEvidenceRecord> evidenceRecords
    ) {
        Objects.requireNonNull(record, "Persistence decision record is required");

        Decision decision = Decision.create(
                new DecisionId(record.id()),
                record.caseId(),
                record.title(),
                record.businessNeed(),
                new EvidenceId(record.originatingEvidenceId()),
                new UserId(record.ownerId()),
                new UserId(record.requiredApproverId()),
                new Timestamp(record.createdAt())
        );

        addEvidence(decision, records(evidenceRecords), record);
        attachRecommendation(decision, record);
        applyPersistedStatus(decision, record);

        return decision;
    }

    private void addEvidence(
            Decision decision,
            Collection<PostgresDecisionEvidenceRecord> evidenceRecords,
            PostgresDecisionRecord record
    ) {
        for (PostgresDecisionEvidenceRecord evidenceRecord : evidenceRecords) {
            UUID evidenceId = evidenceRecord.evidenceId();
            if (!record.originatingEvidenceId().equals(evidenceId)) {
                decision.addEvidence(new EvidenceId(evidenceId), timestamp(record.updatedAt(), record.createdAt()));
            }
        }
    }

    private void attachRecommendation(Decision decision, PostgresDecisionRecord record) {
        Optional.ofNullable(record.recommendationId())
                .map(RecommendationId::new)
                .ifPresent(recommendationId -> decision.attachRecommendation(
                        recommendationId,
                        timestamp(record.updatedAt(), record.createdAt())
                ));
    }

    private void applyPersistedStatus(Decision decision, PostgresDecisionRecord record) {
        DecisionStatus status = new DecisionStatus(record.status());
        if (DecisionStatus.CREATED.equals(status)) {
            return;
        }
        if (DecisionStatus.UNDER_REVIEW.equals(status)) {
            decision.markUnderReview(timestamp(record.updatedAt(), record.createdAt()));
            return;
        }
        if (DecisionStatus.APPROVED.equals(status)) {
            decision.markUnderReview(timestamp(record.reviewedAt(), record.updatedAt()));
            decision.approve(reviewer(record), timestamp(record.reviewedAt(), record.updatedAt()), reviewReason(record));
            return;
        }
        if (DecisionStatus.REJECTED.equals(status)) {
            decision.markUnderReview(timestamp(record.reviewedAt(), record.updatedAt()));
            decision.reject(reviewer(record), timestamp(record.reviewedAt(), record.updatedAt()), reviewReason(record));
            return;
        }
        if (DecisionStatus.DEFERRED.equals(status)) {
            decision.defer(reviewer(record), timestamp(record.reviewedAt(), record.updatedAt()), reviewReason(record));
        }
    }

    private Collection<PostgresDecisionEvidenceRecord> records(Collection<PostgresDecisionEvidenceRecord> records) {
        return records == null ? List.of() : List.copyOf(records);
    }

    private Timestamp timestamp(Instant value, Instant fallback) {
        return new Timestamp(value == null ? Objects.requireNonNull(fallback, "Timestamp fallback is required") : value);
    }

    private UserId reviewer(PostgresDecisionRecord record) {
        return new UserId(Objects.requireNonNull(record.reviewedBy(), "Persisted decision reviewer is required"));
    }

    private String reviewReason(PostgresDecisionRecord record) {
        return Objects.requireNonNull(record.reviewReason(), "Persisted decision review reason is required");
    }
}
