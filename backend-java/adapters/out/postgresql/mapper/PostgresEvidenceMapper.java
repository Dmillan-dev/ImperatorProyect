package imperator.adapters.out.postgresql.mapper;

import imperator.adapters.out.postgresql.model.PostgresEvidenceRecord;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;

import java.util.Objects;

public final class PostgresEvidenceMapper {
    public PostgresEvidenceRecord toRecord(Evidence evidence) {
        Objects.requireNonNull(evidence, "Evidence is required");

        return new PostgresEvidenceRecord(
                evidence.id().value(),
                evidence.timestamp().value(),
                evidence.source(),
                evidence.sourceType(),
                evidence.sourceObjectRef(),
                evidence.entity(),
                evidence.eventType(),
                evidence.severity().value(),
                evidence.actor(),
                evidence.evidenceType(),
                evidence.observedFact(),
                evidence.businessMeaning(),
                evidence.correlationKey(),
                evidence.sensitivity(),
                evidence.confidence(),
                evidence.reviewStatus(),
                evidence.rawPayloadMode(),
                evidence.metadata()
        );
    }

    public Evidence toDomain(PostgresEvidenceRecord record) {
        Objects.requireNonNull(record, "Persistence evidence record is required");

        return new Evidence(
                new EvidenceId(record.id()),
                new Timestamp(record.timestamp()),
                record.source(),
                record.sourceType(),
                record.sourceObjectRef(),
                record.entity(),
                record.eventType(),
                new Severity(record.severity()),
                record.actor(),
                record.evidenceType(),
                record.observedFact(),
                record.businessMeaning(),
                record.correlationKey(),
                record.sensitivity(),
                record.confidence(),
                record.reviewStatus(),
                record.rawPayloadMode(),
                record.metadata()
        );
    }
}
