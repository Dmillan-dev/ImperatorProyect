package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.ports.in.GetDecisionRoiInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Objects;

public final class GetDecisionRoiUseCase implements GetDecisionRoiInputPort {
    private final MvpReadModelQueryPort readModel;

    public GetDecisionRoiUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public DecisionRoiView getDecisionRoi(GetDecisionRoiQuery query) {
        if (query == null || query.decisionId() == null) {
            throw new ValidationException("DECISION_ID_REQUIRED", "Decision id is required");
        }
        return QuerySupport.roi(QuerySupport.snapshot(readModel, query.decisionId()));
    }
}
