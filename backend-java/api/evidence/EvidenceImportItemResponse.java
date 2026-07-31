package imperator.api.evidence;

import java.util.UUID;

public record EvidenceImportItemResponse(
        int line,
        String status,
        UUID evidenceId,
        String reason
) {

    static EvidenceImportItemResponse accepted(int line, UUID evidenceId) {
        return new EvidenceImportItemResponse(line, "ACCEPTED", evidenceId, null);
    }

    static EvidenceImportItemResponse rejected(int line, UUID evidenceId, String reason) {
        return new EvidenceImportItemResponse(line, "REJECTED", evidenceId, reason);
    }
}
