package imperator.application.exceptions;

import imperator.domain.shared.LedgerEntryId;

import java.util.Objects;

public final class LedgerEntryNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

    public LedgerEntryNotFoundException(LedgerEntryId ledgerEntryId) {
        super("LEDGER_ENTRY_NOT_FOUND", "Ledger entry not found: " + idValue(ledgerEntryId));
    }

    private static String idValue(LedgerEntryId ledgerEntryId) {
        return Objects.requireNonNull(ledgerEntryId, "Ledger entry id is required").value().toString();
    }
}
