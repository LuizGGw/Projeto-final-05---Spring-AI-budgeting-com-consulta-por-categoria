package dio.budgeting.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    List<TransactionJpaEntity> findByCategory(String category);
}
