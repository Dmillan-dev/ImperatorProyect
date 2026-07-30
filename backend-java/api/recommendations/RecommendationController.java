package imperator.api.recommendations;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imperator.api.errors.NotImplementedApiException;

@RestController
@RequestMapping("/api/v1/recommendations")
public final class RecommendationController {

    @GetMapping("/{id}")
    void getRecommendation() {
        throw new NotImplementedApiException();
    }
}
