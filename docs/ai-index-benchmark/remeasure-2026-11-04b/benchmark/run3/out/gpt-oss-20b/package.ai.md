### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:12:22Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework for AI‑driven source‑code generation, low‑level performance utilities, and cross‑domain database schemas, all orchestrated to support automated code synthesis, data processing, and business rule enforcement.

#### Purpose
- Central AI‑powered source‑generation engine with pluggable inference back‑ends.
- High‑performance image and stream utilities for data‑engineering tasks.
- Robust database schemas and procedures for library, shop, and warehouse domains.

#### Responsibilities
- **AI Generation** – configuration, file selection, fact extraction, provider abstraction, request/response handling.
- **Utilities** – GPU‑accelerated histograms, lock‑free ring buffers, backup rotation, sales reporting.
- **Database** – schema definition, stored procedures, triggers, views, and audit logging across PostgreSQL, MySQL, and SQL Server.

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – mutable config bean.  
  - `AiGenerationConfig` – immutable provider settings.  
  - `AiOversizeStrategy` – enum for oversized prompt handling.  
  - `AiConditionEvaluator` – tree evaluator for file filtering.  
  - `AiFactExtractor` – deterministic fact block builder.  
  - `AiGenerationProvider` / `AiGenerationProviderFactory` – abstraction and factory.  
  - `LlamaCppJniAiGenerationProvider` – native llama.cpp wrapper.  
  - `OpenAiCompatibleProviderSupport` – HTTP client for OpenAI‑compatible APIs.  
  - `AiCompletionParser` – removes thinking blocks from completions.  
  - `AiGenerationTimings` – DTO for performance metrics.
- **Other**  
  - OpenCL kernels `histogram_partial` / `histogram_reduce`.  
  - Ring buffer API (`ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`).  
  - Scripts `rotate_backups.sh`, `sales_report.py`.
- **SQL**  
  - Library tables (`author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`).  
  - Views (`overdue_loans`, `member_loan_stats`, `popular_books`).  
  - Functions (`fn_checkout_book`, `fn_late_fee`).  
  - Procedures (`sp_return_book`).  
  - Shop tables (`product`, `customer_order`, `order_item`) and procedure `add_order_item`.  
  - Warehouse tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`) and procedure `dbo.usp_SyncStock`.

#### Data flow
1. **AI Generation** – configuration → condition & fact evaluation → provider selection → prompt + facts → provider execution → text + timings returned.  
2. **Utilities** – input stream → OpenCL histogram kernels → 256‑bin result; producer writes to ring buffer → consumer reads; backup script archives directory; sales report parses CSV → aggregates revenue.  
3. **Database** – application calls stored procedures (`fn_checkout_book`, `add_order_item`, `usp_SyncStock`) → triggers log audits → views expose aggregated data for reporting.

#### Dependencies
- **Internal** – AI modules (`AiCondition`, `AiFileContext`, `AiFactCounter`, etc.), utility kernels, database cross‑references.  
- **External** – Java standard libraries, Lombok, Jackson, llama JNI, OpenCL runtime, shell utilities, Python stdlib, PostgreSQL/MySQL/SQL Server built‑ins.

#### Cross‑cutting
- **Thread safety** – stateless evaluators, immutable DTOs, single‑producer/single‑consumer ring buffer.  
- **Error handling** – configuration validation, provider exceptions, SQL error capture into logs.  
- **Performance** – native llama.cpp calls, OpenCL parallelism, ring buffer minimal copying, timing metrics.  
- **Security** – API key handling, loopback URL checks, audit trails.  
- **Scalability** – provider factory supports local and remote back‑ends, database procedures use row‑level locks and transactions.
