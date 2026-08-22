package dio.budgeting.application.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionId;
import dio.budgeting.domain.TransactionRepository;

/**
 * Simple in-memory implementation of {@link TransactionRepository},
 * used to unit test use cases without a real database.
 */
class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return transactions.stream().filter(t -> t.id().equals(id)).findFirst();
    }

    @Override
    public List<Transaction> findAll() {
        return List.copyOf(transactions);
    }

    @Override
    public List<Transaction> findByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.category().equalsIgnoreCase(category))
                .toList();
    }
}
