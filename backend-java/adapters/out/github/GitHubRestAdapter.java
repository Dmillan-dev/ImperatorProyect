package imperator.adapters.out.github;

import imperator.ports.out.EvidenceCandidate;
import imperator.ports.out.EvidenceSourceCapture;
import imperator.ports.out.EvidenceSourceOutcome;
import imperator.ports.out.EvidenceSourcePort;
import imperator.ports.out.EvidenceSourceRequest;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

public final class GitHubRestAdapter implements EvidenceSourcePort {
    private static final URI PRODUCTION_API = URI.create("https://api.github.com");
    private static final Pattern CORRELATION_TOKEN = Pattern.compile(
            "(?<![A-Za-z0-9])IMP-214(?![A-Za-z0-9])"
    );
    private static final int MAX_PAGES_PER_COLLECTION = 20;

    private final GitHubConnectorSettings settings;
    private final URI apiBaseUri;
    private final GitHubHttpClient http;
    private final JsonMapper json;
    private final GitHubEvidenceMapper mapper;

    public GitHubRestAdapter(GitHubConnectorSettings settings) {
        this(
                settings,
                PRODUCTION_API,
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .build(),
                JsonMapper.builder().build(),
                duration -> Thread.sleep(duration.toMillis()),
                Clock.systemUTC(),
                System::nanoTime
        );
    }

    GitHubRestAdapter(
            GitHubConnectorSettings settings,
            URI apiBaseUri,
            HttpClient httpClient,
            JsonMapper json,
            GitHubDelay delay,
            Clock clock,
            LongSupplier nanoTime
    ) {
        this.settings = Objects.requireNonNull(settings, "GitHub settings are required");
        this.apiBaseUri = Objects.requireNonNull(apiBaseUri, "GitHub API base URI is required");
        this.json = Objects.requireNonNull(json, "GitHub JSON mapper is required");
        this.mapper = new GitHubEvidenceMapper(settings);
        this.http = new GitHubHttpClient(
                httpClient,
                apiBaseUri,
                settings.token(),
                delay,
                clock,
                nanoTime
        );
    }

    @Override
    public EvidenceSourceCapture capture(EvidenceSourceRequest request) {
        Objects.requireNonNull(request, "Evidence source request is required");
        GitHubRunMetrics metrics = new GitHubRunMetrics();
        if (!settings.enabled()) {
            return failure(
                    EvidenceSourceOutcome.DISABLED,
                    "GITHUB_CONNECTOR_DISABLED",
                    metrics,
                    Optional.empty()
            );
        }
        Optional<String> configurationFailure = settings.validationFailure();
        if (configurationFailure.isPresent()) {
            return failure(
                    EvidenceSourceOutcome.MISCONFIGURED,
                    configurationFailure.orElseThrow(),
                    metrics,
                    Optional.empty()
            );
        }

        long deadline = http.newDeadline();
        try {
            GitHubRepository repository = repository(metrics, deadline);
            validateRepository(repository);
            List<Long> pullNumbers = qualifyingPullNumbers(repository, request, metrics, deadline);
            if (pullNumbers.isEmpty()) {
                return failure(
                        EvidenceSourceOutcome.NO_MATCH,
                        "GITHUB_CORRELATION_NOT_FOUND",
                        metrics,
                        Optional.empty()
                );
            }
            if (pullNumbers.size() > 1) {
                return failure(
                        EvidenceSourceOutcome.AMBIGUOUS_CORRELATION,
                        "GITHUB_CORRELATION_AMBIGUOUS",
                        metrics,
                        Optional.empty()
                );
            }

            GitHubPullRequest pullRequest = pullRequest(pullNumbers.getFirst(), metrics, deadline);
            validatePullRequest(repository, pullRequest, request);
            ReviewFact review = reviewFact(pullRequest, metrics, deadline).orElse(null);
            DeploymentFact deployment = deploymentFact(pullRequest, metrics, deadline).orElse(null);

            List<EvidenceCandidate> candidates = new ArrayList<>();
            if (inside(request, pullRequest.mergedAt())) {
                candidates.add(mapper.implementation(pullRequest));
            }
            if (review != null && inside(request, review.review().submittedAt())) {
                candidates.add(mapper.review(pullRequest, review.review(), review.approvalCount()));
            }
            if (deployment != null && inside(request, deployment.status().createdAt())) {
                candidates.add(mapper.deployment(
                        pullRequest,
                        deployment.deployment(),
                        deployment.status()
                ));
            }

            Set<String> capturedReferences = new HashSet<>();
            for (EvidenceCandidate candidate : candidates) {
                capturedReferences.add(candidate.evidenceReference());
            }
            List<String> missing = List.of("E-GH-001", "E-GH-002", "E-GH-003").stream()
                    .filter(reference -> !capturedReferences.contains(reference))
                    .toList();
            EvidenceSourceOutcome outcome = missing.isEmpty()
                    ? EvidenceSourceOutcome.COMPLETE
                    : EvidenceSourceOutcome.PARTIAL;
            int qualifyingObjects = 1 + (review == null ? 0 : 1) + (deployment == null ? 0 : 1);
            return new EvidenceSourceCapture(
                    outcome,
                    settings.sourceReference(),
                    GitHubHttpClient.API_VERSION,
                    metrics.requestCount(),
                    metrics.retryCount(),
                    metrics.pageCount(),
                    qualifyingObjects,
                    candidates,
                    missing,
                    missing.isEmpty() ? "" : "GITHUB_EVIDENCE_PARTIAL",
                    Optional.empty()
            );
        } catch (GitHubRequestFailure failure) {
            return failure(failure.outcome(), failure.code(), metrics, failure.retryAt());
        } catch (RuntimeException exception) {
            return failure(
                    EvidenceSourceOutcome.DEGRADED,
                    "GITHUB_CAPTURE_FAILED",
                    metrics,
                    Optional.empty()
            );
        }
    }

    private GitHubRepository repository(GitHubRunMetrics metrics, long deadline) {
        JsonNode root = object(get(repositoryPath(), metrics, deadline));
        JsonNode owner = requiredObject(root, "owner");
        return new GitHubRepository(
                requiredText(owner, "login"),
                requiredText(owner, "type"),
                requiredText(root, "name"),
                requiredText(root, "default_branch")
        );
    }

    private List<Long> qualifyingPullNumbers(
            GitHubRepository repository,
            EvidenceSourceRequest request,
            GitHubRunMetrics metrics,
            long deadline
    ) {
        String query = "state=closed&base=" + encode(repository.defaultBranch())
                + "&sort=updated&direction=desc&per_page=100";
        List<Long> matches = new ArrayList<>();
        for (JsonNode item : arrayPages(repositoryPath() + "/pulls?" + query, metrics, deadline)) {
            String state = optionalText(item, "state");
            Instant mergedAt = optionalInstant(item, "merged_at");
            JsonNode base = requiredObject(item, "base");
            String baseReference = requiredText(base, "ref");
            JsonNode head = requiredObject(item, "head");
            String headReference = requiredText(head, "ref");
            if (!"closed".equals(state)
                    || mergedAt == null
                    || !mergedAt.isBefore(request.untilExclusive())
                    || !repository.defaultBranch().equals(baseReference)) {
                continue;
            }
            if (containsCorrelation(optionalText(item, "title"))
                    || containsCorrelation(optionalText(item, "body"))
                    || containsCorrelation(headReference)) {
                matches.add(requiredLong(item, "number"));
                if (matches.size() > 1) {
                    return List.copyOf(matches);
                }
            }
        }
        return List.copyOf(matches);
    }

    private GitHubPullRequest pullRequest(long number, GitHubRunMetrics metrics, long deadline) {
        JsonNode root = object(get(repositoryPath() + "/pulls/" + number, metrics, deadline));
        JsonNode head = requiredObject(root, "head");
        JsonNode base = requiredObject(root, "base");
        JsonNode baseRepository = requiredObject(base, "repo");
        GitHubPullRequest pullRequest = new GitHubPullRequest(
                requiredLong(root, "number"),
                requiredText(root, "node_id"),
                requiredText(root, "state"),
                requiredInstant(root, "merged_at"),
                requiredText(root, "merge_commit_sha"),
                requiredText(root, "title"),
                optionalText(root, "body"),
                requiredText(head, "ref"),
                requiredText(base, "ref"),
                requiredText(baseRepository, "full_name"),
                nestedLogin(root, "user"),
                nestedLogin(root, "merged_by")
        );
        if (pullRequest.number() != number) {
            throw new GitHubRequestFailure(
                    EvidenceSourceOutcome.INCOMPLETE,
                    "GITHUB_PULL_IDENTITY_MISMATCH",
                    Optional.empty()
            );
        }
        return pullRequest;
    }

    private Optional<ReviewFact> reviewFact(
            GitHubPullRequest pullRequest,
            GitHubRunMetrics metrics,
            long deadline
    ) {
        Set<String> commitIds = new HashSet<>();
        for (JsonNode item : arrayPages(
                repositoryPath() + "/pulls/" + pullRequest.number() + "/commits?per_page=100",
                metrics,
                deadline
        )) {
            commitIds.add(requiredText(item, "sha"));
        }

        List<GitHubReview> approvals = new ArrayList<>();
        for (JsonNode item : arrayPages(
                repositoryPath() + "/pulls/" + pullRequest.number() + "/reviews?per_page=100",
                metrics,
                deadline
        )) {
            GitHubReview review = new GitHubReview(
                    requiredLong(item, "id"),
                    requiredText(item, "state"),
                    requiredInstant(item, "submitted_at"),
                    requiredText(item, "commit_id"),
                    nestedLogin(item, "user")
            );
            if ("APPROVED".equals(review.state().toUpperCase(Locale.ROOT))
                    && !review.submittedAt().isAfter(pullRequest.mergedAt())
                    && !"unknown".equals(pullRequest.author())
                    && !"unknown".equals(review.reviewer())
                    && !review.reviewer().equalsIgnoreCase(pullRequest.author())
                    && commitIds.contains(review.commitId())) {
                approvals.add(review);
            }
        }
        return approvals.stream()
                .max(Comparator.comparing(GitHubReview::submittedAt).thenComparingLong(GitHubReview::id))
                .map(review -> new ReviewFact(review, approvals.size()));
    }

    private Optional<DeploymentFact> deploymentFact(
            GitHubPullRequest pullRequest,
            GitHubRunMetrics metrics,
            long deadline
    ) {
        String query = "sha=" + encode(pullRequest.mergeCommitSha())
                + "&environment=production&per_page=100";
        List<DeploymentFact> successes = new ArrayList<>();
        for (JsonNode item : arrayPages(repositoryPath() + "/deployments?" + query, metrics, deadline)) {
            GitHubDeployment deployment = new GitHubDeployment(
                    requiredLong(item, "id"),
                    requiredText(item, "sha"),
                    requiredText(item, "environment")
            );
            if (!pullRequest.mergeCommitSha().equals(deployment.sha())
                    || !"production".equals(deployment.environment())) {
                continue;
            }
            for (JsonNode statusItem : arrayPages(
                    repositoryPath() + "/deployments/" + deployment.id()
                            + "/statuses?per_page=100",
                    metrics,
                    deadline
            )) {
                GitHubDeploymentStatus status = new GitHubDeploymentStatus(
                        requiredLong(statusItem, "id"),
                        requiredText(statusItem, "state"),
                        requiredInstant(statusItem, "created_at"),
                        nestedLogin(statusItem, "creator")
                );
                if ("success".equals(status.state())
                        && !status.createdAt().isBefore(pullRequest.mergedAt())) {
                    successes.add(new DeploymentFact(deployment, status));
                }
            }
        }
        return successes.stream().min(Comparator
                .comparing((DeploymentFact fact) -> fact.status().createdAt())
                .thenComparingLong(fact -> fact.status().id()));
    }

    private List<JsonNode> arrayPages(
            String relativeUri,
            GitHubRunMetrics metrics,
            long deadline
    ) {
        URI current = uri(relativeUri);
        String collectionPath = current.getPath();
        List<JsonNode> values = new ArrayList<>();
        int pages = 0;
        while (current != null) {
            GitHubHttpResponse response = http.get(current, metrics, deadline);
            JsonNode root = array(response);
            for (JsonNode value : root) {
                values.add(value);
            }
            metrics.incrementPages();
            pages++;
            Optional<URI> next = nextLink(response, current);
            if (next.isPresent() && pages >= MAX_PAGES_PER_COLLECTION) {
                throw new GitHubRequestFailure(
                        EvidenceSourceOutcome.INCOMPLETE,
                        "PAGE_LIMIT_EXCEEDED",
                        Optional.empty()
                );
            }
            if (next.isPresent() && !collectionPath.equals(next.orElseThrow().getPath())) {
                throw new GitHubRequestFailure(
                        EvidenceSourceOutcome.INCOMPLETE,
                        "PAGINATION_PATH_CHANGED",
                        Optional.empty()
                );
            }
            current = next.orElse(null);
        }
        return List.copyOf(values);
    }

    private GitHubHttpResponse get(String relativeUri, GitHubRunMetrics metrics, long deadline) {
        return http.get(uri(relativeUri), metrics, deadline);
    }

    private URI uri(String relativeUri) {
        String base = apiBaseUri.toString();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return URI.create(base + relativeUri);
    }

    private String repositoryPath() {
        return "/repos/" + settings.organization() + "/" + settings.repository();
    }

    private Optional<URI> nextLink(GitHubHttpResponse response, URI current) {
        Optional<String> header = response.header("link");
        if (header.isEmpty()) {
            return Optional.empty();
        }
        for (String part : header.orElseThrow().split(",")) {
            String trimmed = part.trim();
            if (!trimmed.contains("rel=\"next\"")) {
                continue;
            }
            int start = trimmed.indexOf('<');
            int end = trimmed.indexOf('>');
            if (start != 0 || end <= start + 1) {
                throw new GitHubRequestFailure(
                        EvidenceSourceOutcome.INCOMPLETE,
                        "PAGINATION_LINK_INVALID",
                        Optional.empty()
                );
            }
            return Optional.of(current.resolve(trimmed.substring(start + 1, end)));
        }
        return Optional.empty();
    }

    private void validateRepository(GitHubRepository repository) {
        if (!"Organization".equals(repository.ownerType())
                || !settings.organization().equalsIgnoreCase(repository.owner())
                || !settings.repository().equalsIgnoreCase(repository.name())) {
            throw new GitHubRequestFailure(
                    EvidenceSourceOutcome.MISCONFIGURED,
                    "GITHUB_REPOSITORY_SCOPE_MISMATCH",
                    Optional.empty()
            );
        }
    }

    private void validatePullRequest(
            GitHubRepository repository,
            GitHubPullRequest pullRequest,
            EvidenceSourceRequest request
    ) {
        String expectedRepository = settings.organization() + "/" + settings.repository();
        boolean correlated = containsCorrelation(pullRequest.title())
                || containsCorrelation(pullRequest.body())
                || containsCorrelation(pullRequest.headReference());
        if (!"closed".equals(pullRequest.state())
                || !repository.defaultBranch().equals(pullRequest.baseReference())
                || !expectedRepository.equalsIgnoreCase(pullRequest.repositoryFullName())
                || !pullRequest.mergedAt().isBefore(request.untilExclusive())
                || !correlated) {
            throw new GitHubRequestFailure(
                    EvidenceSourceOutcome.INCOMPLETE,
                    "GITHUB_PULL_SCOPE_MISMATCH",
                    Optional.empty()
            );
        }
    }

    private JsonNode object(GitHubHttpResponse response) {
        JsonNode root = parse(response);
        if (!root.isObject()) {
            throw malformed();
        }
        return root;
    }

    private JsonNode array(GitHubHttpResponse response) {
        JsonNode root = parse(response);
        if (!root.isArray()) {
            throw malformed();
        }
        return root;
    }

    private JsonNode parse(GitHubHttpResponse response) {
        try {
            JsonNode root = json.readTree(response.body());
            if (root == null) {
                throw malformed();
            }
            return root;
        } catch (JacksonException exception) {
            throw malformed();
        }
    }

    private GitHubRequestFailure malformed() {
        return new GitHubRequestFailure(
                EvidenceSourceOutcome.INCOMPLETE,
                "GITHUB_RESPONSE_INVALID",
                Optional.empty()
        );
    }

    private JsonNode requiredObject(JsonNode parent, String field) {
        JsonNode value = parent.get(field);
        if (value == null || !value.isObject()) {
            throw malformed();
        }
        return value;
    }

    private String requiredText(JsonNode parent, String field) {
        String value = optionalText(parent, field);
        if (value.isEmpty()) {
            throw malformed();
        }
        return value;
    }

    private String optionalText(JsonNode parent, String field) {
        JsonNode value = parent.get(field);
        if (value == null || value.isNull()) {
            return "";
        }
        if (!value.isString()) {
            throw malformed();
        }
        return value.asString().trim();
    }

    private long requiredLong(JsonNode parent, String field) {
        JsonNode value = parent.get(field);
        if (value == null || !value.isIntegralNumber()) {
            throw malformed();
        }
        return value.asLong();
    }

    private Instant requiredInstant(JsonNode parent, String field) {
        Instant value = optionalInstant(parent, field);
        if (value == null) {
            throw malformed();
        }
        return value;
    }

    private Instant optionalInstant(JsonNode parent, String field) {
        String value = optionalText(parent, field);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (RuntimeException exception) {
            throw malformed();
        }
    }

    private String nestedLogin(JsonNode parent, String field) {
        JsonNode value = parent.get(field);
        if (value == null || value.isNull()) {
            return "unknown";
        }
        if (!value.isObject()) {
            throw malformed();
        }
        String login = optionalText(value, "login");
        return login.isEmpty() ? "unknown" : login;
    }

    private boolean containsCorrelation(String value) {
        return value != null && CORRELATION_TOKEN.matcher(value).find();
    }

    private static boolean inside(EvidenceSourceRequest request, Instant occurrence) {
        return !occurrence.isBefore(request.fromInclusive())
                && occurrence.isBefore(request.untilExclusive());
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private EvidenceSourceCapture failure(
            EvidenceSourceOutcome outcome,
            String failureCode,
            GitHubRunMetrics metrics,
            Optional<Instant> retryAt
    ) {
        return new EvidenceSourceCapture(
                outcome,
                settings.sourceReference(),
                GitHubHttpClient.API_VERSION,
                metrics.requestCount(),
                metrics.retryCount(),
                metrics.pageCount(),
                0,
                List.of(),
                List.of(),
                failureCode,
                retryAt
        );
    }

    private record ReviewFact(GitHubReview review, int approvalCount) {
    }

    private record DeploymentFact(
            GitHubDeployment deployment,
            GitHubDeploymentStatus status
    ) {
    }
}
