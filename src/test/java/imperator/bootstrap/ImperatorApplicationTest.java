package imperator.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import testsupport.security.JwtTestFixture;

class ImperatorApplicationTest {

    @Test
    void startsExecutableWebRuntimeWithoutExternalInfrastructure()
            throws IOException, InterruptedException {
        try (JwtTestFixture jwt = new JwtTestFixture()) {
            SpringApplication application = jwt.application();

            try (ConfigurableApplicationContext context = application.run(jwt.arguments(
                    "--server.address=127.0.0.1",
                    "--server.port=0",
                    "--spring.main.banner-mode=off",
                    "--debug=false",
                    "--logging.level.root=OFF"))) {
            assertTrue(context instanceof ServletWebServerApplicationContext);

            ServletWebServerApplicationContext webContext =
                    (ServletWebServerApplicationContext) context;
            assertNotNull(webContext.getWebServer());

            int port = webContext.getWebServer().getPort();
            assertTrue(port > 0);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

                try (HttpClient client = HttpClient.newHttpClient()) {
                    HttpResponse<Void> response =
                            client.send(request, HttpResponse.BodyHandlers.discarding());
                    assertEquals(404, response.statusCode());
                }
            }
        }
    }
}
