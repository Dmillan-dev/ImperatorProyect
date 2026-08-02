package imperator.ports.in;

import imperator.application.query.LedgerEntryView;
import imperator.application.query.ListLedgerEntriesQuery;
import imperator.application.query.PageResult;

public interface ListLedgerEntriesInputPort {
    PageResult<LedgerEntryView> listLedgerEntries(ListLedgerEntriesQuery query);
}
