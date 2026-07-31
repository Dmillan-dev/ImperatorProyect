package imperator.api.evidence;

import java.util.List;
import java.util.Objects;

public record EvidenceImportResponse(
        String status,
        int accepted,
        int rejected,
        List<EvidenceImportItemResponse> items
) {
    public EvidenceImportResponse {
        status = Objects.requireNonNull(status, "Import status is required");
        items = List.copyOf(Objects.requireNonNull(items, "Import items are required"));
    }

    static EvidenceImportResponse from(List<EvidenceImportItemResponse> items) {
        int accepted = Math.toIntExact(items.stream()
                .filter(item -> "ACCEPTED".equals(item.status()))
                .count());
        int rejected = items.size() - accepted;
        String status = accepted == 0
                ? "REJECTED"
                : rejected == 0 ? "ACCEPTED" : "PARTIAL";
        return new EvidenceImportResponse(status, accepted, rejected, items);
    }
}
