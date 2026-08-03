package imperator.adapters.out.github;

import java.time.Duration;
import java.time.Instant;

record GitHubRateLimit(Duration delay, Instant retryAt) {
}
