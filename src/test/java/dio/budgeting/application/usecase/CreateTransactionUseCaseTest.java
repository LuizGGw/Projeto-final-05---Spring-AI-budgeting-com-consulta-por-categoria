package dio.budgeting.application.usecase;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.TransactionView;
import dio.budgeting.domain.TransactionType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateTransactionUseCaseTest {

    private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    private final CreateTransactionUseCase createTransactionUseCase = new CreateTransactionUseCase(repository);
    private final GetBalanceUseCase getBalanceUseCase = new GetBalanceUseCase(repository);

    @Test
    void shouldCreateTransactionAndReturnItsView() {
        TransactionView view = createTransactionUseCase.execute(
                new CreateTransactionCommand(TransactionType.INCOME, new BigDecimal("100.00"), "salary", "Monthly pay")
        );

        assertNotNull(view.id());
        assertEquals(TransactionType.INCOME, view.type());
        assertEquals(new BigDecimal("100.00"), view.amount());
        assertEquals("SALARY", view.category());
    }

    @Test
    void balanceShouldSumIncomeAndSubtractExpenses() {
        createTransactionUseCase.execute(new CreateTransactionCommand(TransactionType.INCOME, new BigDecimal("200.00"), "salary", "Pay"));
        createTransactionUseCase.execute(new CreateTransactionCommand(TransactionType.EXPENSE, new BigDecimal("50.00"), "food", "Lunch"));

        BigDecimal balance = getBalanceUseCase.execute();

        assertEquals(new BigDecimal("150.00"), balance);
    }
}
