### ai
- H: 1.0
- C: C91EB333
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:06:14Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Orchestrates AI-driven source code summarization, high-performance parallel data processing, and multi-database transactional logic for library, retail, and supply chain domains.

#### Purpose
- Aggregates Maven plugin bindings and runtime settings to control AI model behavior, file selection criteria, and output formatting.
- Computes byte frequency distributions using parallel OpenCL reduction algorithms and manages circular data streams with O(1) operations.
- Implements core data models and transactional procedures for multi-branch library operations, e-commerce order management, and supply chain stock synchronization.

#### Responsibilities
- **Condition Evaluation**: Recursively validates and matches `AiCondition` trees against file metadata (size, lines, paths) using logical operators.
- **Configuration Management**: Serves as a mutable JavaBean carrier for global project settings, fallback Llama.cpp parameters, and provider-specific options.
- **Parallel Histogramming**: Executes two-pass OpenCL kernels to aggregate local histograms into global 256-bin frequency arrays.
- **Streaming Management**: Handles single-producer/single-consumer enqueue/dequeue operations with wrap-around logic and power-of-two sizing.
- **Library Operations**: Manages book inventory, patron loans, late fees, and historical schema evolution from global counts to physical copy tracking.
- **Retail Transactions**: Validates stock availability, reserves inventory, and updates order totals atomically during line item addition.
- **Supply Chain Sync**: Aggregates supplier feed data, merges stock levels, and logs execution metrics for nightly warehouse updates.
- **Operational Automation**: Enforces backup retention policies via atomic archive creation and aggregates CSV order data by region and month.

#### Key units
- `AiConditionEvaluator`: Evaluates `AiCondition` trees against `AiFileContext` to determine file matches and validates tree structure.
- `AiFactExtractor`: Static utility generating formatted facts blocks by counting regex matches in source text.
- `AiGenerationProviderFactory`: Resolves effective provider names and instantiates concrete implementations.
- `LlamaCppJniAiGenerationProvider`: Native JNI wrapper for `LlamaModel` with lazy loading and hardware offloading.
- `histogram_partial`: Parallel kernel computing per-group partial byte counts using atomic increments in local memory.
- `ringbuf_t`: Struct managing circular buffer state via `head`, `tail`, and `capacity` indices.
- `rotate_backups.sh`: Bash utility creating timestamped `.tar.gz` archives and enforcing retention limits.
- `sales_report.py`: Python CLI parsing CSV orders to calculate revenue metrics.
- `fn_checkout_book`, `sp_return_book`: PostgreSQL procedures for validating members and processing loan lifecycle events.
- `add_order_item`: MySQL procedure for atomic line item insertion, stock reservation, and total recalculation.
- `dbo.usp_SyncStock`: T-SQL procedure orchestrating supplier feed aggregation and stock merging.
- `loan_audit`: Immutable log table capturing checkout and return actions via triggers.

#### Data flow
1. **AI Pipeline**: `SrcMorphConfiguration` aggregates inputs -> `AiConditionEvaluator` filters files -> `AiGenerationProviderFactory` resolves backend -> `LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleProviderSupport` generates text -> `AiCompletionParser` sanitizes response.
2. **Histogram Pipeline**: Input byte stream -> `histogram_partial` (local counting) -> `partial` buffer -> `histogram_reduce` (global summation) -> `histogram` output.
3. **Buffer Pipeline**: Validated capacity -> `malloc` allocation -> `ringbuf_write` updates `head` -> `ringbuf_read` updates `tail` -> status codes or byte counts returned.
4. **Library Lifecycle**: `05_seed_data.sql` initializes reference data -> `fn_checkout_book` decrements stock and creates `loan` records -> `trg_loan_audit` logs changes -> `sp_return_book` reverses stock.
5. **Order Processing**: `add_order_item` locks `product` rows, validates stock, upserts `order_item`, decrements `product.stock`, and recalculates `customer_order.total_cents`.
6. **Warehouse Sync**: `dbo.usp_SyncStock` filters `SupplierFeed` by `@Since`, aggregates quantities, executes `MERGE` on `StockLevel`, and writes row counts to `SyncLog`.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.document`, `net.ladenthin.srcmorph.prompt`.
- **External Support**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- **External Libraries**: `com.fasterxml.jackson.databind` (JSON serialization), `lombok` (annotations), `org.jspecify` (null safety), OpenCL C 1.2, Standard C (`stdlib.h`, `string.h`), Python (`argparse`, `csv`, `decimal`).
- **Native**: `net.ladenthin.llama` (native bindings, `LlamaModel`, `ChatResponse`), `llama.cpp` JNI bindings for model inference.
- **Database**: PostgreSQL functions (`CURRENT_DATE`, `generate_series`), MySQL storage engine locking, SQL Server system functions (`SYSUTCDATETIME`, `ERROR_MESSAGE`).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations; setters often reset to defaults if null inputs are provided.
- **Immutability Patterns**: `AiConditionGroup` uses defensive copying; `AiGenerationConfig` returns unmodifiable views for list fields; `LlamaCppJniConfig` and `AiGenerationTimings` are immutable value objects.
- **Concurrency**: OpenCL uses `barrier(CLK_LOCAL_MEM_FENCE)` and `atomic_inc`; Ring buffer assumes single-producer/single-consumer without locks; Row-level locking (`SELECT ... FOR UPDATE`) in MySQL; `MERGE` atomicity in SQL Server.
- **Error Handling**: Validation methods throw `IllegalArgumentException` for structural violations; OpenCL relies on runtime errors; Ring buffer returns specific `status_t` codes; Scripts enforce strict input validation and atomicity.
- **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS for non-loopback hosts when transmitting API keys.
- **Audit Trails**: Trigger-based logging for library loans; explicit logging tables for warehouse sync outcomes.
