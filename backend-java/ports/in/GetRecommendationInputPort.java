package imperator.ports.in;

import imperator.application.query.GetRecommendationQuery;
import imperator.application.query.RecommendationView;

public interface GetRecommendationInputPort {
    RecommendationView getRecommendation(GetRecommendationQuery query);
}
