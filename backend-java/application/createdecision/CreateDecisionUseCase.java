package imperator.application.createdecision;

import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.evidence.Evidence;
import imperator.ports.in.CreateDecisionInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.TransactionRunner;

import java.util.Objects;

public final class CreateDecisionUseCase implements CreateDecisionInputPort {
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

        Decision decision;
        try {
            decision = Decision.create(
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

        decisionRepository.save(decision);

        return new CreateDecisionResult(
                decision.id(),
                decision.caseId(),
                decision.originatingEvidenceId(),
                decision.status(),
                evidence.canSupportApproval(),
                evidence.requiresReview()
        );
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
