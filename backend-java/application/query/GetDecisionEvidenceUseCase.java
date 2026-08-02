package imperator.application.query;

import imperator.application.exceptions.ValidationException;
import imperator.domain.evidence.Evidence;
import imperator.ports.in.GetDecisionEvidenceInputPort;
import imperator.ports.out.MvpReadModelQueryPort;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class GetDecisionEvidenceUseCase implements GetDecisionEvidenceInputPort {
    private final MvpReadModelQueryPort readModel;

    public GetDecisionEvidenceUseCase(MvpReadModelQueryPort readModel) {
        this.readModel = Objects.requireNonNull(readModel, "Read model is required");
    }

    @Override
    public PageResult<EvidenceSummary> getDecisionEvidence(GetDecisionEvidenceQuery query) {
        if (query == null || query.decisionId() == null || query.pageRequest() == null) {
            throw new ValidationException("INVALID_PAGINATION", "Decision Evidence query is required");
        }
        QuerySupport.requireSort(query.pageRequest(), Set.of("timestamp", "source", "severity"), false);
        Comparator<Evidence> comparator = comparator(query.pageRequest().sort());
        if (query.pageRequest().direction() == SortDirection.DESC) {
            comparator = comparator.reversed();
        }
        List<EvidenceSummary> items = QuerySupport.snapshot(readModel, query.decisionId()).evidence().stream()
                .sorted(comparator.thenComparing(item -> item.id().value().toString()))
                .map(QuerySupport::evidence)
                .toList();
        return QuerySupport.page(items, query.pageRequest());
    }

    private Comparator<Evidence> comparator(String sort) {
        return switch (sort) {
            case "timestamp" -> Comparator.comparing(item -> item.timestamp().value());
            case "source" -> Comparator.comparing(Evidence::source);
            case "severity" -> Comparator.comparing(item -> item.severity().value());
            default -> throw new ValidationException("INVALID_PAGINATION", "Unsupported Evidence sort");
        };
    }
}
