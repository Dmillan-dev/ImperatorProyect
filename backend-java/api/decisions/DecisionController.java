package imperator.api.decisions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imperator.api.errors.NotImplementedApiException;

@RestController
@RequestMapping("/api/v1/decisions")
public final class DecisionController {

    @GetMapping
    void listDecisions() {
        throw new NotImplementedApiException();
    }

    @GetMapping("/{id}")
    void getDecision() {
        throw new NotImplementedApiException();
    }
}
