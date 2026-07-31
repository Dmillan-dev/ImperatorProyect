package imperator.api.ledger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imperator.api.errors.NotImplementedApiException;

@RestController
@RequestMapping("/api/v1")
public final class LedgerController {

    @GetMapping("/ledger")
    void listLedgerEntries() {
        throw new NotImplementedApiException();
    }

    @GetMapping("/decisions/{id}/ledger")
    void getDecisionLedger() {
        throw new NotImplementedApiException();
    }
}
