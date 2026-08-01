package imperator.application.appendledgerentry;

import imperator.application.exceptions.AuthorizationException;
import imperator.application.exceptions.ConflictException;
import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.EvidenceTraceabilityViolationException;
import imperator.application.exceptions.LedgerAppendRejectedException;
import imperator.application.exceptions.RecommendationNotFoundException;
import imperator.application.exceptions.RecommendationOwnershipViolationException;
import imperator.application.exceptions.ValidationException;
import imperator.application.ledger.LedgerChain;
import imperator.domain.decision.Decision;
import imperator.domain.decision.DrcAoa001ResultValidationPolicy;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.ROIAmount;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class AppendLedgerEntryUseCase implements AppendLedgerEntryInputPort {
    private static final String PLATFORM_ENGINEER = "PLATFORM_ENGINEER";
    private static final String FINANCE = "FINANCE";

    private final DecisionRepository decisionRepository;
    private final RecommendationRepository recommendationRepository;
    private final EvidenceRepository evidenceRepository;
    private final LedgerRepository ledgerRepository;
    private final TransactionRunner transactionRunner;
    private final DrcAoa001ResultValidationPolicy resultValidationPolicy;

    public AppendLedgerEntryUseCase(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner
    ) {
        this(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner,
                new DrcAoa001ResultValidationPolicy()
        );
    }

    AppendLedgerEntryUseCase(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner,
            DrcAoa001ResultValidationPolicy resultValidationPolicy
    ) {
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.recommendationRepository = Objects.requireNonNull(
                recommendationRepository,
                "Recommendation repository is required"
        );
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "Ledger repository is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
        this.resultValidationPolicy = Objects.requireNonNull(
                resultValidationPolicy,
                "Result validation policy is required"
        );
    }

    @Override
    public AppendLedgerEntryResult appendLedgerEntry(AppendLedgerEntryCommand command) {
        AppendInputs inputs = validateCommand(command);
        return transactionRunner.execute(() -> appendLedgerEntryInTransaction(command, inputs));
    }

    private AppendLedgerEntryResult appendLedgerEntryInTransaction(
            AppendLedgerEntryCommand command,
            AppendInputs inputs
    ) {
        Decision decision = decisionRepository.findByIdForUpdate(command.decisionId())
                .orElseThrow(() -> new DecisionNotFoundException(command.decisionId()));
        Recommendation recommendation = loadRecommendation(decision);
        ensureRecommendationMatchesDecision(decision, recommendation);
        authorize(command.entryType(), inputs.actorRole());
        ensureApprovedDecision(decision);

        List<LedgerEntry> history = ledgerRepository.findByDecisionId(decision.id());
        LedgerChain chain = LedgerChain.from(decision.id(), history);
        Optional<LedgerEntry> existing = ledgerRepository.findById(command.ledgerEntryId());
        if (existing.isPresent()) {
            return resolveReplay(command, inputs, decision, recommendation, chain, existing.get());
        }

        chain.requireExpectedHead(inputs.expectedPreviousEntryId());
        chain.requireNextOccurrence(command.occurredAt());
        ensureCommandCanFollowHead(command.entryType(), decision, recommendation, chain.head());
        Set<EvidenceId> evidenceIds = loadTraceableEvidence(inputs.evidenceIds(), decision);
        LedgerEntry candidate = ledgerEntry(
                command,
                inputs,
                decision,
                recommendation,
                evidenceIds,
                chain.head().map(LedgerEntry::id)
        );

        LedgerEntry authoritative = ledgerRepository.append(candidate);
        if (!candidate.hasSameImmutableState(authoritative)) {
            throw operationConflict(decision, "Ledger operation identifier already has different content");
        }
        return result(candidate, false);
    }

    private AppendLedgerEntryResult resolveReplay(
            AppendLedgerEntryCommand command,
            AppendInputs inputs,
            Decision decision,
            Recommendation recommendation,
            LedgerChain chain,
            LedgerEntry existing
    ) {
        if (!existing.belongsTo(decision.id()) || !chain.contains(existing.id())) {
            throw operationConflict(decision, "Ledger operation identifier belongs to another history");
        }
        Set<EvidenceId> evidenceIds = loadTraceableEvidence(inputs.evidenceIds(), decision);
        LedgerEntry expected = ledgerEntry(
                command,
                inputs,
                decision,
                recommendation,
                evidenceIds,
                inputs.expectedPreviousEntryId()
        );
        if (!expected.hasSameImmutableState(existing)) {
            throw operationConflict(decision, "Replay payload differs from the authoritative Ledger entry");
        }
        return result(existing, true);
    }

    private Recommendation loadRecommendation(Decision decision) {
        imperator.domain.shared.RecommendationId recommendationId = decision.recommendationId()
                .orElseThrow(() -> new LedgerAppendRejectedException(
                        decision.id(),
                        "Ledger governance requires a persisted Recommendation"
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

    private void authorize(LedgerEntryType entryType, String actorRole) {
        if (LedgerEntryType.IMPLEMENTATION_MARKED.equals(entryType) && !PLATFORM_ENGINEER.equals(actorRole)) {
            throw forbidden("Only PLATFORM_ENGINEER may mark implementation");
        }
        if (LedgerEntryType.RESULT_VALIDATED.equals(entryType) && !FINANCE.equals(actorRole)) {
            throw forbidden("Only FINANCE may validate realized recovery");
        }
    }

    private void ensureApprovedDecision(Decision decision) {
        if (!decision.wasApproved()) {
            throw new LedgerAppendRejectedException(
                    decision.id(),
                    "Implementation and result validation require an approved Decision"
            );
        }
    }

    private void ensureCommandCanFollowHead(
            LedgerEntryType entryType,
            Decision decision,
            Recommendation recommendation,
            Optional<LedgerEntry> head
    ) {
        LedgerEntry previous = head.orElseThrow(() -> operationConflict(
                decision,
                "Implementation and validation require existing governance history"
        ));
        if (previous.recommendationId().filter(recommendation.id()::equals).isEmpty()) {
            throw operationConflict(decision, "Ledger head belongs to another Recommendation");
        }
        if (LedgerEntryType.IMPLEMENTATION_MARKED.equals(entryType)
                && !LedgerEntryType.APPROVED.equals(previous.entryType())) {
            throw operationConflict(decision, "Implementation requires the approved Ledger head");
        }
        if (LedgerEntryType.RESULT_VALIDATED.equals(entryType)
                && !LedgerEntryType.IMPLEMENTATION_MARKED.equals(previous.entryType())) {
            throw operationConflict(decision, "Result validation requires the implementation Ledger head");
        }
    }

    private Set<EvidenceId> loadTraceableEvidence(Set<EvidenceId> evidenceIds, Decision decision) {
        Set<EvidenceId> ids = Set.copyOf(evidenceIds);
        for (EvidenceId evidenceId : ids) {
            Evidence evidence = evidenceRepository.findById(evidenceId)
                    .orElseThrow(() -> new EvidenceNotFoundException(evidenceId));
            if (!decision.caseId().equals(evidence.correlationKey())) {
                throw new EvidenceTraceabilityViolationException(
                        evidenceId,
                        decision.id(),
                        "Implementation and validation Evidence must belong to the Decision case"
                );
            }
        }
        return ids;
    }

    private LedgerEntry ledgerEntry(
            AppendLedgerEntryCommand command,
            AppendInputs inputs,
            Decision decision,
            Recommendation recommendation,
            Set<EvidenceId> evidenceIds,
            Optional<LedgerEntryId> previousEntryId
    ) {
        Optional<ROIAmount> realizedSaving = Optional.empty();
        Map<String, String> metadata;
        if (LedgerEntryType.RESULT_VALIDATED.equals(command.entryType())) {
            DrcAoa001ResultValidationPolicy.ValidationResult validation = validateResult(command, recommendation);
            realizedSaving = Optional.of(validation.realizedRecovery());
            metadata = validationMetadata(command, validation);
        } else {
            metadata = Map.of("implementation_period", inputs.period());
        }

        try {
            return new LedgerEntry(
                    command.ledgerEntryId(),
                    decision.id(),
                    Optional.of(recommendation.id()),
                    command.actorId(),
                    inputs.actorRole(),
                    command.occurredAt(),
                    command.entryType(),
                    changeSummary(command.entryType()),
                    command.reason(),
                    evidenceIds,
                    LedgerEntryType.RESULT_VALIDATED.equals(command.entryType())
                            ? Optional.of(recommendation.estimatedSavings())
                            : Optional.empty(),
                    realizedSaving,
                    LedgerEntryType.RESULT_VALIDATED.equals(command.entryType())
                            ? Optional.of(recommendation.confidence())
                            : Optional.empty(),
                    LedgerEntryType.RESULT_VALIDATED.equals(command.entryType())
                            ? Optional.of(recommendation.risk())
                            : Optional.empty(),
                    previousEntryId,
                    metadata
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new LedgerAppendRejectedException(
                    decision.id(),
                    failureMessage(exception, "Governance Ledger entry rejected"),
                    exception
            );
        }
    }

    private DrcAoa001ResultValidationPolicy.ValidationResult validateResult(
            AppendLedgerEntryCommand command,
            Recommendation recommendation
    ) {
        try {
            return resultValidationPolicy.validate(
                    command.annualizedBaselineCost().orElseThrow(),
                    command.annualizedPostActionCost().orElseThrow(),
                    command.actualTransitionCost().orElseThrow(),
                    recommendation.estimatedSavings()
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new LedgerAppendRejectedException(
                    command.decisionId(),
                    failureMessage(exception, "Result validation rejected"),
                    exception
            );
        }
    }

    private Map<String, String> validationMetadata(
            AppendLedgerEntryCommand command,
            DrcAoa001ResultValidationPolicy.ValidationResult validation
    ) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("validation_period", command.period().trim());
        metadata.put("annualized_baseline_cost", amount(command.annualizedBaselineCost().orElseThrow()));
        metadata.put("annualized_post_action_cost", amount(command.annualizedPostActionCost().orElseThrow()));
        metadata.put("actual_transition_cost", amount(command.actualTransitionCost().orElseThrow()));
        metadata.put("variance", validation.variance().toPlainString());
        metadata.put("outcome_note", command.reason().trim());
        return Map.copyOf(metadata);
    }

    private String amount(ROIAmount value) {
        return value.value().amount().toPlainString();
    }

    private String changeSummary(LedgerEntryType entryType) {
        return LedgerEntryType.IMPLEMENTATION_MARKED.equals(entryType)
                ? "Recommendation implementation marked"
                : "Recommendation result validated";
    }

    private AppendLedgerEntryResult result(LedgerEntry entry, boolean replayed) {
        return new AppendLedgerEntryResult(
                entry.id(),
                entry.decisionId(),
                entry.recommendationId(),
                entry.entryType(),
                entry.occurredAt(),
                entry.evidenceSnapshotIds().size(),
                entry.realizedSaving(),
                replayed
        );
    }

    private AppendInputs validateCommand(AppendLedgerEntryCommand command) {
        if (command == null) {
            throw new ValidationException("APPEND_LEDGER_ENTRY_COMMAND_REQUIRED", "Ledger command is required");
        }
        requireValue(command.ledgerEntryId(), "LEDGER_ENTRY_ID_REQUIRED", "Ledger entry id is required");
        requireValue(command.decisionId(), "DECISION_ID_REQUIRED", "Decision id is required");
        requireValue(command.actorId(), "LEDGER_ACTOR_ID_REQUIRED", "Ledger actor id is required");
        String actorRole = requireText(command.actorRole(), "LEDGER_ACTOR_ROLE_REQUIRED", "Ledger actor role is required")
                .toUpperCase(Locale.ROOT);
        requireValue(command.occurredAt(), "LEDGER_OCCURRED_AT_REQUIRED", "Ledger timestamp is required");
        requireSupportedEntryType(command.entryType());
        requireText(command.reason(), "LEDGER_REASON_REQUIRED", "Ledger reason is required");
        String period = requireText(command.period(), "LEDGER_PERIOD_REQUIRED", "Ledger period is required");
        Set<EvidenceId> evidenceIds = requireEvidence(command.evidenceSnapshotIds());
        Optional<LedgerEntryId> expectedPrevious = optional(command.expectedPreviousEntryId());
        Optional<ROIAmount> baseline = optional(command.annualizedBaselineCost());
        Optional<ROIAmount> postAction = optional(command.annualizedPostActionCost());
        Optional<ROIAmount> transition = optional(command.actualTransitionCost());

        if (LedgerEntryType.RESULT_VALIDATED.equals(command.entryType())) {
            if (baseline.isEmpty() || postAction.isEmpty() || transition.isEmpty()) {
                throw new ValidationException(
                        "RESULT_VALIDATION_COSTS_REQUIRED",
                        "Result validation requires baseline, post-action and transition costs"
                );
            }
        } else if (baseline.isPresent() || postAction.isPresent() || transition.isPresent()) {
            throw new ValidationException(
                    "IMPLEMENTATION_COSTS_NOT_ALLOWED",
                    "Implementation marker must not include result-validation costs"
            );
        }

        return new AppendInputs(actorRole, expectedPrevious, evidenceIds, period);
    }

    private void requireSupportedEntryType(LedgerEntryType entryType) {
        requireValue(entryType, "LEDGER_ENTRY_TYPE_REQUIRED", "Ledger entry type is required");
        if (!LedgerEntryType.IMPLEMENTATION_MARKED.equals(entryType)
                && !LedgerEntryType.RESULT_VALIDATED.equals(entryType)) {
            throw new ValidationException(
                    "LEDGER_ENTRY_TYPE_NOT_ALLOWED",
                    "Only implementation_marked and result_validated are accepted by this use case"
            );
        }
    }

    private Set<EvidenceId> requireEvidence(Set<EvidenceId> evidenceIds) {
        if (evidenceIds == null || evidenceIds.isEmpty() || evidenceIds.stream().anyMatch(Objects::isNull)) {
            throw new ValidationException(
                    "LEDGER_EVIDENCE_REQUIRED",
                    "Implementation and validation require non-null Evidence references"
            );
        }
        return Set.copyOf(evidenceIds);
    }

    private AuthorizationException forbidden(String message) {
        return new AuthorizationException("GOVERNANCE_ACTION_FORBIDDEN", message);
    }

    private ConflictException operationConflict(Decision decision, String reason) {
        return new ConflictException(
                "LEDGER_OPERATION_CONFLICT",
                "Ledger operation conflict for Decision " + decision.id().value() + ": " + reason
        );
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private <T> Optional<T> optional(Optional<T> value) {
        return value == null ? Optional.empty() : value;
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

    private record AppendInputs(
            String actorRole,
            Optional<LedgerEntryId> expectedPreviousEntryId,
            Set<EvidenceId> evidenceIds,
            String period
    ) {
    }
}
