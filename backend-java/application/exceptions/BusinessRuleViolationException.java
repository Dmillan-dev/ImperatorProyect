package imperator.application.exceptions;

public class BusinessRuleViolationException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public BusinessRuleViolationException(String code, String message) {
        super(code, message);
    }

    public BusinessRuleViolationException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
