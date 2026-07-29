package imperator.api.errors;

import org.springframework.http.HttpStatus;

public enum ApiErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Not implemented");

    private final HttpStatus status;
    private final String message;

    ApiErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }

    static ApiErrorCode fromStatus(int status) {
        return switch (status) {
            case 400 -> BAD_REQUEST;
            case 404 -> RESOURCE_NOT_FOUND;
            case 405 -> METHOD_NOT_ALLOWED;
            case 501 -> NOT_IMPLEMENTED;
            default -> INTERNAL_SERVER_ERROR;
        };
    }
}
