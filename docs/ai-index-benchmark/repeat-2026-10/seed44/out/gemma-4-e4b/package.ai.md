### ai
- H: 1.0
- C: 6FBCAA52
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:08:40Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package suite provides specialized persistence and operational logic for managing library lending, e-commerce inventory, and warehouse stock synchronization, alongside AI-driven code analysis and low-level data streaming utilities.

#### Purpose
* To manage data persistence and core business logic for library lending, e-commerce, and warehouse inventory.
* To provide infrastructure for AI-driven source code analysis, generation, and high-performance data streaming/reporting.

#### Responsibilities
* AI Code Analysis & Generation: Configuring, evaluating, and executing AI-driven source code analysis using LLM backends.
* Data Streaming & Kernel Computation: Implementing thread-safe byte data transfer and high-speed byte histogram calculation via OpenCL.
* System Automation & Reporting: Automating backup rotation and generating aggregated sales revenue reports from CSV data.
* Persistence Layer Management: Defining and implementing SQL logic for library lending, e-commerce orders, and warehouse stock synchronization.

#### Key units
* **AI Generation**: `SrcMorphConfiguration`, `AiGenerationProvider`, `AiGenerationProviderFactory`, `AiConditionEvaluator`, `LlamaCppJniAiGenerationProvider`.
* **Data Streaming**: `ringbuf_t`, `histogram_partial`, `histogram_reduce`.
* **Reporting/Automation**: `sales_report.py`, `rotate_backups.sh`, `Order`.
* **SQL Persistence**: `author`, `book`, `member`, `loan`, `product`, `customer_order`, `dbo.StockLevel`, `fn_checkout_book`.

#### Data flow
AI configuration flows from `SrcMorphConfiguration` through evaluation (`AiConditionEvaluator`) and extraction (`AiFactExtractor`) before generation via `AiGenerationProvider`. Data streams flow into `ringbuf_t` for processing, while sales data flows into `sales_report.py` for aggregation. SQL operations follow defined transactional paths (e.g., `add_order_item` updating `customer_order`).

#### Dependencies
* **AI**: Depends on internal definitions like `AiFileContext`, `AiPromptDefinition`, and external libraries like `lombok`.
* **Data Streaming**: Depends on standard C libraries and OpenCL synchronization primitives.
* **SQL**: Depends on PostgreSQL features (Library) and defined table structures (`product`, `dbo.StockLevel`).

#### Cross-cutting
* **Configuration**: `SrcMorphConfiguration` serves as the primary configuration hub for AI operations.
* **Error Handling**: AI components use specific exceptions (`IllegalArgumentException`, `IOException`); data streaming uses status codes (`ringbuf_status_t`); SQL uses transactional integrity checks.
* **Patterns**: The Builder pattern is used for configuration objects in the AI layer.
* **Auditing/Logging**: SQL layers implement auditing via `loan_audit` and `dbo.SyncLog`.
