package imperator.adapters.out.github;

import java.time.Duration;

@FunctionalInterface
interface GitHubDelay {
    void sleep(Duration duration) throws InterruptedException;
}
