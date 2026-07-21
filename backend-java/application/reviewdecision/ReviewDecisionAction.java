package imperator.application.reviewdecision;

import imperator.application.exceptions.ValidationException;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public record ReviewDecisionAction(String value) {
    private static final Set<String> ALLOWED_VALUES = Set.of("start_review", "approve", "reject", "defer");

    public static final ReviewDecisionAction START_REVIEW = new ReviewDecisionAction("start_review");
    public static final ReviewDecisionAction APPROVE = new ReviewDecisionAction("approve");
    public static final ReviewDecisionAction REJECT = new ReviewDecisionAction("reject");
    public static final ReviewDecisionAction DEFER = new ReviewDecisionAction("defer");

    public ReviewDecisionAction {
        Objects.requireNonNull(value, "Review decision action is required");
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_VALUES.contains(value)) {
            throw new ValidationException("INVALID_REVIEW_ACTION", "Unsupported review decision action");
        }
    }

    boolean startsReview() { return "start_review".equals(value); }

    boolean approves() { return "approve".equals(value); }

    boolean rejects() { return "reject".equals(value); }

    boolean defers() { return "defer".equals(value); }

    boolean requiresLedgerEntry() { return approves() || rejects() || defers(); }
}
