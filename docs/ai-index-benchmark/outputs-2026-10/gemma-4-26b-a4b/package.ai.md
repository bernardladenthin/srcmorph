### ai
- H: 1.0
- C: FFA8A94C
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T14:12:36Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-disciplinary toolkit providing AI-driven code summarization, high-performance GPU-accelerated data processing, and multi-domain relational database management for library, retail, and warehouse operations.

#### Purpose
* Automate source code documentation using pluggable AI backends and deterministic structural extraction.
* Provide high-speed statistical analysis and asynchronous data buffering via OpenCL and lock-free structures.
* Manage complex relational lifecycles for library circulation, e-commerce fulfillment, and warehouse inventory synchronization.

#### Responsibilities
* **AI Summarization Engine**: Manages configuration, file filtering, metadata extraction, and orchestration of local (JNI/LlamaCpp) or remote (OpenAI) AI models.
* **High-Performance Computing**: Executes parallel histogram generation via OpenCL kernels and manages lock-free SPSC byte-stream buffers.
* **System Automation & Reporting**: Handles directory archiving/rotation and automates financial revenue aggregation from transaction data.
* **Relational Data Management**: Orchestrates library loan lifecycles, e-commerce order processing, and warehouse stock synchronization via transactional pipelines.

#### Key units
* `SrcMorphConfiguration`: Primary JavaBean for global engine and plugin parameters.
* `AiGenerationProvider`: Interface for text generation and performance metrics across different backends.
* `LlamaCppJniAiGenerationProvider`: JNI-based implementation managing native `LlamaModel` lifecycles.
* `AiFactExtractor`: Utility for generating deterministic regex-based code element counts.
* `histogram_partial` / `histogram_reduce`: OpenCL kernels for two-pass parallel histogram generation.
* `ringbuf_t`: Lock-free circular buffer structure for asynchronous byte-stream I/O.
* `usp_SyncStock`: Central warehouse procedure for merging `SupplierFeed` data into `StockLevel`.
* `fn_checkout_book` / `sp_return_book`: Library routines for managing book availability and loan status.
* `customer_order` / `order_item`: E-commerce entities for tracking order metadata and line items.

#### Data flow
* **Summarization Pipeline**: `SrcMorphConfiguration` $\rightarrow$ `AiConditionEvaluator` (filtering) $\rightarrow$ `AiFactExtractor` (augmentation) $\rightarrow$ `AiGenerationProvider` (inference) $\rightarrow$ `AiCompletionParser` (cleaning).
* **Histogram Pipeline**: `input` buffer $\rightarrow$ `histogram_partial` (local accumulation) $\rightarrow$ `partial` global buffer $\rightarrow$ `histogram_reduce` $\rightarrow$ final `histogram` output.
* **Warehouse Pipeline**: `SupplierFeed` $\rightarrow$ `usp_SyncStock` (merge logic) $\rightarrow$ `StockLevel` update + `SyncLog` entry.
* **Retail Pipeline**: `order_item` insertion $\rightarrow$ automatic `product.stock` decrement and `customer_order.total_cents` recalculation.

#### Dependencies
* **AI/Native**: `net.ladenthin.llama` (JNI), Jackson (JSON), Lombok.
* **Hardware/Compute**: OpenCL.
* **Database**: PostgreSQL (`PL/pgSQL`) and T-SQL (`MERGE`).
* **Scripting**: Python (`argparse`, `decimal`), Bash (`tar`, `date`).

#### Cross-cutting
* **Concurrency**: Lock-free SPSC patterns using `volatile` indices; OpenCL `barrier` primitives for work-group synchronization.
* **Integrity & Auditing**: Trigger-based auditing (`loan_audit`), transactional stock validation, and explicit synchronization logging (`SyncLog`).
* **Resource Management**: `AutoCloseable` providers for JNI/network resources; power-of-two buffer capacities for optimized bitwise masking.
* **Error Handling**: Strategies for oversized files (`AiOversizeStrategy`), malformed configuration trees, and shell-level error trapping.
