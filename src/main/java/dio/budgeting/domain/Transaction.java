package dio.budgeting.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Financial transaction aggregate.
 *
 * Modeled as a {@code class} (not a record) because it has identity
 * ({@link TransactionId}) and represents a business concept whose
 * invariants are enforced here, in the domain, and not in the
 * persistence or web layers.
 */
public class Transaction {

    private final TransactionId id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String category;
    private final String description;
    private final Instant createdAt;

    public Transaction(TransactionId id,
                        TransactionType type,
                        BigDecimal amount,
                        String category,
                        String description,
                        Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.amount = validateAmount(amount);
        this.category = normalizeCategory(category);
        this.description = description == null ? "" : description;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static Transaction create(TransactionType type, BigDecimal amount, String category, String description) {
        return new Transaction(TransactionId.newId(), type, amount, category, description, Instant.now());
    }

    private static BigDecimal validateAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
        return amount;
    }

    private static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "GENERAL";
        }
        return category.trim().toUpperCase();
    }

    /**
     * Signed value of this transaction: positive for INCOME, negative for EXPENSE.
     * Used to compute balances without leaking the sign convention outside the domain.
     */
    public BigDecimal signedAmount() {
        return type == TransactionType.EXPENSE ? amount.negate() : amount;
    }

    public TransactionId id() {
        return id;
    }

    public TransactionType type() {
        return type;
    }

    public BigDecimal amount() {
        return amount;
    }

    public String category() {
        return category;
    }

    public String description() {
        return description;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
