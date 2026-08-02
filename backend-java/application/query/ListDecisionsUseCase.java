package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.ports.in.ListDecisionsInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Objects;
import java.util.Set;

public final class ListDecisionsUseCase implements ListDecisionsInputPort {
    private final MvpReadModelQueryPort readModel;

    public ListDecisionsUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public PageResult<DecisionSummary> listDecisions(ListDecisionsQuery query) {
        if (query == null || query.pageRequest() == null) {
            throw new ValidationException("INVALID_PAGINATION", "Decision list query is required");
        }
        QuerySupport.requireSort(query.pageRequest(), Set.of("createdAt", "updatedAt"), false);
        return QuerySupport.mapPage(readModel.findDecisions(query.pageRequest()), QuerySupport::summary);
    }
}
