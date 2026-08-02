package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.ports.in.GetDecisionLedgerInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Objects;
import java.util.Set;

public final class GetDecisionLedgerUseCase implements GetDecisionLedgerInputPort {
    private final MvpReadModelQueryPort readModel;

    public GetDecisionLedgerUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public PageResult<LedgerEntryView> getDecisionLedger(GetDecisionLedgerQuery query) {
        if (query == null || query.decisionId() == null || query.pageRequest() == null) {
            throw new ValidationException("INVALID_PAGINATION", "Decision Ledger query is required");
        }
        QuerySupport.requireSort(query.pageRequest(), Set.of("occurredAt"), true);
        var entries = QuerySupport.snapshot(readModel, query.decisionId()).ledgerEntries().stream()
                .sorted(java.util.Comparator
                        .comparing((imperator.domain.ledger.LedgerEntry entry) -> entry.occurredAt().value())
                        .thenComparing(entry -> entry.id().value().toString()))
                .map(QuerySupport::ledger)
                .toList();
        return QuerySupport.page(entries, query.pageRequest());
    }
}
