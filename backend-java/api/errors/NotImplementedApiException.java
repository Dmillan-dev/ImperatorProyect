package imperator.api.errors;

public final class NotImplementedApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotImplementedApiException() {
        super("Not implemented");
    }
}
