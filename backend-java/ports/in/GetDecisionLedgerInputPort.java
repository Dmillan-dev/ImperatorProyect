package imperator.ports.in;

import imperator.application.query.GetDecisionLedgerQuery;
import imperator.application.query.LedgerEntryView;
import imperator.application.query.PageResult;

public interface GetDecisionLedgerInputPort {
    PageResult<LedgerEntryView> getDecisionLedger(GetDecisionLedgerQuery query);
}
