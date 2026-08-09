package imperator.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "imperator.aws")
public record AwsRuntimeProperties(
        boolean enabled,
        String expectedAccountId,
        String region
) {
    @Override
    public String toString() {
        return "AwsRuntimeProperties[enabled=" + enabled
                + ", expectedAccountId=" + maskedAccount(expectedAccountId)
                + ", region=" + region + "]";
    }

    private static String maskedAccount(String accountId) {
        if (accountId == null || accountId.length() < 4) {
            return "<unset>";
        }
        return "********" + accountId.substring(accountId.length() - 4);
    }
}
