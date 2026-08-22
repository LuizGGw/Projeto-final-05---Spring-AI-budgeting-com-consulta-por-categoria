package dio.budgeting.application.usecase;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;

/**
 * Use case added as this project's improvement (see README section
 * "Melhoria Implementada"): total spent or received in a given category,
 * e.g. "quanto eu gastei com alimentação".
 */
@Component
public class GetTotalByCategoryUseCase {

    private final TransactionRepository repository;

    public GetTotalByCategoryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public BigDecimal execute(String category, TransactionType type) {
        String normalized = category == null ? "GENERAL" : category.trim().toUpperCase();

        return repository.findByCategory(normalized)
                .stream()
                .filter(t -> type == null || t.type() == type)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
