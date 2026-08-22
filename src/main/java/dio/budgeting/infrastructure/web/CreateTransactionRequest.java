package dio.budgeting.infrastructure.web;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import dio.budgeting.domain.TransactionType;

public record CreateTransactionRequest(
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount,
        String category,
        String description
) {
}
