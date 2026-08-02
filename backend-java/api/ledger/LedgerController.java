package imperator.api.ledger;

import imperator.api.security.JwtActorContextResolver;
import imperator.application.query.SortDirection;
import imperator.application.reviewdecision.ReviewDecisionAction;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.GetDecisionLedgerInputPort;
import imperator.ports.in.ListLedgerEntriesInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public final class LedgerController {
    private final ObjectProvider<ListLedgerEntriesInputPort> listPort;
    private final ObjectProvider<GetDecisionLedgerInputPort> decisionLedgerPort;
    private final ObjectProvider<ReviewDecisionInputPort> reviewPort;
    private final ObjectProvider<AppendLedgerEntryInputPort> appendPort;
    private final JwtActorContextResolver actorResolver;

    public LedgerController(
            ObjectProvider<ListLedgerEntriesInputPort> listPort,
            ObjectProvider<GetDecisionLedgerInputPort> decisionLedgerPort,
            ObjectProvider<ReviewDecisionInputPort> reviewPort,
            ObjectProvider<AppendLedgerEntryInputPort> appendPort,
            JwtActorContextResolver actorResolver
    ) {
        this.listPort = listPort;
        this.decisionLedgerPort = decisionLedgerPort;
        this.reviewPort = reviewPort;
        this.appendPort = appendPort;
        this.actorResolver = actorResolver;
    }

    @GetMapping(path = "/ledger", produces = MediaType.APPLICATION_JSON_VALUE)
    LedgerApiModels.PageResponse<LedgerApiModels.LedgerEntryResponse> listLedgerEntries(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction
    ) {
        var request = LedgerRestMapper.pageRequest(page, size, sort, direction, SortDirection.DESC);
        return LedgerRestMapper.response(required(listPort).listLedgerEntries(LedgerRestMapper.ledgerQuery(request)));
    }

    @GetMapping(path = "/decisions/{id}/ledger", produces = MediaType.APPLICATION_JSON_VALUE)
    LedgerApiModels.PageResponse<LedgerApiModels.LedgerEntryResponse> getDecisionLedger(
            @PathVariable("id") String id,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction
    ) {
        var request = LedgerRestMapper.pageRequest(page, size, sort, direction, SortDirection.ASC);
        return LedgerRestMapper.response(required(decisionLedgerPort).getDecisionLedger(
                LedgerRestMapper.decisionLedgerQuery(id, request)
        ));
    }

    @PostMapping(
            path = "/decisions/{id}/ledger/approve",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<LedgerApiModels.ReviewResultResponse> approveDecision(
            @PathVariable("id") String id,
            @RequestBody LedgerApiModels.ReviewRequest body,
            Authentication authentication,
            HttpServletRequest request
    ) {
        var command = LedgerRestMapper.reviewCommand(
                id, request, actorResolver.resolve(authentication), ReviewDecisionAction.APPROVE, body
        );
        var result = required(reviewPort).reviewDecision(command);
        return commandResponse(result.replayed(), LedgerRestMapper.response(result));
    }

    @PostMapping(
            path = "/decisions/{id}/ledger/reject",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<LedgerApiModels.ReviewResultResponse> rejectDecision(
            @PathVariable("id") String id,
            @RequestBody LedgerApiModels.ReviewRequest body,
            Authentication authentication,
            HttpServletRequest request
    ) {
        var command = LedgerRestMapper.reviewCommand(
                id, request, actorResolver.resolve(authentication), ReviewDecisionAction.REJECT, body
        );
        var result = required(reviewPort).reviewDecision(command);
        return commandResponse(result.replayed(), LedgerRestMapper.response(result));
    }

    @PostMapping(
            path = "/decisions/{id}/ledger/defer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<LedgerApiModels.ReviewResultResponse> deferDecision(
            @PathVariable("id") String id,
            @RequestBody LedgerApiModels.DeferRequest body,
            Authentication authentication,
            HttpServletRequest request
    ) {
        var command = LedgerRestMapper.deferCommand(
                id, request, actorResolver.resolve(authentication), body
        );
        var result = required(reviewPort).reviewDecision(command);
        return commandResponse(result.replayed(), LedgerRestMapper.response(result));
    }

    @PostMapping(
            path = "/decisions/{id}/ledger/mark-implemented",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<LedgerApiModels.AppendResultResponse> markDecisionImplemented(
            @PathVariable("id") String id,
            @RequestBody LedgerApiModels.MarkImplementedRequest body,
            Authentication authentication,
            HttpServletRequest request
    ) {
        var command = LedgerRestMapper.markImplementedCommand(
                id, request, actorResolver.resolve(authentication), body
        );
        var result = required(appendPort).appendLedgerEntry(command);
        return commandResponse(result.replayed(), LedgerRestMapper.response(result));
    }

    @PostMapping(
            path = "/decisions/{id}/ledger/validate-result",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<LedgerApiModels.AppendResultResponse> validateDecisionResult(
            @PathVariable("id") String id,
            @RequestBody LedgerApiModels.ValidateResultRequest body,
            Authentication authentication,
            HttpServletRequest request
    ) {
        var command = LedgerRestMapper.validateResultCommand(
                id, request, actorResolver.resolve(authentication), body
        );
        var result = required(appendPort).appendLedgerEntry(command);
        return commandResponse(result.replayed(), LedgerRestMapper.response(result));
    }

    private <T> T required(ObjectProvider<T> provider) {
        T port = provider.getIfAvailable();
        if (port == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return port;
    }

    private <T> ResponseEntity<T> commandResponse(boolean replayed, T body) {
        return ResponseEntity.status(replayed ? HttpStatus.OK : HttpStatus.CREATED).body(body);
    }
}
