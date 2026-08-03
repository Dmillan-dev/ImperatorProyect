package imperator.adapters.out.github;

final class GitHubRunMetrics {
    private int requestCount;
    private int retryCount;
    private int pageCount;

    void incrementRequests() {
        requestCount++;
    }

    void incrementRetries() {
        retryCount++;
    }

    void incrementPages() {
        pageCount++;
    }

    int requestCount() {
        return requestCount;
    }

    int retryCount() {
        return retryCount;
    }

    int pageCount() {
        return pageCount;
    }
}
