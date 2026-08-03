package imperator.adapters.out.github;

import java.time.Instant;

record GitHubReview(
        long id,
        String state,
        Instant submittedAt,
        String commitId,
        String reviewer
) {
}
