package imperator.ports.in;

import imperator.application.query.EvidenceSummary;
import imperator.application.query.GetDecisionEvidenceQuery;
import imperator.application.query.PageResult;

public interface GetDecisionEvidenceInputPort {
    PageResult<EvidenceSummary> getDecisionEvidence(GetDecisionEvidenceQuery query);
}
