### ai
- H: 1.0
- C: CB7EA24B
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:07:30Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework for AI‑driven source‑code transformation, low‑level byte‑stream utilities, and cross‑platform database schemas that support library, e‑commerce, and warehouse operations.

#### Purpose
- Orchestrate AI‑powered code generation with provider abstraction.
- Offer reusable byte‑stream tools (histogram, ring buffer, backup scripts).
- Provide persistent schemas and business logic for libraries, shops, and warehouses.

#### Responsibilities
- **AI Generation Engine** – configuration, file selection, fact extraction, provider abstraction, response parsing, timing.
- **Low‑Level Utilities** – GPU‑accelerated byte histograms, lock‑free ring buffer, backup rotation, sales reporting.
- **Database Layer** – catalog, loan, order, stock tables; stored functions/procedures for checkout, order handling, stock sync; reporting views and audit trails.

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – mutable config bean.  
  - `AiGenerationConfig` – AI model knobs.  
  - `AiOversizeStrategy` – enum for oversized‑file handling.  
  - `AiConditionEvaluator` – stateless validator.  
  - `AiFactExtractor` – deterministic facts block generator.  
  - `AiGenerationProvider` / `AiGenerationProviderFactory` – provider abstraction.  
  - `LlamaCppJniAiGenerationProvider` – local llama.cpp inference.  
  - `OpenAiCompatibleProviderSupport` – OpenAI‑compatible request builder.  
  - `AiCompletionParser` – strips reasoning blocks.  
  - `AiGenerationTimings` – token and decode metrics.
- **Other**  
  - `histogram_partial`, `histogram_reduce` – OpenCL kernels.  
  - `ringbuf_t` and associated API – power‑of‑two ring buffer.  
  - `rotate_backups.sh`, `sales_report.py` – backup and reporting scripts.
- **SQL**  
  - Library tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Shop tables: `product`, `customer_order`, `order_item`; procedure `add_order_item`.  
  - Warehouse tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.  
  - Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.

#### Data flow
1. **AI Pipeline** – Load `SrcMorphConfiguration` → build `AiConditionGroup` → validate with `AiConditionEvaluator` → select files → extract facts via `AiFactExtractor` → apply `AiOversizeStrategy` → create `AiGenerationRequest` → provider chosen by `AiGenerationProviderFactory` → generate text → parse with `AiCompletionParser` → record `AiGenerationTimings`.
2. **Byte‑Stream Utilities** – Input stream → `histogram_partial` → `histogram_reduce` → 256‑bin histogram; producer writes to `ringbuf_t` → consumer reads; backup script compresses source → rotates archives; sales script parses CSV → aggregates → TSV output.
3. **Database Operations** – `fn_checkout_book` validates member → updates `book.copies_free` → inserts `loan` → triggers `loan_audit`; `sp_return_book` updates `loan` → increments `book.copies_free`; `add_order_item` locks `product` → updates `order_item` and `customer_order.total_cents`; `dbo.usp_SyncStock` merges `SupplierFeed` into `StockLevel` → logs in `SyncLog`.

#### Dependencies
- **Internal** – AI config classes, condition trees, fact counters, prompt definitions, generation requests, timing records; OpenCL kernels; ring buffer C API; SQL schema FK constraints.
- **External** – Lombok, Jackson, `org.jspecify.annotations`, `net.ladenthin.llama` native bindings, OpenCL 1.2, shell utilities (`tar`, `date`, `rm`), Python stdlib, PostgreSQL/MySQL/SQL Server built‑ins, environment variables for API keys.

#### Cross‑cutting
- Stateless, immutable components (`AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`).  
- Unified error handling: configuration errors → `IllegalArgumentException`; I/O → `IOException`.  
- Timing instrumentation via `AiGenerationTimings`.  
- Consistent UTC timestamp format `YYYYMMDDTHHMMSSZ` for backups.  
- Referential integrity and audit mechanisms across all schemas.  
- Concurrency control: MySQL `FOR UPDATE`, SQL Server MERGE with transaction rollback, lock‑free ring buffer.
