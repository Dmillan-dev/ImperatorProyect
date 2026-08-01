package imperator.application.reviewdecision;

import imperator.application.exceptions.AuthorizationException;
import imperator.application.exceptions.ConflictException;
import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.EvidenceTraceabilityViolationException;
import imperator.application.exceptions.InvalidDecisionTransitionException;
import imperator.application.exceptions.LedgerAppendRejectedException;
import imperator.application.exceptions.RecommendationNotFoundException;
import imperator.application.exceptions.RecommendationOwnershipViolationException;
import imperator.application.exceptions.ValidationException;
import imperator.application.ledger.LedgerChain;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.ports.in.ReviewDecisionInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class ReviewDecisionUseCase implements ReviewDecisionInputPort {
    private static final String ADMIN = "ADMIN";
    private static final Set<String> DEFERRAL_ROLES = Set.of("ADMIN", "PLATFORM_ENGINEER", "FINANCE");

    private final DecisionRepository decisionRepository;
    private final RecommendationRepository recommendationRepository;
    private final EvidenceRepository evidenceRepository;
    private final LedgerRepository ledgerRepository;
    private final TransactionRunner transactionRunner;

    public ReviewDecisionUseCase(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner
    ) {
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.recommendationRepository = Objects.requireNonNull(
                recommendationRepository,
                "Recommendation repository is required"
        );
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "Ledger repository is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
    }

    @Override
    public ReviewDecisionResult reviewDecision(ReviewDecisionCommand command) {
        ReviewInputs inputs = validateCommand(command);
        return transactionRunner.execute(() -> reviewDecisionInTransaction(command, inputs));
    }

    private ReviewDecisionResult reviewDecisionInTransaction(
            ReviewDecisionCommand command,
            ReviewInputs inputs
    ) {
        Decision decision = decisionRepository.findByIdForUpdate(command.decisionId())
                .orElseThrow(() -> new DecisionNotFoundException(command.decisionId()));
        Recommendation recommendation = loadRecommendation(decision);
        ensureRecommendationMatchesDecision(decision, recommendation);
        ensureReviewTimestamp(command, recommendation);
        authorize(command, inputs.actorRole(), decision, recommendation);

        List<LedgerEntry> history = ledgerRepository.findByDecisionId(decision.id());
        LedgerChain chain = LedgerChain.from(decision.id(), history);
        Optional<LedgerEntry> existing = ledgerRepository.findById(command.ledgerEntryId());
        if (existing.isPresent()) {
            return resolveReplay(command, inputs, decision, recommendation, chain, existing.get());
        }

        chain.requireExpectedHead(inputs.expectedPreviousEntryId());
        chain.requireNextOccurrence(command.reviewedAt());
        ensureReviewCanFollowHead(command, decision, chain.head());
        applyReviewAction(decision, command);

        LedgerEntry candidate = reviewEntry(
                command,
                inputs,
                decision,
                recommendation,
                chain.head().map(LedgerEntry::id)
        );

        decisionRepository.save(decision);
        LedgerEntry authoritative = ledgerRepository.append(candidate);
        if (!candidate.hasSameImmutableState(authoritative)) {
            throw operationConflict(decision.id(), "Ledger operation identifier already has different content");
        }

        return result(candidate, decision.status(), false);
    }

    private ReviewDecisionResult resolveReplay(
            ReviewDecisionCommand command,
            ReviewInputs inputs,
            Decision decision,
            Recommendation recommendation,
            LedgerChain chain,
            LedgerEntry existing
    ) {
        if (!existing.belongsTo(decision.id()) || !chain.contains(existing.id())) {
            throw operationConflict(decision.id(), "Ledger operation identifier belongs to another history");
        }
        LedgerEntry expected = reviewEntry(
                command,
                inputs,
                decision,
                recommendation,
                inputs.expectedPreviousEntryId()
        );
        if (!expected.hasSameImmutableState(existing)) {
            throw operationConflict(decision.id(), "Replay payload differs from the authoritative Ledger entry");
        }
        return result(existing, statusFor(existing.entryType()), true);
    }

    private Recommendation loadRecommendation(Decision decision) {
        RecommendationId recommendationId = decision.recommendationId()
                .orElseThrow(() -> new LedgerAppendRejectedException(
                        decision.id(),
                        "Decision review requires a persisted Recommendation"
                ));
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException(recommendationId));
    }

    private void ensureRecommendationMatchesDecision(Decision decision, Recommendation recommendation) {
        if (!recommendation.belongsTo(decision.id())
                || decision.recommendationId().filter(recommendation.id()::equals).isEmpty()
                || !decision.ownerId().equals(recommendation.ownerId())
                || !decision.requiredApproverId().equals(recommendation.requiredApproverId())) {
            throw new RecommendationOwnershipViolationException(recommendation.id(), decision.id());
        }
    }

    private void ensureReviewTimestamp(ReviewDecisionCommand command, Recommendation recommendation) {
        if (!command.reviewedAt().value().isAfter(recommendation.createdAt().value())) {
            throw new ValidationException(
                    "REVIEW_TIMESTAMP_INVALID",
                    "Decision review timestamp must be later than Recommendation creation"
            );
        }
    }

    private void authorize(
            ReviewDecisionCommand command,
            String actorRole,
            Decision decision,
            Recommendation recommendation
    ) {
        if (command.action().approves() || command.action().rejects()) {
            if (!ADMIN.equals(actorRole)) {
                throw forbidden("Only ADMIN may approve or reject in the Phase 1 governance slice");
            }
            if (!command.reviewerId().equals(decision.requiredApproverId())
                    || !command.reviewerId().equals(recommendation.requiredApproverId())) {
                throw forbidden("Review actor must be the assigned Decision approver");
            }
            return;
        }
        if (command.action().defers() && !DEFERRAL_ROLES.contains(actorRole)) {
            throw forbidden("Actor role is not authorized to defer the Decision");
        }
    }

    private void ensureReviewCanFollowHead(
            ReviewDecisionCommand command,
            Decision decision,
            Optional<LedgerEntry> head
    ) {
        if (head.isEmpty()) {
            if (!DecisionStatus.CREATED.equals(decision.status())
                    && !DecisionStatus.UNDER_REVIEW.equals(decision.status())) {
                throw operationConflict(decision.id(), "First governance command requires a reviewable Decision");
            }
            return;
        }

        LedgerEntry previous = head.get();
        if (!LedgerEntryType.DEFERRED.equals(previous.entryType())
                || !DecisionStatus.DEFERRED.equals(decision.status())
                || command.action().defers()) {
            throw operationConflict(decision.id(), "Decision already has an authoritative review outcome");
        }
    }

    private void applyReviewAction(Decision decision, ReviewDecisionCommand command) {
        try {
            if (command.action().approves() || command.action().rejects()) {
                if (DecisionStatus.CREATED.equals(decision.status()) || DecisionStatus.DEFERRED.equals(decision.status())) {
                    decision.markUnderReview(command.reviewedAt());
                }
                if (command.action().approves()) {
                    decision.approve(command.reviewerId(), command.reviewedAt(), command.reviewReason());
                } else {
                    decision.reject(command.reviewerId(), command.reviewedAt(), command.reviewReason());
                }
                return;
            }
            decision.defer(command.reviewerId(), command.reviewedAt(), command.reviewReason());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException exception) {
            throw new InvalidDecisionTransitionException(
                    decision.id(),
                    failureMessage(exception, "Decision review transition rejected"),
                    exception
            );
        }
    }

    private LedgerEntry reviewEntry(
            ReviewDecisionCommand command,
            ReviewInputs inputs,
            Decision decision,
            Recommendation recommendation,
            Optional<LedgerEntryId> previousEntryId
    ) {
        Set<EvidenceId> evidenceIds = recommendation.evidenceIds();
        ensureReviewEvidenceIsPersisted(evidenceIds, decision, recommendation, command.action());
        try {
            return new LedgerEntry(
                    command.ledgerEntryId(),
                    decision.id(),
                    Optional.of(recommendation.id()),
                    command.reviewerId(),
                    inputs.actorRole(),
                    command.reviewedAt(),
                    entryType(command.action()),
                    changeSummary(command.action()),
                    command.reviewReason(),
                    evidenceIds,
                    Optional.of(recommendation.estimatedSavings()),
                    Optional.empty(),
                    Optional.of(recommendation.confidence()),
                    Optional.of(recommendation.risk()),
                    previousEntryId,
                    inputs.metadata()
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new LedgerAppendRejectedException(
                    decision.id(),
                    failureMessage(exception, "Review Ledger entry rejected"),
                    exception
            );
        }
    }

    private void ensureReviewEvidenceIsPersisted(
            Set<EvidenceId> evidenceIds,
            Decision decision,
            Recommendation recommendation,
            ReviewDecisionAction action
    ) {
        for (EvidenceId evidenceId : evidenceIds) {
            Evidence evidence = evidenceRepository.findById(evidenceId)
                    .orElseThrow(() -> new EvidenceNotFoundException(evidenceId));
            if (!decision.caseId().equals(evidence.correlationKey()) || !recommendation.isSupportedBy(evidenceId)) {
                throw new EvidenceTraceabilityViolationException(
                        evidenceId,
                        decision.id(),
                        "Review Evidence must belong to the Decision and support the Recommendation"
                );
            }
            if (action.approves() && !evidence.canSupportApproval()) {
                throw new EvidenceTraceabilityViolationException(
                        evidenceId,
                        decision.id(),
                        "Approval Evidence must be accepted"
                );
            }
        }
    }

    private ReviewDecisionResult result(LedgerEntry entry, DecisionStatus status, boolean replayed) {
        return new ReviewDecisionResult(
                entry.id(),
                entry.decisionId(),
                entry.recommendationId().orElseThrow(),
                status,
                entry.actorId(),
                entry.actorRole(),
                entry.reason(),
                replayed
        );
    }

    private DecisionStatus statusFor(LedgerEntryType entryType) {
        if (LedgerEntryType.APPROVED.equals(entryType)) {
            return DecisionStatus.APPROVED;
        }
        if (LedgerEntryType.REJECTED.equals(entryType)) {
            return DecisionStatus.REJECTED;
        }
        if (LedgerEntryType.DEFERRED.equals(entryType)) {
            return DecisionStatus.DEFERRED;
        }
        throw new ConflictException("LEDGER_OPERATION_CONFLICT", "Ledger replay is not a review outcome");
    }

    private LedgerEntryType entryType(ReviewDecisionAction action) {
        if (action.approves()) {
            return LedgerEntryType.APPROVED;
        }
        if (action.rejects()) {
            return LedgerEntryType.REJECTED;
        }
        return LedgerEntryType.DEFERRED;
    }

    private String changeSummary(ReviewDecisionAction action) {
        if (action.approves()) {
            return "Decision approved";
        }
        if (action.rejects()) {
            return "Decision rejected";
        }
        return "Decision deferred";
    }

    private ReviewInputs validateCommand(ReviewDecisionCommand command) {
        if (command == null) {
            throw new ValidationException("REVIEW_DECISION_COMMAND_REQUIRED", "Review decision command is required");
        }
        requireValue(command.ledgerEntryId(), "LEDGER_ENTRY_ID_REQUIRED", "Ledger entry id is required");
        requireValue(command.decisionId(), "DECISION_ID_REQUIRED", "Decision id is required");
        requireValue(command.action(), "REVIEW_ACTION_REQUIRED", "Review action is required");
        requireValue(command.reviewerId(), "REVIEWER_ID_REQUIRED", "Reviewer id is required");
        String actorRole = requireText(command.reviewerRole(), "REVIEWER_ROLE_REQUIRED", "Reviewer role is required")
                .toUpperCase(Locale.ROOT);
        requireValue(command.reviewedAt(), "REVIEWED_AT_REQUIRED", "Review timestamp is required");
        requireText(command.reviewReason(), "REVIEW_REASON_REQUIRED", "Review reason is required");

        Optional<LedgerEntryId> expectedPrevious = optional(command.expectedPreviousEntryId());
        Optional<String> requiredEvidence = optionalText(command.requiredEvidence());
        Optional<LocalDate> reviewDate = optional(command.reviewDate());
        Map<String, String> metadata = reviewMetadata(command.action(), requiredEvidence, reviewDate);
        return new ReviewInputs(actorRole, expectedPrevious, metadata);
    }

    private Map<String, String> reviewMetadata(
            ReviewDecisionAction action,
            Optional<String> requiredEvidence,
            Optional<LocalDate> reviewDate
    ) {
        if (!action.defers()) {
            if (requiredEvidence.isPresent() || reviewDate.isPresent()) {
                throw new ValidationException(
                        "REVIEW_FOLLOW_UP_NOT_ALLOWED",
                        "Approval and rejection must not include deferral follow-up"
                );
            }
            return Map.of();
        }
        if (requiredEvidence.isPresent() == reviewDate.isPresent()) {
            throw new ValidationException(
                    "DEFERRAL_FOLLOW_UP_REQUIRED",
                    "Deferral requires exactly one of required Evidence or review date"
            );
        }
        return requiredEvidence
                .<Map<String, String>>map(value -> Map.of("required_evidence", value))
                .orElseGet(() -> Map.of("review_date", reviewDate.orElseThrow().toString()));
    }

    private AuthorizationException forbidden(String message) {
        return new AuthorizationException("GOVERNANCE_ACTION_FORBIDDEN", message);
    }

    private ConflictException operationConflict(imperator.domain.shared.DecisionId decisionId, String reason) {
        return new ConflictException(
                "LEDGER_OPERATION_CONFLICT",
                "Ledger operation conflict for Decision " + decisionId.value() + ": " + reason
        );
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private <T> Optional<T> optional(Optional<T> value) {
        return value == null ? Optional.empty() : value;
    }

    private Optional<String> optionalText(Optional<String> value) {
        Optional<String> candidate = optional(value);
        return candidate.map(item -> requireText(
                item,
                "REQUIRED_EVIDENCE_INVALID",
                "Required Evidence description must not be blank"
        ));
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

    private record ReviewInputs(
            String actorRole,
            Optional<LedgerEntryId> expectedPreviousEntryId,
            Map<String, String> metadata
    ) {
    }
}
