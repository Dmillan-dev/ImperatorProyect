package imperator.adapters.out.postgresql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

final class PostgresLocalTransactions {
    private PostgresLocalTransactions() {
    }

    static void execute(DataSource dataSource, TransactionalWork work) throws SQLException {
        Objects.requireNonNull(dataSource, "Data source is required");
        Objects.requireNonNull(work, "Transactional work is required");

        try (Connection connection = dataSource.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            Throwable failure = null;

            try {
                connection.setAutoCommit(false);
                work.execute(connection);
                connection.commit();
            } catch (SQLException | RuntimeException | Error exception) {
                failure = exception;
                rollback(connection, exception);
                throw exception;
            } finally {
                restoreAutoCommit(connection, previousAutoCommit, failure);
            }
        }
    }

    private static void rollback(Connection connection, Throwable cause) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            cause.addSuppressed(rollbackException);
        }
    }

    private static void restoreAutoCommit(
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
    interface TransactionalWork {
        void execute(Connection connection) throws SQLException;
    }
}
