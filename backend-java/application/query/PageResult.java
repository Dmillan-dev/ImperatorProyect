package imperator.application.query;

import java.util.List;
import java.util.Objects;

public record PageResult<T>(List<T> items, int page, int size, long totalItems, int totalPages) {
    public PageResult {
        items = List.copyOf(Objects.requireNonNull(items, "Page items are required"));
        if (page < 0 || size < 1 || totalItems < 0 || totalPages < 0) {
            throw new IllegalArgumentException("Page metadata is invalid");
        }
    }

    public static <T> PageResult<T> of(List<T> items, PageRequest request, long totalItems) {
        long pages = totalItems == 0 ? 0 : ((totalItems - 1) / request.size()) + 1;
        if (pages > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Page count exceeds the supported range");
        }
        return new PageResult<>(items, request.page(), request.size(), totalItems, (int) pages);
    }
}
