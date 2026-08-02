package imperator.api.errors;

import org.springframework.http.HttpStatus;

import java.util.Objects;

/** Safe transport-level failure with a frozen public code and status. */
public final class ApiContractException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final String code;

    public ApiContractException(HttpStatus status, String code, String message) {
        super(requireText(message, "API error message is required"));
        this.status = Objects.requireNonNull(status, "API error status is required");
        this.code = requireText(code, "API error code is required");
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
