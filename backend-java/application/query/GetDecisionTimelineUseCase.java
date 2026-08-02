package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.ports.in.GetDecisionTimelineInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Objects;
import java.util.Set;

public final class GetDecisionTimelineUseCase implements GetDecisionTimelineInputPort {
    private final MvpReadModelQueryPort readModel;

    public GetDecisionTimelineUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public PageResult<DecisionTimelineItem> getDecisionTimeline(GetDecisionTimelineQuery query) {
        if (query == null || query.decisionId() == null || query.pageRequest() == null) {
            throw new ValidationException("INVALID_PAGINATION", "Decision timeline query is required");
        }
        QuerySupport.requireSort(query.pageRequest(), Set.of("occurredAt"), true);
        return QuerySupport.page(
                QuerySupport.timeline(QuerySupport.snapshot(readModel, query.decisionId())),
                query.pageRequest()
        );
    }
}
