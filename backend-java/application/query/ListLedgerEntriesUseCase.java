package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.ports.in.ListLedgerEntriesInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Objects;
import java.util.Set;

public final class ListLedgerEntriesUseCase implements ListLedgerEntriesInputPort {
    private final MvpReadModelQueryPort readModel;

    public ListLedgerEntriesUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public PageResult<LedgerEntryView> listLedgerEntries(ListLedgerEntriesQuery query) {
        if (query == null || query.pageRequest() == null) {
            throw new ValidationException("INVALID_PAGINATION", "Ledger list query is required");
        }
        QuerySupport.requireSort(query.pageRequest(), Set.of("occurredAt"), false);
        return QuerySupport.mapPage(readModel.findLedgerEntries(query.pageRequest()), QuerySupport::ledger);
    }
}
