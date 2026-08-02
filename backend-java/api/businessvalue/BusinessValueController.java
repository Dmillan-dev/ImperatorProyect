package imperator.api.businessvalue;

import imperator.ports.in.ProjectBusinessValueInputPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/business-value")
public final class BusinessValueController {
    private final ObjectProvider<ProjectBusinessValueInputPort> inputPort;

    public BusinessValueController(ObjectProvider<ProjectBusinessValueInputPort> inputPort) {
        this.inputPort = inputPort;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    BusinessValueResponse getBusinessValue(@RequestParam("decisionId") String decisionId) {
        ProjectBusinessValueInputPort port = inputPort.getIfAvailable();
        if (port == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return BusinessValueRestMapper.response(port.projectBusinessValue(
                BusinessValueRestMapper.decisionId(decisionId), Optional.empty()
        ));
    }
}
