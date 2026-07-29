package imperator.adapters.out.postgresql;

import imperator.ports.out.TransactionRunner;

import java.util.Objects;
import java.util.function.Supplier;

public final class PostgresTransactionRunner implements TransactionRunner {
    private final PostgresConnectionProvider connectionProvider;

    public PostgresTransactionRunner(PostgresConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "Connection provider is required");
    }

    @Override
    public <T> T execute(Supplier<T> operation) {
        return connectionProvider.executeInTransaction(operation);
    }
}
