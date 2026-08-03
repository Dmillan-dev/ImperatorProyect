package imperator.api.decisions;

import imperator.api.security.JwtActorContextResolver;
import imperator.application.query.EvidenceSummary;
import imperator.application.query.GetDecisionEvidenceQuery;
import imperator.application.query.GetDecisionRoiQuery;
import imperator.application.query.GetDecisionTimelineQuery;
import imperator.application.query.PageRequest;
import imperator.application.query.SortDirection;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.EvidenceId;
import imperator.ports.in.GetDecisionEvidenceInputPort;
import imperator.ports.in.GetDecisionRoiInputPort;
import imperator.ports.in.GetDecisionTimelineInputPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/decisions/{id}")
public final class DecisionContextController {
    private final ObjectProvider<GetDecisionTimelineInputPort> timelinePort;
    private final ObjectProvider<GetDecisionEvidenceInputPort> evidencePort;
    private final ObjectProvider<GetDecisionRoiInputPort> roiPort;
    private final JwtActorContextResolver actorResolver;
    private final DecisionEvidenceAuthorizationPolicy evidenceAuthorization;

    public DecisionContextController(
            ObjectProvider<GetDecisionTimelineInputPort> timelinePort,
            ObjectProvider<GetDecisionEvidenceInputPort> evidencePort,
            ObjectProvider<GetDecisionRoiInputPort> roiPort,
            JwtActorContextResolver actorResolver,
            DecisionEvidenceAuthorizationPolicy evidenceAuthorization
    ) {
        this.timelinePort = timelinePort;
        this.evidencePort = evidencePort;
        this.roiPort = roiPort;
        this.actorResolver = actorResolver;
        this.evidenceAuthorization = evidenceAuthorization;
    }

    @GetMapping(path = "/timeline", produces = MediaType.APPLICATION_JSON_VALUE)
    DecisionApiModels.PageResponse<DecisionApiModels.TimelineItemResponse> getTimeline(
            @PathVariable("id") String id,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction,
            Authentication authentication
    ) {
        var request = DecisionRestMapper.pageRequest(
                page, size, sort, direction, "occurredAt", SortDirection.ASC
        );
        DecisionId decisionId = DecisionRestMapper.decisionId(id);
        var result = required(timelinePort).getDecisionTimeline(
                new GetDecisionTimelineQuery(decisionId, request)
        );
        String role = actorResolver.resolve(authentication).actorRole();
        var authorized = evidenceAuthorization.authorizeTimeline(
                result, role, evidenceById(decisionId)
        );
        return DecisionRestMapper.response(authorized, DecisionRestMapper::response);
    }

    @GetMapping(path = "/evidence", produces = MediaType.APPLICATION_JSON_VALUE)
    DecisionApiModels.PageResponse<DecisionApiModels.EvidenceSummaryResponse> getEvidence(
            @PathVariable("id") String id,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction,
            Authentication authentication
    ) {
        var request = DecisionRestMapper.pageRequest(
                page, size, sort, direction, "timestamp", SortDirection.ASC
        );
        var result = required(evidencePort).getDecisionEvidence(
                new GetDecisionEvidenceQuery(DecisionRestMapper.decisionId(id), request)
        );
        String role = actorResolver.resolve(authentication).actorRole();
        var authorized = evidenceAuthorization.authorizeEvidence(result, role);
        return DecisionRestMapper.response(authorized, DecisionRestMapper::response);
    }

    @GetMapping(path = "/roi", produces = MediaType.APPLICATION_JSON_VALUE)
    DecisionApiModels.RoiResponse getRoi(@PathVariable("id") String id) {
        var result = required(roiPort).getDecisionRoi(
                new GetDecisionRoiQuery(DecisionRestMapper.decisionId(id))
        );
        return DecisionRestMapper.response(result);
    }

    private Map<EvidenceId, EvidenceSummary> evidenceById(DecisionId decisionId) {
        GetDecisionEvidenceInputPort port = required(evidencePort);
        Map<EvidenceId, EvidenceSummary> evidence = new LinkedHashMap<>();
        int page = 0;
        int totalPages;
        do {
            var result = port.getDecisionEvidence(new GetDecisionEvidenceQuery(
                    decisionId,
                    new PageRequest(page, 100, "timestamp", SortDirection.ASC)
            ));
            result.items().forEach(item -> evidence.put(item.evidenceId(), item));
            totalPages = result.totalPages();
            page++;
        } while (page < totalPages);
        return Map.copyOf(evidence);
    }

    private <T> T required(ObjectProvider<T> provider) {
        T port = provider.getIfAvailable();
        if (port == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return port;
    }
}
