package dio.budgeting.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionId;
import dio.budgeting.domain.TransactionRepository;

/**
 * Infrastructure adapter: implements the domain repository contract
 * on top of Spring Data JPA, translating between {@link Transaction}
 * (domain) and {@link TransactionJpaEntity} (persistence model).
 */
@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final SpringDataTransactionRepository jpaRepository;

    public JpaTransactionRepository(SpringDataTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity saved = jpaRepository.save(TransactionJpaEntity.fromDomain(transaction));
        return saved.toDomain();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return jpaRepository.findById(id.value()).map(TransactionJpaEntity::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(TransactionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByCategory(String category) {
        return jpaRepository.findByCategory(category)
                .stream()
                .map(TransactionJpaEntity::toDomain)
                .toList();
    }
}
