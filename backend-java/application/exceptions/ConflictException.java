package imperator.application.exceptions;

public class ConflictException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public ConflictException(String code, String message) {
        super(code, message);
    }

    public ConflictException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
