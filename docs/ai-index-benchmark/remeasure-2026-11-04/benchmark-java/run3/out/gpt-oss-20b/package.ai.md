### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:50:14Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code transformation, GPU‑accelerated data processing, and relational data management for library, shop, and warehouse domains.

#### Purpose
- Centralize AI‑based code transformation workflows.
- Provide high‑performance GPU kernels and a lock‑free ring buffer for streaming data.
- Persist and enforce business rules across library, shop, and warehouse domains with audit and reporting.

#### Responsibilities
- **AI Source Morph** – configuration, condition evaluation, fact extraction, oversize handling, provider selection, prompt generation, response parsing, and timing.
- **GPU & Streaming Utilities** – OpenCL byte‑histogram kernels, power‑of‑two ring buffer, and command‑line backup/report scripts.
- **Relational Data Management** – schemas, stored procedures, triggers, and views for library loans, e‑commerce orders, and warehouse inventory.

#### Key units
- **Java (AI)**  
  - `SrcMorphConfiguration` – runtime parameters.  
  - `AiConditionEvaluator` – file‑selection logic.  
  - `AiFactExtractor` – deterministic fact counting.  
  - `AiOversizeStrategy` – context‑limit policy.  
  - `AiGenerationProviderFactory` / `AiGenerationProvider` – provider abstraction.  
  - `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `MockAiGenerationProvider` – concrete providers.  
  - `AiCompletionParser` – output sanitization.  
  - `LlamaCppJniConfig` – llama.cpp settings.
- **C / OpenCL (GPU)**  
  - `histogram_partial`, `histogram_reduce` – 256‑bin histogram kernels.  
  - `ringbuf_t`, `ringbuf_status_t` – ring buffer API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`, etc.).
- **Shell / Python (Scripts)**  
  - `rotate_backups.sh` – backup rotation.  
  - `sales_report.py` – CSV parsing and revenue aggregation.
- **SQL (Library / Shop / Warehouse)**  
  - Library tables (`author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`), views (`overdue_loans`, `member_loan_stats`, `popular_books`), functions (`fn_checkout_book`, `fn_late_fee`), procedure (`sp_return_book`), trigger (`trg_loan_audit`).  
  - Shop tables (`product`, `customer_order`, `order_item`), procedure (`add_order_item`).  
  - Warehouse tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`), procedure (`dbo.usp_SyncStock`).

#### Data flow
1. **AI Morph** – load `SrcMorphConfiguration` → evaluate files with `AiConditionEvaluator` → extract facts via `AiFactExtractor` → resolve provider with `AiGenerationProviderFactory` → generate code, parse with `AiCompletionParser`, return snippet with `AiGenerationTimings`.  
2. **GPU Histogram** – input byte buffer → `histogram_partial` → `histogram_reduce` → 256‑bin result.  
3. **Ring Buffer** – producer writes (`ringbuf_write`), consumer reads (`ringbuf_read`), status via `ringbuf_used`/`ringbuf_free_space`.  
4. **Scripts** – `rotate_backups.sh` archives directories; `sales_report.py` reads CSVs, filters, aggregates, outputs tabular report.  
5. **SQL Workflows** –  
   - Library: `fn_checkout_book` creates loan + audit; `sp_return_book` updates loan + audit; `fn_late_fee` calculates fees; views provide reporting.  
   - Shop: `add_order_item` validates stock, updates order, decrements inventory, recalculates total.  
   - Warehouse: `dbo.usp_SyncStock` merges `SupplierFeed` into `StockLevel`, logs in `SyncLog`.

#### Dependencies
- **Java** – Jackson, Lombok, OpenAI HTTP client, llama.cpp JNI, Java8CompatibilityHelper, environment variables for API keys.  
- **C / OpenCL** – OpenCL runtime (`get_local_id`, `atomic_inc`, `barrier`), standard C headers.  
- **Shell / Python** – Bash utilities (`tar`, `date`, `du`, `rm`), Python stdlib (`argparse`, `csv`, `datetime`, `decimal`).  
- **SQL** – PostgreSQL/MySQL/SQL Server built‑ins, foreign keys, indexes, audit tables.

#### Cross‑cutting
- Stateless utilities and immutable configs ensure thread safety; providers may cache native models.  
- Consistent error handling: `IOException`/`IllegalArgumentException` in Java, status enums in C, exit codes in Bash, exceptions in Python.  
- Timing metrics (`AiGenerationTimings`) capture token counts and throughput.  
- Atomic operations and barriers guarantee safety in GPU kernels; ring buffer enforces power‑of‑two capacity with wrap‑around logic.  
- Referential integrity, row locking, and audit logging provide data consistency across all domains.
