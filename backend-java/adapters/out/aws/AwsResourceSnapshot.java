package imperator.adapters.out.aws;

import java.util.List;
import java.util.Set;

record AwsResourceSnapshot(
        List<AwsScopedResource> resources,
        List<AwsScopedResource> supportedResources,
        Set<String> services,
        String fingerprint,
        boolean unsupportedService,
        boolean ownerComplete
) {
    List<AwsScopedResource> lambdaResources() {
        return supportedResources.stream().filter(AwsScopedResource::isLambda).toList();
    }
}
