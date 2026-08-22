package dio.budgeting.application.dto;

import java.math.BigDecimal;
import dio.budgeting.domain.TransactionType;

/**
 * Input for {@code CreateTransactionUseCase}.
 * Used both by the REST controller and by the AI tool-calling layer,
 * which keeps a single entry point for the "create transaction" business rule.
 */
public record CreateTransactionCommand(
        TransactionType type,
        BigDecimal amount,
        String category,
        String description
) {
}
