package imperator.application.synchronizeevidence;

import imperator.application.exceptions.DuplicateEvidenceException;
import imperator.application.exceptions.ValidationException;
import imperator.application.importevidence.ImportEvidenceCommand;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.SynchronizeEvidenceInputPort;
import imperator.ports.out.EvidenceCandidate;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.EvidenceSourceCapture;
import imperator.ports.out.EvidenceSourcePort;
import imperator.ports.out.EvidenceSourceRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SynchronizeEvidenceUseCase implements SynchronizeEvidenceInputPort {
    private static final Duration MAXIMUM_WINDOW = Duration.ofDays(180);

    private final EvidenceSourcePort evidenceSource;
    private final ImportEvidenceInputPort evidenceImporter;
    private final EvidenceRepository evidenceRepository;
    private final AtomicBoolean running = new AtomicBoolean();

    public SynchronizeEvidenceUseCase(
            EvidenceSourcePort evidenceSource,
            ImportEvidenceInputPort evidenceImporter,
            EvidenceRepository evidenceRepository
    ) {
        this.evidenceSource = Objects.requireNonNull(evidenceSource, "Evidence source is required");
        this.evidenceImporter = Objects.requireNonNull(evidenceImporter, "Evidence importer is required");
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
    }

    @Override
    public SynchronizeEvidenceResult synchronize(SynchronizeEvidenceCommand command) {
        validate(command);
        if (!running.compareAndSet(false, true)) {
            return notStarted(command, SynchronizationStatus.SYNC_ALREADY_RUNNING, "SYNC_ALREADY_RUNNING");
        }

        try {
            return synchronizeOnce(command);
        } finally {
            running.set(false);
        }
    }

    private SynchronizeEvidenceResult synchronizeOnce(SynchronizeEvidenceCommand command) {
        EvidenceSourceCapture capture;
        try {
            capture = evidenceSource.capture(new EvidenceSourceRequest(
                    command.fromInclusive(),
                    command.untilExclusive(),
                    command.correlationId()
            ));
        } catch (RuntimeException exception) {
            return notStarted(command, SynchronizationStatus.DEGRADED, "SOURCE_CAPTURE_FAILED");
        }

        if (capture.outcome() != imperator.ports.out.EvidenceSourceOutcome.COMPLETE
                && capture.outcome() != imperator.ports.out.EvidenceSourceOutcome.PARTIAL) {
            return resultWithoutImports(command, capture);
        }

        List<EvidenceCandidate> candidates = capture.candidates().stream()
                .sorted(Comparator
                        .comparing((EvidenceCandidate candidate) -> candidate.timestamp().value())
                        .thenComparing(EvidenceCandidate::evidenceReference)
                        .thenComparing(candidate -> candidate.evidenceId().value().toString()))
                .toList();
        List<EvidenceId> evidenceIds = new ArrayList<>();
        List<String> evidenceReferences = new ArrayList<>();
        int accepted = 0;
        int unchanged = 0;
        int rejected = 0;
        boolean identityConflict = false;
        boolean invalidCandidate = false;

        for (EvidenceCandidate candidate : candidates) {
            ImportEvidenceCommand importCommand = toImportCommand(candidate);
            Optional<Evidence> existing = evidenceRepository.findById(candidate.evidenceId());
            if (existing.isPresent()) {
                if (sameNormalizedFact(existing.orElseThrow(), importCommand)) {
                    unchanged++;
                    addEvidenceResult(candidate, evidenceIds, evidenceReferences);
                } else {
                    rejected++;
                    identityConflict = true;
                }
                continue;
            }

            try {
                evidenceImporter.importEvidence(importCommand);
                accepted++;
                addEvidenceResult(candidate, evidenceIds, evidenceReferences);
            } catch (DuplicateEvidenceException exception) {
                Optional<Evidence> concurrent = evidenceRepository.findById(candidate.evidenceId());
                if (concurrent.isPresent() && sameNormalizedFact(concurrent.orElseThrow(), importCommand)) {
                    unchanged++;
                    addEvidenceResult(candidate, evidenceIds, evidenceReferences);
                } else {
                    rejected++;
                    identityConflict = true;
                }
            } catch (ValidationException exception) {
                rejected++;
                invalidCandidate = true;
            }
        }

        SynchronizationStatus status = SynchronizationStatus.from(capture.outcome());
        String failureCode = capture.failureCode();
        if (identityConflict) {
            status = SynchronizationStatus.SOURCE_IDENTITY_CONFLICT;
            failureCode = "SOURCE_IDENTITY_CONFLICT";
        } else if (invalidCandidate) {
            status = SynchronizationStatus.INCOMPLETE;
            failureCode = "INVALID_EVIDENCE_CANDIDATE";
        }

        return new SynchronizeEvidenceResult(
                command.correlationId(),
                status,
                command.fromInclusive(),
                command.untilExclusive(),
                capture.sourceReference(),
                capture.apiVersion(),
                capture.requestCount(),
                capture.retryCount(),
                capture.pageCount(),
                capture.qualifyingSourceObjectCount(),
                accepted,
                unchanged,
                rejected,
                capture.missingEvidenceReferences().size(),
                evidenceIds,
                evidenceReferences,
                failureCode,
                capture.retryAt()
        );
    }

    private SynchronizeEvidenceResult resultWithoutImports(
            SynchronizeEvidenceCommand command,
            EvidenceSourceCapture capture
    ) {
        return new SynchronizeEvidenceResult(
                command.correlationId(),
                SynchronizationStatus.from(capture.outcome()),
                command.fromInclusive(),
                command.untilExclusive(),
                capture.sourceReference(),
                capture.apiVersion(),
                capture.requestCount(),
                capture.retryCount(),
                capture.pageCount(),
                capture.qualifyingSourceObjectCount(),
                0,
                0,
                0,
                capture.missingEvidenceReferences().size(),
                List.of(),
                List.of(),
                capture.failureCode(),
                capture.retryAt()
        );
    }

    private SynchronizeEvidenceResult notStarted(
            SynchronizeEvidenceCommand command,
            SynchronizationStatus status,
            String failureCode
    ) {
        return new SynchronizeEvidenceResult(
                command.correlationId(),
                status,
                command.fromInclusive(),
                command.untilExclusive(),
                "",
                "",
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                List.of(),
                List.of(),
                failureCode,
                Optional.empty()
        );
    }

    private ImportEvidenceCommand toImportCommand(EvidenceCandidate candidate) {
        Map<String, String> metadata = new LinkedHashMap<>(candidate.metadata());
        metadata.putIfAbsent("case_hint", candidate.caseHint());
        return new ImportEvidenceCommand(
                candidate.evidenceId(),
                candidate.timestamp(),
                candidate.source(),
                candidate.sourceType(),
                candidate.sourceObjectRef(),
                candidate.entity(),
                candidate.eventType(),
                candidate.severity(),
                candidate.actor(),
                candidate.evidenceType(),
                candidate.observedFact(),
                candidate.businessMeaning(),
                candidate.correlationKey(),
                candidate.sensitivity(),
                candidate.confidence(),
                candidate.reviewStatus(),
                candidate.rawPayloadMode(),
                Map.copyOf(metadata)
        );
    }

    private boolean sameNormalizedFact(Evidence evidence, ImportEvidenceCommand command) {
        return evidence.id().equals(command.evidenceId())
                && evidence.timestamp().equals(command.timestamp())
                && evidence.source().equals(command.source())
                && evidence.sourceType().equals(command.sourceType())
                && evidence.sourceObjectRef().equals(command.sourceObjectRef())
                && evidence.entity().equals(command.entity())
                && evidence.eventType().equals(command.eventType())
                && evidence.severity().equals(command.severity())
                && evidence.actor().equals(command.actor())
                && evidence.evidenceType().equals(command.evidenceType())
                && evidence.observedFact().equals(command.observedFact())
                && evidence.businessMeaning().equals(command.businessMeaning())
                && evidence.correlationKey().equals(command.correlationKey())
                && evidence.sensitivity().equalsIgnoreCase(command.sensitivity())
                && evidence.confidence().equalsIgnoreCase(command.confidence())
                && evidence.reviewStatus().equalsIgnoreCase(command.reviewStatus())
                && evidence.rawPayloadMode().equalsIgnoreCase(command.rawPayloadMode())
                && evidence.metadata().equals(command.metadata());
    }

    private void addEvidenceResult(
            EvidenceCandidate candidate,
            List<EvidenceId> evidenceIds,
            List<String> evidenceReferences
    ) {
        evidenceIds.add(candidate.evidenceId());
        evidenceReferences.add(candidate.evidenceReference());
    }

    private void validate(SynchronizeEvidenceCommand command) {
        if (command == null) {
            throw new ValidationException(
                    "SYNCHRONIZE_EVIDENCE_COMMAND_REQUIRED",
                    "Synchronize Evidence command is required"
            );
        }
        if (command.fromInclusive() == null
                || command.untilExclusive() == null
                || command.correlationId() == null) {
            throw new ValidationException(
                    "SYNCHRONIZATION_BOUNDARY_REQUIRED",
                    "Synchronization window and correlation ID are required"
            );
        }
        if (!command.fromInclusive().isBefore(command.untilExclusive())) {
            throw new ValidationException(
                    "INVALID_SYNCHRONIZATION_WINDOW",
                    "Synchronization window start must precede its end"
            );
        }
        if (Duration.between(command.fromInclusive(), command.untilExclusive()).compareTo(MAXIMUM_WINDOW) > 0) {
            throw new ValidationException(
                    "SYNCHRONIZATION_WINDOW_TOO_LARGE",
                    "Synchronization window must not exceed 180 days"
            );
        }
    }
}
