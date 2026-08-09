package imperator.adapters.out.aws;

final class AwsRunMetrics {
    private int requestCount;
    private int transmissionCount;
    private int pageCount;

    void incrementRequests() {
        requestCount++;
    }

    void incrementTransmissions() {
        transmissionCount++;
    }

    void incrementPages() {
        pageCount++;
    }

    int requestCount() {
        return requestCount;
    }

    int retryCount() {
        return Math.max(0, transmissionCount - requestCount);
    }

    int pageCount() {
        return pageCount;
    }
}
