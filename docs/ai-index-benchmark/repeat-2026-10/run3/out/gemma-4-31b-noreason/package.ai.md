### ai
- H: 1.0
- C: 973AD197
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:33:32Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-domain toolkit providing AI-driven source code analysis, high-performance GPU-accelerated data processing, and relational database schemas for inventory and logistics management.

#### Purpose
* Automate code metadata extraction and summarization via local or remote LLMs.
* Execute high-speed byte-stream frequency analysis and lock-free data transport.
* Manage transactional inventory, lending, and warehouse synchronization logic.

#### Responsibilities
* **AI Analysis Pipeline**: Orchestrates file filtering, deterministic fact extraction via regex, and LLM-based text generation with post-processing to remove reasoning blocks.
* **Low-Level Data Processing**: Implements GPU-accelerated histograms using OpenCL and SPSC circular buffers for efficient byte-stream handling.
* **System Maintenance & Reporting**: Provides Bash-based backup rotation and Python-based financial aggregation from CSV data.
* **Domain Data Management**: Maintains schemas for library lending (loans/members), retail e-commerce (orders/SKUs), and warehouse logistics (supplier synchronization).

#### Key units
* `AiGenerationProvider` / `LlamaCppJniAiGenerationProvider`: Interface and JNI implementation for local LLM inference.
* `AiConditionEvaluator` / `SrcMorphConfiguration`: Logic for filtering source files based on configurable boolean trees.
* `AiFactExtractor` / `AiCompletionParser`: Utilities for regex-based metric extraction and LLM output cleaning.
* `histogram_partial` / `histogram_reduce`: OpenCL kernels for parallel byte-frequency counting.
* `ringbuf_t`: Lock-free circular buffer for `uint8_t` data transport.
* `sales_report.py` / `rotate_backups.sh`: Python revenue aggregator and Bash archive manager.
* `fn_checkout_book` / `sp_return_book`: PL/pgSQL functions for library loan lifecycles.
* `add_order_item`: Atomic T-SQL procedure for retail stock reservation.
* `dbo.usp_SyncStock`: T-SQL orchestrator for aggregating supplier feeds into warehouse stock levels.

#### Data flow
* **AI Pipeline**: `SrcMorphConfiguration` $\rightarrow$ `AiConditionEvaluator` (Filter) $\rightarrow$ `AiFactExtractor` (Metrics) $\rightarrow$ `AiGenerationProvider` (Inference) $\rightarrow$ `AiCompletionParser` (Clean output).
* **GPU Analysis**: Input byte stream $\rightarrow$ `local_bins` (GPU local) $\rightarrow$ `partial` (Global buffer) $\rightarrow$ Final 256-bin histogram.
* **Logistics Sync**: `dbo.SupplierFeed` $\rightarrow$ SKU/Warehouse aggregation $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ `dbo.SyncLog`.

#### Dependencies
* **AI/Java**: `net.ladenthin.llama` (llama.cpp JNI), `com.fasterxml.jackson`.
* **Processing/Other**: OpenCL C 1.2, Python Standard Library, Bash.
* **Database**: PostgreSQL/PL/pgSQL, T-SQL.

#### Cross-cutting
* **Performance & Optimization**: Use of GPU atomics and bitwise masking for ring buffers; B-tree and partial indexes for database queries.
* **Resource & Error Management**: `AutoCloseable` for native AI handles; T-SQL TRY/CATCH and Bash `trap` for failure recovery.
* **Security & Precision**: API key transmission restrictions for remote LLMs; Python `Decimal` for financial accuracy.
* **Auditability**: Database triggers for loan auditing and dedicated synchronization logs for warehouse updates.
