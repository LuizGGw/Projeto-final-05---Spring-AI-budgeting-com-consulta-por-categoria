# Budgeting AI — Projeto Final 05: Spring AI (DIO)

API de orçamento (budgeting) que usa **Spring AI** para interpretar comandos de voz e executar
operações financeiras reais (registrar transações, consultar saldo e consultar gastos por
categoria), seguindo a arquitetura em camadas (`domain` / `application` / `infrastructure`)
usada em todo o Learning Track de Spring Boot da DIO.

## O que o projeto faz

Fluxo principal:

1. O cliente envia um arquivo de áudio com um comando de voz (`POST /api/assistant/voice`).
2. O áudio é transcrito em texto usando o modelo de *speech-to-text* (Whisper, via Spring AI).
3. Um `ChatClient` interpreta a intenção do texto e escolhe qual *tool* (função real da
   aplicação) deve ser chamada.
4. A *tool* correspondente executa um **use case** de verdade: criar transação, listar
   transações, obter saldo ou obter total por categoria.
5. A resposta final em texto é convertida em áudio (*text-to-speech*) e devolvida ao cliente.

Também existe um endpoint texto-a-texto (`POST /api/assistant/text`) e endpoints REST simples
(`/api/transactions/**`) que usam exatamente os mesmos use cases, sem precisar de áudio —
úteis para testar e depurar o fluxo de negócio isoladamente da parte de IA.

## Como executar a aplicação

Pré-requisitos: Java 17+ e uma chave de API da OpenAI.

```bash
export OPENAI_API_KEY="sua_chave_aqui"

./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080`, usando um banco H2 em memória (não é preciso
configurar nenhum banco externo).

### Rodando os testes

```bash
./gradlew test
```

## Como testar o fluxo principal

**Via voz (fluxo completo):**

```bash
curl -X POST http://localhost:8080/api/assistant/voice \
  -F "audio=@comando.mp3" \
  --output resposta.mp3
```

**Via texto (mesma lógica de IA, sem gravar áudio):**

```bash
curl -X POST "http://localhost:8080/api/assistant/text" \
  --data-urlencode "message=Registre um gasto de 45 reais com alimentação"
```

**Via REST puro (sem IA, direto nos use cases):**

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":45.00,"category":"FOOD","description":"Almoço"}'

curl http://localhost:8080/api/transactions/balance
curl http://localhost:8080/api/transactions/category/FOOD
```

## Melhoria implementada

Dentre as sugestões do desafio ("Adicionar novos tipos de consulta financeira"), foi
implementada a consulta de **total gasto/recebido por categoria**:

- Cada transação agora carrega uma `category` (ex.: `FOOD`, `TRANSPORT`, `SALARY`).
- Novo use case `GetTotalByCategoryUseCase`, com testes unitários próprios.
- Nova *tool* de IA `getTotalByCategory`, para perguntas como "quanto eu gastei com
  alimentação este mês".
- Novo endpoint REST `GET /api/transactions/category/{category}` para o mesmo propósito
  sem depender de IA.

Essa melhoria não altera as camadas `infrastructure` de forma intrusiva: o novo comportamento
nasce no `domain` (campo `category` na entidade `Transaction`) e é exposto de duas formas
(REST e tool calling) reaproveitando o mesmo use case.

## Estrutura do projeto

```
src/main/java/dio/budgeting/
├── domain/            -> Transaction, TransactionId, TransactionType, TransactionRepository (contrato)
├── application/        
│   ├── usecase/        -> CreateTransactionUseCase, ListTransactionsUseCase,
│   │                       GetBalanceUseCase, GetTotalByCategoryUseCase
│   └── dto/             -> CreateTransactionCommand, TransactionView
└── infrastructure/
    ├── persistence/      -> TransactionJpaEntity, SpringDataTransactionRepository,
    │                        JpaTransactionRepository (implementa o contrato do domain)
    ├── web/              -> TransactionController (REST puro)
    └── ai/                -> AiConfig (ChatClient), BudgetingTools (@Tool),
                              VoiceAssistantController (voz e texto)
```

- `domain` não depende de Spring nem de JPA: define apenas as regras de negócio.
- `application` orquestra o domínio e é reutilizada tanto pelo REST quanto pela IA.
- `infrastructure` conecta tudo a frameworks e tecnologias concretas (Spring Web, Spring
  Data JPA, Spring AI/OpenAI).

## Tecnologias utilizadas

- Java 17
- Spring Boot 3
- Spring AI (ChatClient, Tool Calling, Audio Transcription, Audio Speech — OpenAI)
- Spring Data JPA + H2 (banco em memória)
- Gradle
- JUnit 5

## O que foi aprendido

- Como conectar um `ChatClient` a funções reais da aplicação via `@Tool`/Tool Calling, sem
  deixar a IA acessar diretamente o domínio ou a persistência.
- Como estruturar transcrição de áudio (STT) e síntese de voz (TTS) como adaptadores de
  infraestrutura, mantendo o núcleo de negócio (use cases) agnóstico de IA.
- Como evoluir uma API existente (adicionando `category` e uma nova consulta financeira)
  sem quebrar a separação de responsabilidades entre `domain`, `application` e
  `infrastructure`.
