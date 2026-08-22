package dio.budgeting.infrastructure.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.TransactionView;
import dio.budgeting.application.usecase.CreateTransactionUseCase;
import dio.budgeting.application.usecase.GetBalanceUseCase;
import dio.budgeting.application.usecase.GetTotalByCategoryUseCase;
import dio.budgeting.application.usecase.ListTransactionsUseCase;
import dio.budgeting.domain.TransactionType;

/**
 * Plain REST endpoints, useful to test the same use cases used by the
 * AI tool-calling layer without needing an audio file.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final GetTotalByCategoryUseCase getTotalByCategoryUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                  ListTransactionsUseCase listTransactionsUseCase,
                                  GetBalanceUseCase getBalanceUseCase,
                                  GetTotalByCategoryUseCase getTotalByCategoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getBalanceUseCase = getBalanceUseCase;
        this.getTotalByCategoryUseCase = getTotalByCategoryUseCase;
    }

    @PostMapping
    public TransactionView create(@Valid @RequestBody CreateTransactionRequest request) {
        return createTransactionUseCase.execute(new CreateTransactionCommand(
                request.type(), request.amount(), request.category(), request.description()
        ));
    }

    @GetMapping
    public List<TransactionView> list() {
        return listTransactionsUseCase.execute();
    }

    @GetMapping("/balance")
    public Map<String, BigDecimal> balance() {
        return Map.of("balance", getBalanceUseCase.execute());
    }

    @GetMapping("/category/{category}")
    public Map<String, BigDecimal> totalByCategory(@PathVariable String category,
                                                     @RequestParam(required = false) TransactionType type) {
        return Map.of("total", getTotalByCategoryUseCase.execute(category, type));
    }
}
