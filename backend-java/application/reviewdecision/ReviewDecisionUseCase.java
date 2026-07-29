package imperator.application.reviewdecision;

import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.InvalidDecisionTransitionException;
import imperator.application.exceptions.RecommendationNotFoundException;
import imperator.application.exceptions.RecommendationOwnershipViolationException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.ports.in.ReviewDecisionInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;

import java.util.Objects;

public final class ReviewDecisionUseCase implements ReviewDecisionInputPort {
    private final DecisionRepository decisionRepository;
    private final RecommendationRepository recommendationRepository;
    private final TransactionRunner transactionRunner;

    public ReviewDecisionUseCase(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            TransactionRunner transactionRunner
    ) {
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.recommendationRepository = Objects.requireNonNull(recommendationRepository, "Recommendation repository is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
    }

    @Override
    public ReviewDecisionResult reviewDecision(ReviewDecisionCommand command) {
        validateCommand(command);

        return transactionRunner.execute(() -> reviewDecisionInTransaction(command));
    }

    private ReviewDecisionResult reviewDecisionInTransaction(ReviewDecisionCommand command) {
        Decision decision = decisionRepository.findById(command.decisionId())
                .orElseThrow(() -> new DecisionNotFoundException(command.decisionId()));

        Recommendation recommendation = recommendationRepository.findById(command.recommendationId())
                .orElseThrow(() -> new RecommendationNotFoundException(command.recommendationId()));

        ensureRecommendationMatchesDecision(decision, recommendation);
        try {
            applyReviewAction(decision, command);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException exception) {
            throw new InvalidDecisionTransitionException(
                    decision.id(),
                    failureMessage(exception, "Decision review transition rejected"),
                    exception
            );
        }
        decisionRepository.save(decision);

        return new ReviewDecisionResult(
                decision.id(),
                recommendation.id(),
                decision.status(),
                command.reviewerId(),
                command.reviewReason(),
                command.action().requiresLedgerEntry()
        );
    }

    private void ensureRecommendationMatchesDecision(Decision decision, Recommendation recommendation) {
        if (!recommendation.belongsTo(decision.id())) {
            throw new RecommendationOwnershipViolationException(recommendation.id(), decision.id());
        }
        if (decision.recommendationId().filter(recommendation.id()::equals).isEmpty()) {
            throw new RecommendationOwnershipViolationException(recommendation.id(), decision.id());
        }
    }

    private void applyReviewAction(Decision decision, ReviewDecisionCommand command) {
        if (command.action().startsReview()) {
            decision.markUnderReview(command.reviewedAt());
            return;
        }
        if (command.action().approves()) {
            decision.approve(command.reviewerId(), command.reviewedAt(), command.reviewReason());
            return;
        }
        if (command.action().rejects()) {
            decision.reject(command.reviewerId(), command.reviewedAt(), command.reviewReason());
            return;
        }
        if (command.action().defers()) {
            decision.defer(command.reviewerId(), command.reviewedAt(), command.reviewReason());
        }
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private void validateCommand(ReviewDecisionCommand command) {
        if (command == null) {
            throw new ValidationException("REVIEW_DECISION_COMMAND_REQUIRED", "Review decision command is required");
        }
        requireValue(command.decisionId(), "DECISION_ID_REQUIRED", "Decision id is required");
        requireValue(command.recommendationId(), "RECOMMENDATION_ID_REQUIRED", "Recommendation id is required");
        requireValue(command.action(), "REVIEW_ACTION_REQUIRED", "Review action is required");
        requireValue(command.reviewerId(), "REVIEWER_ID_REQUIRED", "Reviewer id is required");
        requireValue(command.reviewedAt(), "REVIEWED_AT_REQUIRED", "Review timestamp is required");
        requireText(command.reviewReason(), "REVIEW_REASON_REQUIRED", "Review reason is required");
    }

    private <T> T requireValue(T value, String code, String message) {
        if (value == null) {
            throw new ValidationException(code, message);
        }
        return value;
    }

    private String requireText(String value, String code, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(code, message);
        }
        return value.trim();
    }
}
