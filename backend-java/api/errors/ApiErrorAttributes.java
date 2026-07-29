package imperator.api.errors;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public final class ApiErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(
            WebRequest webRequest,
            ErrorAttributeOptions options
    ) {
        Map<String, Object> defaults = super.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.defaults());
        int status = statusFrom(defaults);
        ApiErrorCode errorCode = ApiErrorCode.fromStatus(status);
        String correlationId = CorrelationIdFilter.currentCorrelationId(webRequest);

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("code", errorCode.name());
        attributes.put("message", errorCode.message());
        attributes.put("correlationId", correlationId);
        attributes.put("details", Map.of());
        return attributes;
    }

    private static int statusFrom(Map<String, Object> attributes) {
        Object status = attributes.get("status");
        if (status instanceof Number number) {
            return number.intValue();
        }
        return 500;
    }
}
