package imperator.application.ledger;

import imperator.application.exceptions.ConflictException;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LedgerChainTest {
    private static final Instant BASE_TIME = Instant.parse("2026-08-01T10:00:00Z");

    @Test
    void acceptsOneLinearChainAndDerivesItsUniqueHead() {
        DecisionId decisionId = decisionId(1);
        LedgerEntry first = entry(decisionId, entryId(1), Optional.empty(), 1);
        LedgerEntry second = entry(decisionId, entryId(2), Optional.of(first.id()), 2);
        LedgerEntry third = entry(decisionId, entryId(3), Optional.of(second.id()), 3);

        LedgerChain chain = LedgerChain.from(decisionId, List.of(third, first, second));

        assertEquals(third.id(), chain.head().orElseThrow().id());
        assertTrue(chain.contains(first.id()));
        assertTrue(chain.contains(second.id()));
        assertTrue(chain.contains(third.id()));
    }

    @Test
    void rejectsMissingPredecessorSoSequenceCannotSkipAnEntry() {
        DecisionId decisionId = decisionId(2);
        LedgerEntry first = entry(decisionId, entryId(4), Optional.empty(), 1);
        LedgerEntry third = entry(decisionId, entryId(6), Optional.of(entryId(5)), 3);

        assertThrows(ConflictException.class, () -> LedgerChain.from(decisionId, List.of(first, third)));
    }

    @Test
    void rejectsTwoChildrenOfTheSameHead() {
        DecisionId decisionId = decisionId(3);
        LedgerEntry first = entry(decisionId, entryId(7), Optional.empty(), 1);
        LedgerEntry second = entry(decisionId, entryId(8), Optional.of(first.id()), 2);
        LedgerEntry competingSecond = entry(decisionId, entryId(9), Optional.of(first.id()), 3);

        assertThrows(
                ConflictException.class,
                () -> LedgerChain.from(decisionId, List.of(first, second, competingSecond))
        );
    }

    @Test
    void rejectsDuplicateEntryIdentity() {
        DecisionId decisionId = decisionId(4);
        LedgerEntry first = entry(decisionId, entryId(10), Optional.empty(), 1);
        LedgerEntry duplicate = entry(decisionId, entryId(10), Optional.empty(), 2);

        assertThrows(ConflictException.class, () -> LedgerChain.from(decisionId, List.of(first, duplicate)));
    }

    @Test
    void rejectsNonIncreasingOccurrenceTime() {
        DecisionId decisionId = decisionId(5);
        LedgerEntry first = entry(decisionId, entryId(11), Optional.empty(), 2);
        LedgerEntry second = entry(decisionId, entryId(12), Optional.of(first.id()), 1);

        assertThrows(ConflictException.class, () -> LedgerChain.from(decisionId, List.of(first, second)));
    }

    private LedgerEntry entry(
            DecisionId decisionId,
            LedgerEntryId entryId,
            Optional<LedgerEntryId> previousEntryId,
            long seconds
    ) {
        return new LedgerEntry(
                entryId,
                decisionId,
                Optional.of(recommendationId()),
                userId(),
                "PLATFORM_ENGINEER",
                new Timestamp(BASE_TIME.plusSeconds(seconds)),
                LedgerEntryType.IMPLEMENTATION_MARKED,
                "Ledger sequence test",
                "Validate a linear immutable history",
                Set.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                previousEntryId,
                Map.of()
        );
    }

    private DecisionId decisionId(int suffix) {
        return new DecisionId(new UUID(0, suffix));
    }

    private LedgerEntryId entryId(int suffix) {
        return new LedgerEntryId(new UUID(1, suffix));
    }

    private RecommendationId recommendationId() {
        return new RecommendationId(new UUID(2, 1));
    }

    private UserId userId() {
        return new UserId(new UUID(3, 1));
    }
}
