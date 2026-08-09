package imperator.adapters.out.aws;

@FunctionalInterface
interface AwsSdkClientFactory {
    AwsSdkClients create(AwsConnectorSettings settings, AwsSdkCallMetrics metrics);
}
