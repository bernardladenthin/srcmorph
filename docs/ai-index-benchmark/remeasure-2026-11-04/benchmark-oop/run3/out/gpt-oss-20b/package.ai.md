### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:57:18Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A Maven‑based framework that orchestrates AI‑driven source‑code generation, validation, and file‑selection, backed by pluggable local or remote inference engines and a set of low‑level utilities for data processing and database‑centric business logic.

#### Purpose
- Centralize AI generation parameters and file‑selection logic for the srcmorph Maven plugin.
- Provide a unified, pluggable provider interface for local llama.cpp, mock, and OpenAI‑compatible backends.
- Offer reusable OpenCL kernels, a lock‑free ring buffer, and auxiliary scripts for backup and reporting.
- Persist and enforce business rules for library lending, e‑commerce order lines, and warehouse stock synchronization.

#### Responsibilities
- **AI Generation Framework** – configuration, condition evaluation, fact extraction, oversize handling, provider abstraction, local/remote inference, and completion parsing.
- **Low‑Level Utilities** – OpenCL histogram kernels, power‑of‑two ring buffer, and command‑line scripts for backups and revenue reports.
- **Database Modules** – library catalog and loan logic, shop order line and stock management, warehouse stock sync and audit.

#### Key units
- **Java AI Core**
  - `SrcMorphConfiguration`, `AiGenerationConfig` – plugin‑level and step‑level settings.
  - `AiConditionEvaluator`, `AiCondition`, `AiConditionGroup` – file‑selection logic.
  - `AiFactExtractor` – deterministic fact block builder.
  - `AiOversizeStrategy` – enum for handling files that exceed context window.
  - `AiGenerationProvider`, `AiGenerationProviderFactory` – provider contract and resolver.
  - `LlamaCppJniAiGenerationProvider` – local llama.cpp inference.
  - `OpenAiCompatibleProviderSupport` – HTTP‑based remote inference.
  - `AiCompletionParser` – strips internal thinking blocks.
- **Other Utilities**
  - `histogram_partial`, `histogram_reduce` – OpenCL kernels for 256‑bin byte histograms.
  - `ringbuf_t`, `ringbuf_*` – lock‑free single‑producer/single‑consumer byte buffer.
  - `rotate_backups.sh`, `sales_report.py` – backup rotation and revenue reporting scripts.
- **SQL Modules**
  - **Library** – tables (`author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`), functions (`fn_checkout_book`, `fn_late_fee`), procedures (`sp_return_book`), trigger (`trg_loan_audit_fn`), views (`overdue_loans`, `member_loan_stats`, `popular_books`).
  - **Shop** – tables (`product`, `customer_order`, `order_item`), procedure (`add_order_item`).
  - **Warehouse** – tables (`StockLevel`, `SupplierFeed`, `SyncLog`), procedure (`usp_SyncStock`).

#### Data flow
1. **AI Pipeline**
   - Load `SrcMorphConfiguration` → build `AiGenerationConfig` per step.
   - Evaluate `AiCondition` tree against `AiFileContext` to select files.
   - Extract facts with `AiFactExtractor` and assemble prompt.
   - Apply `AiOversizeStrategy` if file size exceeds model context.
   - Resolve provider via `AiGenerationProviderFactory` → instantiate `LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleProviderSupport`.
   - Generate completion → parse with `AiCompletionParser` → return final text.
2. **Utility Flow**
   - Histogram: input byte stream → `histogram_partial` → `histogram_reduce` → 256‑bin result.
   - Ring buffer: `ringbuf_init` → `ringbuf_write`/`ringbuf_read` → status queries.
   - Scripts: `rotate_backups.sh` archives source → prunes old archives; `sales_report.py` parses CSV → aggregates revenue → outputs report.
3. **Database Flow**
   - Library: `fn_checkout_book` validates, inserts `loan`, updates `book.copies_free`, triggers audit; `sp_return_book` updates `loan`, restores copies, triggers audit; `fn_late_fee` calculates overdue charges; views aggregate data.
   - Shop: `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements stock, recomputes order total.
   - Warehouse: `usp_SyncStock` aggregates `SupplierFeed` since a timestamp, merges into `StockLevel`, logs run in `SyncLog`.

#### Dependencies
- **Internal** – AI core classes (`AiCondition`, `AiPromptDefinition`, `AiModelDefinition`, etc.), OpenCL kernels, ring buffer structs, SQL tables and procedures.
- **External** – `net.ladenthin.llama` for local inference, `com.fasterxml.jackson.databind` for JSON, Lombok, Java time and regex, OpenCL runtime, standard C libraries, Bash utilities (`tar`, `date`, etc.), Python modules (`argparse`, `csv`, `datetime`), database engines (PostgreSQL, MySQL 8, SQL Server).

#### Cross‑cutting
- Defensive copying and stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Immutable configuration builders (`LlamaCppJniConfig.Builder`).
- Validation via `IllegalArgumentException`; I/O errors via `IOException`.
- Timing metrics captured in `AiGenerationTimings`.
- Thread‑safe provider usage; first‑call race on lazy `LlamaModel` noted.
- Ring buffer uses bitwise modulo for wrap‑around; single‑producer/single‑consumer design removes locks.
- Two‑pass reduction pattern in histogram kernels.
- Consistent error handling across utilities and scripts (status enums, exit codes).
- Audit tables (`loan_audit`, `SyncLog`) and triggers enforce transactional integrity.
- Concurrency controls: row‑level locking on `product` in Shop, transactional sync in Warehouse.
