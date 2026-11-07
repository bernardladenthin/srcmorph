### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:04:27Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework for AI‑driven source‑code transformation, coupled with low‑latency GPU analytics and a multi‑domain SQL data‑management suite.

#### Purpose
- Centralize AI generation, evaluation, and provider abstraction for source‑code transformation.
- Offer GPU‑accelerated byte‑stream histograms and lock‑free ring buffers for high‑throughput analytics.
- Persist and enforce business rules across library, shop, and warehouse domains with audit and reporting.

#### Responsibilities
- **AI Generation Engine** – configuration, condition evaluation, fact extraction, oversize handling, provider abstraction, prompt rendering, timing, and post‑processing.
- **GPU Analytics Pipeline** – two‑pass histogram kernels, reduction, and result aggregation.
- **Ring Buffer Utility** – lock‑free FIFO for single‑producer/single‑consumer scenarios.
- **Backup & Reporting** – automated tarball rotation and monthly sales reporting.
- **SQL Data Management** – library loan logic, shop order processing, warehouse stock synchronization, audit trails, and cross‑platform compatibility.

#### Key units
- **Java**  
  - `SrcMorphConfiguration`, `AiGenerationConfig` – runtime settings.  
  - `AiConditionEvaluator`, `AiFactExtractor` – deterministic decision logic.  
  - `AiOversizeStrategy` – enum for context‑window handling.  
  - `AiGenerationProvider`, `AiGenerationProviderFactory` – provider abstraction.  
  - `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` – concrete back‑ends.  
  - `AiCompletionParser`, `AiGenerationTimings` – post‑processing and metrics.
- **C / OpenCL**  
  - `histogram_partial`, `histogram_reduce` – GPU kernels.  
  - `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – lock‑free FIFO API.
- **Shell / Python**  
  - `rotate_backups.sh` – backup rotation script.  
  - `sales_report.py` – CSV parsing, revenue aggregation, CLI entry point.
- **SQL**  
  - Library tables (`author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`), views (`overdue_loans`, `member_loan_stats`, `popular_books`), functions (`fn_checkout_book`, `fn_late_fee`), procedures (`sp_return_book`), trigger (`trg_loan_audit`).  
  - Shop tables (`product`, `customer_order`, `order_item`), procedure (`add_order_item`).  
  - Warehouse tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`), procedure (`dbo.usp_SyncStock`).

#### Data flow
1. **AI Engine**  
   - Load `SrcMorphConfiguration` → build `AiCondition` trees → evaluate files with `AiConditionEvaluator`.  
   - If oversized, `AiOversizeStrategy` decides path.  
   - Extract facts via `AiFactExtractor`.  
   - Build `AiGenerationRequest` → resolve provider with `AiGenerationProviderFactory`.  
   - Generate text (llama.cpp or OpenAI‑compatible) → parse completion → record timings → return result.
2. **GPU Histogram**  
   - `histogram_partial` processes byte stream → writes local bins → `histogram_reduce` aggregates → final 256‑bin histogram.
3. **Ring Buffer**  
   - Producer calls `ringbuf_write`; consumer calls `ringbuf_read`; status queried via `ringbuf_status_t`.
4. **Backup & Reporting**  
   - `rotate_backups.sh` creates timestamped tarballs, removes old ones.  
   - `sales_report.py` reads CSV orders → aggregates revenue by month/region → outputs report.
5. **SQL Domains**  
   - Library: `fn_checkout_book` → `loan` insert, `book.copies_free` decrement, audit log.  
   - Shop: `add_order_item` → stock check, `order_item` insert/update, `product.stock` decrement, `customer_order.total_cents` recompute.  
   - Warehouse: `usp_SyncStock` → merge `SupplierFeed` into `StockLevel`, log in `SyncLog`.

#### Dependencies
- **Java** – Lombok, Jackson, Maven plugin APIs, `net.ladenthin.llama` (model, parameters, JSON parser), Java 8+ utilities.  
- **C / OpenCL** – OpenCL built‑ins, standard C libraries.  
- **Shell / Python** – Unix utilities (`tar`, `date`, `du`, etc.), Python stdlib (`argparse`, `csv`, `datetime`, `decimal`).  
- **SQL** – PostgreSQL, MySQL 8, SQL Server 2022; system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **External Services** – OpenAI‑compatible HTTP endpoints, environment‑based credentials.

#### Cross‑cutting
- Defensive copying and immutability in configuration objects.  
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).  
- Lazy, non‑thread‑safe model loading for llama.cpp; external coordination required.  
- Consistent error handling (`IllegalArgumentException`, `IllegalStateException`, `IOException`, SQL exceptions).  
- Ring buffer uses power‑of‑two capacity and bit‑masking; provides clear error codes.  
- Histogram kernels employ two‑pass accumulation and global reduction.  
- Audit trails (`loan_audit`, `SyncLog`) capture every state change across domains.  
- Concurrency controls via row‑level locks and `SELECT … FOR UPDATE` in SQL.
