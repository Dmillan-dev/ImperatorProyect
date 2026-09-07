package imperator.api.decisions;

import imperator.api.security.JwtActorContextResolver;
import imperator.application.query.GetDecisionQuery;
import imperator.application.query.ListDecisionsQuery;
import imperator.application.query.SortDirection;
import imperator.ports.in.ComposeDrcAoa001InputPort;
import imperator.ports.in.GetDecisionInputPort;
import imperator.ports.in.ListDecisionsInputPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/decisions")
public final class DecisionController {
    private final ObjectProvider<ListDecisionsInputPort> listPort;
    private final ObjectProvider<GetDecisionInputPort> detailPort;
    private final ObjectProvider<ComposeDrcAoa001InputPort> compositionPort;
    private final JwtActorContextResolver actorResolver;

    public DecisionController(
            ObjectProvider<ListDecisionsInputPort> listPort,
            ObjectProvider<GetDecisionInputPort> detailPort,
            ObjectProvider<ComposeDrcAoa001InputPort> compositionPort,
            JwtActorContextResolver actorResolver
    ) {
        this.listPort = listPort;
        this.detailPort = detailPort;
        this.compositionPort = compositionPort;
        this.actorResolver = actorResolver;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<DecisionApiModels.ComposeDrcAoa001Response> composeDrcAoa001(
            @RequestBody(required = false) byte[] body,
            Authentication authentication
    ) {
        var actor = actorResolver.resolve(authentication);
        var request = DecisionRestMapper.compositionRequest(body);
        var command = DecisionRestMapper.compositionCommand(request, actor);
        var result = required(compositionPort).compose(command);
        HttpStatus status = result.replayed() || result.resumed()
                ? HttpStatus.OK
                : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(DecisionRestMapper.response(result));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    DecisionApiModels.PageResponse<DecisionApiModels.DecisionSummaryResponse> listDecisions(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction
    ) {
        var request = DecisionRestMapper.pageRequest(
                page, size, sort, direction, "updatedAt", SortDirection.DESC
        );
        var result = required(listPort).listDecisions(new ListDecisionsQuery(request));
        return DecisionRestMapper.response(result, DecisionRestMapper::response);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    DecisionApiModels.DecisionDetailResponse getDecision(@PathVariable("id") String id) {
        var result = required(detailPort).getDecision(new GetDecisionQuery(DecisionRestMapper.decisionId(id)));
        return DecisionRestMapper.response(result);
    }

    private <T> T required(ObjectProvider<T> provider) {
        T port = provider.getIfAvailable();
        if (port == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return port;
    }
}
