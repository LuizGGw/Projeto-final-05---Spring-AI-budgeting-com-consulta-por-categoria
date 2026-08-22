package dio.budgeting.infrastructure.ai;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.TransactionView;
import dio.budgeting.application.usecase.CreateTransactionUseCase;
import dio.budgeting.application.usecase.GetBalanceUseCase;
import dio.budgeting.application.usecase.GetTotalByCategoryUseCase;
import dio.budgeting.application.usecase.ListTransactionsUseCase;
import dio.budgeting.domain.TransactionType;

/**
 * Tools registered with the {@code ChatClient}. Each method only calls an
 * existing application use case: the AI layer never talks to the domain or
 * to persistence directly, which keeps the same use cases reusable from
 * REST and from voice commands.
 */
@Component
public class BudgetingTools {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final GetTotalByCategoryUseCase getTotalByCategoryUseCase;

    public BudgetingTools(CreateTransactionUseCase createTransactionUseCase,
                           ListTransactionsUseCase listTransactionsUseCase,
                           GetBalanceUseCase getBalanceUseCase,
                           GetTotalByCategoryUseCase getTotalByCategoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getBalanceUseCase = getBalanceUseCase;
        this.getTotalByCategoryUseCase = getTotalByCategoryUseCase;
    }

    @Tool(description = "Registers a new financial transaction (income or expense) with an amount, a category and a short description")
    public TransactionView registerTransaction(
            @ToolParam(description = "INCOME to add money, EXPENSE to remove money") TransactionType type,
            @ToolParam(description = "Positive amount of the transaction") BigDecimal amount,
            @ToolParam(description = "Category such as FOOD, TRANSPORT, SALARY, LEISURE") String category,
            @ToolParam(description = "Short free-text description of the transaction") String description
    ) {
        return createTransactionUseCase.execute(new CreateTransactionCommand(type, amount, category, description));
    }

    @Tool(description = "Lists every financial transaction recorded so far")
    public List<TransactionView> listTransactions() {
        return listTransactionsUseCase.execute();
    }

    @Tool(description = "Returns the current balance: total income minus total expenses")
    public BigDecimal getBalance() {
        return getBalanceUseCase.execute();
    }

    @Tool(description = "Returns the total amount spent or received in a specific category, optionally filtered by INCOME or EXPENSE")
    public BigDecimal getTotalByCategory(
            @ToolParam(description = "Category to sum, e.g. FOOD") String category,
            @ToolParam(description = "Optional: INCOME or EXPENSE, leave null to sum both", required = false) TransactionType type
    ) {
        return getTotalByCategoryUseCase.execute(category, type);
    }
}
