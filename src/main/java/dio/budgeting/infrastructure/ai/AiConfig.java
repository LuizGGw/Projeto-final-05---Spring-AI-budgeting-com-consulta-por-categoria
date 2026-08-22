package dio.budgeting.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private static final String SYSTEM_PROMPT = """
            You are a voice-driven budgeting assistant.
            Interpret the user's financial command and call exactly one tool
            to fulfill it: register a transaction, list transactions, get the
            balance, or get a total by category. Reply with a short, clear
            confirmation sentence based on the tool result, suitable to be
            read aloud by a text-to-speech engine.
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, BudgetingTools budgetingTools) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(budgetingTools)
                .build();
    }
}
