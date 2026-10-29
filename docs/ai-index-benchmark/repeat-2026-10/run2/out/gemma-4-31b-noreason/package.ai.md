### ai
- H: 1.0
- C: C91EB333
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:00:58Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-domain toolkit providing AI-driven source code analysis, high-performance GPU/streaming utilities, and relational database systems for inventory and transactional management.

#### Purpose
* Orchestrate LLM-based code analysis with pluggable local and remote backends.
* Provide low-level GPU acceleration and lock-free data streaming primitives.
* Manage complex transactional lifecycles for library, retail, and warehouse domains.

#### Responsibilities
* **AI Orchestration & Analysis**: Managing LLM provider lifecycles, evaluating file-filtering logic, and extracting deterministic code facts via regex.
* **High-Performance Computing**: Implementing OpenCL kernels for byte frequency analysis and SPSC circular buffers for asynchronous data transfer.
* **System & Financial Administration**: Automating backup rotations via shell scripts and aggregating regional revenue via Python.
* **Domain Data Management**: Handling book loans, e-commerce order processing, and warehouse stock synchronization.

#### Key units
* `AiGenerationProvider` / `AiGenerationProviderFactory`: Interface and factory for resolving LLM backends (e.g., `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`).
* `SrcMorphConfiguration` / `AiConditionEvaluator`: Configuration root and logic engine for filtering source files.
* `AiFactExtractor` / `AiCompletionParser`: Utilities for regex-based metadata extraction and cleaning AI response blocks.
* `histogram_partial` / `histogram_reduce`: OpenCL kernels for parallel byte aggregation.
* `ringbuf_t`: Lock-free circular buffer structure for fragmented data transfer.
* `fn_checkout_book` / `sp_return_book`: PL/pgSQL logic for library loan lifecycles.
* `add_order_item`: Transactional orchestration for e-commerce stock and order updates.
* `dbo.usp_SyncStock`: T-SQL merge logic for synchronizing warehouse stock from supplier feeds.

#### Data flow
* **AI Pipeline**: `SrcMorphConfiguration` $\rightarrow$ `AiConditionEvaluator` (filter) $\rightarrow$ `AiFactExtractor` (augment) $\rightarrow$ `AiGenerationProvider` (inference) $\rightarrow$ `AiCompletionParser` (clean).
* **GPU Analysis**: Input buffer $\rightarrow$ `local_bins` $\rightarrow$ `partial` buffer $\rightarrow$ `histogram` output.
* **Lending/Order Pipeline**: Request $\rightarrow$ Validation/Locking $\rightarrow$ Entity Update $\rightarrow$ Audit Log (via triggers or explicit logs).
* **Warehouse Sync**: `dbo.SupplierFeed` $\rightarrow$ Aggregation $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ `dbo.SyncLog`.

#### Dependencies
* **External Libraries**: `net.ladenthin.llama` (JNI), `com.fasterxml.jackson`, OpenCL C 1.2, Python Standard Library (`decimal`, `csv`).
* **Infrastructure**: PostgreSQL/PL/pgSQL, SQL Server/T-SQL, Unix utilities (`tar`, `du`).
* **Internal**: `AiPromptSupport`, `AiPromptDefinition`, `AiModelDefinition`.

#### Cross-cutting
* **Performance & Concurrency**: Use of lock-free SPSC patterns with `volatile` qualifiers, bitwise masking for buffer indexing, and row-level `FOR UPDATE` locking in SQL.
* **Data Integrity**: Financial precision via `decimal.Decimal`, SQL `CHECK` constraints, and `TRY...CATCH` error handling.
* **Configuration & Validation**: Mix of mutable JavaBeans and immutable builders; pre-execution `validate()` methods for regex and format checks.
* **Auditability**: Standardized timing via `AiGenerationTimings` and automated event tracking via SQL triggers and logs.
