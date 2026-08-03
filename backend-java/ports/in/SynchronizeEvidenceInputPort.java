package imperator.ports.in;

import imperator.application.synchronizeevidence.SynchronizeEvidenceCommand;
import imperator.application.synchronizeevidence.SynchronizeEvidenceResult;

public interface SynchronizeEvidenceInputPort {
    SynchronizeEvidenceResult synchronize(SynchronizeEvidenceCommand command);
}
