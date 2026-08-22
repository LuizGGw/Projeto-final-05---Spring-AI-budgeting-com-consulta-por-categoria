package dio.budgeting.application.usecase;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;

/**
 * Use case: compute the current balance (sum of incomes minus expenses).
 */
@Component
public class GetBalanceUseCase {

    private final TransactionRepository repository;

    public GetBalanceUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public BigDecimal execute() {
        return repository.findAll()
                .stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
