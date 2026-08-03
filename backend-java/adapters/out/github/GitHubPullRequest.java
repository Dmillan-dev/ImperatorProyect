package imperator.adapters.out.github;

import java.time.Instant;

record GitHubPullRequest(
        long number,
        String nodeId,
        String state,
        Instant mergedAt,
        String mergeCommitSha,
        String title,
        String body,
        String headReference,
        String baseReference,
        String repositoryFullName,
        String author,
        String mergedBy
) {
}
