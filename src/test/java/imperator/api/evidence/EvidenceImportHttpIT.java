package imperator.api.evidence;

import imperator.bootstrap.ImperatorApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvidenceImportHttpIT {
    private static final String FIXTURE = "/evidence/drc-aoa-001-sample.jsonl";
    private static final List<UUID> EVIDENCE_IDS = List.of(
            UUID.fromString("00000000-0000-4000-8000-000000000101"),
            UUID.fromString("00000000-0000-4000-8000-000000000102"),
            UUID.fromString("00000000-0000-4000-8000-000000000103")
    );
    private static final JsonMapper JSON = JsonMapper.shared();

    private static ConfigurableApplicationContext context;
    private static HttpClient client;
    private static String dbUrl;
    private static String adminUser;
    private static String adminPassword;
    private static String databaseName;
    private static String appUser;
    private static String appPassword;
    private static String fixture;
    private static int port;

    @BeforeAll
    static void startRuntime() throws IOException, SQLException {
        dbUrl = requiredProperty("imperator.it.dbUrl");
        adminUser = requiredProperty("imperator.it.adminUser");
        adminPassword = requiredProperty("imperator.it.adminPassword");
        databaseName = requiredProperty("imperator.it.database");
        appUser = requiredProperty("imperator.it.appUser");
        appPassword = requiredProperty("imperator.it.appPassword");
        requireSafeTestIdentifier(databaseName);
        requireSafeTestIdentifier(appUser);
        fixture = readFixture();
        deleteFixtureEvidence();
        provisionApplicationRole();

        SpringApplication application = new SpringApplication(ImperatorApplication.class);
        context = application.run(
                "--server.address=127.0.0.1",
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF",
                "--imperator.postgresql.enabled=true",
                "--imperator.postgresql.url=" + dbUrl,
                "--imperator.postgresql.username=" + appUser,
                "--imperator.postgresql.password=" + appPassword
        );

        ServletWebServerApplicationContext webContext =
                (ServletWebServerApplicationContext) context;
        port = webContext.getWebServer().getPort();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopRuntimeAndCleanEvidence() throws SQLException {
        if (client != null) {
            client.close();
        }
        if (context != null) {
            context.close();
        }
        if (dbUrl != null) {
            try {
                deleteFixtureEvidence();
            } finally {
                removeApplicationRole();
            }
        }
    }

    @Test
    void importsTheRealJsonlFixtureThroughSpringApplicationAndPostgresql()
            throws IOException, InterruptedException, SQLException {
        HttpResponse<String> first = postFixture();

        assertEquals(200, first.statusCode());
        assertTrue(first.headers()
                .firstValue("Content-Type")
                .orElse("")
                .startsWith("application/json"));
        assertCanonicalCorrelationHeader(first);
        JsonNode firstBody = JSON.readTree(first.body());
        assertEquals("ACCEPTED", firstBody.get("status").asString());
        assertEquals(3, firstBody.get("accepted").asInt());
        assertEquals(0, firstBody.get("rejected").asInt());
        assertEquals(3, persistedFixtureCount());
        assertPersistedEvidenceContract();

        HttpResponse<String> repeated = postFixture();

        assertEquals(200, repeated.statusCode());
        JsonNode repeatedBody = JSON.readTree(repeated.body());
        assertEquals("REJECTED", repeatedBody.get("status").asString());
        assertEquals(0, repeatedBody.get("accepted").asInt());
        assertEquals(3, repeatedBody.get("rejected").asInt());
        for (JsonNode item : repeatedBody.get("items")) {
            assertEquals("DUPLICATE", item.get("reason").asString());
        }
        assertEquals(3, persistedFixtureCount());
    }

    private static HttpResponse<String> postFixture() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/api/v1/evidence/import"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", EvidenceController.NDJSON_MEDIA_TYPE)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(fixture))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static int persistedFixtureCount() throws SQLException {
        try (
                Connection connection = adminConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM evidence WHERE id = ANY (?)"
                )
        ) {
            java.sql.Array ids = connection.createArrayOf("uuid", EVIDENCE_IDS.toArray());
            statement.setArray(1, ids);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            } finally {
                ids.free();
            }
        }
    }

    private static void assertPersistedEvidenceContract() throws SQLException {
        try (
                Connection connection = adminConnection();
                PreparedStatement statement = connection.prepareStatement(
                        """
                        SELECT source, source_type, correlation_key, raw_payload_mode, metadata
                        FROM evidence
                        WHERE id = ?
                        """
                )
        ) {
            statement.setObject(1, EVIDENCE_IDS.get(0));
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals("AWS", resultSet.getString("source"));
                assertEquals("cloud_cost", resultSet.getString("source_type"));
                assertEquals("DRC-AOA-001", resultSet.getString("correlation_key"));
                assertEquals("not_stored", resultSet.getString("raw_payload_mode"));
                String metadata = resultSet.getString("metadata");
                assertTrue(metadata.contains("E-AWS-001"));
                assertTrue(metadata.contains("ingested_at"));
                assertTrue(!metadata.contains("raw_prompt"));
            }
        }
    }

    private static void deleteFixtureEvidence() throws SQLException {
        try (
                Connection connection = adminConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM evidence WHERE id = ANY (?)"
                )
        ) {
            java.sql.Array ids = connection.createArrayOf("uuid", EVIDENCE_IDS.toArray());
            statement.setArray(1, ids);
            try {
                statement.executeUpdate();
            } finally {
                ids.free();
            }
        }
    }

    private static void provisionApplicationRole() throws SQLException {
        boolean roleExists;
        try (
                Connection connection = adminConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = ?)"
                )
        ) {
            statement.setString(1, appUser);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                roleExists = resultSet.getBoolean(1);
            }
        }

        String roleCommand = roleExists ? "ALTER ROLE " : "CREATE ROLE ";
        executeAdmin(
                roleCommand + quoteIdentifier(appUser)
                        + " LOGIN PASSWORD " + quoteLiteral(appPassword)
        );
        executeAdmin("REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM "
                + quoteIdentifier(appUser));
        executeAdmin("GRANT CONNECT ON DATABASE " + quoteIdentifier(databaseName)
                + " TO " + quoteIdentifier(appUser));
        executeAdmin("GRANT USAGE ON SCHEMA public TO " + quoteIdentifier(appUser));
        executeAdmin("GRANT SELECT, INSERT ON TABLE evidence TO " + quoteIdentifier(appUser));
    }

    private static void removeApplicationRole() throws SQLException {
        if (appUser == null) {
            return;
        }
        executeAdmin("DROP OWNED BY " + quoteIdentifier(appUser));
        executeAdmin("DROP ROLE IF EXISTS " + quoteIdentifier(appUser));
    }

    private static void executeAdmin(String sql) throws SQLException {
        try (
                Connection connection = adminConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute(sql);
        }
    }

    private static Connection adminConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, adminUser, adminPassword);
    }

    private static String readFixture() throws IOException {
        try (InputStream input = EvidenceImportHttpIT.class.getResourceAsStream(FIXTURE)) {
            return new String(
                    Objects.requireNonNull(input, "Evidence fixture is required").readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

    private static String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required integration property is missing: " + name);
        }
        return value.trim();
    }

    private static void requireSafeTestIdentifier(String value) {
        if (!value.startsWith("imperator_it_") || !value.matches("[a-z][a-z0-9_]{2,62}")) {
            throw new IllegalStateException("Unsafe integration-test identifier: " + value);
        }
    }

    private static String quoteIdentifier(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static String quoteLiteral(String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    private static void assertCanonicalCorrelationHeader(HttpResponse<String> response) {
        String correlationId = response.headers()
                .firstValue("X-Correlation-ID")
                .orElseThrow(() -> new AssertionError("Correlation header is missing"));
        assertEquals(correlationId, UUID.fromString(correlationId).toString());
    }
}
