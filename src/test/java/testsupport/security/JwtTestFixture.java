package testsupport.security;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import imperator.bootstrap.ImperatorApplication;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public final class JwtTestFixture implements AutoCloseable {
    public static final String ISSUER = "https://issuer.test/imperator";
    public static final String AUDIENCE = "imperator-api";
    public static final UUID DEFAULT_ACTOR_ID =
            UUID.fromString("a53d5ef0-d890-42da-961f-fef206d72979");
    public static final String KEY_ID = "imperator-test-key-1";

    private final KeyPair signingKey;
    private final KeyPair untrustedKey;
    private final HttpServer jwksServer;
    private final String jwksUri;

    public JwtTestFixture() {
        try {
            signingKey = generateKeyPair();
            untrustedKey = generateKeyPair();
            jwksServer = HttpServer.create(
                    new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0
            );
            jwksServer.createContext("/jwks", this::serveJwks);
            jwksServer.start();
            jwksUri = "http://127.0.0.1:" + jwksServer.getAddress().getPort() + "/jwks";
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start the test JWKS server", exception);
        }
    }

    public SpringApplication application() {
        return new SpringApplication(
                ImperatorApplication.class, JwtTestSecurityConfiguration.class
        );
    }

    public String[] arguments(String... applicationArguments) {
        List<String> arguments = new ArrayList<>(List.of(applicationArguments));
        arguments.add("--spring.security.oauth2.resourceserver.jwt.issuer-uri=" + ISSUER);
        arguments.add("--spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://jwks.test/keys");
        arguments.add("--spring.security.oauth2.resourceserver.jwt.audiences=" + AUDIENCE);
        arguments.add("--spring.security.oauth2.resourceserver.jwt.jws-algorithms=RS256");
        arguments.add("--imperator.test.jwt.jwk-set-uri=" + jwksUri);
        return arguments.toArray(String[]::new);
    }

    public String authorizationHeader() {
        return "Bearer " + validToken();
    }

    public String authorizationHeader(UUID actorId, String role) {
        return "Bearer " + validToken(actorId, role);
    }

    public String validToken() {
        return validToken(DEFAULT_ACTOR_ID, "ADMIN");
    }

    public String validToken(UUID actorId, String role) {
        Instant now = Instant.now();
        return signedToken(
                "RS256", KEY_ID, ISSUER, AUDIENCE, actorId.toString(),
                now.plusSeconds(300).getEpochSecond(), now.minusSeconds(5).getEpochSecond(),
                role, signingKey.getPrivate()
        );
    }

    public String invalidSignatureToken() {
        Instant now = Instant.now();
        return signedToken(
                "RS256", KEY_ID, ISSUER, AUDIENCE, DEFAULT_ACTOR_ID.toString(),
                now.plusSeconds(300).getEpochSecond(), now.minusSeconds(5).getEpochSecond(),
                "ADMIN", untrustedKey.getPrivate()
        );
    }

    public String token(
            String algorithm,
            String keyId,
            String issuer,
            String audience,
            String subject,
            Long expiresAt,
            Long notBefore,
            String role
    ) {
        return signedToken(
                algorithm, keyId, issuer, audience, subject, expiresAt, notBefore, role,
                signingKey.getPrivate()
        );
    }

    @Override
    public void close() {
        jwksServer.stop(0);
    }

    private void serveJwks(HttpExchange exchange) throws IOException {
        byte[] response = jwks().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private String jwks() {
        RSAPublicKey publicKey = (RSAPublicKey) signingKey.getPublic();
        return "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"alg\":\"RS256\","
                + "\"kid\":\"" + KEY_ID + "\",\"n\":\""
                + base64Url(unsigned(publicKey.getModulus().toByteArray()))
                + "\",\"e\":\"" + base64Url(unsigned(publicKey.getPublicExponent().toByteArray()))
                + "\"}]}";
    }

    private String signedToken(
            String algorithm,
            String keyId,
            String issuer,
            String audience,
            String subject,
            Long expiresAt,
            Long notBefore,
            String role,
            PrivateKey privateKey
    ) {
        String header = "{\"alg\":\"" + json(algorithm) + "\",\"typ\":\"JWT\""
                + (keyId == null ? "" : ",\"kid\":\"" + json(keyId) + "\"") + "}";
        StringBuilder payload = new StringBuilder("{\"iss\":\"")
                .append(json(issuer)).append("\",\"aud\":[\"").append(json(audience))
                .append("\"],\"sub\":\"").append(json(subject)).append('"');
        if (expiresAt != null) {
            payload.append(",\"exp\":").append(expiresAt);
        }
        if (notBefore != null) {
            payload.append(",\"nbf\":").append(notBefore);
        }
        if (role != null) {
            payload.append(",\"imperator_role\":\"").append(json(role)).append('"');
        }
        payload.append('}');

        String encodedHeader = base64Url(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64Url(payload.toString().getBytes(StandardCharsets.UTF_8));
        String signingInput = encodedHeader + "." + encodedPayload;
        if ("none".equals(algorithm)) {
            return signingInput + ".";
        }
        if (!"RS256".equals(algorithm)) {
            return signingInput + "." + base64Url("not-an-rs256-signature".getBytes(StandardCharsets.UTF_8));
        }

        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(signingInput.getBytes(StandardCharsets.US_ASCII));
            return signingInput + "." + base64Url(signature.sign());
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign a test JWT", exception);
        }
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to generate a test RSA key", exception);
        }
    }

    private byte[] unsigned(byte[] value) {
        if (value.length > 1 && value[0] == 0) {
            byte[] unsigned = new byte[value.length - 1];
            System.arraycopy(value, 1, unsigned, 0, unsigned.length);
            return unsigned;
        }
        return value;
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private String json(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
