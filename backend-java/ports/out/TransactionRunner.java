package imperator.ports.out;

import java.util.function.Supplier;

public interface TransactionRunner {
    <T> T execute(Supplier<T> operation);
}
