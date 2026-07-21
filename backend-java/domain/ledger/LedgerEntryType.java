package imperator.domain.ledger;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public record LedgerEntryType(String value) {
    private static final Set<String> ALLOWED_VALUES = Set.of(
            "recommendation_created",
            "approved",
            "rejected",
            "deferred",
            "implementation_marked",
            "result_validated",
            "evidence_requested",
            "case_closed"
    );

    public static final LedgerEntryType RECOMMENDATION_CREATED = new LedgerEntryType("recommendation_created");
    public static final LedgerEntryType APPROVED = new LedgerEntryType("approved");
    public static final LedgerEntryType REJECTED = new LedgerEntryType("rejected");
    public static final LedgerEntryType DEFERRED = new LedgerEntryType("deferred");
    public static final LedgerEntryType IMPLEMENTATION_MARKED = new LedgerEntryType("implementation_marked");
    public static final LedgerEntryType RESULT_VALIDATED = new LedgerEntryType("result_validated");
    public static final LedgerEntryType EVIDENCE_REQUESTED = new LedgerEntryType("evidence_requested");
    public static final LedgerEntryType CASE_CLOSED = new LedgerEntryType("case_closed");

    public LedgerEntryType {
        Objects.requireNonNull(value, "Ledger entry type is required");
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_VALUES.contains(value)) {
            throw new IllegalArgumentException("Unsupported ledger entry type");
        }
    }

    boolean requiresRecommendation() {
        return Set.of(
                "recommendation_created",
                "approved",
                "rejected",
                "deferred",
                "implementation_marked",
                "result_validated"
        ).contains(value);
    }

    boolean requiresEvidenceSnapshot() {
        return Set.of("recommendation_created", "approved", "rejected", "deferred").contains(value);
    }

    boolean requiresEstimatedSavingSnapshot() {
        return Set.of("recommendation_created", "approved", "deferred").contains(value);
    }

    boolean recordsRealizedValue() {
        return "result_validated".equals(value);
    }
}

