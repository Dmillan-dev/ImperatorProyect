package imperator.application.exceptions;

public class NotFoundException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String code, String message) {
        super(code, message);
    }

    public NotFoundException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
