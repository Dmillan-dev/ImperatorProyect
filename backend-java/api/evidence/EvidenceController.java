package imperator.api.evidence;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imperator.api.errors.NotImplementedApiException;

@RestController
@RequestMapping("/api/v1/evidence")
public final class EvidenceController {

    @PostMapping("/import")
    void importEvidence() {
        throw new NotImplementedApiException();
    }
}
