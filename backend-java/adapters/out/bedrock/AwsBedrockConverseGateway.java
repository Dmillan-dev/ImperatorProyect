package imperator.adapters.out.bedrock;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.retries.StandardRetryStrategy;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock;

import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AwsBedrockConverseGateway implements BedrockConverseGateway {
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration ATTEMPT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration CALL_TIMEOUT = Duration.ofSeconds(25);

    private final BedrockExplanationSettings settings;
    private final DefaultCredentialsProvider credentials;
    private final SdkHttpClient httpClient;
    private final BedrockRuntimeClient client;

    private AwsBedrockConverseGateway(
            BedrockExplanationSettings settings,
            DefaultCredentialsProvider credentials,
            SdkHttpClient httpClient,
            BedrockRuntimeClient client
    ) {
        this.settings = Objects.requireNonNull(settings, "Bedrock settings are required");
        this.credentials = Objects.requireNonNull(credentials, "Bedrock credentials are required");
        this.httpClient = Objects.requireNonNull(httpClient, "Bedrock HTTP client is required");
        this.client = Objects.requireNonNull(client, "Bedrock client is required");
    }

    public static AwsBedrockConverseGateway production(BedrockExplanationSettings settings) {
        BedrockExplanationSettings configuration = Objects.requireNonNull(settings, "Bedrock settings are required");
        DefaultCredentialsProvider credentials = DefaultCredentialsProvider.builder().build();
        SdkHttpClient httpClient = UrlConnectionHttpClient.builder()
                .connectionTimeout(CONNECTION_TIMEOUT)
                .socketTimeout(ATTEMPT_TIMEOUT)
                .build();
        ClientOverrideConfiguration override = ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(ATTEMPT_TIMEOUT)
                .apiCallTimeout(CALL_TIMEOUT)
                .retryStrategy(StandardRetryStrategy.builder().maxAttempts(2).build())
                .build();
        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .credentialsProvider(credentials)
                .region(configuration.sdkRegion())
                .httpClient(httpClient)
                .overrideConfiguration(override)
                .build();
        return new AwsBedrockConverseGateway(configuration, credentials, httpClient, client);
    }

    @Override
    public BedrockConverseResult converse(String systemPrompt, String userPrompt) {
        Message message = Message.builder()
                .role(ConversationRole.USER)
                .content(ContentBlock.fromText(userPrompt))
                .build();
        InferenceConfiguration inference = InferenceConfiguration.builder()
                .maxTokens(settings.maxOutputTokens())
                .temperature(settings.temperature())
                .build();
        ConverseResponse response = client.converse(request -> request
                .modelId(settings.modelId())
                .system(SystemContentBlock.fromText(systemPrompt))
                .messages(message)
                .inferenceConfig(inference));
        String output = response.output().message().content().stream()
                .map(ContentBlock::text)
                .filter(Objects::nonNull)
                .collect(Collectors.joining())
                .trim();
        return new BedrockConverseResult(
                output,
                response.usage().inputTokens(),
                response.usage().outputTokens(),
                response.metrics().latencyMs()
        );
    }

    @Override
    public void close() {
        closeQuietly(client);
        closeQuietly(httpClient);
        closeQuietly(credentials);
    }

    private static void closeQuietly(AutoCloseable resource) {
        try {
            resource.close();
        } catch (Exception ignored) {
            // Closing is best effort and must not expose credential-provider details.
        }
    }
}
