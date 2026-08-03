package imperator.adapters.out.github;

import java.time.Instant;

record GitHubDeploymentStatus(
        long id,
        String state,
        Instant createdAt,
        String creator
) {
}
