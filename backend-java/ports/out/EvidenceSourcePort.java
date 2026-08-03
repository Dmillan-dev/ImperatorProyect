package imperator.ports.out;

public interface EvidenceSourcePort {
    EvidenceSourceCapture capture(EvidenceSourceRequest request);
}
