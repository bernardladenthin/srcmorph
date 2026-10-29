### ai
- H: 1.0
- C: C91EB333
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:18:34Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A configuration‑driven AI‑powered source‑code morphing engine that orchestrates file selection, condition evaluation, fact extraction, and AI generation for code transformations.

#### Purpose
- Orchestrate AI‑driven source‑code transformations.
- Provide a pluggable AI provider layer (local llama.cpp or OpenAI‑compatible APIs).

#### Responsibilities
- **Configuration** – load runtime settings (`SrcMorphConfiguration`, `AiGenerationConfig`).
- **File handling** – filter by extension, size, exclude globs; decide if source text is needed.
- **Condition logic** – evaluate `AiCondition` trees against file metadata.
- **Fact extraction** – build deterministic fact blocks from regex counters.
- **Oversize strategy** – choose failure, sampling, or split for files exceeding context windows.
- **AI generation** – abstract generation via `AiGenerationProvider`; select provider via factory.
- **Local inference** – load GGUF models with llama.cpp JNI.
- **Remote inference** – build OpenAI‑compatible requests.
- **Response cleanup** – strip internal reasoning blocks from completions.

#### Key units
- `SrcMorphConfiguration` – mutable bean for run‑time settings.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – deterministic fact counter utility.
- `AiOversizeStrategy` – enum for oversized‑file handling.
- `AiGenerationProvider` – interface for generating text and timings.
- `AiGenerationProviderFactory` – resolves provider keys.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference provider.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible APIs.
- `AiCompletionParser` – removes internal “thinking” blocks from completions.

#### Data flow
1. Load `SrcMorphConfiguration` from Maven/JSON/YAML.  
2. Filter files by extension, size, exclude globs.  
3. Use `AiConditionEvaluator` to decide if source text is required.  
4. Extract facts with `AiFactExtractor` to build a facts header.  
5. Build an `AiGenerationRequest` (prompt key, source file, text, header).  
6. Resolve provider via `AiGenerationProviderFactory`; instantiate provider.  
7. Call provider’s `generate`/`generateWithTimings` (local or remote).  
8. Parse completion with `AiCompletionParser`.  
9. Apply `AiOversizeStrategy` if file exceeds context limits.

#### Dependencies
- **Internal** – `AiCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.  
- **External** – Java core (`java.time`, `java.util`, `java.io`, `java.util.regex`), Lombok, JSPECIFY, `net.ladenthin.llama` (model, parameters, JSON parsing), Jackson, `java.net.URL`.

#### Cross‑cutting
- Stateless evaluators and extractors; providers are stateless after construction.  
- Immutability via record‑style `LlamaCppJniConfig`.  
- Error handling: `IllegalArgumentException` for misconfig, `IOException` for provider failures.  
- Timing metrics captured in `AiGenerationTimings`.

---

#### Other Utilities
> GPU‑accelerated histogram builder, lock‑free byte ring buffer, and command‑line utilities for backup rotation and sales reporting.

#### Purpose
- High‑performance byte histogramming via OpenCL.  
- Lightweight, single‑producer/single‑consumer ring buffer.  
- Reusable shell/Python scripts for backup and revenue aggregation.

#### Responsibilities
- **Histogram** – per‑group partial histograms → global 256‑bin result.  
- **Ring buffer** – lock‑free enqueue/dequeue, power‑of‑two sizing.  
- **Scripts** – backup rotation with retention policy; CSV order parsing and revenue aggregation.

#### Key units
- OpenCL kernels: `histogram_partial`, `histogram_reduce`.  
- Ring buffer API: `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.  
- Scripts: `rotate_backups.sh`, `sales_report.py` (Order dataclass, revenue aggregation).

#### Data flow
1. **Histogram** – `histogram_partial` updates local bins; `histogram_reduce` aggregates into final histogram.  
2. **Ring buffer** – `ringbuf_init` allocates; `ringbuf_write`/`ringbuf_read` move data; occupancy via `ringbuf_used`/`ringbuf_free_space`.  
3. **Scripts** – `rotate_backups.sh` creates timestamped tar archives, moves them, deletes old ones; `sales_report.py` reads CSV, aggregates revenue by month/region, outputs TSV.

#### Dependencies
- OpenCL built‑ins for histogram kernels.  
- C standard library for ring buffer.  
- Shell utilities (`tar`, `date`, `du`, etc.) for backup script.  
- Python stdlib (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross‑cutting
- Fixed 256‑bin histogram size.  
- Power‑of‑two ring buffer capacity for efficient modulo.  
- Simple status codes for error reporting.

---

#### SQL Schemas & Procedures
> Relational schemas and stored procedures for library cataloging, webshop orders, and warehouse inventory, with business logic, reporting, and audit trails.

#### Purpose
- Persist and enforce business rules across library, shop, and warehouse domains.  
- Provide reporting views and audit logs.  
- Expose transactional stored procedures.

#### Responsibilities
- **Library** – catalog, member, loan management; checkout/return logic; overdue and popularity reporting.  
- **Shop** – product catalog, order processing, stock reservation, order total calculation.  
- **Warehouse** – nightly supplier feed ingestion, inventory aggregation, sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.  
  - Trigger: `trg_loan_audit` on `loan`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `StockLevel`, `SupplierFeed`, `SyncLog`.  
  - Procedure: `usp_SyncStock(@Since)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member, updates `book.copies_free`, inserts `loan`, triggers audit. `sp_return_book` updates `loan.returned_on`, restores copies, triggers audit. Views aggregate loan data.  
2. **Shop** – `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` by `(WarehouseId, Sku)`, merges into `StockLevel`, logs run details in `SyncLog`.

#### Dependencies
- Internal cross‑package tables referenced by procedures and triggers.  
- External system catalogs (PostgreSQL, MySQL, SQL Server functions).  
- Migration scripts for evolving library schema.

#### Cross‑cutting
- Audit & logging via `loan_audit` and `SyncLog`.  
- Constraints, indexes, and checks enforce data integrity.  
- Error handling in procedures raises descriptive errors; SQL Server `TRY…CATCH` logs failures.  
- Transactional integrity ensured by explicit transactions.  
- Late fee logic capped at $20.00 with 50¢ per overdue day.  
- Branch & copy model migrated to explicit `book_copy` rows.
