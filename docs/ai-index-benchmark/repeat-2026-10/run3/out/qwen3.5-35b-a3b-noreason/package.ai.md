### ai
- H: 1.0
- C: 973AD197
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:45:45Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Orchestrates deterministic AI-driven code generation, high-performance byte-level data analysis, and multi-domain inventory transaction management across Java, C, and SQL layers.

#### Purpose
- Aggregates configuration, filters files, and orchestrates pluggable AI inference backends (local/remote) for source code summarization.
- Provides parallel byte-frequency analysis, lock-free streaming buffers, and operational scripts for backup and financial reporting.
- Manages inventory lifecycles, lending logic, and stock synchronization for library, retail, and warehouse domains via SQL.

#### Responsibilities
- **AI Orchestration**: Resolves provider strategies, validates file conditions, and sanitizes model outputs to prevent reasoning leakage.
- **Parallel Processing**: Executes OpenCL kernels for histogram reduction and manages high-throughput circular buffers for byte streams.
- **Transactional Data**: Enforces atomicity for retail orders, library loans, and warehouse stock merges using database-specific locking and triggers.
- **Operational Automation**: Automates backup retention policies and aggregates financial revenue data from CSV exports.

#### Key units
- `SrcMorphConfiguration`: Central mutable JavaBean aggregating global project settings, model paths, and generation parameters.
- `AiGenerationProviderFactory`: Resolves provider priority and dispatches instantiation to concrete classes based on string constants.
- `LlamaCppJniAiGenerationProvider`: Implements the provider interface using native JNI handles for GGUF models with lazy initialization.
- `AiCompletionParser`: Strips `<|channel>thought` blocks from raw text to prevent reasoning leakage in persisted data.
- `histogram_partial` / `histogram_reduce`: OpenCL kernels performing local atomic counting and global reduction of byte frequencies.
- `ringbuf_t`: Core structure managing `data`, `head`, `tail`, and `capacity` for lock-free byte streaming.
- `loan_audit`: Immutable log table capturing `CHECKOUT` and `RETURN` actions via triggers.
- `add_order_item`: MySQL stored procedure for atomic order line insertion and stock reservation.
- `dbo.usp_SyncStock`: T-SQL procedure merging supplier feeds into `dbo.StockLevel`.

#### Data flow
1.  **AI Pipeline**: Maven config populates `SrcMorphConfiguration` -> `AiConditionEvaluator` filters files -> `AiFactExtractor` computes metrics -> `AiGenerationProviderFactory` selects backend -> `generate()` executes inference -> `AiCompletionParser` sanitizes output.
2.  **Histogram Pipeline**: Input byte stream -> `histogram_partial` (local counting) -> `histogram_reduce` (global summation) -> Final 256-bin output.
3.  **Buffer Pipeline**: `ringbuf_init` allocates memory -> `ringbuf_write` updates `head` with wrap logic -> `ringbuf_read` updates `tail` -> `ringbuf_free` deallocates.
4.  **Library Flow**: Schema migrations evolve models -> triggers intercept loan state changes -> write to `loan_audit` -> views aggregate for reporting.
5.  **Retail Flow**: Procedure locks `product` rows -> decrements stock -> updates `order_item` -> recalculates `customer_order.total_cents`.
6.  **Warehouse Flow**: `@Since` parameter filters `SupplierFeed` -> `MERGE` operations on `StockLevel` -> metrics written to `SyncLog`.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiModelDefinition`, `Order` dataclasses, `loan` table, `product` table, `StockLevel`.
- **External**: `java.time.Instant`, `java.util.regex`, `com.fasterxml.jackson.databind`, `OpenCL C 1.2`, `Python Standard Lib` (csv, dataclasses), `PostgreSQL`/`MySQL`/`T-SQL` native types.
- **Native**: `llama.cpp` library accessed via JNI for model inference.
- **Infrastructure**: Maven Plexus, Jackson/YAML, System Utilities (bash, tar, date).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive copying in Java configuration classes.
- **Concurrency**: OpenCL kernels use `barrier` and `atomic_inc`; C buffer is lock-free; MySQL uses `SELECT ... FOR UPDATE`; T-SQL uses `TRY...CATCH`.
- **Validation**: Strict checks for regex syntax, power-of-two capacity, CSV headers, and monetary precision.
- **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS/loopback checks; `AiCompletionParser` removes chain-of-thought markers.
- **Immutability**: `LlamaCppJniConfig` is fully immutable; `AiGenerationConfig` returns unmodifiable list views.
- **Audit Pattern**: Dedicated `loan_audit` table and trigger mechanism for libraries; `SyncLog` table for warehouse operations.
