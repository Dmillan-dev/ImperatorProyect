package imperator.adapters.out.github;

import imperator.ports.out.EvidenceSourceOutcome;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.LongSupplier;

final class GitHubHttpClient {
    static final String API_VERSION = "2026-03-10";
    static final int MAX_RESPONSE_BYTES = 2 * 1024 * 1024;
    static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    static final Duration RUN_TIMEOUT = Duration.ofSeconds(120);

    private final HttpClient httpClient;
    private final URI apiBaseUri;
    private final String token;
    private final GitHubDelay delay;
    private final Clock clock;
    private final LongSupplier nanoTime;

    GitHubHttpClient(
            HttpClient httpClient,
            URI apiBaseUri,
            String token,
            GitHubDelay delay,
            Clock clock,
            LongSupplier nanoTime
    ) {
        this.httpClient = Objects.requireNonNull(httpClient, "HTTP client is required");
        this.apiBaseUri = Objects.requireNonNull(apiBaseUri, "GitHub API base URI is required");
        this.token = Objects.requireNonNull(token, "GitHub token is required");
        this.delay = Objects.requireNonNull(delay, "GitHub retry delay is required");
        this.clock = Objects.requireNonNull(clock, "GitHub clock is required");
        this.nanoTime = Objects.requireNonNull(nanoTime, "GitHub monotonic clock is required");
    }

    long newDeadline() {
        return nanoTime.getAsLong() + RUN_TIMEOUT.toNanos();
    }

    GitHubHttpResponse get(URI requestedUri, GitHubRunMetrics metrics, long deadlineNanos) {
        URI current = validateSameOrigin(requestedUri);
        int redirects = 0;
        while (true) {
            GitHubHttpResponse response = requestWithPolicies(current, metrics, deadlineNanos);
            if (!isRedirect(response.statusCode())) {
                return requireSuccessful(response);
            }
            if (redirects >= 3) {
                throw failure(EvidenceSourceOutcome.INCOMPLETE, "REDIRECT_LIMIT_EXCEEDED");
            }
            String location = response.header("location")
                    .orElseThrow(() -> failure(EvidenceSourceOutcome.INCOMPLETE, "REDIRECT_LOCATION_MISSING"));
            URI redirect = validateSameOrigin(current.resolve(location));
            if (!current.getPath().equals(redirect.getPath())) {
                throw failure(EvidenceSourceOutcome.INCOMPLETE, "REDIRECT_PATH_CHANGED");
            }
            current = redirect;
            redirects++;
        }
    }

    private GitHubHttpResponse requestWithPolicies(
            URI uri,
            GitHubRunMetrics metrics,
            long deadlineNanos
    ) {
        int transientRetries = 0;
        boolean rateRetried = false;
        while (true) {
            requireBudget(deadlineNanos);
            GitHubHttpResponse response;
            try {
                response = sendOnce(uri, metrics, deadlineNanos);
            } catch (IOException exception) {
                if (transientRetries >= 2) {
                    throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_IO_RETRY_EXHAUSTED");
                }
                transientRetries++;
                retryAfter(Duration.ofSeconds(transientRetries), metrics, deadlineNanos);
                continue;
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_REQUEST_INTERRUPTED");
            }

            if (isRateLimited(response)) {
                GitHubRateLimit limit = rateLimit(response);
                if (rateRetried || !canWait(limit.delay(), deadlineNanos)) {
                    throw new GitHubRequestFailure(
                            EvidenceSourceOutcome.RATE_LIMITED,
                            "GITHUB_RATE_LIMITED",
                            Optional.of(limit.retryAt())
                    );
                }
                rateRetried = true;
                retryAfter(limit.delay(), metrics, deadlineNanos);
                continue;
            }

            if (isTransient(response.statusCode())) {
                if (transientRetries >= 2) {
                    throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_RETRY_EXHAUSTED");
                }
                transientRetries++;
                retryAfter(Duration.ofSeconds(transientRetries), metrics, deadlineNanos);
                continue;
            }
            return response;
        }
    }

    private GitHubHttpResponse sendOnce(URI uri, GitHubRunMetrics metrics, long deadlineNanos)
            throws IOException, InterruptedException {
        Duration timeout = remainingRequestTimeout(deadlineNanos);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", API_VERSION)
                .header("User-Agent", "IMPERATOR/0.1.0")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        metrics.incrementRequests();
        HttpResponse<InputStream> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofInputStream()
        );
        int declaredLength = response.headers().firstValueAsLong("content-length")
                .stream()
                .mapToInt(value -> value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value)
                .findFirst()
                .orElse(-1);
        if (declaredLength > MAX_RESPONSE_BYTES) {
            response.body().close();
            throw failure(EvidenceSourceOutcome.INCOMPLETE, "RESPONSE_TOO_LARGE");
        }
        byte[] body;
        try (InputStream input = response.body()) {
            body = input.readNBytes(MAX_RESPONSE_BYTES + 1);
        }
        if (body.length > MAX_RESPONSE_BYTES) {
            throw failure(EvidenceSourceOutcome.INCOMPLETE, "RESPONSE_TOO_LARGE");
        }
        return new GitHubHttpResponse(response.statusCode(), response.headers(), body);
    }

    private GitHubHttpResponse requireSuccessful(GitHubHttpResponse response) {
        return switch (response.statusCode()) {
            case 200 -> response;
            case 400, 406, 422 -> throw failure(
                    EvidenceSourceOutcome.INCOMPLETE,
                    "GITHUB_REQUEST_REJECTED"
            );
            case 401 -> throw failure(EvidenceSourceOutcome.UNAUTHORIZED, "GITHUB_UNAUTHORIZED");
            case 403 -> throw failure(EvidenceSourceOutcome.FORBIDDEN, "GITHUB_FORBIDDEN");
            case 404 -> throw failure(
                    EvidenceSourceOutcome.REPOSITORY_UNAVAILABLE,
                    "GITHUB_REPOSITORY_UNAVAILABLE"
            );
            case 410 -> throw failure(
                    EvidenceSourceOutcome.API_VERSION_UNSUPPORTED,
                    "GITHUB_API_VERSION_UNSUPPORTED"
            );
            default -> throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_UNEXPECTED_STATUS");
        };
    }

    private boolean isRateLimited(GitHubHttpResponse response) {
        if (response.statusCode() != 403 && response.statusCode() != 429) {
            return false;
        }
        if (response.statusCode() == 429) {
            return true;
        }
        if ("0".equals(response.header("x-ratelimit-remaining").orElse(""))) {
            return true;
        }
        if (response.header("retry-after").isPresent()) {
            return true;
        }
        String body = response.bodyAsString().toLowerCase(Locale.ROOT);
        return body.contains("secondary rate limit") || body.contains("rate limit exceeded");
    }

    private GitHubRateLimit rateLimit(GitHubHttpResponse response) {
        Optional<Long> retryAfter = positiveLong(response.header("retry-after"));
        if (retryAfter.isPresent()) {
            Duration wait = Duration.ofSeconds(retryAfter.orElseThrow());
            return new GitHubRateLimit(wait, clock.instant().plus(wait));
        }
        Optional<Long> reset = positiveLong(response.header("x-ratelimit-reset"));
        if ("0".equals(response.header("x-ratelimit-remaining").orElse("")) && reset.isPresent()) {
            Instant retryAt = Instant.ofEpochSecond(reset.orElseThrow());
            Duration wait = Duration.between(clock.instant(), retryAt);
            return new GitHubRateLimit(wait.isNegative() ? Duration.ZERO : wait, retryAt);
        }
        Duration wait = Duration.ofSeconds(60);
        return new GitHubRateLimit(wait, clock.instant().plus(wait));
    }

    private void retryAfter(Duration wait, GitHubRunMetrics metrics, long deadlineNanos) {
        if (!canWait(wait, deadlineNanos)) {
            throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_RUN_TIMEOUT");
        }
        metrics.incrementRetries();
        try {
            delay.sleep(wait);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_RETRY_INTERRUPTED");
        }
    }

    private boolean canWait(Duration wait, long deadlineNanos) {
        return !wait.isNegative()
                && wait.compareTo(Duration.ofSeconds(60)) <= 0
                && wait.toNanos() < remainingNanos(deadlineNanos);
    }

    private Duration remainingRequestTimeout(long deadlineNanos) {
        long remaining = remainingNanos(deadlineNanos);
        if (remaining <= 0) {
            throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_RUN_TIMEOUT");
        }
        return Duration.ofNanos(Math.min(REQUEST_TIMEOUT.toNanos(), remaining));
    }

    private void requireBudget(long deadlineNanos) {
        if (remainingNanos(deadlineNanos) <= 0) {
            throw failure(EvidenceSourceOutcome.DEGRADED, "GITHUB_RUN_TIMEOUT");
        }
    }

    private long remainingNanos(long deadlineNanos) {
        return deadlineNanos - nanoTime.getAsLong();
    }

    private URI validateSameOrigin(URI uri) {
        if (!sameOrigin(apiBaseUri, uri)) {
            throw failure(EvidenceSourceOutcome.INCOMPLETE, "CROSS_ORIGIN_REDIRECT_REJECTED");
        }
        return uri;
    }

    private static boolean sameOrigin(URI expected, URI candidate) {
        return expected.getScheme() != null
                && candidate.getScheme() != null
                && expected.getScheme().equalsIgnoreCase(candidate.getScheme())
                && expected.getHost() != null
                && candidate.getHost() != null
                && expected.getHost().equalsIgnoreCase(candidate.getHost())
                && effectivePort(expected) == effectivePort(candidate);
    }

    private static int effectivePort(URI uri) {
        if (uri.getPort() >= 0) {
            return uri.getPort();
        }
        return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
    }

    private static boolean isRedirect(int status) {
        return status == 301 || status == 302 || status == 307 || status == 308;
    }

    private static boolean isTransient(int status) {
        return status == 408 || status == 500 || status == 502 || status == 503 || status == 504;
    }

    private static Optional<Long> positiveLong(Optional<String> value) {
        if (value.isEmpty()) {
            return Optional.empty();
        }
        try {
            long parsed = Long.parseLong(value.orElseThrow().trim());
            return parsed >= 0 ? Optional.of(parsed) : Optional.empty();
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private static GitHubRequestFailure failure(EvidenceSourceOutcome outcome, String code) {
        return new GitHubRequestFailure(outcome, code, Optional.empty());
    }
}
