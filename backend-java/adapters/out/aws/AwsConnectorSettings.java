package imperator.adapters.out.aws;

import software.amazon.awssdk.regions.Region;

import java.util.Optional;
import java.util.regex.Pattern;

public final class AwsConnectorSettings {
    private static final Pattern ACCOUNT_ID = Pattern.compile("[0-9]{12}");

    private final boolean enabled;
    private final String expectedAccountId;
    private final String region;

    public AwsConnectorSettings(boolean enabled, String expectedAccountId, String region) {
        this.enabled = enabled;
        this.expectedAccountId = normalize(expectedAccountId);
        this.region = normalize(region).toLowerCase(java.util.Locale.ROOT);
    }

    boolean enabled() {
        return enabled;
    }

    String expectedAccountId() {
        return expectedAccountId;
    }

    String region() {
        return region;
    }

    Region sdkRegion() {
        return Region.of(region);
    }

    String sourceReference() {
        if (expectedAccountId.length() != 12 || region.isEmpty()) {
            return "";
        }
        return "aws:account:********" + expectedAccountId.substring(8) + ":" + region;
    }

    Optional<String> validationFailure() {
        if (!enabled) {
            return Optional.empty();
        }
        if (!ACCOUNT_ID.matcher(expectedAccountId).matches()) {
            return Optional.of("AWS_CONFIGURATION_INVALID");
        }
        Region candidate = Region.of(region);
        if (!Region.regions().contains(candidate)
                || candidate.isGlobalRegion()
                || candidate.metadata() == null
                || !"aws".equals(candidate.metadata().partition().id())) {
            return Optional.of("AWS_CONFIGURATION_INVALID");
        }
        return Optional.empty();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
