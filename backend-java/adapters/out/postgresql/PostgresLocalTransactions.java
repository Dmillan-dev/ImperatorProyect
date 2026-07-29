package imperator.adapters.out.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

final class PostgresLocalTransactions {
    private PostgresLocalTransactions() {
    }

    static void execute(PostgresConnectionProvider connectionProvider, TransactionalWork work) throws SQLException {
        Objects.requireNonNull(connectionProvider, "Connection provider is required");
        Objects.requireNonNull(work, "Transactional work is required");

        connectionProvider.executeJdbcWork(work::execute);
    }

    @FunctionalInterface
    interface TransactionalWork {
        void execute(Connection connection) throws SQLException;
    }
}
