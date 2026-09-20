package imperator.application.generaterecommendation;

import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.EvidenceTraceabilityViolationException;
import imperator.application.exceptions.RecommendationCreationConflictException;
import imperator.application.exceptions.RecommendationNotReadyException;
import imperator.application.exceptions.ValidationException;
import imperator.domain.decision.Decision;
import imperator.domain.decision.DrcAoa001RecommendationPolicy;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Timestamp;
import imperator.ports.in.GenerateRecommendationInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.ExplanationStatus;
import imperator.ports.out.ExplanationProvider;
import imperator.ports.out.RecommendationExplanation;
import imperator.ports.out.RecommendationExplanationEvidence;
import imperator.ports.out.RecommendationExplanationRecord;
import imperator.ports.out.RecommendationExplanationRequest;
import imperator.ports.out.RecommendationExplanationRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;

import java.time.Clock;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public final class GenerateRecommendationUseCase implements GenerateRecommendationInputPort {
    private final DecisionRepository decisionRepository;
    private final EvidenceRepository evidenceRepository;
    private final RecommendationRepository recommendationRepository;
    private final TransactionRunner transactionRunner;
    private final DrcAoa001RecommendationPolicy policy;
    private final ExplanationProvider explanationProvider;
    private final RecommendationExplanationRepository explanationRepository;
    private final Clock clock;
    private final Supplier<UUID> explanationIds;

    public GenerateRecommendationUseCase(
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            TransactionRunner transactionRunner
    ) {
        this(
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                transactionRunner,
                ignored -> Optional.empty(),
                unavailableExplanationRepository()
        );
    }

    public GenerateRecommendationUseCase(
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            TransactionRunner transactionRunner,
            ExplanationProvider explanationProvider
    ) {
        this(
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                transactionRunner,
                explanationProvider,
                unavailableExplanationRepository()
        );
    }

    public GenerateRecommendationUseCase(
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            TransactionRunner transactionRunner,
            ExplanationProvider explanationProvider,
            RecommendationExplanationRepository explanationRepository
    ) {
        this(
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                transactionRunner,
                new DrcAoa001RecommendationPolicy(),
                explanationProvider,
                explanationRepository,
                Clock.systemUTC(),
                UUID::randomUUID
        );
    }

    GenerateRecommendationUseCase(
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            TransactionRunner transactionRunner,
            DrcAoa001RecommendationPolicy policy,
            ExplanationProvider explanationProvider,
            RecommendationExplanationRepository explanationRepository,
            Clock clock,
            Supplier<UUID> explanationIds
    ) {
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.recommendationRepository = Objects.requireNonNull(recommendationRepository, "Recommendation repository is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
        this.policy = Objects.requireNonNull(policy, "Recommendation policy is required");
        this.explanationProvider = Objects.requireNonNull(explanationProvider, "Explanation provider is required");
        this.explanationRepository = Objects.requireNonNull(
                explanationRepository, "Explanation repository is required"
        );
        this.clock = Objects.requireNonNull(clock, "Explanation clock is required");
        this.explanationIds = Objects.requireNonNull(explanationIds, "Explanation id supplier is required");
    }

    @Override
    public GenerateRecommendationResult generateRecommendation(GenerateRecommendationCommand command) {
        validateCommand(command);
        GenerationOutcome outcome = transactionRunner.execute(() -> generateInTransaction(command));
        Optional<String> explanation = generateExplanation(outcome.explanationRequest());
        return outcome.resultWith(explanation);
    }

    private GenerationOutcome generateInTransaction(GenerateRecommendationCommand command) {
        Decision initialDecision = findDecision(command);
        Set<Evidence> evidence = loadTraceableEvidence(command, initialDecision);
        Recommendation candidate = evaluatePolicy(command, initialDecision, evidence);

        Recommendation persisted = recommendationRepository.createIfAbsent(candidate);
        if (!hasSameImmutableCreationState(candidate, persisted)) {
            throw new RecommendationCreationConflictException(initialDecision.id());
        }

        Decision authoritativeDecision = findDecision(command);
        attachOnlyOnFirstCreation(authoritativeDecision, persisted, command);

        return new GenerationOutcome(
                persisted,
                authoritativeDecision,
                explanationRequest(persisted, authoritativeDecision, evidence)
        );
    }

    private Optional<String> generateExplanation(RecommendationExplanationRequest request) {
        Optional<RecommendationExplanationRecord> previous = explanationByGeneration(
                request.recommendationId(),
                explanationProvider.providerName(),
                explanationProvider.modelId(),
                explanationProvider.promptVersion()
        );
        if (previous.isPresent()) {
            return previous.orElseThrow().text();
        }

        Timestamp requestedAt = new Timestamp(clock.instant());
        if (request.evidenceContext().stream()
                .anyMatch(item -> "RESTRICTED".equals(item.sensitivity()))) {
            persistExplanation(record(
                    request,
                    ExplanationStatus.REJECTED,
                    Optional.empty(),
                    explanationProvider.providerName(),
                    explanationProvider.modelId(),
                    explanationProvider.promptVersion(),
                    requestedAt,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("EXPLANATION_CONTEXT_RESTRICTED")
            ));
            return Optional.empty();
        }
        try {
            Optional<RecommendationExplanation> generated = explanationProvider.generateExplanation(request);
            if (generated == null) {
                persistExplanation(record(
                        request,
                        ExplanationStatus.UNAVAILABLE,
                        Optional.empty(),
                        explanationProvider.providerName(),
                        explanationProvider.modelId(),
                        explanationProvider.promptVersion(),
                        requestedAt,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of("EXPLANATION_PROVIDER_UNAVAILABLE")
                ));
                return Optional.empty();
            }
            if (generated.isEmpty()) {
                persistExplanation(record(
                        request,
                        ExplanationStatus.UNAVAILABLE,
                        Optional.empty(),
                        explanationProvider.providerName(),
                        explanationProvider.modelId(),
                        explanationProvider.promptVersion(),
                        requestedAt,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of("EXPLANATION_PROVIDER_UNAVAILABLE")
                ));
                return Optional.empty();
            }
            RecommendationExplanation explanation = generated.orElseThrow();
            if (!explanation.provider().equals(explanationProvider.providerName())
                    || !explanation.modelId().equals(explanationProvider.modelId())
                    || !explanation.promptVersion().equals(explanationProvider.promptVersion())) {
                throw new IllegalArgumentException("Explanation provider metadata does not match its configuration");
            }
            RecommendationExplanationRecord generatedRecord = record(
                    request,
                    ExplanationStatus.GENERATED,
                    Optional.of(explanation.text()),
                    explanation.provider(),
                    explanation.modelId(),
                    explanation.promptVersion(),
                    requestedAt,
                    Optional.of(explanation.inputTokens()),
                    Optional.of(explanation.outputTokens()),
                    Optional.of(explanation.latencyMillis()),
                    Optional.empty()
            );
            if (!persistExplanation(generatedRecord)) {
                return Optional.empty();
            }
            return explanationByGeneration(
                    request.recommendationId(),
                    explanation.provider(),
                    explanation.modelId(),
                    explanation.promptVersion()
            ).flatMap(RecommendationExplanationRecord::text);
        } catch (IllegalArgumentException exception) {
            persistExplanation(record(
                    request,
                    ExplanationStatus.REJECTED,
                    Optional.empty(),
                    explanationProvider.providerName(),
                    explanationProvider.modelId(),
                    explanationProvider.promptVersion(),
                    requestedAt,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("EXPLANATION_OUTPUT_REJECTED")
            ));
            return Optional.empty();
        } catch (RuntimeException exception) {
            persistExplanation(record(
                    request,
                    ExplanationStatus.FAILED,
                    Optional.empty(),
                    explanationProvider.providerName(),
                    explanationProvider.modelId(),
                    explanationProvider.promptVersion(),
                    requestedAt,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("EXPLANATION_PROVIDER_FAILED")
            ));
            return Optional.empty();
        }
    }

    private Optional<RecommendationExplanationRecord> explanationByGeneration(
            imperator.domain.shared.RecommendationId recommendationId,
            String provider,
            String modelId,
            String promptVersion
    ) {
        try {
            Optional<RecommendationExplanationRecord> result =
                    explanationRepository.findByGeneration(
                            recommendationId, provider, modelId, promptVersion
                    );
            return result == null ? Optional.empty() : result;
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private RecommendationExplanationRecord record(
            RecommendationExplanationRequest request,
            ExplanationStatus status,
            Optional<String> text,
            String provider,
            String modelId,
            String promptVersion,
            Timestamp requestedAt,
            Optional<Integer> inputTokens,
            Optional<Integer> outputTokens,
            Optional<Long> latencyMillis,
            Optional<String> failureCode
    ) {
        java.time.Instant completedInstant = clock.instant();
        if (completedInstant.isBefore(requestedAt.value())) {
            completedInstant = requestedAt.value();
        }
        return new RecommendationExplanationRecord(
                explanationIds.get(),
                request.recommendationId(),
                status,
                text,
                provider,
                modelId,
                promptVersion,
                requestedAt,
                new Timestamp(completedInstant),
                inputTokens,
                outputTokens,
                latencyMillis,
                failureCode,
                request.evidenceIds(),
                request.assumptionIds()
        );
    }

    private boolean persistExplanation(RecommendationExplanationRecord explanation) {
        try {
            explanationRepository.save(explanation);
            return true;
        } catch (RuntimeException ignored) {
            // Explanation audit failure cannot alter committed business state.
            return false;
        }
    }

    private RecommendationExplanationRequest explanationRequest(
            Recommendation recommendation,
            Decision decision,
            Set<Evidence> evidence
    ) {
        List<EvidenceId> evidenceIds = evidence.stream()
                .map(Evidence::id)
                .sorted(Comparator.comparing(id -> id.value().toString()))
                .toList();
        List<String> assumptionIds = evidence.stream()
                .map(item -> item.metadata().get("assumption_id"))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
        String policyVersion = evidence.stream()
                .map(item -> item.metadata().get("policy_version"))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(DrcAoa001RecommendationPolicy.POLICY_VERSION);

        return new RecommendationExplanationRequest(
                recommendation.id(),
                decision.id(),
                decision.caseId(),
                decision.businessNeed(),
                recommendation.type(),
                recommendation.suggestedAction(),
                recommendation.reason(),
                recommendation.estimatedSavings(),
                recommendation.confidence(),
                recommendation.risk(),
                evidenceIds,
                assumptionIds,
                policyVersion,
                evidence.stream()
                        .map(this::explanationEvidence)
                        .sorted(Comparator.comparing(RecommendationExplanationEvidence::reference))
                        .toList()
        );
    }

    private RecommendationExplanationEvidence explanationEvidence(Evidence evidence) {
        String reference = Optional.ofNullable(evidence.metadata().get("evidence_ref"))
                .orElseGet(() -> Optional.ofNullable(evidence.metadata().get("assumption_id"))
                        .orElseGet(() -> Optional.ofNullable(evidence.metadata().get("policy_version"))
                                .orElse(evidence.id().value().toString())));
        boolean protectedContent = Set.of("CONFIDENTIAL", "RESTRICTED").contains(evidence.sensitivity());
        return new RecommendationExplanationEvidence(
                evidence.id(),
                reference,
                protectedContent ? "Protected Evidence content withheld from the model" : evidence.observedFact(),
                protectedContent
                        ? "Available only to an authorized human reviewer"
                        : evidence.businessMeaning(),
                evidence.sensitivity(),
                evidence.confidence()
        );
    }

    private Decision findDecision(GenerateRecommendationCommand command) {
        return decisionRepository.findById(command.decisionId())
                .orElseThrow(() -> new DecisionNotFoundException(command.decisionId()));
    }

    private Recommendation evaluatePolicy(
            GenerateRecommendationCommand command,
            Decision decision,
            Set<Evidence> evidence
    ) {
        try {
            return policy.evaluate(command.recommendationId(), decision, evidence, command.generatedAt());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException exception) {
            throw new RecommendationNotReadyException(
                    decision.id(),
                    failureMessage(exception, "Deterministic Recommendation policy requirements were not met"),
                    exception
            );
        }
    }

    private Set<Evidence> loadTraceableEvidence(GenerateRecommendationCommand command, Decision decision) {
        if (!command.evidenceIds().contains(decision.originatingEvidenceId())) {
            throw new EvidenceTraceabilityViolationException(
                    decision.originatingEvidenceId(),
                    decision.id(),
                    "Recommendation must reference the Decision originating Evidence"
            );
        }

        Set<Evidence> evidence = new LinkedHashSet<>();
        for (EvidenceId evidenceId : command.evidenceIds()) {
            if (evidenceId == null) {
                throw new ValidationException(
                        "RECOMMENDATION_EVIDENCE_ID_REQUIRED",
                        "Recommendation Evidence id is required"
                );
            }
            Evidence item = evidenceRepository.findById(evidenceId)
                    .orElseThrow(() -> new EvidenceNotFoundException(evidenceId));
            if (!decision.caseId().equals(item.correlationKey())) {
                throw new EvidenceTraceabilityViolationException(
                        evidenceId,
                        decision.id(),
                        "Recommendation Evidence must belong to the Decision case"
                );
            }
            evidence.add(item);
        }
        return Set.copyOf(evidence);
    }

    private void attachOnlyOnFirstCreation(
            Decision decision,
            Recommendation recommendation,
            GenerateRecommendationCommand command
    ) {
        if (decision.hasRecommendation()) {
            if (decision.recommendationId().filter(recommendation.id()::equals).isEmpty()) {
                throw new RecommendationCreationConflictException(decision.id());
            }
            return;
        }
        if (!DecisionStatus.CREATED.equals(decision.status())) {
            throw new RecommendationCreationConflictException(decision.id());
        }

        try {
            decision.attachRecommendation(recommendation.id(), command.generatedAt());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException exception) {
            throw new RecommendationCreationConflictException(decision.id());
        }
        decisionRepository.save(decision);
    }

    private boolean hasSameImmutableCreationState(Recommendation candidate, Recommendation persisted) {
        return candidate.id().equals(persisted.id())
                && candidate.decisionId().equals(persisted.decisionId())
                && candidate.type().equals(persisted.type())
                && candidate.suggestedAction().equals(persisted.suggestedAction())
                && candidate.reason().equals(persisted.reason())
                && candidate.evidenceIds().equals(persisted.evidenceIds())
                && candidate.estimatedSavings().equals(persisted.estimatedSavings())
                && candidate.confidence().equals(persisted.confidence())
                && candidate.risk().equals(persisted.risk())
                && candidate.ownerId().equals(persisted.ownerId())
                && candidate.requiredApproverId().equals(persisted.requiredApproverId())
                && candidate.createdAt().equals(persisted.createdAt());
    }

    private String failureMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private void validateCommand(GenerateRecommendationCommand command) {
        if (command == null) {
            throw new ValidationException(
                    "GENERATE_RECOMMENDATION_COMMAND_REQUIRED",
                    "Generate Recommendation command is required"
            );
        }
        requireValue(command.recommendationId(), "RECOMMENDATION_ID_REQUIRED", "Recommendation id is required");
        requireValue(command.decisionId(), "DECISION_ID_REQUIRED", "Decision id is required");
        if (command.evidenceIds() == null || command.evidenceIds().isEmpty()) {
            throw new ValidationException(
                    "RECOMMENDATION_EVIDENCE_REQUIRED",
                    "Recommendation must reference Evidence"
            );
        }
        requireValue(
                command.generatedAt(),
                "RECOMMENDATION_GENERATED_AT_REQUIRED",
                "Recommendation generation timestamp is required"
        );
    }

    private <T> T requireValue(T value, String code, String message) {
        if (value == null) {
            throw new ValidationException(code, message);
        }
        return value;
    }

    private static RecommendationExplanationRepository unavailableExplanationRepository() {
        return new RecommendationExplanationRepository() {
            @Override
            public void save(RecommendationExplanationRecord explanation) {
                // Persistence is optional for non-runtime unit compositions.
            }

            @Override
            public Optional<RecommendationExplanationRecord> findLatestByRecommendationId(
                    imperator.domain.shared.RecommendationId recommendationId
            ) {
                return Optional.empty();
            }
        };
    }

    private record GenerationOutcome(
            Recommendation recommendation,
            Decision decision,
            RecommendationExplanationRequest explanationRequest
    ) {
        private GenerateRecommendationResult resultWith(Optional<String> explanation) {
            return new GenerateRecommendationResult(
                    recommendation.id(),
                    recommendation.decisionId(),
                    recommendation.type(),
                    recommendation.evidenceIds().size(),
                    decision.recommendationId().filter(recommendation.id()::equals).isPresent(),
                    recommendation.estimatedSavings(),
                    recommendation.confidence(),
                    recommendation.risk(),
                    explanation
            );
        }
    }
}
