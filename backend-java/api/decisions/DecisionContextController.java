package imperator.api.decisions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imperator.api.errors.NotImplementedApiException;

@RestController
@RequestMapping("/api/v1/decisions/{id}")
public final class DecisionContextController {

    @GetMapping("/timeline")
    void getTimeline() {
        throw new NotImplementedApiException();
    }

    @GetMapping("/evidence")
    void getEvidence() {
        throw new NotImplementedApiException();
    }

    @GetMapping("/roi")
    void getRoi() {
        throw new NotImplementedApiException();
    }
}
