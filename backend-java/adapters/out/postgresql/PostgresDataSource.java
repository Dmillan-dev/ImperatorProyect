package imperator.adapters.out.postgresql;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

public final class PostgresDataSource implements DataSource {
    private static final Logger LOGGER = Logger.getLogger(PostgresDataSource.class.getName());

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public PostgresDataSource(String jdbcUrl, String username, String password) {
        this.jdbcUrl = requireText(jdbcUrl, "JDBC URL");
        this.username = requireText(username, "Database username");
        this.password = Objects.requireNonNull(password, "Database password is required");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public Connection getConnection(String requestedUsername, String requestedPassword) throws SQLException {
        return DriverManager.getConnection(
                jdbcUrl,
                requireText(requestedUsername, "Database username"),
                Objects.requireNonNull(requestedPassword, "Database password is required")
        );
    }

    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter output) {
        DriverManager.setLogWriter(output);
    }

    @Override
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return LOGGER;
    }

    @Override
    public <T> T unwrap(Class<T> type) throws SQLException {
        Objects.requireNonNull(type, "Wrapper type is required");
        if (type.isInstance(this)) {
            return type.cast(this);
        }
        throw new SQLException("PostgresDataSource does not wrap " + type.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> type) {
        return type != null && type.isInstance(this);
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
