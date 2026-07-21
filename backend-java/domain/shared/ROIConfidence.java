package imperator.domain.shared;

public record ROIConfidence(int percentage) {
    public ROIConfidence {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("ROI confidence percentage must be between 0 and 100");
        }
    }
}

