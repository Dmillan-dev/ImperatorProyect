package imperator.application.appendledgerentry;

import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.EvidenceTraceabilityViolationException;
import imperator.application.exceptions.LedgerAppendRejectedException;
import imperator.application.exceptions.LedgerEntryNotFoundException;
import imperator.application.exceptions.RecommendationNotFoundException;
import imperator.application.exceptions.RecommendationOwnershipViolationException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.LedgerEntryId;
import imperator.domain.shared.RecommendationId;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class AppendLedgerEntryUseCase implements AppendLedgerEntryInputPort {
    private final DecisionRepository decisionRepository;
    private final RecommendationRepository recommendationRepository;
    private final EvidenceRepository evidenceRepository;
    private final LedgerRepository ledgerRepository;
    private final TransactionRunner transactionRunner;

    public AppendLedgerEntryUseCase(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner
    ) {
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.recommendationRepository = Objects.requireNonNull(recommendationRepository, "Recommendation repository is required");
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "Ledger repository is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
    }

    @Override
    public AppendLedgerEntryResult appendLedgerEntry(AppendLedgerEntryCommand command) {
        validateCommand(command);
        Optional<RecommendationId> recommendationId = optional(command.recommendationId());
        Set<EvidenceId> evidenceSnapshotIds = evidenceSnapshots(command.evidenceSnapshotIds());
        Optional<LedgerEntryId> previousEntryId = optional(command.previousEntryId());

        return transactionRunner.execute(() -> appendLedgerEntryInTransaction(
                command,
                recommendationId,
                evidenceSnapshotIds,
                previousEntryId
        ));
    }

    private AppendLedgerEntryResult appendLedgerEntryInTransaction(
            AppendLedgerEntryCommand command,
            Optional<RecommendationId> recommendationId,
            Set<EvidenceId> evidenceSnapshotIds,
            Optional<LedgerEntryId> previousEntryId
    ) {
        Decision decision = decisionRepository.findById(command.decisionId())
                .orElseThrow(() -> new DecisionNotFoundException(command.decisionId()));

        Optional<Recommendation> recommendation = recommendationId.map(this::loadRecommendation);

        ensureRecommendationMatchesDecision(decision, recommendation);
        ensureDecisionStateMatchesLedgerFact(decision, command.entryType());
        ensureEvidenceSnapshotsAreTraceable(evidenceSnapshotIds, decision, recommendation);
        ensurePreviousEntryBelongsToDecision(previousEntryId, decision);

        LedgerEntry ledgerEntry = createLedgerEntry(command, decision, recommendationId, evidenceSnapshotIds, previousEntryId);

        ledgerRepository.append(ledgerEntry);

        return new AppendLedgerEntryResult(
                ledgerEntry.id(),
                ledgerEntry.decisionId(),
                ledgerEntry.recommendationId(),
                ledgerEntry.entryType(),
                ledgerEntry.occurredAt(),
                ledgerEntry.evidenceSnapshotIds().size()
        );
    }

    private Recommendation loadRecommendation(RecommendationId recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException(recommendationId));
    }

    private void ensureRecommendationMatchesDecision(
            Decision decision,
            Optional<Recommendation> recommendation
    ) {
        recommendation.ifPresent(item -> {
            if (!item.belongsTo(decision.id())) {
                throw new RecommendationOwnershipViolationException(item.id(), decision.id());
            }
            if (decision.recommendationId().filter(item.id()::equals).isEmpty()) {
                throw new RecommendationOwnershipViolationException(item.id(), decision.id());
            }
        });
    }

    private void ensureDecisionStateMatchesLedgerFact(Decision decision, LedgerEntryType entryType) {
        if (LedgerEntryType.RECOMMENDATION_CREATED.equals(entryType) && !decision.hasRecommendation()) {
            throw new LedgerAppendRejectedException(decision.id(), "Recommendation ledger entry requires a decision with a recommendation");
        }
        if (LedgerEntryType.APPROVED.equals(entryType) && !decision.wasApproved()) {
            throw new LedgerAppendRejectedException(decision.id(), "Approved ledger entry requires an approved decision");
        }
        if (LedgerEntryType.REJECTED.equals(entryType) && !decision.wasRejected()) {
            throw new LedgerAppendRejectedException(decision.id(), "Rejected ledger entry requires a rejected decision");
        }
        if (LedgerEntryType.DEFERRED.equals(entryType) && !decision.isDeferred()) {
            throw new LedgerAppendRejectedException(decision.id(), "Deferred ledger entry requires a deferred decision");
        }
    }

    private void ensureEvidenceSnapshotsAreTraceable(
            Set<EvidenceId> evidenceSnapshotIds,
            Decision decision,
            Optional<Recommendation> recommendation
    ) {
        for (EvidenceId evidenceId : evidenceSnapshotIds) {
            Evidence evidence = evidenceRepository.findById(evidenceId)
                    .orElseThrow(() -> new EvidenceNotFoundException(evidenceId));
            if (!decision.caseId().equals(evidence.correlationKey())) {
                throw new EvidenceTraceabilityViolationException(
                        evidenceId,
                        decision.id(),
                        "Ledger evidence snapshot must belong to the decision case"
                );
            }
            recommendation.ifPresent(item -> {
                if (!item.isSupportedBy(evidenceId)) {
                    throw new EvidenceTraceabilityViolationException(
                            evidenceId,
                            decision.id(),
                            "Ledger evidence snapshot must support the recommendation"
                    );
                }
            });
        }
    }

    private void ensurePreviousEntryBelongsToDecision(
            Optional<LedgerEntryId> previousEntryId,
            Decision decision
    ) {
        previousEntryId.ifPresent(entryId -> {
            LedgerEntry previousEntry = ledgerRepository.findById(entryId)
                    .orElseThrow(() -> new LedgerEntryNotFoundException(entryId));
            if (!previousEntry.belongsTo(decision.id())) {
                throw new LedgerAppendRejectedException(decision.id(), "Previous ledger entry must belong to the same decision");
            }
        });
    }

    private LedgerEntry createLedgerEntry(
            AppendLedgerEntryCommand command,
            Decision decision,
            Optional<RecommendationId> recommendationId,
            Set<EvidenceId> evidenceSnapshotIds,
            Optional<LedgerEntryId> previousEntryId
    ) {
        try {
            return new LedgerEntry(
                    command.ledgerEntryId(),
                    decision.id(),
                    recommendationId,
                    command.actorId(),
                    command.actorRole(),
                    command.occurredAt(),
                    command.entryType(),
                    command.changeSummary(),
                    command.reason(),
                    evidenceSnapshotIds,
                    command.estimatedSaving(),
                    command.realizedSaving(),
                    command.confidenceSnapshot(),
                    command.riskSnapshot(),
                    previousEntryId,
                    command.metadata()
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new LedgerAppendRejectedException(decision.id(), failureMessage(exception, "Ledger entry rejected"), exception);
        }
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private void validateCommand(AppendLedgerEntryCommand command) {
        if (command == null) {
            throw new ValidationException("APPEND_LEDGER_ENTRY_COMMAND_REQUIRED", "Append ledger entry command is required");
        }
        requireValue(command.ledgerEntryId(), "LEDGER_ENTRY_ID_REQUIRED", "Ledger entry id is required");
        requireValue(command.decisionId(), "DECISION_ID_REQUIRED", "Decision id is required");
        requireValue(command.actorId(), "LEDGER_ACTOR_ID_REQUIRED", "Ledger actor id is required");
        requireText(command.actorRole(), "LEDGER_ACTOR_ROLE_REQUIRED", "Ledger actor role is required");
        requireValue(command.occurredAt(), "LEDGER_OCCURRED_AT_REQUIRED", "Ledger timestamp is required");
        requireValue(command.entryType(), "LEDGER_ENTRY_TYPE_REQUIRED", "Ledger entry type is required");
        requireText(command.changeSummary(), "LEDGER_CHANGE_SUMMARY_REQUIRED", "Ledger change summary is required");
        requireText(command.reason(), "LEDGER_REASON_REQUIRED", "Ledger reason is required");
    }

    private Set<EvidenceId> evidenceSnapshots(Set<EvidenceId> evidenceSnapshotIds) {
        if (evidenceSnapshotIds == null || evidenceSnapshotIds.isEmpty()) {
            return Set.of();
        }
        for (EvidenceId evidenceId : evidenceSnapshotIds) {
            if (evidenceId == null) {
                throw new ValidationException("LEDGER_EVIDENCE_SNAPSHOT_ID_REQUIRED", "Ledger evidence snapshot id is required");
            }
        }
        return Set.copyOf(evidenceSnapshotIds);
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
}
