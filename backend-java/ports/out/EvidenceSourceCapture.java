package imperator.ports.out;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record EvidenceSourceCapture(
        EvidenceSourceOutcome outcome,
        String sourceReference,
        String apiVersion,
        int requestCount,
        int retryCount,
        int pageCount,
        int qualifyingSourceObjectCount,
        List<EvidenceCandidate> candidates,
        List<String> missingEvidenceReferences,
        String failureCode,
        Optional<Instant> retryAt
) {
    public EvidenceSourceCapture {
        outcome = Objects.requireNonNull(outcome, "Source outcome is required");
        sourceReference = normalize(sourceReference);
        apiVersion = normalize(apiVersion);
        requireNonNegative(requestCount, "Source request count");
        requireNonNegative(retryCount, "Source retry count");
        requireNonNegative(pageCount, "Source page count");
        requireNonNegative(qualifyingSourceObjectCount, "Qualifying source object count");
        candidates = List.copyOf(Objects.requireNonNull(candidates, "Evidence candidates are required"));
        missingEvidenceReferences = List.copyOf(Objects.requireNonNull(
                missingEvidenceReferences,
                "Missing Evidence references are required"
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
