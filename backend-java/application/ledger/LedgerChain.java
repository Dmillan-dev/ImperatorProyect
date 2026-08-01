package imperator.application.ledger;

import imperator.application.exceptions.ConflictException;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Validates and exposes one immutable, linear Decision Ledger chain. */
public final class LedgerChain {
    private final DecisionId decisionId;
    private final List<LedgerEntry> entries;

    private LedgerChain(DecisionId decisionId, List<LedgerEntry> entries) {
        this.decisionId = decisionId;
        this.entries = entries;
    }

    public static LedgerChain from(DecisionId decisionId, List<LedgerEntry> unorderedEntries) {
        DecisionId owner = Objects.requireNonNull(decisionId, "Decision id is required");
        List<LedgerEntry> source = unorderedEntries == null ? List.of() : List.copyOf(unorderedEntries);
        if (source.isEmpty()) {
            return new LedgerChain(owner, List.of());
        }

        Map<LedgerEntryId, LedgerEntry> byId = new HashMap<>();
        Map<LedgerEntryId, LedgerEntry> childByParent = new HashMap<>();
        List<LedgerEntry> roots = new ArrayList<>();

        for (LedgerEntry entry : source) {
            LedgerEntry item = Objects.requireNonNull(entry, "Ledger entry is required");
            if (!item.belongsTo(owner)) {
                throw conflict(owner, "Ledger entry belongs to another Decision");
            }
            if (byId.put(item.id(), item) != null) {
                throw conflict(owner, "Ledger sequence contains a duplicate entry identifier");
            }
            if (item.previousEntryId().isEmpty()) {
                roots.add(item);
            }
        }

        if (roots.size() != 1) {
            throw conflict(owner, "Ledger sequence must contain exactly one root");
        }

        for (LedgerEntry entry : source) {
            entry.previousEntryId().ifPresent(previousId -> {
                if (!byId.containsKey(previousId)) {
                    throw conflict(owner, "Ledger sequence contains a missing predecessor");
                }
                if (childByParent.put(previousId, entry) != null) {
                    throw conflict(owner, "Ledger sequence contains a fork");
                }
            });
        }

        List<LedgerEntry> ordered = new ArrayList<>(source.size());
        Set<LedgerEntryId> visited = new HashSet<>();
        LedgerEntry current = roots.getFirst();
        while (current != null) {
            if (!visited.add(current.id())) {
                throw conflict(owner, "Ledger sequence contains a cycle");
            }
            if (!ordered.isEmpty()) {
                LedgerEntry previous = ordered.getLast();
                if (!current.occurredAt().value().isAfter(previous.occurredAt().value())) {
                    throw conflict(owner, "Ledger occurrence time must increase strictly");
                }
            }
            ordered.add(current);
            current = childByParent.get(current.id());
        }

        if (ordered.size() != source.size()) {
            throw conflict(owner, "Ledger sequence is disconnected or cyclic");
        }

        return new LedgerChain(owner, List.copyOf(ordered));
    }

    public Optional<LedgerEntry> head() {
        return entries.isEmpty() ? Optional.empty() : Optional.of(entries.getLast());
    }

    /** Returns the validated chain from its root to its current head. */
    public List<LedgerEntry> entries() {
        return entries;
    }

    public boolean contains(LedgerEntryId entryId) {
        LedgerEntryId id = Objects.requireNonNull(entryId, "Ledger entry id is required");
        return entries.stream().anyMatch(entry -> entry.id().equals(id));
    }

    public void requireExpectedHead(Optional<LedgerEntryId> expectedHeadId) {
        Optional<LedgerEntryId> expected = expectedHeadId == null ? Optional.empty() : expectedHeadId;
        Optional<LedgerEntryId> actual = head().map(LedgerEntry::id);
        if (!actual.equals(expected)) {
            throw conflict(decisionId, "Ledger head changed before the command could execute");
        }
    }

    public void requireNextOccurrence(Timestamp occurredAt) {
        Timestamp next = Objects.requireNonNull(occurredAt, "Ledger timestamp is required");
        head().ifPresent(previous -> {
            if (!next.value().isAfter(previous.occurredAt().value())) {
                throw conflict(decisionId, "Ledger occurrence time must be later than the current head");
            }
        });
    }

    private static ConflictException conflict(DecisionId decisionId, String reason) {
        return new ConflictException(
                "LEDGER_SEQUENCE_CONFLICT",
                "Ledger sequence conflict for Decision " + decisionId.value() + ": " + reason
        );
    }
}
