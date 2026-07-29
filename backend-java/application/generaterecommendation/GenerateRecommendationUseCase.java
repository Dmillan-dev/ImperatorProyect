package imperator.application.generaterecommendation;

import imperator.application.exceptions.DecisionAlreadyHasRecommendationException;
import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.EvidenceTraceabilityViolationException;
import imperator.application.exceptions.InvalidDecisionTransitionException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.EvidenceId;
import imperator.ports.in.GenerateRecommendationInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.ExplanationProvider;
import imperator.ports.out.RecommendationExplanationRequest;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class GenerateRecommendationUseCase implements GenerateRecommendationInputPort {
    private final DecisionRepository decisionRepository;
    private final EvidenceRepository evidenceRepository;
    private final RecommendationRepository recommendationRepository;
    private final ExplanationProvider explanationProvider;
    private final TransactionRunner transactionRunner;

    public GenerateRecommendationUseCase(
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            ExplanationProvider explanationProvider,
            TransactionRunner transactionRunner
    ) {
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.recommendationRepository = Objects.requireNonNull(recommendationRepository, "Recommendation repository is required");
        this.explanationProvider = Objects.requireNonNull(explanationProvider, "Explanation provider is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
    }

    @Override
    public GenerateRecommendationResult generateRecommendation(GenerateRecommendationCommand command) {
        validateCommand(command);

        Decision decision = decisionRepository.findById(command.decisionId())
                .orElseThrow(() -> new DecisionNotFoundException(command.decisionId()));

        ensureDecisionCanReceiveRecommendation(decision);
        Set<Evidence> evidence = loadTraceableEvidence(command, decision);

        Optional<String> explanation = explanationProvider.generateExplanation(explanationRequest(command, decision, evidence.size()))
                .map(explanationText -> explanationText.text());

        Recommendation recommendation;
        try {
            recommendation = new Recommendation(
                    command.recommendationId(),
                    decision.id(),
                    command.recommendationType(),
                    command.suggestedAction(),
                    command.deterministicReason(),
                    command.evidenceIds(),
                    command.estimatedSavings(),
                    command.confidence(),
                    command.risk(),
                    decision.ownerId(),
                    decision.requiredApproverId(),
                    command.generatedAt()
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ValidationException(
                    "RECOMMENDATION_VALIDATION_FAILED",
                    failureMessage(exception, "Recommendation validation failed"),
                    exception
            );
        }

        try {
            decision.attachRecommendation(recommendation.id(), command.generatedAt());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException exception) {
            throw new InvalidDecisionTransitionException(
                    decision.id(),
                    failureMessage(exception, "Recommendation could not be attached to the decision"),
                    exception
            );
        }

        return transactionRunner.execute(() -> {
            recommendationRepository.save(recommendation);
            decisionRepository.save(decision);
            return new GenerateRecommendationResult(
                    recommendation.id(),
                    decision.id(),
                    evidence.size(),
                    true,
                    explanation
            );
        });
    }

    private void ensureDecisionCanReceiveRecommendation(Decision decision) {
        if (decision.hasRecommendation()) {
            throw new DecisionAlreadyHasRecommendationException(decision.id());
        }
    }

    private Set<Evidence> loadTraceableEvidence(GenerateRecommendationCommand command, Decision decision) {
        if (!command.evidenceIds().contains(decision.originatingEvidenceId())) {
            throw new EvidenceTraceabilityViolationException(
                    decision.originatingEvidenceId(),
                    decision.id(),
                    "Recommendation must reference the decision's originating evidence"
            );
        }

        Set<Evidence> evidence = new LinkedHashSet<>();
        for (EvidenceId evidenceId : command.evidenceIds()) {
            if (evidenceId == null) {
                throw new ValidationException("RECOMMENDATION_EVIDENCE_ID_REQUIRED", "Recommendation evidence id is required");
            }
            Evidence item = evidenceRepository.findById(evidenceId)
                    .orElseThrow(() -> new EvidenceNotFoundException(evidenceId));
            if (!decision.caseId().equals(item.correlationKey())) {
                throw new EvidenceTraceabilityViolationException(
                        evidenceId,
                        decision.id(),
                        "Recommendation evidence must belong to the decision case"
                );
            }
            evidence.add(item);
        }
        return Set.copyOf(evidence);
    }

    private RecommendationExplanationRequest explanationRequest(
            GenerateRecommendationCommand command,
            Decision decision,
            int evidenceCount
    ) {
        return new RecommendationExplanationRequest(
                decision.id(),
                decision.caseId(),
                decision.businessNeed(),
                command.recommendationType(),
                command.suggestedAction(),
                command.deterministicReason(),
                command.estimatedSavings(),
                command.confidence(),
                command.risk(),
                evidenceCount
        );
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private void validateCommand(GenerateRecommendationCommand command) {
        if (command == null) {
            throw new ValidationException("GENERATE_RECOMMENDATION_COMMAND_REQUIRED", "Generate recommendation command is required");
        }
        requireValue(command.recommendationId(), "RECOMMENDATION_ID_REQUIRED", "Recommendation id is required");
        requireValue(command.decisionId(), "DECISION_ID_REQUIRED", "Decision id is required");
        requireValue(command.recommendationType(), "RECOMMENDATION_TYPE_REQUIRED", "Recommendation type is required");
        requireText(command.suggestedAction(), "RECOMMENDATION_ACTION_REQUIRED", "Recommendation suggested action is required");
        requireText(command.deterministicReason(), "RECOMMENDATION_REASON_REQUIRED", "Recommendation deterministic reason is required");
        if (command.evidenceIds() == null || command.evidenceIds().isEmpty()) {
            throw new ValidationException("RECOMMENDATION_EVIDENCE_REQUIRED", "Recommendation must reference evidence");
        }
        requireValue(command.estimatedSavings(), "RECOMMENDATION_ESTIMATED_SAVING_REQUIRED", "Recommendation estimated savings is required");
        requireValue(command.confidence(), "RECOMMENDATION_CONFIDENCE_REQUIRED", "Recommendation confidence is required");
        requireValue(command.risk(), "RECOMMENDATION_RISK_REQUIRED", "Recommendation risk is required");
        requireValue(command.generatedAt(), "RECOMMENDATION_GENERATED_AT_REQUIRED", "Recommendation generation timestamp is required");
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
