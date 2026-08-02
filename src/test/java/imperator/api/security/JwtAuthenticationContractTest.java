package imperator.api.security;

import imperator.api.errors.CorrelationIdFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import testsupport.security.JwtTestFixture;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtAuthenticationContractTest {
    private static final String DECISION_ID = "10000000-0000-4000-8000-000000000001";
    private static final String RECOMMENDATION_ID = "20000000-0000-4000-8000-000000000002";
    private static final List<Route> ROUTES = List.of(
            new Route("POST", "/api/v1/evidence/import"),
            new Route("GET", "/api/v1/decisions"),
            new Route("GET", "/api/v1/decisions/" + DECISION_ID),
            new Route("GET", "/api/v1/decisions/" + DECISION_ID + "/timeline"),
            new Route("GET", "/api/v1/decisions/" + DECISION_ID + "/evidence"),
            new Route("GET", "/api/v1/decisions/" + DECISION_ID + "/roi"),
            new Route("GET", "/api/v1/recommendations/" + RECOMMENDATION_ID),
            new Route("GET", "/api/v1/decisions/" + DECISION_ID + "/ledger"),
            new Route("POST", "/api/v1/decisions/" + DECISION_ID + "/ledger/approve"),
            new Route("POST", "/api/v1/decisions/" + DECISION_ID + "/ledger/reject"),
            new Route("POST", "/api/v1/decisions/" + DECISION_ID + "/ledger/defer"),
            new Route("POST", "/api/v1/decisions/" + DECISION_ID + "/ledger/mark-implemented"),
            new Route("POST", "/api/v1/decisions/" + DECISION_ID + "/ledger/validate-result"),
            new Route("GET", "/api/v1/business-value?decisionId=" + DECISION_ID),
            new Route("GET", "/api/v1/ledger")
    );
    private static final JsonMapper JSON = JsonMapper.shared();
    private static final JwtTestFixture JWT = new JwtTestFixture();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static int port;

    @BeforeAll
    static void startRuntime() {
        SpringApplication application = JWT.application();
        context = application.run(JWT.arguments(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF"
        ));
        port = ((ServletWebServerApplicationContext) context).getWebServer().getPort();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopRuntime() {
        if (client != null) {
            client.close();
        }
        if (context != null) {
            context.close();
        }
        JWT.close();
    }

    @Test
    void protectsAllFifteenD086RoutesAndUnknownApiRoutes() throws IOException, InterruptedException {
        for (Route route : ROUTES) {
            assertAuthenticationError(send(route.method(), route.path(), null, null),
                    "AUTHENTICATION_REQUIRED", "Authentication required");
        }

        assertAuthenticationError(send("GET", "/api/v1/unknown", null, null),
                "AUTHENTICATION_REQUIRED", "Authentication required");

        HttpResponse<String> authenticatedUnknown = send(
                "GET", "/api/v1/unknown", JWT.validToken(), null
        );
        assertEquals(404, authenticatedUnknown.statusCode());
    }

    @Test
    void acceptsEveryFrozenRoleAndPreservesExistingRouteBehavior()
            throws IOException, InterruptedException {
        for (String role : Set.of("ADMIN", "PLATFORM_ENGINEER", "FINANCE", "AUDITOR")) {
            String token = JWT.validToken(JwtTestFixture.DEFAULT_ACTOR_ID, role);
            HttpResponse<String> response = send("GET", "/api/v1/decisions", token, null);
            assertEquals(503, response.statusCode(), role);
            assertFalse(response.headers().allValues("Set-Cookie").stream()
                    .anyMatch(value -> value.contains("JSESSIONID")));
        }
    }

    @Test
    void rejectsInvalidSignatureIssuerAudienceTimeAndClaims()
            throws IOException, InterruptedException {
        Instant now = Instant.now();
        List<String> invalidTokens = List.of(
                JWT.invalidSignatureToken(),
                token("RS256", JwtTestFixture.KEY_ID, "https://wrong.test", JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), "ADMIN"),
                token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, "wrong-audience",
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), "ADMIN"),
                token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.minusSeconds(120), now.minusSeconds(300), "ADMIN"),
                token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.plusSeconds(120), "ADMIN"),
                token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        "not-a-uuid", now.plusSeconds(300), now.minusSeconds(5), "ADMIN"),
                token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString().toUpperCase(), now.plusSeconds(300),
                        now.minusSeconds(5), "ADMIN"),
                token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), "admin"),
                token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), null),
                JWT.token("RS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), null, now.minusSeconds(5).getEpochSecond(), "ADMIN")
        );

        for (String invalidToken : invalidTokens) {
            assertAuthenticationError(send("GET", "/api/v1/decisions", invalidToken, null),
                    "INVALID_TOKEN", "Invalid authentication token");
        }
    }

    @Test
    void rejectsMissingUnknownOrUnsupportedJoseHeaders()
            throws IOException, InterruptedException {
        Instant now = Instant.now();
        List<String> invalidTokens = List.of(
                token("RS256", null, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), "ADMIN"),
                token("RS256", "unknown-key", JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), "ADMIN"),
                token("none", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), "ADMIN"),
                token("HS256", JwtTestFixture.KEY_ID, JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE,
                        JwtTestFixture.DEFAULT_ACTOR_ID.toString(), now.plusSeconds(300), now.minusSeconds(5), "ADMIN")
        );

        for (String invalidToken : invalidTokens) {
            assertAuthenticationError(send("GET", "/api/v1/decisions", invalidToken, null),
                    "INVALID_TOKEN", "Invalid authentication token");
        }
    }

    @Test
    void acceptsOnlyOneAuthorizationBearerHeaderAndIgnoresTrustedActorHeaders()
            throws IOException, InterruptedException {
        HttpRequest trustedHeadersOnly = request("GET", "/api/v1/decisions", null, null)
                .header("X-Imperator-Actor-ID", UUID.randomUUID().toString())
                .header("X-Imperator-Actor-Role", "ADMIN")
                .build();
        assertAuthenticationError(send(trustedHeadersOnly),
                "AUTHENTICATION_REQUIRED", "Authentication required");

        HttpRequest multipleAuthorization = request("GET", "/api/v1/decisions", null, null)
                .header("Authorization", JWT.authorizationHeader())
                .header("Authorization", JWT.authorizationHeader())
                .build();
        assertAuthenticationError(send(multipleAuthorization),
                "INVALID_TOKEN", "Invalid authentication token");

        HttpRequest malformedBearer = request("GET", "/api/v1/decisions", null, null)
                .header("Authorization", "Bearer")
                .build();
        assertAuthenticationError(send(malformedBearer),
                "INVALID_TOKEN", "Invalid authentication token");

        assertAuthenticationError(send(
                        "GET", "/api/v1/decisions?access_token=" + JWT.validToken(), null, null),
                "AUTHENTICATION_REQUIRED", "Authentication required");

        HttpRequest cookieCredential = request("GET", "/api/v1/decisions", null, null)
                .header("Cookie", "access_token=" + JWT.validToken())
                .build();
        assertAuthenticationError(send(cookieCredential),
                "AUTHENTICATION_REQUIRED", "Authentication required");

        HttpRequest trustedHeadersWithJwt = request("GET", "/api/v1/decisions", JWT.validToken(), null)
                .header("X-Imperator-Actor-ID", UUID.randomUUID().toString())
                .header("X-Imperator-Actor-Role", "INVALID")
                .build();
        assertEquals(503, send(trustedHeadersWithJwt).statusCode());
    }

    @Test
    void productionDecoderRequiresHttpsJwksAndExactlyRs256() {
        JwtResourceServerConfiguration configuration = new JwtResourceServerConfiguration();
        OAuth2TokenValidator<Jwt> validator = configuration.jwtTokenValidator(
                JwtTestFixture.ISSUER, JwtTestFixture.AUDIENCE
        );

        assertThrows(IllegalStateException.class, () ->
                configuration.jwtDecoder("http://issuer.test/jwks", "RS256", validator));
        assertThrows(IllegalStateException.class, () ->
                configuration.jwtDecoder("https://issuer.test/jwks", "RS512", validator));
    }

    @Test
    void returnsTheFrozenJsonEnvelopeForEveryAcceptAndCorrelationId()
            throws IOException, InterruptedException {
        String correlationId = UUID.randomUUID().toString().toUpperCase();
        for (String accept : Set.of(
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.TEXT_HTML_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.ALL_VALUE
        )) {
            HttpResponse<String> response = send("GET", "/api/v1/decisions", null, accept, correlationId);
            assertAuthenticationError(response, "AUTHENTICATION_REQUIRED", "Authentication required");
            assertEquals(correlationId.toLowerCase(), correlationId(response));
        }
    }

    private static String token(
            String algorithm,
            String keyId,
            String issuer,
            String audience,
            String subject,
            Instant expiresAt,
            Instant notBefore,
            String role
    ) {
        return JWT.token(
                algorithm, keyId, issuer, audience, subject,
                expiresAt == null ? null : expiresAt.getEpochSecond(),
                notBefore == null ? null : notBefore.getEpochSecond(), role
        );
    }

    private static HttpResponse<String> send(
            String method,
            String path,
            String token,
            String accept
    ) throws IOException, InterruptedException {
        return send(request(method, path, token, accept).build());
    }

    private static HttpResponse<String> send(
            String method,
            String path,
            String token,
            String accept,
            String correlationId
    ) throws IOException, InterruptedException {
        return send(request(method, path, token, accept)
                .header(CorrelationIdFilter.HEADER_NAME, correlationId)
                .build());
    }

    private static HttpRequest.Builder request(
            String method,
            String path,
            String token,
            String accept
    ) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", accept == null ? MediaType.APPLICATION_JSON_VALUE : accept);
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder.method(method, HttpRequest.BodyPublishers.noBody());
    }

    private static HttpResponse<String> send(HttpRequest request)
            throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void assertAuthenticationError(
            HttpResponse<String> response,
            String code,
            String message
    ) throws IOException {
        assertEquals(401, response.statusCode());
        assertEquals("Bearer", response.headers().firstValue("WWW-Authenticate").orElseThrow());
        assertTrue(response.headers().firstValue("Content-Type").orElse("")
                .startsWith(MediaType.APPLICATION_JSON_VALUE));
        JsonNode body = JSON.readTree(response.body());
        assertEquals(
                Set.of("code", "message", "correlationId", "details"),
                body.properties().stream().map(entry -> entry.getKey())
                        .collect(Collectors.toUnmodifiableSet())
        );
        assertEquals(code, body.get("code").asString());
        assertEquals(message, body.get("message").asString());
        assertTrue(body.get("details").isObject());
        assertEquals(0, body.get("details").size());
        assertEquals(correlationId(response), body.get("correlationId").asString());
        assertFalse(response.body().contains("Bearer "));
    }

    private static String correlationId(HttpResponse<String> response) {
        return response.headers().firstValue(CorrelationIdFilter.HEADER_NAME).orElseThrow();
    }

    private record Route(String method, String path) {
    }
}
