package dio.budgeting.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionType;

/**
 * Read-only projection of a {@link Transaction}, safe to expose
 * through REST responses or to the AI model as tool output.
 */
public record TransactionView(
        String id,
        TransactionType type,
        BigDecimal amount,
        String category,
        String description,
        Instant createdAt
) {
    public static TransactionView from(Transaction transaction) {
        return new TransactionView(
                transaction.id().toString(),
                transaction.type(),
                transaction.amount(),
                transaction.category(),
                transaction.description(),
                transaction.createdAt()
        );
    }
}
