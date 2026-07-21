package imperator.domain.ledger;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.ROIAmount;
import imperator.domain.shared.ROIConfidence;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record LedgerEntry(
        LedgerEntryId id,
        DecisionId decisionId,
        Optional<RecommendationId> recommendationId,
        UserId actorId,
        String actorRole,
        Timestamp occurredAt,
        LedgerEntryType entryType,
        String changeSummary,
        String reason,
        Set<EvidenceId> evidenceSnapshotIds,
        Optional<ROIAmount> estimatedSaving,
        Optional<ROIAmount> realizedSaving,
        Optional<ROIConfidence> confidenceSnapshot,
        Optional<Severity> riskSnapshot,
        Optional<LedgerEntryId> previousEntryId,
        Map<String, String> metadata
) {
    private static final int MAX_METADATA_ENTRIES = 10;
    private static final int MAX_METADATA_KEY_LENGTH = 80;
    private static final int MAX_METADATA_VALUE_LENGTH = 500;
    private static final Set<String> FORBIDDEN_METADATA_KEY_TOKENS = Set.of(
            "secret",
            "token",
            "credential",
            "password",
            "payload",
            "prompt",
            "completion"
    );

    public LedgerEntry {
        id = Objects.requireNonNull(id, "Ledger entry id is required");
        decisionId = Objects.requireNonNull(decisionId, "Ledger decision id is required");
        recommendationId = optional(recommendationId);
        actorId = Objects.requireNonNull(actorId, "Ledger actor id is required");
        actorRole = requireText(actorRole, "Ledger actor role");
        occurredAt = Objects.requireNonNull(occurredAt, "Ledger timestamp is required");
        entryType = Objects.requireNonNull(entryType, "Ledger entry type is required");
        changeSummary = requireText(changeSummary, "Ledger change summary");
        reason = requireText(reason, "Ledger reason");
        evidenceSnapshotIds = evidenceSnapshotIds == null ? Set.of() : Set.copyOf(evidenceSnapshotIds);
        estimatedSaving = optional(estimatedSaving);
        realizedSaving = optional(realizedSaving);
        confidenceSnapshot = optional(confidenceSnapshot);
        riskSnapshot = optional(riskSnapshot);
        previousEntryId = optional(previousEntryId);
        metadata = sanitizedMetadata(metadata);

        enforceEntryTypeInvariants();
    }

    public LedgerEntryType whatHappened() {
        return entryType;
    }

    public String whatChanged() {
        return changeSummary;
    }

    public String why() {
        return reason;
    }

    public boolean belongsTo(DecisionId decisionId) {
        return this.decisionId.equals(Objects.requireNonNull(decisionId, "Decision id is required"));
    }

    public boolean wasPerformedBy(UserId actorId) {
        return this.actorId.equals(Objects.requireNonNull(actorId, "Actor id is required"));
    }

    @Override
    public boolean equals(Object candidate) {
        return candidate instanceof LedgerEntry ledgerEntry && id.equals(ledgerEntry.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private void enforceEntryTypeInvariants() {
        if (entryType.requiresRecommendation() && recommendationId.isEmpty()) {
            throw new IllegalArgumentException("Ledger entry type requires a recommendation id");
        }
        if (entryType.requiresEvidenceSnapshot() && evidenceSnapshotIds.isEmpty()) {
            throw new IllegalArgumentException("Ledger entry type requires evidence snapshot references");
        }
        if (entryType.requiresEstimatedSavingSnapshot() && estimatedSaving.isEmpty()) {
            throw new IllegalArgumentException("Ledger entry type requires estimated saving snapshot");
        }
        if (entryType.requiresEstimatedSavingSnapshot() && (confidenceSnapshot.isEmpty() || riskSnapshot.isEmpty())) {
            throw new IllegalArgumentException("Ledger entry type requires confidence and risk snapshots");
        }
        if (entryType.recordsRealizedValue() && realizedSaving.isEmpty()) {
            throw new IllegalArgumentException("Result validation ledger entry requires realized saving");
        }
        if (previousEntryId.filter(id::equals).isPresent()) {
            throw new IllegalArgumentException("Ledger entry cannot reference itself as previous entry");
        }
    }

    private static <T> Optional<T> optional(Optional<T> value) {
        return value == null ? Optional.empty() : value;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static Map<String, String> sanitizedMetadata(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return Map.of();
        }
        if (metadata.size() > MAX_METADATA_ENTRIES) {
            throw new IllegalArgumentException("Ledger metadata is too large");
        }

        Map<String, String> sanitized = new LinkedHashMap<>();
        metadata.forEach((key, value) -> sanitized.put(safeMetadataKey(key), safeMetadataValue(value)));
        return Map.copyOf(sanitized);
    }

    private static String safeMetadataKey(String key) {
        String normalized = requireText(key, "Ledger metadata key");
        if (normalized.length() > MAX_METADATA_KEY_LENGTH) {
            throw new IllegalArgumentException("Ledger metadata key is too long");
        }
        String lowerKey = normalized.toLowerCase();
        if (FORBIDDEN_METADATA_KEY_TOKENS.stream().anyMatch(lowerKey::contains)) {
            throw new IllegalArgumentException("Ledger metadata key is not allowed");
        }
        return normalized;
    }

    private static String safeMetadataValue(String value) {
        String normalized = requireText(value, "Ledger metadata value");
        if (normalized.length() > MAX_METADATA_VALUE_LENGTH) {
            throw new IllegalArgumentException("Ledger metadata value is too long");
        }
        return normalized;
    }
}

