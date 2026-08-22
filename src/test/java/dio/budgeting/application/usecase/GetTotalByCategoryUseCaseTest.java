package dio.budgeting.application.usecase;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.domain.TransactionType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetTotalByCategoryUseCaseTest {

    private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    private final CreateTransactionUseCase createTransactionUseCase = new CreateTransactionUseCase(repository);
    private final GetTotalByCategoryUseCase getTotalByCategoryUseCase = new GetTotalByCategoryUseCase(repository);

    @Test
    void shouldSumOnlyTransactionsFromTheGivenCategory() {
        createTransactionUseCase.execute(new CreateTransactionCommand(TransactionType.EXPENSE, new BigDecimal("30.00"), "food", "Lunch"));
        createTransactionUseCase.execute(new CreateTransactionCommand(TransactionType.EXPENSE, new BigDecimal("20.00"), "food", "Dinner"));
        createTransactionUseCase.execute(new CreateTransactionCommand(TransactionType.EXPENSE, new BigDecimal("15.00"), "transport", "Bus"));

        BigDecimal total = getTotalByCategoryUseCase.execute("food", TransactionType.EXPENSE);

        assertEquals(new BigDecimal("50.00"), total);
    }

    @Test
    void shouldReturnZeroWhenCategoryHasNoTransactions() {
        BigDecimal total = getTotalByCategoryUseCase.execute("travel", null);

        assertEquals(BigDecimal.ZERO, total);
    }
}
