package imperator.adapters.out.aws;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.retries.StandardRetryStrategy;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.sts.StsClient;

import java.time.Duration;

final class AwsSdkClients implements AutoCloseable {
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration ATTEMPT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration CALL_TIMEOUT = Duration.ofSeconds(15);

    private final AwsCredentialsProvider credentialsProvider;
    private final AutoCloseable credentialsResource;
    private final SdkHttpClient httpClient;
    private final StsClient sts;
    private final ResourceGroupsTaggingApiClient tagging;
    private final CostExplorerClient costExplorer;
    private final CloudWatchClient cloudWatch;

    AwsSdkClients(
            AwsCredentialsProvider credentialsProvider,
            AutoCloseable credentialsResource,
            SdkHttpClient httpClient,
            StsClient sts,
            ResourceGroupsTaggingApiClient tagging,
            CostExplorerClient costExplorer,
            CloudWatchClient cloudWatch
    ) {
        this.credentialsProvider = credentialsProvider;
        this.credentialsResource = credentialsResource;
        this.httpClient = httpClient;
        this.sts = sts;
        this.tagging = tagging;
        this.costExplorer = costExplorer;
        this.cloudWatch = cloudWatch;
    }

    static AwsSdkClients production(
            AwsConnectorSettings settings,
            AwsSdkCallMetrics metrics
    ) {
        DefaultCredentialsProvider credentials = DefaultCredentialsProvider.builder().build();
        SdkHttpClient httpClient = UrlConnectionHttpClient.builder()
                .connectionTimeout(CONNECTION_TIMEOUT)
                .socketTimeout(ATTEMPT_TIMEOUT)
                .build();
        ClientOverrideConfiguration override = ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(ATTEMPT_TIMEOUT)
                .apiCallTimeout(CALL_TIMEOUT)
                .retryStrategy(StandardRetryStrategy.builder().maxAttempts(3).build())
                .addExecutionInterceptor(metrics)
                .build();
        Region workloadRegion = settings.sdkRegion();
        return new AwsSdkClients(
                credentials,
                credentials,
                httpClient,
                StsClient.builder()
                        .credentialsProvider(credentials)
                        .region(workloadRegion)
                        .httpClient(httpClient)
                        .overrideConfiguration(override)
                        .build(),
                ResourceGroupsTaggingApiClient.builder()
                        .credentialsProvider(credentials)
                        .region(workloadRegion)
                        .httpClient(httpClient)
                        .overrideConfiguration(override)
                        .build(),
                CostExplorerClient.builder()
                        .credentialsProvider(credentials)
                        .region(Region.US_EAST_1)
                        .httpClient(httpClient)
                        .overrideConfiguration(override)
                        .build(),
                CloudWatchClient.builder()
                        .credentialsProvider(credentials)
                        .region(workloadRegion)
                        .httpClient(httpClient)
                        .overrideConfiguration(override)
                        .build()
        );
    }

    AwsCredentialsProvider credentialsProvider() {
        return credentialsProvider;
    }

    StsClient sts() {
        return sts;
    }

    ResourceGroupsTaggingApiClient tagging() {
        return tagging;
    }

    CostExplorerClient costExplorer() {
        return costExplorer;
    }

    CloudWatchClient cloudWatch() {
        return cloudWatch;
    }

    @Override
    public void close() {
        closeQuietly(cloudWatch);
        closeQuietly(costExplorer);
        closeQuietly(tagging);
        closeQuietly(sts);
        closeQuietly(httpClient);
        closeQuietly(credentialsResource);
    }

    private static void closeQuietly(AutoCloseable resource) {
        try {
            resource.close();
        } catch (Exception ignored) {
            // Closing is best effort; no provider detail may escape this adapter.
        }
    }
}
