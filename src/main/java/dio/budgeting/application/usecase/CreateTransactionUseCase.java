package dio.budgeting.application.usecase;

import org.springframework.stereotype.Component;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.TransactionView;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;

/**
 * Use case: create (persist) a financial transaction.
 * Called both by the REST controller and by the AI tool-calling layer,
 * so the business rule for "creating a transaction" lives in a single place.
 */
@Component
public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public TransactionView execute(CreateTransactionCommand command) {
        Transaction transaction = Transaction.create(
                command.type(),
                command.amount(),
                command.category(),
                command.description()
        );

        Transaction saved = repository.save(transaction);
        return TransactionView.from(saved);
    }
}
