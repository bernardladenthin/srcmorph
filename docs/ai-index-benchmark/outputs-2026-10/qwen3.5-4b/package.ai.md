### ai
- H: 1.0
- C: 3047C358
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:07:15Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-domain package integrating AI-driven code generation and summarization, high-performance OpenCL histogram computation, and a PostgreSQL-based lending library system with shop order management.

#### Purpose
*   Orchestrates AI inference engines (local Llama.cpp, remote OpenAI) for source code generation and summarization with dynamic file filtering.
*   Executes two-pass OpenCL kernel routines for byte frequency histogram analysis and manages fixed-capacity circular buffers.
*   Manages the full lifecycle of book loans, inventory tracking, and shop order synchronization within a PostgreSQL database.

#### Responsibilities
*   **AI Code Generation**: Manages configuration schemas, validates file inclusion rules, executes inference via JNI or HTTP backends, and handles oversize context windows.
*   **OpenCL Data Processing**: Performs histogram binning, global aggregation, and zero-copy data transfers using circular buffers and atomic operations.
*   **Library Operations**: Handles checkout validation, return processing, late fee calculation, and branch-based inventory tracking.
*   **Shop Order Management**: Syncs warehouse stock levels, processes supplier feeds, and manages customer orders via stored procedures.
*   **Data Archival & Reporting**: Automates backup rotation, purging, and generates financial revenue reports from CSV exports.

#### Key units
*   **AI Modules**: `SrcMorphConfiguration`, `AiConditionEvaluator`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiFactExtractor`.
*   **OpenCL Modules**: `histogram_partial`, `histogram_reduce`, `ringbuf_t`, `ringbuf_write`, `ringbuf_read`.
*   **Database Tables**: `author`, `book`, `member`, `loan`, `book_copy`, `branch`, `loan_audit`, `customer_order`, `order_item`, `dbo.StockLevel`.
*   **Database Procedures**: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `add_order_item`, `dbo.SupplierFeed`.

#### Data flow
*   **AI Pipeline**: Configuration binds to `SrcMorphConfiguration` -> `AiConditionEvaluator` filters files -> `AiFactExtractor` prepends metadata -> Provider factory selects backend -> Inference executes -> `AiCompletionParser` strips reasoning blocks -> Metrics aggregate.
*   **OpenCL Pipeline**: Input byte array sliced by work-groups (Pass 1) -> Partial sums distributed -> Global aggregation (Pass 2) -> Final 256-bin histogram computed via `histogram_reduce`.
*   **Loan Pipeline**: User request -> `fn_checkout_book` validates member/stock -> decrements `copies_free` -> inserts `loan` record -> triggers `trg_loan_audit`.
*   **Order Pipeline**: SKU/quantity input -> `add_order_item` reserves stock -> inserts `order_item` -> updates `customer_order` totals -> `dbo.StockLevel` syncs via `MERGE`.

#### Dependencies
*   **Internal**: `AiCondition`, `AiGenerationRequest`, `LlamaModel`, `Order` dataclass, `BIGSERIAL` IDs, `TIMESTAMPTZ` timestamps.
*   **External Modules**: `LlamaCppJniProviderSupport` (native bindings), `ObjectMapper` (JSON), `PostgreSQL PL/pgSQL`, `T-SQL MERGE`, `OpenCL Runtime`.
*   **Native**: `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `get_local_id`, `barrier`.
*   **Configuration**: `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`.

#### Cross-cutting
*   **Validation**: Strict rule trees for AI conditions, power-of-two capacity constraints for buffers, and relational integrity for database constraints.
*   **Concurrency**: Thread-safe evaluators, atomic buffer operations, work-group barriers, and transactional database routines.
*   **Security**: HTTPS enforcement for API keys, bitwise masking for buffer optimization, and UTC timestamp consistency.
*   **Reasoning Support**: `chat_template_enable_thinking` parameters and dry run modes for simulation.
*   **Migration**: Schema evolution from per-book copy counts to branch-specific physical copy tracking.
