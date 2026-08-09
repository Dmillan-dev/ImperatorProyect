package imperator.bootstrap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AwsRuntimePropertiesTest {

    @Test
    void masksTheAccountAndDoesNotExposeCredentialsAsConfiguration() {
        AwsRuntimeProperties properties = new AwsRuntimeProperties(
                true,
                "123456789012",
                "eu-west-1"
        );

        assertFalse(properties.toString().contains("123456789012"));
        assertTrue(properties.toString().contains("********9012"));
        assertTrue(properties.toString().contains("region=eu-west-1"));
        assertTrue(AwsRuntimeProperties.class.isRecord());
        assertEquals(3, AwsRuntimeProperties.class.getRecordComponents().length);
    }
}
