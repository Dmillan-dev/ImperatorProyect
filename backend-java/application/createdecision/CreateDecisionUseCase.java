package imperator.application.createdecision;

import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.DecisionCreationConflictException;
import imperator.application.exceptions.EvidenceTraceabilityViolationException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.evidence.Evidence;
import imperator.ports.in.CreateDecisionInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.TransactionRunner;

import java.util.Objects;

public final class CreateDecisionUseCase implements CreateDecisionInputPort {
    private static final String MVP_CASE_ID = "DRC-AOA-001";
    private static final String ORIGINATING_EVIDENCE_TYPE = "business_context";
    private static final String ORIGINATING_EVENT_TYPE = "business_context_requested";

    private final EvidenceRepository evidenceRepository;
    private final DecisionRepository decisionRepository;
    private final TransactionRunner transactionRunner;

    public CreateDecisionUseCase(
            EvidenceRepository evidenceRepository,
            DecisionRepository decisionRepository,
            TransactionRunner transactionRunner
    ) {
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
    }

    @Override
    public CreateDecisionResult createDecision(CreateDecisionCommand command) {
        validateCommand(command);

        return transactionRunner.execute(() -> createDecisionInTransaction(command));
    }

    private CreateDecisionResult createDecisionInTransaction(CreateDecisionCommand command) {
        Evidence evidence = evidenceRepository.findById(command.originatingEvidenceId())
                .orElseThrow(() -> new EvidenceNotFoundException(command.originatingEvidenceId()));
        validateOriginatingEvidence(evidence, command);

        Decision candidate;
        try {
            candidate = Decision.create(
                    command.decisionId(),
                    evidence.correlationKey(),
                    command.title(),
                    command.businessNeed(),
                    evidence.id(),
                    command.ownerId(),
                    command.requiredApproverId(),
                    command.createdAt()
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ValidationException("DECISION_VALIDATION_FAILED", failureMessage(exception, "Decision validation failed"), exception);
        }

        Decision persisted = decisionRepository.createIfAbsent(candidate);
        if (!hasSameImmutableCreationState(candidate, persisted)) {
            throw new DecisionCreationConflictException(candidate.id());
        }

        return new CreateDecisionResult(
                persisted.id(),
                persisted.caseId(),
                persisted.originatingEvidenceId(),
                persisted.status(),
                evidence.canSupportApproval(),
                evidence.requiresReview()
        );
    }

    private void validateOriginatingEvidence(Evidence evidence, CreateDecisionCommand command) {
        String rejectionReason = originatingEvidenceRejectionReason(evidence);
        if (rejectionReason != null) {
            throw new EvidenceTraceabilityViolationException(
                    evidence.id(),
                    command.decisionId(),
                    rejectionReason
            );
        }
    }

    private String originatingEvidenceRejectionReason(Evidence evidence) {
        if (!MVP_CASE_ID.equals(evidence.correlationKey())) {
            return "correlation key must be " + MVP_CASE_ID;
        }
        if (!ORIGINATING_EVIDENCE_TYPE.equals(evidence.evidenceType())) {
            return "evidence type must be " + ORIGINATING_EVIDENCE_TYPE;
        }
        if (!ORIGINATING_EVENT_TYPE.equals(evidence.eventType())) {
            return "event type must be " + ORIGINATING_EVENT_TYPE;
        }
        if (!"ACCEPTED".equals(evidence.reviewStatus())) {
            return "review status must be ACCEPTED";
        }
        if ("RESTRICTED".equals(evidence.sensitivity())) {
            return "RESTRICTED evidence cannot originate the Decision";
        }
        if (!"not_stored".equals(evidence.rawPayloadMode())) {
            return "raw payload mode must be not_stored";
        }
        return null;
    }

    private boolean hasSameImmutableCreationState(Decision candidate, Decision persisted) {
        return candidate.id().equals(persisted.id())
                && candidate.caseId().equals(persisted.caseId())
                && candidate.originatingEvidenceId().equals(persisted.originatingEvidenceId())
                && candidate.title().equals(persisted.title())
                && candidate.businessNeed().equals(persisted.businessNeed())
                && candidate.ownerId().equals(persisted.ownerId())
                && candidate.requiredApproverId().equals(persisted.requiredApproverId())
                && candidate.createdAt().equals(persisted.createdAt());
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private void validateCommand(CreateDecisionCommand command) {
        if (command == null) {
            throw new ValidationException("CREATE_DECISION_COMMAND_REQUIRED", "Create decision command is required");
        }
        requireValue(command.decisionId(), "DECISION_ID_REQUIRED", "Decision id is required");
        requireValue(command.originatingEvidenceId(), "ORIGINATING_EVIDENCE_ID_REQUIRED", "Originating evidence id is required");
        requireText(command.title(), "DECISION_TITLE_REQUIRED", "Decision title is required");
        requireText(command.businessNeed(), "DECISION_BUSINESS_NEED_REQUIRED", "Decision business need is required");
        requireValue(command.ownerId(), "DECISION_OWNER_REQUIRED", "Decision owner is required");
        requireValue(command.requiredApproverId(), "DECISION_APPROVER_REQUIRED", "Decision approver is required");
        requireValue(command.createdAt(), "DECISION_CREATED_AT_REQUIRED", "Decision creation timestamp is required");
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
