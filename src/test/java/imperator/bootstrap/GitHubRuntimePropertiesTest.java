package imperator.bootstrap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubRuntimePropertiesTest {

    @Test
    void redactsTheConnectorTokenFromItsStringRepresentation() {
        GitHubRuntimeProperties properties = new GitHubRuntimeProperties(
                true,
                "github_pat_secret",
                "acme",
                "imperator-demo"
        );

        assertFalse(properties.toString().contains("github_pat_secret"));
        assertTrue(properties.toString().contains("token=<redacted>"));
    }
}
