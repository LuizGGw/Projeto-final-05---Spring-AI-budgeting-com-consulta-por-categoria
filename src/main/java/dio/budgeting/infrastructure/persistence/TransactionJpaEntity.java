package dio.budgeting.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionId;
import dio.budgeting.domain.TransactionType;

/**
 * Persistence model. Kept separate from {@link Transaction} on purpose:
 * the domain class must not depend on JPA annotations/framework details.
 */
@Entity
@Table(name = "transactions")
public class TransactionJpaEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String category;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    protected TransactionJpaEntity() {
        // required by JPA
    }

    public TransactionJpaEntity(UUID id, TransactionType type, BigDecimal amount,
                                 String category, String description, Instant createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static TransactionJpaEntity fromDomain(Transaction transaction) {
        return new TransactionJpaEntity(
                transaction.id().value(),
                transaction.type(),
                transaction.amount(),
                transaction.category(),
                transaction.description(),
                transaction.createdAt()
        );
    }

    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(id),
                type,
                amount,
                category,
                description,
                createdAt
        );
    }
}
