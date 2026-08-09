package imperator.adapters.out.aws;

import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;

final class AwsSdkCallMetrics implements ExecutionInterceptor {
    private final ThreadLocal<AwsRunMetrics> activeRun = new ThreadLocal<>();

    void activate(AwsRunMetrics metrics) {
        activeRun.set(metrics);
    }

    void clear() {
        activeRun.remove();
    }

    @Override
    public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes attributes) {
        AwsRunMetrics metrics = activeRun.get();
        if (metrics != null) {
            metrics.incrementRequests();
        }
    }

    @Override
    public void beforeTransmission(Context.BeforeTransmission context, ExecutionAttributes attributes) {
        AwsRunMetrics metrics = activeRun.get();
        if (metrics != null) {
            metrics.incrementTransmissions();
        }
    }
}
