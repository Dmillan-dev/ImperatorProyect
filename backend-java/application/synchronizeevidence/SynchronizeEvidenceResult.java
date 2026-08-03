package imperator.application.synchronizeevidence;

import imperator.domain.shared.EvidenceId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record SynchronizeEvidenceResult(
        UUID correlationId,
        SynchronizationStatus status,
        Instant fromInclusive,
        Instant untilExclusive,
        String sourceReference,
        String apiVersion,
        int requestCount,
        int retryCount,
        int pageCount,
        int qualifyingSourceObjectCount,
        int acceptedEvidenceCount,
        int unchangedEvidenceCount,
        int rejectedEvidenceCount,
        int missingEvidenceCount,
        List<EvidenceId> evidenceIds,
        List<String> evidenceReferences,
        String failureCode,
        Optional<Instant> retryAt
) {
    public SynchronizeEvidenceResult {
        correlationId = Objects.requireNonNull(correlationId, "Synchronization correlation ID is required");
        status = Objects.requireNonNull(status, "Synchronization status is required");
        fromInclusive = Objects.requireNonNull(fromInclusive, "Synchronization window start is required");
        untilExclusive = Objects.requireNonNull(untilExclusive, "Synchronization window end is required");
        sourceReference = normalize(sourceReference);
        apiVersion = normalize(apiVersion);
        requireNonNegative(requestCount, "Request count");
        requireNonNegative(retryCount, "Retry count");
        requireNonNegative(pageCount, "Page count");
        requireNonNegative(qualifyingSourceObjectCount, "Qualifying source object count");
        requireNonNegative(acceptedEvidenceCount, "Accepted Evidence count");
        requireNonNegative(unchangedEvidenceCount, "Unchanged Evidence count");
        requireNonNegative(rejectedEvidenceCount, "Rejected Evidence count");
        requireNonNegative(missingEvidenceCount, "Missing Evidence count");
        evidenceIds = List.copyOf(Objects.requireNonNull(evidenceIds, "Evidence IDs are required"));
        evidenceReferences = List.copyOf(Objects.requireNonNull(
                evidenceReferences,
                "Evidence references are required"
        ));
        failureCode = normalize(failureCode);
        retryAt = Objects.requireNonNull(retryAt, "Retry time container is required");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static void requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
    }
}
