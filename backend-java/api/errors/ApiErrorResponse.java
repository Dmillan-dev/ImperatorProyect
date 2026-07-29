package imperator.api.errors;

import java.util.Map;
import java.util.Objects;

public record ApiErrorResponse(
        String code,
        String message,
        String correlationId,
        Map<String, Object> details
) {
    public ApiErrorResponse {
        code = requireText(code, "Error code is required");
        message = requireText(message, "Error message is required");
        correlationId = requireText(correlationId, "Correlation ID is required");
        details = Map.copyOf(Objects.requireNonNull(details, "Error details are required"));
    }

    static ApiErrorResponse from(ApiErrorCode errorCode, String correlationId) {
        Objects.requireNonNull(errorCode, "Error code is required");
        return new ApiErrorResponse(
                errorCode.name(),
                errorCode.message(),
                correlationId,
                Map.of());
    }

    private static String requireText(String value, String message) {
        Objects.requireNonNull(value, message);
        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
