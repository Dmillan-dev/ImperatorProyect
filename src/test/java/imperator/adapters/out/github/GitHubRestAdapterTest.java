package imperator.adapters.out.github;

import imperator.adapters.out.github.GitHubContractStubServer.Mode;
import imperator.adapters.out.github.GitHubContractStubServer.RequestSnapshot;
import imperator.ports.out.EvidenceCandidate;
import imperator.ports.out.EvidenceSourceCapture;
import imperator.ports.out.EvidenceSourceOutcome;
import imperator.ports.out.EvidenceSourceRequest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubRestAdapterTest {
    private static final String TOKEN = "github_pat_test-secret-never-leak";
    private static final Instant FROM = Instant.parse("2026-03-01T00:00:00Z");
    private static final Instant UNTIL = Instant.parse("2026-07-01T00:00:00Z");

    @Test
    void capturesThreeCanonicalEvidenceItemsThroughTheExactReadOnlySurface() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.HAPPY)) {
            List<Duration> delays = new ArrayList<>();
            EvidenceSourceCapture capture = adapter(server, enabledSettings(), delays).capture(request());
            Map<String, EvidenceCandidate> evidence = capture.candidates().stream()
                    .collect(Collectors.toUnmodifiableMap(
                            EvidenceCandidate::evidenceReference,
                            Function.identity()
                    ));

            assertAll(
                    () -> assertEquals(EvidenceSourceOutcome.COMPLETE, capture.outcome()),
                    () -> assertEquals("acme/imperator-demo", capture.sourceReference()),
                    () -> assertEquals("2026-03-10", capture.apiVersion()),
                    () -> assertEquals(7, capture.requestCount()),
                    () -> assertEquals(0, capture.retryCount()),
                    () -> assertEquals(5, capture.pageCount()),
                    () -> assertEquals(3, capture.qualifyingSourceObjectCount()),
                    () -> assertEquals(Set.of("E-GH-001", "E-GH-002", "E-GH-003"), evidence.keySet()),
                    () -> assertTrue(delays.isEmpty()),
                    () -> assertEquals("code_change_merged", evidence.get("E-GH-001").eventType()),
                    () -> assertEquals("code_review_observed", evidence.get("E-GH-002").eventType()),
                    () -> assertEquals(
                            "deployment_reference_observed",
                            evidence.get("E-GH-003").eventType()
                    ),
                    () -> assertEquals("CONFIDENTIAL", evidence.get("E-GH-001").sensitivity()),
                    () -> assertEquals("INTERNAL", evidence.get("E-GH-003").sensitivity()),
                    () -> assertTrue(evidence.values().stream()
                            .allMatch(candidate -> candidate.evidenceId().value().version() == 5)),
                    () -> assertFalse(capture.toString().contains(TOKEN)),
                    () -> assertFalse(capture.toString().contains("raw-title-must-not-persist")),
                    () -> assertFalse(capture.toString().contains("raw-body-must-not-persist"))
            );
            assertExactRequests(server.requests());
        }
    }

    @Test
    void disabledConnectorMakesNoNetworkCall() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.HAPPY)) {
            GitHubConnectorSettings settings = new GitHubConnectorSettings(false, null, null, null);

            EvidenceSourceCapture capture = adapter(server, settings, new ArrayList<>()).capture(request());

            assertEquals(EvidenceSourceOutcome.DISABLED, capture.outcome());
            assertEquals(0, capture.requestCount());
            assertTrue(server.requests().isEmpty());
        }
    }

    @Test
    void invalidConfigurationMakesNoNetworkCall() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.HAPPY)) {
            GitHubConnectorSettings settings = new GitHubConnectorSettings(true, "", "acme", "demo");

            EvidenceSourceCapture capture = adapter(server, settings, new ArrayList<>()).capture(request());

            assertEquals(EvidenceSourceOutcome.MISCONFIGURED, capture.outcome());
            assertEquals("GITHUB_TOKEN_REQUIRED", capture.failureCode());
            assertEquals(0, capture.requestCount());
            assertTrue(server.requests().isEmpty());
        }
    }

    @Test
    void rejectsNearMatchAndAmbiguousCorrelationWithoutGuessing() throws IOException {
        try (
                GitHubContractStubServer noMatchServer = new GitHubContractStubServer(Mode.NO_MATCH);
                GitHubContractStubServer ambiguousServer = new GitHubContractStubServer(Mode.AMBIGUOUS)
        ) {
            EvidenceSourceCapture noMatch = adapter(
                    noMatchServer,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(request());
            EvidenceSourceCapture ambiguous = adapter(
                    ambiguousServer,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(request());

            assertEquals(EvidenceSourceOutcome.NO_MATCH, noMatch.outcome());
            assertEquals(EvidenceSourceOutcome.AMBIGUOUS_CORRELATION, ambiguous.outcome());
            assertTrue(noMatch.candidates().isEmpty());
            assertTrue(ambiguous.candidates().isEmpty());
        }
    }

    @Test
    void reportsPartialEvidenceWithoutFabricatingReviewOrDeployment() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.PARTIAL)) {
            EvidenceSourceCapture capture = adapter(
                    server,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(request());

            assertEquals(EvidenceSourceOutcome.PARTIAL, capture.outcome());
            assertEquals(List.of("E-GH-001"), capture.candidates().stream()
                    .map(EvidenceCandidate::evidenceReference)
                    .toList());
            assertEquals(List.of("E-GH-002", "E-GH-003"), capture.missingEvidenceReferences());
            assertFalse(capture.candidates().stream()
                    .anyMatch(candidate -> "E-GH-004".equals(candidate.evidenceReference())));
        }
    }

    @Test
    void followsLinkPaginationSequentiallyAndRetriesOneRateLimit() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.PAGINATED_RATE_LIMIT)) {
            List<Duration> delays = new ArrayList<>();

            EvidenceSourceCapture capture = adapter(server, enabledSettings(), delays).capture(request());

            assertEquals(EvidenceSourceOutcome.COMPLETE, capture.outcome());
            assertEquals(9, capture.requestCount());
            assertEquals(1, capture.retryCount());
            assertEquals(6, capture.pageCount());
            assertEquals(List.of(Duration.ofSeconds(1)), delays);
            assertTrue(server.requests().stream()
                    .anyMatch(request -> request.query().contains("page=2")));
        }
    }

    @Test
    void retriesTransientFailuresWithTheFrozenDeterministicSchedule() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.TRANSIENT_RETRY)) {
            List<Duration> delays = new ArrayList<>();

            EvidenceSourceCapture capture = adapter(server, enabledSettings(), delays).capture(request());

            assertEquals(EvidenceSourceOutcome.COMPLETE, capture.outcome());
            assertEquals(9, capture.requestCount());
            assertEquals(2, capture.retryCount());
            assertEquals(List.of(Duration.ofSeconds(1), Duration.ofSeconds(2)), delays);
        }
    }

    @Test
    void rejectsPageAndResponseLimitsWithoutSilentTruncation() throws IOException {
        try (
                GitHubContractStubServer pageLimit = new GitHubContractStubServer(Mode.PAGE_LIMIT);
                GitHubContractStubServer responseLimit = new GitHubContractStubServer(Mode.RESPONSE_TOO_LARGE)
        ) {
            EvidenceSourceCapture tooManyPages = adapter(
                    pageLimit,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(request());
            EvidenceSourceCapture oversizedResponse = adapter(
                    responseLimit,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(request());

            assertEquals(EvidenceSourceOutcome.INCOMPLETE, tooManyPages.outcome());
            assertEquals("PAGE_LIMIT_EXCEEDED", tooManyPages.failureCode());
            assertEquals(20, tooManyPages.pageCount());
            assertEquals(EvidenceSourceOutcome.INCOMPLETE, oversizedResponse.outcome());
            assertEquals("RESPONSE_TOO_LARGE", oversizedResponse.failureCode());
        }
    }

    @Test
    void enforcesTheCompleteRunTimeoutWithAMonotonicClock() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.HAPPY)) {
            AtomicInteger clockReads = new AtomicInteger();
            GitHubRestAdapter adapter = new GitHubRestAdapter(
                    enabledSettings(),
                    server.baseUri(),
                    HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(5))
                            .followRedirects(HttpClient.Redirect.NEVER)
                            .build(),
                    JsonMapper.builder().build(),
                    duration -> {
                    },
                    Clock.fixed(Instant.parse("2026-07-01T00:00:00Z"), ZoneOffset.UTC),
                    () -> clockReads.getAndIncrement() == 0
                            ? 0L
                            : GitHubHttpClient.RUN_TIMEOUT.toNanos()
            );

            EvidenceSourceCapture capture = adapter.capture(request());

            assertEquals(EvidenceSourceOutcome.DEGRADED, capture.outcome());
            assertEquals("GITHUB_RUN_TIMEOUT", capture.failureCode());
            assertEquals(0, capture.requestCount());
            assertTrue(server.requests().isEmpty());
        }
    }

    @Test
    void marksEvidenceOutsideTheExplicitWindowAsMissing() throws IOException {
        try (GitHubContractStubServer server = new GitHubContractStubServer(Mode.HAPPY)) {
            EvidenceSourceRequest lateWindow = new EvidenceSourceRequest(
                    Instant.parse("2026-04-03T00:00:00Z"),
                    UNTIL,
                    UUID.fromString("f6a27eef-829d-4305-9686-f578ad3a4d76")
            );

            EvidenceSourceCapture capture = adapter(
                    server,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(lateWindow);

            assertEquals(EvidenceSourceOutcome.PARTIAL, capture.outcome());
            assertEquals(List.of("E-GH-003"), capture.candidates().stream()
                    .map(EvidenceCandidate::evidenceReference)
                    .toList());
            assertEquals(List.of("E-GH-001", "E-GH-002"), capture.missingEvidenceReferences());
        }
    }

    @Test
    void mapsUnavailableRepositoryAndRejectsCrossOriginRedirect() throws IOException {
        try (
                GitHubContractStubServer missing = new GitHubContractStubServer(Mode.NOT_FOUND);
                GitHubContractStubServer redirect = new GitHubContractStubServer(Mode.CROSS_ORIGIN_REDIRECT)
        ) {
            EvidenceSourceCapture unavailable = adapter(
                    missing,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(request());
            EvidenceSourceCapture rejectedRedirect = adapter(
                    redirect,
                    enabledSettings(),
                    new ArrayList<>()
            ).capture(request());

            assertEquals(EvidenceSourceOutcome.REPOSITORY_UNAVAILABLE, unavailable.outcome());
            assertEquals("GITHUB_REPOSITORY_UNAVAILABLE", unavailable.failureCode());
            assertEquals(EvidenceSourceOutcome.INCOMPLETE, rejectedRedirect.outcome());
            assertEquals("CROSS_ORIGIN_REDIRECT_REJECTED", rejectedRedirect.failureCode());
            assertEquals(1, redirect.requests().size());
        }
    }

    private static GitHubRestAdapter adapter(
            GitHubContractStubServer server,
            GitHubConnectorSettings settings,
            List<Duration> delays
    ) {
        return new GitHubRestAdapter(
                settings,
                server.baseUri(),
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .build(),
                JsonMapper.builder().build(),
                delays::add,
                Clock.fixed(Instant.parse("2026-07-01T00:00:00Z"), ZoneOffset.UTC),
                System::nanoTime
        );
    }

    private static GitHubConnectorSettings enabledSettings() {
        return new GitHubConnectorSettings(true, TOKEN, "acme", "imperator-demo");
    }

    private static EvidenceSourceRequest request() {
        return new EvidenceSourceRequest(
                FROM,
                UNTIL,
                UUID.fromString("f6a27eef-829d-4305-9686-f578ad3a4d76")
        );
    }

    private static void assertExactRequests(List<RequestSnapshot> requests) {
        Set<String> expectedPaths = Set.of(
                "/repos/acme/imperator-demo",
                "/repos/acme/imperator-demo/pulls",
                "/repos/acme/imperator-demo/pulls/184",
                "/repos/acme/imperator-demo/pulls/184/commits",
                "/repos/acme/imperator-demo/pulls/184/reviews",
                "/repos/acme/imperator-demo/deployments",
                "/repos/acme/imperator-demo/deployments/601/statuses"
        );
        assertEquals(expectedPaths, requests.stream().map(RequestSnapshot::path).collect(Collectors.toSet()));
        assertTrue(requests.stream().allMatch(request -> "GET".equals(request.method())));
        assertTrue(requests.stream().allMatch(request ->
                "application/vnd.github+json".equals(request.accept())
                        && "2026-03-10".equals(request.apiVersion())
                        && "IMPERATOR/0.1.0".equals(request.userAgent())
                        && ("Bearer " + TOKEN).equals(request.authorization())
        ));
        RequestSnapshot pulls = requests.stream()
                .filter(request -> request.path().endsWith("/pulls"))
                .findFirst()
                .orElseThrow();
        assertTrue(pulls.query().contains("state=closed"));
        assertTrue(pulls.query().contains("base=main"));
        assertTrue(pulls.query().contains("sort=updated"));
        assertTrue(pulls.query().contains("direction=desc"));
        assertTrue(pulls.query().contains("per_page=100"));
    }

}
