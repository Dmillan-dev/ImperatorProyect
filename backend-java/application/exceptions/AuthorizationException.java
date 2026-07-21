package imperator.application.exceptions;

public class AuthorizationException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public AuthorizationException(String code, String message) {
        super(code, message);
    }

    public AuthorizationException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
