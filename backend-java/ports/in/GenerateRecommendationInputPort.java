package imperator.ports.in;

import imperator.application.generaterecommendation.GenerateRecommendationCommand;
import imperator.application.generaterecommendation.GenerateRecommendationResult;

public interface GenerateRecommendationInputPort {
    GenerateRecommendationResult generateRecommendation(GenerateRecommendationCommand command);
}
