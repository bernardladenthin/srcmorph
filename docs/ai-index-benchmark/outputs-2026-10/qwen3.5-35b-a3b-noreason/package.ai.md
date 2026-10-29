### ai
- H: 1.0
- C: 78C02B4E
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:58:26Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Orchestrates AI-driven code analysis and generation while providing parallel data processing, operational utilities, and transactional data layers for library, e-commerce, and warehouse domains.

#### Purpose
- Aggregates configuration, validation, and pluggable execution for AI model interactions within the srcmorph build tool.
- Delivers high-performance parallel computing, backup automation, and financial reporting alongside relational data schemas for library, shop, and warehouse operations.

#### Responsibilities
- **AI Orchestration**: Manages configuration validation, file filtering logic, and backend execution for local (llama.cpp) or remote (OpenAI) models.
- **Parallel Processing**: Executes byte frequency analysis via OpenCL kernels and manages lock-free circular buffers for stream handling.
- **Operational Utilities**: Automates backup retention policies and aggregates CSV order data into financial revenue reports.
- **Data Persistence**: Implements relational schemas and business logic for library lending, order fulfillment, and batch inventory synchronization.

#### Key units
- `AiGenerationProviderFactory`: Resolves configuration keys to `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- `AiConditionEvaluator`: Evaluates logical trees against `AiFileContext` to determine file inclusion.
- `AiFactExtractor`: Computes regex-based statistics for AI summaries.
- `histogram_partial` / `histogram_reduce`: OpenCL kernels for parallel byte frequency reduction.
- `ringbuf_t`: Lock-free circular buffer struct for single-producer/single-consumer data transfer.
- `rotate_backups.sh`: Shell script for timestamped archive creation and retention enforcement.
- `sales_report.py`: Python CLI for parsing CSVs and computing revenue by month and region.
- `fn_checkout_book` / `sp_return_book`: Library procedures for loan state transitions and availability updates.
- `add_order_item`: MySQL procedure for atomic order line insertion and stock reservation.
- `dbo.usp_SyncStock`: Stored procedure for nightly supplier feed ingestion and stock level merging.

#### Data flow
- **AI Pipeline**: `SrcMorphConfiguration` populates `AiGenerationConfig` -> `AiConditionEvaluator` filters files -> `AiGenerationProviderFactory` selects backend -> `generate()` executes -> `AiCompletionParser` sanitizes output.
- **Histogram Pipeline**: Raw input bytes split into work-groups -> `histogram_partial` writes local counts -> `histogram_reduce` aggregates to final array.
- **Buffer Pipeline**: `ringbuf_init` allocates memory -> `ringbuf_write` updates head with masking -> `ringbuf_read` updates tail with masking.
- **Backup Pipeline**: Validates inputs -> creates `.partial` archive -> moves to timestamped name -> deletes oldest beyond limit.
- **Report Pipeline**: Parses CLI args -> validates CSV headers -> aggregates `(month, region)` dict -> outputs tab-separated results.
- **Library Flow**: Seed data populates tables -> checkout/return procedures validate status -> triggers update `loan_audit` -> views aggregate for reporting.
- **Shop Flow**: Input parameters trigger row-level locking -> merge order items -> decrement stock -> recalculate totals.
- **Warehouse Flow**: Filter supplier feed -> aggregate by warehouse/SKU -> `MERGE` updates `StockLevel` -> log results/errors to `SyncLog`.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- **External**: `net.ladenthin.llama.LlamaModel`, `java.time`, `java.util.regex`, `lombok`, `org.jspecify.annotations`, `com.fasterxml.jackson.databind.ObjectMapper`, OpenCL C 1.2 runtime, standard C library, shell utilities, Python standard library.
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`.

#### Cross-cutting
- **Null Safety**: Extensive `@Nullable` annotations and defensive copying in configuration classes.
- **Validation**: Strict integrity checks in `AiConditionEvaluator` and `AiFactExtractor` throwing `IllegalArgumentException`.
- **Concurrency**: OpenCL `atomic_inc` and `CLK_LOCAL_MEM_FENCE`; C buffer `volatile` qualifiers; DB `FOR UPDATE` locking and `MERGE` semantics.
- **State Management**: Mutable JavaBeans for configuration; immutable value types for `LlamaCppJniConfig` and `AiGenerationTimings`.
- **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS for API keys; `rotate_backups.sh` uses `trap` for cleanup.
- **Error Handling**: Consistent wrapping of `DateTimeParseException` and `PatternSyntaxException`; `AiCompletionParser` throws `IOException` on truncated blocks.
- **Resource Management**: `AiGenerationProvider` extends `AutoCloseable` for native handle cleanup.
