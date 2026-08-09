package imperator.adapters.out.aws;

import imperator.ports.out.EvidenceCandidate;
import imperator.ports.out.EvidenceSourceCapture;
import imperator.ports.out.EvidenceSourceOutcome;
import imperator.ports.out.EvidenceSourcePort;
import imperator.ports.out.EvidenceSourceRequest;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;
import software.amazon.awssdk.services.cloudwatch.model.ScanBy;
import software.amazon.awssdk.services.cloudwatch.model.StatusCode;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;
import software.amazon.awssdk.services.costexplorer.model.DimensionValues;
import software.amazon.awssdk.services.costexplorer.model.Expression;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageRequest;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Granularity;
import software.amazon.awssdk.services.costexplorer.model.MatchOption;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;
import software.amazon.awssdk.services.costexplorer.model.TagValues;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesResponse;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.TagFilter;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class AwsSdkEvidenceSourceAdapter implements EvidenceSourcePort, AutoCloseable {
    static final String API_VERSION = "aws-sdk-java-v2=2.49.6;sts=2011-06-15;"
            + "ce=2017-10-25;tag=2017-01-26;cw=2010-08-01";

    private static final String PROJECT_KEY = "project";
    private static final String PROJECT_VALUE = "customer-onboarding";
    private static final String JIRA_KEY = "jira_ticket";
    private static final String JIRA_VALUE = "IMP-214";
    private static final String RESOURCE_GROUP_KEY = "resource_group";
    private static final String RESOURCE_GROUP_VALUE = "onboarding-assistant-prod";
    private static final String OWNER_KEY = "owner";
    private static final String OWNER_VALUE = "platform-team";
    private static final Set<String> ALLOWED_TAGS = Set.of(
            PROJECT_KEY,
            JIRA_KEY,
            RESOURCE_GROUP_KEY,
            OWNER_KEY
    );
    private static final Set<String> SUPPORTED_SERVICES = Set.of("lambda", "apigateway");
    private static final List<String> EVIDENCE_REFERENCES = List.of(
            "E-AWS-001",
            "E-AWS-002",
            "E-AWS-003",
            "E-AWS-004"
    );
    private static final Pattern ACCOUNT_ID = Pattern.compile("[0-9]{12}");
    private static final int MAX_RESOURCES = 25;
    private static final int MAX_TAG_PAGES = 20;
    private static final int MAX_COST_PAGES = 5;
    private static final int MAX_METRIC_PAGES = 5;
    private static final int MAX_METRIC_DATAPOINTS = 5000;
    private static final long RUN_TIMEOUT_NANOS = Duration.ofSeconds(120).toNanos();
    private static final long CALL_BUDGET_NANOS = Duration.ofSeconds(15).toNanos();

    private final AwsConnectorSettings settings;
    private final AwsSdkClientFactory clientFactory;
    private final Clock clock;
    private final LongSupplier nanoTime;
    private final AwsSdkCallMetrics callMetrics = new AwsSdkCallMetrics();
    private final AwsEvidenceMapper mapper;

    private volatile AwsSdkClients clients;

    public AwsSdkEvidenceSourceAdapter(AwsConnectorSettings settings) {
        this(settings, AwsSdkClients::production, Clock.systemUTC(), System::nanoTime);
    }

    AwsSdkEvidenceSourceAdapter(
            AwsConnectorSettings settings,
            AwsSdkClientFactory clientFactory,
            Clock clock,
            LongSupplier nanoTime
    ) {
        this.settings = Objects.requireNonNull(settings, "AWS settings are required");
        this.clientFactory = Objects.requireNonNull(clientFactory, "AWS client factory is required");
        this.clock = Objects.requireNonNull(clock, "AWS clock is required");
        this.nanoTime = Objects.requireNonNull(nanoTime, "AWS monotonic clock is required");
        this.mapper = new AwsEvidenceMapper(settings);
    }

    @Override
    public EvidenceSourceCapture capture(EvidenceSourceRequest request) {
        Objects.requireNonNull(request, "Evidence source request is required");
        AwsRunMetrics metrics = new AwsRunMetrics();
        if (!settings.enabled()) {
            return failure(EvidenceSourceOutcome.DISABLED, "AWS_DISABLED", metrics, Optional.empty());
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

        Instant runStartedAt = clock.instant();
        if (request.untilExclusive().isAfter(runStartedAt)) {
            return failure(
                    EvidenceSourceOutcome.MISCONFIGURED,
                    "AWS_FUTURE_WINDOW_FORBIDDEN",
                    metrics,
                    Optional.empty()
            );
        }
        Optional<AwsBillingPeriod> selectedPeriod = AwsBillingPeriod.latestComplete(request);
        if (selectedPeriod.isEmpty()
                || selectedPeriod.orElseThrow().endExclusive().isAfter(runStartedAt)) {
            return failure(
                    EvidenceSourceOutcome.MISCONFIGURED,
                    "AWS_COMPLETE_BILLING_PERIOD_REQUIRED",
                    metrics,
                    Optional.empty()
            );
        }

        long deadline = saturatedDeadline(nanoTime.getAsLong());
        callMetrics.activate(metrics);
        try {
            AwsSdkClients sdk = sdkClients();
            resolveCredentials(sdk);
            String accountId = callerAccount(sdk, deadline);
            if (!settings.expectedAccountId().equals(accountId)) {
                throw new AwsRequestFailure(
                        EvidenceSourceOutcome.MISCONFIGURED,
                        "AWS_ACCOUNT_MISMATCH"
                );
            }

            AwsBillingPeriod period = selectedPeriod.orElseThrow();
            AwsResourceSnapshot resources = readResources(sdk, metrics, deadline);
            Optional<AwsCostObservation> cost = readCost(sdk, period, metrics, deadline);
            if (resources.resources().isEmpty() && cost.isEmpty()) {
                return failure(
                        EvidenceSourceOutcome.NO_MATCH,
                        "AWS_SCOPE_NOT_FOUND",
                        metrics,
                        Optional.empty()
                );
            }
            if (resources.unsupportedService() && cost.isEmpty()) {
                return failure(
                        EvidenceSourceOutcome.INCOMPLETE,
                        "AWS_UNSUPPORTED_SCOPED_SERVICE",
                        metrics,
                        Optional.empty()
                );
            }

            String freshness = freshness(period, request.untilExclusive());
            List<EvidenceCandidate> candidates = new ArrayList<>();
            cost.map(observation -> mapper.cost(period, observation, freshness))
                    .ifPresent(candidates::add);

            Optional<AwsUtilizationObservation> utilization = Optional.empty();
            if (!resources.unsupportedService() && !resources.supportedResources().isEmpty()) {
                candidates.add(mapper.resources(period, resources, freshness));
                utilization = readUtilization(sdk, period, resources, metrics, deadline);
                utilization.map(observation -> mapper.utilization(
                                period,
                                resources,
                                observation,
                                freshness
                        ))
                        .ifPresent(candidates::add);
                if (resources.ownerComplete()) {
                    candidates.add(mapper.owner(period, resources, freshness));
                }
            }

            candidates.sort(Comparator.comparing(
                            (EvidenceCandidate candidate) -> candidate.timestamp().value()
                    )
                    .thenComparing(EvidenceCandidate::evidenceReference)
                    .thenComparing(EvidenceCandidate::sourceObjectRef)
                    .thenComparing(candidate -> candidate.evidenceId().value().toString()));
            Set<String> present = new HashSet<>();
            candidates.forEach(candidate -> present.add(candidate.evidenceReference()));
            List<String> missing = EVIDENCE_REFERENCES.stream()
                    .filter(reference -> !present.contains(reference))
                    .toList();
            EvidenceSourceOutcome outcome = missing.isEmpty()
                    ? EvidenceSourceOutcome.COMPLETE
                    : EvidenceSourceOutcome.PARTIAL;
            return new EvidenceSourceCapture(
                    outcome,
                    settings.sourceReference(),
                    API_VERSION,
                    metrics.requestCount(),
                    metrics.retryCount(),
                    metrics.pageCount(),
                    resources.resources().size(),
                    candidates,
                    missing,
                    partialFailureCode(resources, cost, utilization),
                    Optional.empty()
            );
        } catch (AwsRequestFailure failure) {
            return failure(failure.outcome(), failure.code(), metrics, failure.retryAt());
        } catch (RuntimeException exception) {
            return failure(
                    EvidenceSourceOutcome.DEGRADED,
                    "AWS_CAPTURE_FAILED",
                    metrics,
                    Optional.empty()
            );
        } finally {
            callMetrics.clear();
        }
    }

    @Override
    public synchronized void close() {
        if (clients != null) {
            clients.close();
            clients = null;
        }
    }

    private synchronized AwsSdkClients sdkClients() {
        if (clients == null) {
            clients = clientFactory.create(settings, callMetrics);
        }
        return clients;
    }

    private void resolveCredentials(AwsSdkClients sdk) {
        try {
            sdk.credentialsProvider().resolveCredentials();
        } catch (RuntimeException exception) {
            throw new AwsRequestFailure(EvidenceSourceOutcome.UNAUTHORIZED, "AWS_UNAUTHORIZED");
        }
    }

    private String callerAccount(AwsSdkClients sdk, long deadline) {
        GetCallerIdentityResponse response = call(
                () -> sdk.sts().getCallerIdentity(GetCallerIdentityRequest.builder().build()),
                deadline,
                true
        );
        String account = normalize(response.account());
        if (!ACCOUNT_ID.matcher(account).matches()) {
            throw new AwsRequestFailure(
                    EvidenceSourceOutcome.API_VERSION_UNSUPPORTED,
                    "AWS_API_VERSION_UNSUPPORTED"
            );
        }
        return account;
    }

    private AwsResourceSnapshot readResources(
            AwsSdkClients sdk,
            AwsRunMetrics metrics,
            long deadline
    ) {
        GetResourcesRequest baseRequest = GetResourcesRequest.builder()
                .tagFilters(
                        tagFilter(PROJECT_KEY, PROJECT_VALUE),
                        tagFilter(JIRA_KEY, JIRA_VALUE),
                        tagFilter(RESOURCE_GROUP_KEY, RESOURCE_GROUP_VALUE)
                )
                .resourcesPerPage(100)
                .build();
        List<AwsScopedResource> resources = new ArrayList<>();
        Set<String> arns = new HashSet<>();
        Set<String> tokens = new HashSet<>();
        String token = null;
        int pages = 0;
        do {
            GetResourcesRequest request = token == null
                    ? baseRequest
                    : baseRequest.toBuilder().paginationToken(token).build();
            GetResourcesResponse response = call(
                    () -> sdk.tagging().getResources(request),
                    deadline,
                    false
            );
            metrics.incrementPages();
            pages++;
            for (ResourceTagMapping mapping : response.resourceTagMappingList()) {
                AwsScopedResource resource = scopedResource(mapping);
                if (!arns.add(resource.arn())) {
                    throw boundedFailure("AWS_RESOURCE_IDENTITY_DUPLICATE");
                }
                resources.add(resource);
                if (resources.size() > MAX_RESOURCES) {
                    throw boundedFailure("AWS_RESOURCE_SCOPE_TOO_BROAD");
                }
            }
            token = nextToken(response.paginationToken(), tokens, pages, MAX_TAG_PAGES);
        } while (token != null);

        resources.sort(Comparator.comparing(AwsScopedResource::arn));
        List<AwsScopedResource> supported = resources.stream()
                .filter(resource -> SUPPORTED_SERVICES.contains(resource.service()))
                .toList();
        Set<String> services = new TreeSet<>();
        supported.forEach(resource -> services.add(resource.service()));
        boolean unsupported = supported.size() != resources.size();
        boolean ownerComplete = !resources.isEmpty() && resources.stream()
                .allMatch(resource -> OWNER_VALUE.equals(resource.tags().get(OWNER_KEY)));
        return new AwsResourceSnapshot(
                List.copyOf(resources),
                supported,
                Set.copyOf(services),
                sha256(resources.stream().map(AwsScopedResource::arn).toList()),
                unsupported,
                ownerComplete
        );
    }

    private Optional<AwsCostObservation> readCost(
            AwsSdkClients sdk,
            AwsBillingPeriod period,
            AwsRunMetrics metrics,
            long deadline
    ) {
        GetCostAndUsageRequest baseRequest = GetCostAndUsageRequest.builder()
                .timePeriod(DateInterval.builder()
                        .start(period.startInclusive().toString().substring(0, 10))
                        .end(period.endExclusive().toString().substring(0, 10))
                        .build())
                .granularity(Granularity.MONTHLY)
                .metrics("UnblendedCost")
                .filter(costFilter())
                .build();
        List<ResultByTime> results = new ArrayList<>();
        Set<String> tokens = new HashSet<>();
        String token = null;
        int pages = 0;
        do {
            GetCostAndUsageRequest request = token == null
                    ? baseRequest
                    : baseRequest.toBuilder().nextPageToken(token).build();
            GetCostAndUsageResponse response = call(
                    () -> sdk.costExplorer().getCostAndUsage(request),
                    deadline,
                    false
            );
            metrics.incrementPages();
            pages++;
            results.addAll(response.resultsByTime());
            token = nextToken(response.nextPageToken(), tokens, pages, MAX_COST_PAGES);
        } while (token != null);

        if (results.isEmpty()) {
            return Optional.empty();
        }
        if (results.size() != 1) {
            throw boundedFailure("AWS_COST_RESULT_INVALID");
        }
        ResultByTime result = results.getFirst();
        if (!matchingPeriod(result, period) || !Boolean.FALSE.equals(result.estimated())) {
            return Optional.empty();
        }
        if (!result.hasTotal() || result.total().size() != 1) {
            throw boundedFailure("AWS_COST_RESULT_INVALID");
        }
        MetricValue metric = result.total().get("UnblendedCost");
        if (metric == null) {
            throw boundedFailure("AWS_COST_RESULT_INVALID");
        }
        String amountText = normalize(metric.amount());
        String currency = normalize(metric.unit()).toUpperCase(Locale.ROOT);
        if (amountText.isEmpty() || currency.isEmpty()) {
            throw boundedFailure("AWS_COST_RESULT_INVALID");
        }
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.signum() < 0) {
                throw boundedFailure("AWS_COST_RESULT_INVALID");
            }
            return Optional.of(new AwsCostObservation(
                    amount,
                    amount.setScale(2, RoundingMode.HALF_EVEN),
                    currency
            ));
        } catch (NumberFormatException exception) {
            throw boundedFailure("AWS_COST_RESULT_INVALID");
        }
    }

    private Optional<AwsUtilizationObservation> readUtilization(
            AwsSdkClients sdk,
            AwsBillingPeriod period,
            AwsResourceSnapshot resources,
            AwsRunMetrics metrics,
            long deadline
    ) {
        List<AwsScopedResource> lambdas = resources.lambdaResources();
        if (lambdas.isEmpty()) {
            return Optional.empty();
        }
        List<MetricDataQuery> queries = new ArrayList<>();
        Map<String, MetricKind> expected = new LinkedHashMap<>();
        for (int index = 0; index < lambdas.size(); index++) {
            String functionName = lambdas.get(index).lambdaFunctionName();
            addMetricQuery(queries, expected, metricId(index, "i"), functionName, "Invocations", MetricKind.INVOCATIONS);
            addMetricQuery(queries, expected, metricId(index, "e"), functionName, "Errors", MetricKind.ERRORS);
            addMetricQuery(queries, expected, metricId(index, "d"), functionName, "Duration", MetricKind.DURATION);
        }

        GetMetricDataRequest baseRequest = GetMetricDataRequest.builder()
                .startTime(period.startInclusive())
                .endTime(period.endExclusive())
                .metricDataQueries(queries)
                .scanBy(ScanBy.TIMESTAMP_ASCENDING)
                .maxDatapoints(MAX_METRIC_DATAPOINTS)
                .build();
        Map<String, BigDecimal> totals = new HashMap<>();
        Set<String> observed = new HashSet<>();
        Set<String> tokens = new HashSet<>();
        String token = null;
        int pages = 0;
        int datapoints = 0;
        do {
            GetMetricDataRequest request = token == null
                    ? baseRequest
                    : baseRequest.toBuilder().nextToken(token).build();
            GetMetricDataResponse response = call(
                    () -> sdk.cloudWatch().getMetricData(request),
                    deadline,
                    false
            );
            metrics.incrementPages();
            pages++;
            for (MetricDataResult result : response.metricDataResults()) {
                String id = normalize(result.id());
                if (!expected.containsKey(id)
                        || result.statusCode() != StatusCode.COMPLETE
                        || result.values().isEmpty()) {
                    return Optional.empty();
                }
                observed.add(id);
                for (Double value : result.values()) {
                    if (value == null || !Double.isFinite(value) || value < 0) {
                        return Optional.empty();
                    }
                    totals.merge(id, BigDecimal.valueOf(value), BigDecimal::add);
                    datapoints++;
                    if (datapoints > MAX_METRIC_DATAPOINTS) {
                        throw boundedFailure("AWS_METRIC_DATAPOINT_LIMIT_EXCEEDED");
                    }
                }
            }
            token = nextToken(response.nextToken(), tokens, pages, MAX_METRIC_PAGES);
        } while (token != null);

        if (!observed.equals(expected.keySet())) {
            return Optional.empty();
        }
        BigDecimal invocations = BigDecimal.ZERO;
        BigDecimal errors = BigDecimal.ZERO;
        BigDecimal duration = BigDecimal.ZERO;
        for (Map.Entry<String, MetricKind> entry : expected.entrySet()) {
            BigDecimal value = totals.get(entry.getKey());
            if (value == null) {
                return Optional.empty();
            }
            try {
                switch (entry.getValue()) {
                    case INVOCATIONS -> invocations = invocations.add(value.setScale(0, RoundingMode.UNNECESSARY));
                    case ERRORS -> errors = errors.add(value.setScale(0, RoundingMode.UNNECESSARY));
                    case DURATION -> duration = duration.add(value);
                }
            } catch (ArithmeticException exception) {
                return Optional.empty();
            }
        }
        return Optional.of(new AwsUtilizationObservation(
                lambdas.size(),
                invocations.setScale(0, RoundingMode.UNNECESSARY),
                errors.setScale(0, RoundingMode.UNNECESSARY),
                duration.setScale(3, RoundingMode.HALF_EVEN)
        ));
    }

    private <T> T call(
            Supplier<T> operation,
            long deadline,
            boolean identityCall
    ) {
        ensureCallBudget(deadline);
        try {
            return operation.get();
        } catch (AwsServiceException exception) {
            throw translateServiceFailure(exception, identityCall);
        } catch (SdkClientException exception) {
            if (isProtocolFailure(exception)) {
                throw new AwsRequestFailure(
                        EvidenceSourceOutcome.API_VERSION_UNSUPPORTED,
                        "AWS_API_VERSION_UNSUPPORTED"
                );
            }
            throw new AwsRequestFailure(
                    EvidenceSourceOutcome.DEGRADED,
                    "AWS_SERVICE_UNAVAILABLE"
            );
        }
    }

    private AwsRequestFailure translateServiceFailure(
            AwsServiceException exception,
            boolean identityCall
    ) {
        if (exception.isThrottlingException()) {
            return new AwsRequestFailure(
                    EvidenceSourceOutcome.RATE_LIMITED,
                    "AWS_RATE_LIMITED",
                    retryAt(exception)
            );
        }
        if (identityCall && exception.statusCode() >= 400 && exception.statusCode() < 500) {
            return new AwsRequestFailure(EvidenceSourceOutcome.UNAUTHORIZED, "AWS_UNAUTHORIZED");
        }
        if (exception.statusCode() == 401) {
            return new AwsRequestFailure(EvidenceSourceOutcome.UNAUTHORIZED, "AWS_UNAUTHORIZED");
        }
        if (exception.statusCode() == 403) {
            return new AwsRequestFailure(EvidenceSourceOutcome.FORBIDDEN, "AWS_FORBIDDEN");
        }
        String errorCode = exception.awsErrorDetails() == null
                ? ""
                : normalize(exception.awsErrorDetails().errorCode()).toLowerCase(Locale.ROOT);
        if (errorCode.contains("accessdenied")
                || errorCode.contains("unauthorizedoperation")) {
            return new AwsRequestFailure(EvidenceSourceOutcome.FORBIDDEN, "AWS_FORBIDDEN");
        }
        if (exception.statusCode() >= 500 || exception.isRetryableException()) {
            return new AwsRequestFailure(
                    EvidenceSourceOutcome.DEGRADED,
                    "AWS_SERVICE_UNAVAILABLE"
            );
        }
        return new AwsRequestFailure(
                EvidenceSourceOutcome.API_VERSION_UNSUPPORTED,
                "AWS_API_VERSION_UNSUPPORTED"
        );
    }

    private void ensureCallBudget(long deadline) {
        long remaining = deadline - nanoTime.getAsLong();
        if (remaining < CALL_BUDGET_NANOS) {
            throw new AwsRequestFailure(EvidenceSourceOutcome.DEGRADED, "AWS_RUN_TIMEOUT");
        }
    }

    private long saturatedDeadline(long startedAt) {
        if (startedAt > Long.MAX_VALUE - RUN_TIMEOUT_NANOS) {
            return Long.MAX_VALUE;
        }
        return startedAt + RUN_TIMEOUT_NANOS;
    }

    private AwsScopedResource scopedResource(ResourceTagMapping mapping) {
        String arn = normalize(mapping.resourceARN());
        if (arn.isEmpty() || arn.indexOf('\n') >= 0 || arn.indexOf('\r') >= 0) {
            throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
        }
        Map<String, String> tags = new HashMap<>();
        mapping.tags().forEach(tag -> {
            String key = normalize(tag.key());
            if (ALLOWED_TAGS.contains(key)) {
                String previous = tags.putIfAbsent(key, normalize(tag.value()));
                if (previous != null && !previous.equals(normalize(tag.value()))) {
                    throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
                }
            }
        });
        if (!PROJECT_VALUE.equals(tags.get(PROJECT_KEY))
                || !JIRA_VALUE.equals(tags.get(JIRA_KEY))
                || !RESOURCE_GROUP_VALUE.equals(tags.get(RESOURCE_GROUP_KEY))) {
            throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
        }

        String[] parts = arn.split(":", 6);
        if (parts.length != 6 || !"arn".equals(parts[0]) || !"aws".equals(parts[1])) {
            throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
        }
        String service = parts[2];
        if (!SUPPORTED_SERVICES.contains(service)) {
            return new AwsScopedResource(arn, service, "", Map.copyOf(tags));
        }
        if (!settings.region().equals(parts[3])) {
            throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
        }
        if ("lambda".equals(service)) {
            if (!settings.expectedAccountId().equals(parts[4])
                    || !parts[5].startsWith("function:")) {
                throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
            }
            String functionName = parts[5].substring("function:".length());
            if (functionName.isBlank() || functionName.indexOf(':') >= 0) {
                throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
            }
            return new AwsScopedResource(arn, service, functionName, Map.copyOf(tags));
        }
        if ((!parts[4].isEmpty() && !settings.expectedAccountId().equals(parts[4]))
                || parts[5].isBlank()) {
            throw boundedFailure("AWS_RESOURCE_SCOPE_INVALID");
        }
        return new AwsScopedResource(arn, service, "", Map.copyOf(tags));
    }

    private static TagFilter tagFilter(String key, String value) {
        return TagFilter.builder().key(key).values(value).build();
    }

    private Expression costFilter() {
        return Expression.builder().and(
                Expression.builder()
                        .dimensions(DimensionValues.builder()
                                .key(software.amazon.awssdk.services.costexplorer.model.Dimension.LINKED_ACCOUNT)
                                .values(settings.expectedAccountId())
                                .matchOptions(MatchOption.EQUALS)
                                .build())
                        .build(),
                costTag(PROJECT_KEY, PROJECT_VALUE),
                costTag(JIRA_KEY, JIRA_VALUE),
                costTag(RESOURCE_GROUP_KEY, RESOURCE_GROUP_VALUE)
        ).build();
    }

    private static Expression costTag(String key, String value) {
        return Expression.builder()
                .tags(TagValues.builder()
                        .key(key)
                        .values(value)
                        .matchOptions(MatchOption.EQUALS)
                        .build())
                .build();
    }

    private static boolean matchingPeriod(ResultByTime result, AwsBillingPeriod period) {
        return result.timePeriod() != null
                && period.startInclusive().toString().substring(0, 10)
                .equals(result.timePeriod().start())
                && period.endExclusive().toString().substring(0, 10)
                .equals(result.timePeriod().end());
    }

    private static void addMetricQuery(
            List<MetricDataQuery> queries,
            Map<String, MetricKind> expected,
            String id,
            String functionName,
            String metricName,
            MetricKind kind
    ) {
        Metric metric = Metric.builder()
                .namespace("AWS/Lambda")
                .metricName(metricName)
                .dimensions(Dimension.builder()
                        .name("FunctionName")
                        .value(functionName)
                        .build())
                .build();
        queries.add(MetricDataQuery.builder()
                .id(id)
                .metricStat(MetricStat.builder()
                        .metric(metric)
                        .period(86400)
                        .stat("Sum")
                        .build())
                .returnData(true)
                .build());
        expected.put(id, kind);
    }

    private static String metricId(int index, String suffix) {
        return String.format(Locale.ROOT, "l%02d%s", index, suffix);
    }

    private static String nextToken(
            String rawToken,
            Set<String> observedTokens,
            int pages,
            int maximumPages
    ) {
        if (rawToken == null || rawToken.isEmpty()) {
            return null;
        }
        if (rawToken.isBlank() || containsControlCharacter(rawToken)
                || !observedTokens.add(rawToken)) {
            throw boundedFailure("AWS_PAGINATION_TOKEN_INVALID");
        }
        if (pages >= maximumPages) {
            throw boundedFailure("AWS_PAGE_LIMIT_EXCEEDED");
        }
        return rawToken;
    }

    private static String freshness(AwsBillingPeriod period, Instant untilExclusive) {
        Duration age = Duration.between(period.endExclusive(), untilExclusive);
        return age.compareTo(Duration.ofDays(45)) <= 0 ? "fresh" : "stale";
    }

    private static String partialFailureCode(
            AwsResourceSnapshot resources,
            Optional<AwsCostObservation> cost,
            Optional<AwsUtilizationObservation> utilization
    ) {
        if (resources.unsupportedService()) {
            return "AWS_UNSUPPORTED_SCOPED_SERVICE";
        }
        if (cost.isEmpty()) {
            return "AWS_FINALIZED_COST_MISSING";
        }
        if (resources.supportedResources().isEmpty()) {
            return "AWS_RESOURCE_ATTRIBUTION_MISSING";
        }
        if (utilization.isEmpty()) {
            return "AWS_METRICS_INCOMPLETE";
        }
        if (!resources.ownerComplete()) {
            return "AWS_OWNER_AMBIGUOUS";
        }
        return "";
    }

    private static String sha256(List<String> sortedArns) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(String.join("\n", sortedArns)
                    .getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private Optional<Instant> retryAt(AwsServiceException exception) {
        if (exception.awsErrorDetails() == null
                || exception.awsErrorDetails().sdkHttpResponse() == null) {
            return Optional.empty();
        }
        Optional<String> retryAfter = exception.awsErrorDetails()
                .sdkHttpResponse()
                .firstMatchingHeader("Retry-After");
        if (retryAfter.isEmpty()) {
            return Optional.empty();
        }
        String value = retryAfter.orElseThrow().trim();
        try {
            long seconds = Long.parseLong(value);
            return seconds < 0
                    ? Optional.empty()
                    : Optional.of(clock.instant().plusSeconds(seconds));
        } catch (NumberFormatException ignored) {
            try {
                return Optional.of(ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant());
            } catch (DateTimeParseException invalidDate) {
                return Optional.empty();
            }
        }
    }

    private static boolean isProtocolFailure(Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            String type = current.getClass().getName().toLowerCase(Locale.ROOT);
            if (type.contains("unmarshall") || type.contains("protocol")
                    || type.contains("json") || type.contains("xml")
                    || type.contains("parse")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static AwsRequestFailure boundedFailure(String code) {
        return new AwsRequestFailure(EvidenceSourceOutcome.INCOMPLETE, code);
    }

    private EvidenceSourceCapture failure(
            EvidenceSourceOutcome outcome,
            String failureCode,
            AwsRunMetrics metrics,
            Optional<Instant> retryAt
    ) {
        return new EvidenceSourceCapture(
                outcome,
                settings.sourceReference(),
                API_VERSION,
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

    private static boolean containsControlCharacter(String value) {
        return value.chars().anyMatch(Character::isISOControl);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private enum MetricKind {
        INVOCATIONS,
        ERRORS,
        DURATION
    }
}
