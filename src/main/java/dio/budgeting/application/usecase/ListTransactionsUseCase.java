package dio.budgeting.application.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import dio.budgeting.application.dto.TransactionView;
import dio.budgeting.domain.TransactionRepository;

/**
 * Use case: list all recorded transactions.
 */
@Component
public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<TransactionView> execute() {
        return repository.findAll()
                .stream()
                .map(TransactionView::from)
                .toList();
    }
}
