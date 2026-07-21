package imperator.application.importevidence;

import imperator.application.exceptions.ValidationException;
import imperator.domain.evidence.Evidence;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.out.EvidenceRepository;

import java.util.Objects;

public final class ImportEvidenceUseCase implements ImportEvidenceInputPort {
    private final EvidenceRepository evidenceRepository;

    public ImportEvidenceUseCase(EvidenceRepository evidenceRepository) {
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
    }

    @Override
    public ImportEvidenceResult importEvidence(ImportEvidenceCommand command) {
        if (command == null) {
            throw new ValidationException("IMPORT_EVIDENCE_COMMAND_REQUIRED", "Import evidence command is required");
        }

        Evidence evidence;
        try {
            evidence = new Evidence(
                    command.evidenceId(),
                    command.timestamp(),
                    command.source(),
                    command.sourceType(),
                    command.sourceObjectRef(),
                    command.entity(),
                    command.eventType(),
                    command.severity(),
                    command.actor(),
                    command.evidenceType(),
                    command.observedFact(),
                    command.businessMeaning(),
                    command.correlationKey(),
                    command.sensitivity(),
                    command.confidence(),
                    command.reviewStatus(),
                    command.rawPayloadMode(),
                    command.metadata()
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ValidationException("EVIDENCE_VALIDATION_FAILED", failureMessage(exception, "Evidence validation failed"), exception);
        }

        evidenceRepository.save(evidence);

        return new ImportEvidenceResult(
                evidence.id(),
                evidence.correlationKey(),
                evidence.canSupportApproval(),
                evidence.requiresReview()
        );
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }
}
