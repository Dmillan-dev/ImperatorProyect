package imperator.adapters.out.aws;

import java.util.Map;

record AwsScopedResource(
        String arn,
        String service,
        String lambdaFunctionName,
        Map<String, String> tags
) {
    boolean isLambda() {
        return "lambda".equals(service);
    }
}
