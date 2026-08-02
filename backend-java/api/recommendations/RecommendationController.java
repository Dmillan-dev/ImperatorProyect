package imperator.api.recommendations;

import imperator.application.query.GetRecommendationQuery;
import imperator.ports.in.GetRecommendationInputPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/recommendations")
public final class RecommendationController {
    private final ObjectProvider<GetRecommendationInputPort> inputPort;

    public RecommendationController(ObjectProvider<GetRecommendationInputPort> inputPort) {
        this.inputPort = inputPort;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    RecommendationResponse getRecommendation(@PathVariable("id") String id) {
        GetRecommendationInputPort port = inputPort.getIfAvailable();
        if (port == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return RecommendationRestMapper.response(port.getRecommendation(
                new GetRecommendationQuery(RecommendationRestMapper.recommendationId(id))
        ));
    }
}
