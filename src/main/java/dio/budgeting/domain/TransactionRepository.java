package dio.budgeting.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository contract owned by the business/domain side.
 * Technology-specific implementations (JPA, in-memory, etc.) live in the
 * infrastructure layer and implement this interface.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    List<Transaction> findAll();

    List<Transaction> findByCategory(String category);
}
