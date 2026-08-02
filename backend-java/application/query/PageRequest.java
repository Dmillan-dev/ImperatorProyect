package imperator.application.query;

import imperator.application.exceptions.ValidationException;

import java.util.Objects;

public record PageRequest(int page, int size, String sort, SortDirection direction) {
    public PageRequest {
        if (page < 0) {
            throw invalid("Page must not be negative");
        }
        if (size < 1 || size > 100) {
            throw invalid("Page size must be between 1 and 100");
        }
        if (sort == null || sort.isBlank()) {
            throw invalid("Sort field is required");
        }
        sort = sort.trim();
        direction = Objects.requireNonNull(direction, "Sort direction is required");
    }

    private static ValidationException invalid(String message) {
        return new ValidationException("INVALID_PAGINATION", message);
    }
}
