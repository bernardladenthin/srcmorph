### ai
- H: 1.0
- C: F0404DDE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:47:20Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-domain toolkit providing AI-powered source code summarization, high-performance data processing primitives, and diverse inventory management database systems.

#### Purpose
* Provide an AI-driven engine for source code analysis and summarization.
* Offer low-level GPU-accelerated analysis and memory-efficient data streaming utilities.
* Implement transactional database schemas for library, retail, and warehouse inventory tracking.

#### Responsibilities
* **AI Summarization Engine**: Managing project configuration, file filtering via `AiConditionEvaluator`, regex-based fact extraction, and pluggable LLM integrations.
* **High-Performance Computing & Admin**: Executing GPU byte-frequency analysis, managing SPSC circular buffers, automating system backups, and generating financial reports.
* **Inventory & Resource Management**: Handling loan lifecycles, retail order processing with stock reservation, and warehouse stock synchronization.

#### Key units
* **AI Engine**: `SrcMorphConfiguration` (central settings), `AiGenerationProvider` (LLM contract), `LlamaCppJniAiGenerationProvider` (local GGUF/JNI), `OpenAiCompatibleProviderSupport` (remote API), `AiConditionEvaluator` (filtering logic), and `AiCompletionParser` (response cleaning).
* **Performance/Admin**: `histogram_partial`/`reduce` (OpenCL kernels), `ringbuf_t` (SPSC circular buffer), `rotate_backups.sh` (archive rotation), and `sales_report.py` (revenue aggregation).
* **Database**: `loan`/`book`/`member` (Library system), `product`/`customer_order` (Retail shop), and `dbo.StockLevel`/`dbo.usp_SyncStock` (Warehouse synchronization).

#### Data flow
* **AI Pipeline**: `SrcMorphConfiguration` $\rightarrow$ `AiFileContext` $\rightarrow$ `AiConditionEvaluator` $\rightarrow$ `AiFactExtractor` $\rightarrow$ `AiGenerationProvider` $\rightarrow$ `AiCompletionParser`.
* **Data Processing**: Input buffer $\rightarrow$ OpenCL kernels $\rightarrow$ Histogram; Producer $\rightarrow$ `ringbuf_write` $\rightarrow$ `ringbuf_t` $\rightarrow$ `ringbuf_read` $\rightarrow$ Consumer.
* **Inventory Cycles**: Transactional requests $\rightarrow$ Stock validation $\rightarrow$ State change (e.g., `loan` or `order_item`) $\rightarrow$ Audit logs/Reporting views.

#### Dependencies
* **External Libraries**: `net.ladenthin.llama` (JNI), `com.fasterxml.jackson.databind.ObjectMapper` (JSON), OpenCL C 1.2, Python (csv, dataclasses).
* **Database Engines**: PostgreSQL, MySQL 8, and T-SQL/SQL Server.
* **System Tools**: Bash, `tar`, and standard Unix utilities.

#### Cross-cutting
* **Performance Optimization**: Use of GPU acceleration, SPSC concurrency patterns, power-of-two bitmasking in buffers, and lazy initialization of LLM models.
* **Audit & Integrity**: Implementation of `loan_audit` triggers, `dbo.SyncLog` for warehouse sync, and `validate()` methods for AI logic trees.
* **Configuration & Security**: Use of immutable builders for `LlamaCppJniConfig` and enforcement of HTTPS for remote AI API keys.
