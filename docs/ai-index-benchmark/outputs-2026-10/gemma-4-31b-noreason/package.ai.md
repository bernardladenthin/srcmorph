### ai
- H: 1.0
- C: B3E04815
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:56:23Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-domain toolkit providing AI-driven source code analysis, high-performance GPU/system utilities, and relational database schemas for inventory and transaction management.

#### Purpose
* Orchestrate LLM-based code analysis with deterministic metric extraction and configurable file filtering.
* Provide low-level performance primitives for byte analysis and stream buffering.
* Manage relational data models for library lending, retail e-commerce, and warehouse synchronization.

#### Responsibilities
* **AI Analysis & Orchestration**: Manages LLM provider lifecycles (Local GGUF/OpenAI), prompt rendering, and response cleaning.
* **Code Evaluation**: Filters files via logical condition trees and handles context-limit strategies for oversize files.
* **High-Performance Computing**: Executes GPU-accelerated byte-frequency histograms and lock-free SPSC circular buffering.
* **System & Data Automation**: Automates backup rotation and financial revenue aggregation via scripts.
* **Inventory & Transaction Modeling**: Implements state transitions and audit logging for books, retail products, and warehouse stock.

#### Key units
* `AiGenerationProvider` / `AiGenerationProviderFactory`: Interface and factory for resolving LLM backends (e.g., `LlamaCppJniAiGenerationProvider`).
* `AiConditionEvaluator`: Logic engine for matching `AiFileContext` against `AiCondition` trees.
* `AiFactExtractor`: Regex-based utility for extracting deterministic code facts.
* `AiOversizeStrategy`: Enum defining handling for files exceeding context limits (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
* `histogram_partial` / `histogram_reduce`: OpenCL kernels for parallel byte-count reduction.
* `ringbuf_t`: SPSC circular buffer structure for high-throughput byte-stream storage.
* `fn_checkout_book` / `sp_return_book`: PL/pgSQL logic for library loan lifecycles.
* `add_order_item`: Atomic procedure for retail stock decrement and order total updates.
* `dbo.usp_SyncStock`: T-SQL logic for merging `dbo.SupplierFeed` into `dbo.StockLevel`.

#### Data flow
* **AI Pipeline**: `SrcMorphConfiguration` $\rightarrow$ `AiConditionEvaluator` (filter) $\rightarrow$ `AiFactExtractor` (facts) $\rightarrow$ `AiGenerationProvider` (LLM) $\rightarrow$ `AiCompletionParser` (clean output).
* **GPU Pipeline**: `input` buffer $\rightarrow$ `local_bins` (atomics) $\rightarrow$ `partial` buffer $\rightarrow$ `histogram` buffer.
* **Retail Pipeline**: `add_order_item` $\rightarrow$ stock validation $\rightarrow$ `order_item` insertion $\rightarrow$ `product.stock` decrement $\rightarrow$ `customer_order.total_cents` update.
* **Warehouse Pipeline**: `dbo.SupplierFeed` $\rightarrow$ date-filtered aggregation $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ `dbo.SyncLog`.

#### Dependencies
* **AI/Java**: `net.ladenthin.llama` (JNI), `Jackson`, `Lombok`.
* **Low-level/Other**: OpenCL C 1.2, Python (csv, collections, decimal), Bash.
* **Database**: PostgreSQL (PL/pgSQL), T-SQL.

#### Cross-cutting
* **Performance & Concurrency**: Use of JNI for native model execution, OpenCL for GPU acceleration, and volatile indexing for lock-free SPSC buffers.
* **Integrity & Audit**: Implementation of `validate()` methods in AI configs, `loan_audit` and `dbo.SyncLog` tables for state tracking, and T-SQL TRY/CATCH blocks.
* **Resource Management**: `AutoCloseable` implementation for native AI memory release and manual heap allocation for GPU buffers.
