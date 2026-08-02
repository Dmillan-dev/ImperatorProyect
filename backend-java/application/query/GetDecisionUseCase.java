package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.ports.in.GetDecisionInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Objects;

public final class GetDecisionUseCase implements GetDecisionInputPort {
    private final MvpReadModelQueryPort readModel;

    public GetDecisionUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public DecisionDetail getDecision(GetDecisionQuery query) {
        if (query == null || query.decisionId() == null) {
            throw new ValidationException("DECISION_ID_REQUIRED", "Decision id is required");
        }
        return QuerySupport.detail(QuerySupport.snapshot(readModel, query.decisionId()).decision());
    }
}
