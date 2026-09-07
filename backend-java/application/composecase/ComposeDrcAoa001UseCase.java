package imperator.application.composecase;

import imperator.application.createdecision.CreateDecisionCommand;
import imperator.application.createdecision.CreateDecisionResult;
import imperator.application.exceptions.ValidationException;
import imperator.application.generaterecommendation.GenerateRecommendationCommand;
import imperator.application.generaterecommendation.GenerateRecommendationResult;
import imperator.domain.decision.Decision;
import imperator.domain.shared.EvidenceId;
import imperator.ports.in.ComposeDrcAoa001InputPort;
import imperator.ports.in.CreateDecisionInputPort;
import imperator.ports.in.GenerateRecommendationInputPort;
import imperator.ports.out.DecisionRepository;

import java.util.Objects;
import java.util.Optional;

public final class ComposeDrcAoa001UseCase implements ComposeDrcAoa001InputPort {
    public static final String CASE_ID = "DRC-AOA-001";
    public static final String TITLE = "Optimize AI onboarding assistant cost";
    public static final String BUSINESS_NEED =
            "Reduce recurring AI expenditure without losing exception-handling quality";
    public static final int EVIDENCE_COUNT = 28;

    private final CreateDecisionInputPort decisionCreator;
    private final GenerateRecommendationInputPort recommendationGenerator;
    private final DecisionRepository decisionRepository;

    public ComposeDrcAoa001UseCase(
            CreateDecisionInputPort decisionCreator,
            GenerateRecommendationInputPort recommendationGenerator,
            DecisionRepository decisionRepository
    ) {
        this.decisionCreator = Objects.requireNonNull(decisionCreator, "Decision creator is required");
        this.recommendationGenerator = Objects.requireNonNull(
                recommendationGenerator,
                "Recommendation generator is required"
        );
        this.decisionRepository = Objects.requireNonNull(
                decisionRepository,
                "Decision repository is required"
        );
    }

    @Override
    public ComposeDrcAoa001Result compose(ComposeDrcAoa001Command command) {
        validate(command);

        Optional<Decision> stateBeforeComposition = decisionRepository.findByCaseId(CASE_ID);
        boolean replayed = stateBeforeComposition
                .flatMap(Decision::recommendationId)
                .filter(command.recommendationId()::equals)
                .isPresent();
        boolean resumed = stateBeforeComposition.isPresent()
                && stateBeforeComposition.orElseThrow().recommendationId().isEmpty();

        CreateDecisionResult decision = decisionCreator.createDecision(new CreateDecisionCommand(
                command.decisionId(),
                command.originatingEvidenceId(),
                command.title(),
                command.businessNeed(),
                command.ownerId(),
                command.requiredApproverId(),
                command.decisionCreatedAt()
        ));
        GenerateRecommendationResult recommendation = recommendationGenerator.generateRecommendation(
                new GenerateRecommendationCommand(
                        command.recommendationId(),
                        decision.decisionId(),
                        command.evidenceIds(),
                        command.recommendationGeneratedAt()
                )
        );

        if (!recommendation.linkedToDecision() || recommendation.evidenceCount() != EVIDENCE_COUNT) {
            throw new IllegalStateException("Composition did not produce a workspace-ready Recommendation");
        }

        return new ComposeDrcAoa001Result(
                decision.caseId(),
                decision.decisionId(),
                decision.status(),
                recommendation.recommendationId(),
                recommendation.recommendationType(),
                recommendation.estimatedSavings(),
                recommendation.confidence(),
                recommendation.risk(),
                recommendation.evidenceCount(),
                replayed,
                resumed
        );
    }

    private void validate(ComposeDrcAoa001Command command) {
        if (command == null) {
            throw invalid("COMPOSITION_COMMAND_REQUIRED", "Composition command is required");
        }
        requireValue(command.decisionId(), "COMPOSITION_DECISION_ID_REQUIRED", "Decision id is required");
        requireValue(
                command.recommendationId(),
                "COMPOSITION_RECOMMENDATION_ID_REQUIRED",
                "Recommendation id is required"
        );
        requireValue(
                command.originatingEvidenceId(),
                "COMPOSITION_ORIGINATING_EVIDENCE_REQUIRED",
                "Originating Evidence id is required"
        );
        requireValue(command.ownerId(), "COMPOSITION_OWNER_REQUIRED", "Owner id is required");
        requireValue(command.requiredApproverId(), "COMPOSITION_APPROVER_REQUIRED", "Approver id is required");
        requireValue(command.initiatingActorId(), "COMPOSITION_ACTOR_REQUIRED", "Initiating actor id is required");
        requireValue(
                command.decisionCreatedAt(),
                "COMPOSITION_DECISION_TIMESTAMP_REQUIRED",
                "Decision creation timestamp is required"
        );
        requireValue(
                command.recommendationGeneratedAt(),
                "COMPOSITION_RECOMMENDATION_TIMESTAMP_REQUIRED",
                "Recommendation generation timestamp is required"
        );

        if (!CASE_ID.equals(command.caseId())) {
            throw invalid("COMPOSITION_CASE_UNSUPPORTED", "Only DRC-AOA-001 can be composed");
        }
        if (!TITLE.equals(command.title()) || !BUSINESS_NEED.equals(command.businessNeed())) {
            throw invalid("COMPOSITION_TEXT_INVALID", "Canonical Decision text is required");
        }
        if (command.decisionId().value().version() != 4
                || command.recommendationId().value().version() != 4) {
            throw invalid("COMPOSITION_ID_INVALID", "Decision and Recommendation ids must be UUID v4");
        }
        if (!command.requiredApproverId().equals(command.initiatingActorId())) {
            throw invalid(
                    "COMPOSITION_APPROVER_MISMATCH",
                    "Required approver must equal the authenticated actor"
            );
        }
        if (command.evidenceIds() == null
                || command.evidenceIds().size() != EVIDENCE_COUNT
                || command.evidenceIds().stream().anyMatch(Objects::isNull)) {
            throw invalid(
                    "COMPOSITION_EVIDENCE_SET_INVALID",
                    "Composition requires exactly 28 distinct Evidence ids"
            );
        }
        EvidenceId origin = command.originatingEvidenceId();
        if (!command.evidenceIds().contains(origin)) {
            throw invalid(
                    "COMPOSITION_ORIGIN_MISSING",
                    "Composition Evidence must include the originating Evidence"
            );
        }
        if (command.recommendationGeneratedAt().value().isBefore(command.decisionCreatedAt().value())) {
            throw invalid(
                    "COMPOSITION_CHRONOLOGY_INVALID",
                    "Recommendation timestamp cannot precede Decision creation"
            );
        }
    }

    private <T> T requireValue(T value, String code, String message) {
        if (value == null) {
            throw invalid(code, message);
        }
        return value;
    }

    private ValidationException invalid(String code, String message) {
        return new ValidationException(code, message);
    }
}
