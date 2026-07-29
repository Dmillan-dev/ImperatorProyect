package imperator.adapters.out.postgresql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Owns JDBC connections for one PostgreSQL adapter composition.
 *
 * <p>The transaction connection is intentionally bound to the current thread so repository
 * calls made through one {@link PostgresTransactionRunner} share the same JDBC transaction.
 * The context is instance-scoped, never static, and is removed in the transaction
 * {@code finally} block. It is a synchronous JDBC adapter mechanism and must not be used to
 * propagate a transaction across threads or asynchronous work.</p>
 */
public final class PostgresConnectionProvider {
    private final DataSource dataSource;
    private final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();

    public PostgresConnectionProvider(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "Data source is required");
    }

    ConnectionLease acquire() throws SQLException {
        Connection activeConnection = transactionConnection.get();
        if (activeConnection != null) {
            return new ConnectionLease(activeConnection, false);
        }
        return new ConnectionLease(dataSource.getConnection(), true);
    }

    <T> T executeInTransaction(Supplier<T> operation) {
        Objects.requireNonNull(operation, "Transactional operation is required");

        try {
            return executeReturningJdbcWork(connection -> operation.get());
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not execute PostgreSQL transaction", exception);
        }
    }

    void executeJdbcWork(JdbcWork work) throws SQLException {
        Objects.requireNonNull(work, "JDBC work is required");
        executeReturningJdbcWork(connection -> {
            work.execute(connection);
            return null;
        });
    }

    private <T> T executeReturningJdbcWork(JdbcReturningWork<T> work) throws SQLException {
        Connection activeConnection = transactionConnection.get();
        if (activeConnection != null) {
            return work.execute(activeConnection);
        }

        try (Connection connection = dataSource.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            Throwable failure = null;

            try {
                connection.setAutoCommit(false);
                transactionConnection.set(connection);
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException | Error exception) {
                failure = exception;
                rollback(connection, exception);
                throw exception;
            } finally {
                transactionConnection.remove();
                restoreAutoCommit(connection, previousAutoCommit, failure);
            }
        }
    }

    private void rollback(Connection connection, Throwable cause) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            cause.addSuppressed(rollbackException);
        }
    }

    private void restoreAutoCommit(
            Connection connection,
            boolean previousAutoCommit,
            Throwable priorFailure
    ) throws SQLException {
        try {
            connection.setAutoCommit(previousAutoCommit);
        } catch (SQLException restoreException) {
            if (priorFailure == null) {
                throw restoreException;
            }
            priorFailure.addSuppressed(restoreException);
        }
    }

    @FunctionalInterface
    interface JdbcWork {
        void execute(Connection connection) throws SQLException;
    }

    @FunctionalInterface
    private interface JdbcReturningWork<T> {
        T execute(Connection connection) throws SQLException;
    }

    static final class ConnectionLease implements AutoCloseable {
        private final Connection connection;
        private final boolean closeConnection;

        private ConnectionLease(Connection connection, boolean closeConnection) {
            this.connection = Objects.requireNonNull(connection, "Connection is required");
            this.closeConnection = closeConnection;
        }

        Connection connection() {
            return connection;
        }

        @Override
        public void close() throws SQLException {
            if (closeConnection) {
                connection.close();
            }
        }
    }
}
