package imperator.ports.in;

import imperator.application.appendledgerentry.AppendLedgerEntryCommand;
import imperator.application.appendledgerentry.AppendLedgerEntryResult;

public interface AppendLedgerEntryInputPort {
    AppendLedgerEntryResult appendLedgerEntry(AppendLedgerEntryCommand command);
}
