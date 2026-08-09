package imperator.adapters.out.aws;

import imperator.ports.out.EvidenceCandidate;
import imperator.ports.out.EvidenceSourceCapture;
import imperator.ports.out.EvidenceSourceOutcome;
import imperator.ports.out.EvidenceSourceRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AwsSdkEvidenceSourceAdapterTest {
    private static final Instant RUN_TIME = Instant.parse("2026-08-01T00:00:00Z");
    private static final EvidenceSourceRequest JUNE_WINDOW = new EvidenceSourceRequest(
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-07-01T00:00:00Z"),
            UUID.fromString("aa479ef5-83f4-42da-8945-8490aff081b0")
    );

    @Test
    void capturesTheFourFrozenEvidenceItemsThroughTheExactReadOnlyApiSurface() throws IOException {
        try (AwsContractStubServer server = new AwsContractStubServer(AwsContractStubServer.Mode.HAPPY);
             AwsSdkEvidenceSourceAdapter adapter = adapter(server)) {
            EvidenceSourceCapture first = adapter.capture(JUNE_WINDOW);
            EvidenceSourceCapture replay = adapter.capture(JUNE_WINDOW);

            assertEquals(
                    EvidenceSourceOutcome.COMPLETE,
                    first.outcome(),
                    () -> "failure=" + first.failureCode() + ", requests=" + server.requests()
            );
            assertEquals(List.of("E-AWS-001", "E-AWS-002", "E-AWS-003", "E-AWS-004"),
                    first.candidates().stream().map(EvidenceCandidate::evidenceReference).toList());
            assertEquals(4, first.requestCount());
            assertEquals(0, first.retryCount());
            assertEquals(3, first.pageCount());
            assertEquals(1, first.qualifyingSourceObjectCount());
            assertEquals(AwsSdkEvidenceSourceAdapter.API_VERSION, first.apiVersion());
            assertTrue(first.sourceReference().contains("********9012"));
            assertFalse(first.sourceReference().contains("123456789012"));
            assertEquals(
                    first.candidates().stream().map(EvidenceCandidate::evidenceId).toList(),
                    replay.candidates().stream().map(EvidenceCandidate::evidenceId).toList()
            );
            assertEquals(
                    first.candidates().stream().map(EvidenceCandidate::metadata).toList(),
                    replay.candidates().stream().map(EvidenceCandidate::metadata).toList()
            );
            assertEquals(
                    List.of(
                            UUID.fromString("439d74fc-8b76-59bc-8065-73caf2ec5e5f"),
                            UUID.fromString("e51f7337-f9d9-5f15-af2c-6ee29e4266a1"),
                            UUID.fromString("7e3ea0ad-96f4-51ec-bb40-dbb070f49c3c"),
                            UUID.fromString("ee7beeee-ef87-55ec-9d7f-6998dddc9a4b")
                    ),
                    first.candidates().stream()
                            .map(candidate -> candidate.evidenceId().value())
                            .toList()
            );

            EvidenceCandidate cost = first.candidates().getFirst();
            assertEquals("410.00", cost.metadata().get("monthly_cost"));
            assertEquals("410", cost.metadata().get("source_amount"));
            assertEquals("EUR", cost.metadata().get("currency"));
            assertEquals("2026-06-30T23:59:59.999999Z", cost.timestamp().value().toString());
            assertEquals("CONFIDENTIAL", cost.sensitivity());
            assertEquals("not_stored", cost.rawPayloadMode());
            EvidenceCandidate utilization = first.candidates().get(2);
            assertEquals("42", utilization.metadata().get("invocation_sum"));
            assertEquals("2", utilization.metadata().get("error_sum"));
            assertEquals("1234.568", utilization.metadata().get("duration_ms_sum"));
            assertEquals(
                    "4a17b367b9ff2d5b675f96e921cacd6e347e793d93f8ec6920d6d9a5280d7d94",
                    utilization.metadata().get("resource_set_sha256")
            );

            assertReadOnlyRequests(server.requests().subList(0, 4));
        }
    }

    @Test
    void disabledInvalidAndFutureWindowConfigurationsMakeZeroAwsCalls() {
        AtomicInteger clientCreations = new AtomicInteger();
        AwsSdkClientFactory forbiddenFactory = (settings, metrics) -> {
            clientCreations.incrementAndGet();
            throw new AssertionError("AWS clients must not be created");
        };
        try (AwsSdkEvidenceSourceAdapter disabled = adapter(
                new AwsConnectorSettings(false, "", ""),
                forbiddenFactory
        ); AwsSdkEvidenceSourceAdapter invalid = adapter(
                new AwsConnectorSettings(true, "invalid", "eu-west-1"),
                forbiddenFactory
        ); AwsSdkEvidenceSourceAdapter future = adapter(
                settings(),
                forbiddenFactory
        )) {
            assertEquals(EvidenceSourceOutcome.DISABLED, disabled.capture(JUNE_WINDOW).outcome());
            assertEquals(EvidenceSourceOutcome.MISCONFIGURED, invalid.capture(JUNE_WINDOW).outcome());
            EvidenceSourceRequest futureWindow = new EvidenceSourceRequest(
                    Instant.parse("2026-07-01T00:00:00Z"),
                    RUN_TIME.plusSeconds(1),
                    UUID.fromString("c1f7cbd8-6e47-49bd-9f92-55a626a5d1a1")
            );
            EvidenceSourceCapture result = future.capture(futureWindow);
            assertEquals(EvidenceSourceOutcome.MISCONFIGURED, result.outcome());
            assertEquals("AWS_FUTURE_WINDOW_FORBIDDEN", result.failureCode());
            assertEquals(0, clientCreations.get());
        }
    }

    @Test
    void accountMismatchStopsAfterStsAndFailsClosed() throws IOException {
        try (AwsContractStubServer server = new AwsContractStubServer(
                AwsContractStubServer.Mode.ACCOUNT_MISMATCH
        ); AwsSdkEvidenceSourceAdapter adapter = adapter(server)) {
            EvidenceSourceCapture result = adapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.MISCONFIGURED, result.outcome());
            assertEquals("AWS_ACCOUNT_MISMATCH", result.failureCode());
            assertEquals(1, result.requestCount());
            assertTrue(result.candidates().isEmpty());
            assertEquals(1, server.requests().size());
        }
    }

    @Test
    void estimatedCostIsAbsentWithoutFabricatingOrBlockingIndependentEvidence() throws IOException {
        try (AwsContractStubServer server = new AwsContractStubServer(
                AwsContractStubServer.Mode.ESTIMATED_COST
        ); AwsSdkEvidenceSourceAdapter adapter = adapter(server)) {
            EvidenceSourceCapture result = adapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.PARTIAL, result.outcome());
            assertEquals("AWS_FINALIZED_COST_MISSING", result.failureCode());
            assertEquals(List.of("E-AWS-002", "E-AWS-003", "E-AWS-004"),
                    result.candidates().stream().map(EvidenceCandidate::evidenceReference).toList());
            assertEquals(List.of("E-AWS-001"), result.missingEvidenceReferences());
        }
    }

    @Test
    void ambiguousOwnerAndMissingMetricsRemainExplicitlyPartial() throws IOException {
        try (AwsContractStubServer ownerServer = new AwsContractStubServer(
                AwsContractStubServer.Mode.MISSING_OWNER
        ); AwsSdkEvidenceSourceAdapter ownerAdapter = adapter(ownerServer);
             AwsContractStubServer metricServer = new AwsContractStubServer(
                     AwsContractStubServer.Mode.MISSING_METRICS
             ); AwsSdkEvidenceSourceAdapter metricAdapter = adapter(metricServer)) {
            EvidenceSourceCapture owner = ownerAdapter.capture(JUNE_WINDOW);
            EvidenceSourceCapture metric = metricAdapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.PARTIAL, owner.outcome());
            assertEquals("AWS_OWNER_AMBIGUOUS", owner.failureCode());
            assertEquals(List.of("E-AWS-004"), owner.missingEvidenceReferences());
            assertEquals(EvidenceSourceOutcome.PARTIAL, metric.outcome());
            assertEquals("AWS_METRICS_INCOMPLETE", metric.failureCode());
            assertEquals(List.of("E-AWS-003"), metric.missingEvidenceReferences());
        }
    }

    @Test
    void unsupportedScopedServiceKeepsOnlyTruthfulCostEvidence() throws IOException {
        try (AwsContractStubServer server = new AwsContractStubServer(
                AwsContractStubServer.Mode.UNSUPPORTED_RESOURCE
        ); AwsSdkEvidenceSourceAdapter adapter = adapter(server)) {
            EvidenceSourceCapture result = adapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.PARTIAL, result.outcome());
            assertEquals("AWS_UNSUPPORTED_SCOPED_SERVICE", result.failureCode());
            assertEquals(List.of("E-AWS-001"),
                    result.candidates().stream().map(EvidenceCandidate::evidenceReference).toList());
            assertEquals(3, result.requestCount());
            assertEquals(3, server.requests().size());
        }
    }

    @Test
    void cyclingPaginationTokenFailsWithoutImportableCandidates() throws IOException {
        try (AwsContractStubServer server = new AwsContractStubServer(
                AwsContractStubServer.Mode.PAGINATION_LOOP
        ); AwsSdkEvidenceSourceAdapter adapter = adapter(server)) {
            EvidenceSourceCapture result = adapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.INCOMPLETE, result.outcome());
            assertEquals("AWS_PAGINATION_TOKEN_INVALID", result.failureCode());
            assertTrue(result.candidates().isEmpty());
            assertEquals(2, result.pageCount());
        }
    }

    @Test
    void nonEuroCostRemainsTruthfulAndDoesNotChangeConnectorCompleteness() throws IOException {
        try (AwsContractStubServer server = new AwsContractStubServer(
                AwsContractStubServer.Mode.NON_EUR_COST
        ); AwsSdkEvidenceSourceAdapter adapter = adapter(server)) {
            EvidenceSourceCapture result = adapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.COMPLETE, result.outcome());
            EvidenceCandidate cost = result.candidates().getFirst();
            assertEquals("USD", cost.metadata().get("currency"));
            assertEquals("99.95", cost.metadata().get("monthly_cost"));
            assertEquals("99.95", cost.metadata().get("source_amount"));
        }
    }

    @Test
    void throttlingUsesTheSdkRetryBudgetAndAccessDeniedFailsClosed() throws IOException {
        try (AwsContractStubServer throttleServer = new AwsContractStubServer(
                AwsContractStubServer.Mode.THROTTLED_TAG
        ); AwsSdkEvidenceSourceAdapter throttleAdapter = adapter(throttleServer);
             AwsContractStubServer forbiddenServer = new AwsContractStubServer(
                     AwsContractStubServer.Mode.FORBIDDEN_TAG
             ); AwsSdkEvidenceSourceAdapter forbiddenAdapter = adapter(forbiddenServer)) {
            EvidenceSourceCapture throttled = throttleAdapter.capture(JUNE_WINDOW);
            EvidenceSourceCapture forbidden = forbiddenAdapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.RATE_LIMITED, throttled.outcome());
            assertEquals("AWS_RATE_LIMITED", throttled.failureCode());
            assertEquals(2, throttled.requestCount());
            assertEquals(2, throttled.retryCount());
            assertEquals(4, throttleServer.requests().size());
            assertEquals(EvidenceSourceOutcome.FORBIDDEN, forbidden.outcome());
            assertEquals("AWS_FORBIDDEN", forbidden.failureCode());
            assertTrue(forbidden.candidates().isEmpty());
        }
    }

    @Test
    void exhaustedRunBudgetStopsBeforeTheNextAwsCall() throws IOException {
        AtomicLong monotonic = new AtomicLong();
        try (AwsContractStubServer server = new AwsContractStubServer(AwsContractStubServer.Mode.HAPPY);
             AwsSdkEvidenceSourceAdapter adapter = new AwsSdkEvidenceSourceAdapter(
                     settings(),
                     (settings, metrics) -> AwsTestClients.create(settings, metrics, server.endpoint()),
                     Clock.fixed(RUN_TIME, ZoneOffset.UTC),
                     () -> monotonic.getAndAdd(java.time.Duration.ofSeconds(106).toNanos())
             )) {
            EvidenceSourceCapture result = adapter.capture(JUNE_WINDOW);

            assertEquals(EvidenceSourceOutcome.DEGRADED, result.outcome());
            assertEquals("AWS_RUN_TIMEOUT", result.failureCode());
            assertEquals(0, result.requestCount());
            assertTrue(server.requests().isEmpty());
        }
    }

    private static AwsSdkEvidenceSourceAdapter adapter(AwsContractStubServer server) {
        return adapter(
                settings(),
                (settings, metrics) -> AwsTestClients.create(settings, metrics, server.endpoint())
        );
    }

    private static AwsSdkEvidenceSourceAdapter adapter(
            AwsConnectorSettings settings,
            AwsSdkClientFactory clientFactory
    ) {
        return new AwsSdkEvidenceSourceAdapter(
                settings,
                clientFactory,
                Clock.fixed(RUN_TIME, ZoneOffset.UTC),
                System::nanoTime
        );
    }

    private static AwsConnectorSettings settings() {
        return new AwsConnectorSettings(true, "123456789012", "eu-west-1");
    }

    private static void assertReadOnlyRequests(List<AwsContractStubServer.RequestSnapshot> requests) {
        assertEquals(4, requests.size());
        assertEquals("GetCallerIdentity", requests.get(0).target());
        assertTrue(requests.get(1).target().endsWith(".GetResources"));
        assertTrue(requests.get(2).target().endsWith(".GetCostAndUsage"));
        assertEquals("GetMetricData", requests.get(3).target());
        requests.forEach(request -> assertTrue(request.authorization().startsWith("AWS4-HMAC-SHA256")));

        String tags = requests.get(1).body();
        assertTrue(tags.contains("\"ResourcesPerPage\":100"));
        assertTrue(tags.contains("\"Key\":\"project\""));
        assertTrue(tags.contains("\"Key\":\"jira_ticket\""));
        assertTrue(tags.contains("\"Key\":\"resource_group\""));
        assertFalse(tags.contains("ResourceTypeFilters"));
        assertFalse(tags.contains("ResourceARNList"));

        String cost = requests.get(2).body();
        assertTrue(cost.contains("\"Granularity\":\"MONTHLY\""));
        assertTrue(cost.contains("\"Metrics\":[\"UnblendedCost\"]"));
        assertTrue(cost.contains("\"Key\":\"LINKED_ACCOUNT\""));
        assertTrue(cost.contains("123456789012"));
        assertFalse(cost.contains("GroupBy"));
        assertFalse(cost.contains("BillingViewArn"));

        String metrics = requests.get(3).body();
        assertEquals("application/cbor", requests.get(3).contentType());
        assertEquals("rpc-v2-cbor", requests.get(3).smithyProtocol());
        assertEquals("true", requests.get(3).queryMode());
        assertTrue(metrics.contains("MaxDatapoints"));
        assertTrue(metrics.contains("TimestampAscending"));
        assertTrue(metrics.contains("AWS/Lambda"));
        assertTrue(metrics.contains("Invocations"));
        assertTrue(metrics.contains("Errors"));
        assertTrue(metrics.contains("Duration"));
        assertTrue(metrics.contains("Period"));
        assertFalse(metrics.contains("Expression"));
    }
}
