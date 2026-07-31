package imperator.api.businessvalue;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imperator.api.errors.NotImplementedApiException;

@RestController
@RequestMapping("/api/v1/business-value")
public final class BusinessValueController {

    @GetMapping
    void getBusinessValue() {
        throw new NotImplementedApiException();
    }
}
