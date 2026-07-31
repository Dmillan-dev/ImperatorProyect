package imperator.api.ledger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/decisions/{id}/ledger/approve")
    void approveDecision() {
        throw new NotImplementedApiException();
    }

    @PostMapping("/decisions/{id}/ledger/reject")
    void rejectDecision() {
        throw new NotImplementedApiException();
    }

    @PostMapping("/decisions/{id}/ledger/defer")
    void deferDecision() {
        throw new NotImplementedApiException();
    }

    @PostMapping("/decisions/{id}/ledger/mark-implemented")
    void markDecisionImplemented() {
        throw new NotImplementedApiException();
    }

    @PostMapping("/decisions/{id}/ledger/validate-result")
    void validateDecisionResult() {
        throw new NotImplementedApiException();
    }
}
