package imperator.application.exceptions;

public abstract class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String code;

    protected ApplicationException(String code, String message) {
        super(requireText(message, "Application error message"));
        this.code = requireText(code, "Application error code");
    }

    protected ApplicationException(String code, String message, Throwable cause) {
        super(requireText(message, "Application error message"), cause);
        this.code = requireText(code, "Application error code");
    }

    public String code() {
        return code;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return fieldName;
        }
        String normalized = value.trim();
        return normalized;
    }
}
