package imperator.application.exceptions;

public class ValidationException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String code, String message) {
        super(code, message);
    }

    public ValidationException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
