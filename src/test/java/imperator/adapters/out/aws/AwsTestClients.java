package imperator.adapters.out.aws;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.retries.StandardRetryStrategy;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.sts.StsClient;

import java.net.URI;
import java.time.Duration;

final class AwsTestClients {
    private AwsTestClients() {
    }

    static AwsSdkClients create(
            AwsConnectorSettings settings,
            AwsSdkCallMetrics metrics,
            URI endpoint
    ) {
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create("AKIDEXAMPLETEST", "dummy-test-secret")
        );
        SdkHttpClient http = UrlConnectionHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(3))
                .socketTimeout(Duration.ofSeconds(5))
                .build();
        ClientOverrideConfiguration override = ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(Duration.ofSeconds(5))
                .apiCallTimeout(Duration.ofSeconds(15))
                .retryStrategy(StandardRetryStrategy.builder().maxAttempts(3).build())
                .addExecutionInterceptor(metrics)
                .build();
        return new AwsSdkClients(
                credentials,
                () -> {
                },
                http,
                StsClient.builder()
                        .credentialsProvider(credentials)
                        .region(settings.sdkRegion())
                        .endpointOverride(endpoint)
                        .httpClient(http)
                        .overrideConfiguration(override)
                        .build(),
                ResourceGroupsTaggingApiClient.builder()
                        .credentialsProvider(credentials)
                        .region(settings.sdkRegion())
                        .endpointOverride(endpoint)
                        .httpClient(http)
                        .overrideConfiguration(override)
                        .build(),
                CostExplorerClient.builder()
                        .credentialsProvider(credentials)
                        .region(Region.US_EAST_1)
                        .endpointOverride(endpoint)
                        .httpClient(http)
                        .overrideConfiguration(override)
                        .build(),
                CloudWatchClient.builder()
                        .credentialsProvider(credentials)
                        .region(settings.sdkRegion())
                        .endpointOverride(endpoint)
                        .httpClient(http)
                        .overrideConfiguration(override)
                        .build()
        );
    }
}
