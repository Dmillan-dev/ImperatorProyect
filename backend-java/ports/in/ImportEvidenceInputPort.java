package imperator.ports.in;

import imperator.application.importevidence.ImportEvidenceCommand;
import imperator.application.importevidence.ImportEvidenceResult;

public interface ImportEvidenceInputPort {
    ImportEvidenceResult importEvidence(ImportEvidenceCommand command);
}
