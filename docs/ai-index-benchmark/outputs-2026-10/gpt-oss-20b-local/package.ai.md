### ai
- H: 1.0
- C: FFA8A94C
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:24:56Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A runtime‑driven AI source‑code transformation engine that orchestrates file selection, deterministic fact extraction, and AI‑powered code generation or analysis across a multi‑language codebase.

#### Purpose
- Orchestrate AI‑driven source‑code transformations.
- Validate file eligibility, extract deterministic facts, and route oversized files.
- Provide a pluggable AI generation layer with timing and parsing support.

#### Responsibilities
- **Configuration & Validation** – load runtime settings, evaluate file‑selection conditions, enforce oversize strategies.
- **Fact Extraction** – build deterministic fact blocks from regex counters for AI inputs.
- **AI Generation** – select provider, construct prompts, send requests, parse completions, expose timing metrics.

#### Key units
- `SrcMorphConfiguration` – mutable bean for paths, models, prompts, routing rules, overrides.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – static builder for deterministic fact headers.
- `AiOversizeStrategy` – enum (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- `AiGenerationProviderFactory` – resolves and creates `AiGenerationProvider` implementations.
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`.
- Concrete providers: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniConfig` – immutable config for llama.cpp inference.
- `AiCompletionParser` – strips chain‑of‑thought blocks.
- `AiGenerationTimings` – captures prompt/cache tokens, decode rates, predicted token counts.

#### Data flow
1. Load `SrcMorphConfiguration` from Maven/JSON/YAML.  
2. `AiConditionEvaluator.validate` checks `AiCondition` trees; `matches` decides file inclusion.  
3. `AiFactExtractor.factsBlock` creates deterministic facts header for oversized files.  
4. `AiGenerationProviderFactory.create` selects provider.  
5. Provider builds chat‑completion payload via `AiPromptSupport`.  
6. Provider sends request (local llama.cpp or OpenAI‑compatible API) and receives raw completion.  
7. `AiCompletionParser.parseCompletion` removes internal reasoning blocks.  
8. Return plain string or `AiGenerationTimings` to caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.  
- **External** – `net.ladenthin.llama` (JNI model, parameters, JSON parsing), Jackson (`ObjectMapper`), `org.jspecify.annotations.Nullable`.  
- **Runtime** – native llama.cpp library, HTTP client for OpenAI‑compatible endpoints.  
- **Java Core** – `java.util`, `java.time`, `java.io`, `java.util.regex`.

#### Cross-cutting
- Stateless & thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`, `LlamaCppJniConfig`).  
- Defensive copying & immutability in configuration beans.  
- Validation errors (`IllegalArgumentException`, `IllegalStateException`); IOExceptions for missing reasoning blocks or endpoint issues.  
- Extensibility via `AiOversizeStrategy` and future condition node types.  
- Timing & metrics (`AiGenerationTimings`) for performance analysis.  
- Logging of truncation, cache reuse, and key validation issues.

---

#### other/
> Provides low‑level byte‑stream utilities and maintenance scripts for data pipelines.

#### Purpose
- Efficient byte‑frequency analysis via OpenCL kernels.  
- Lock‑free ring buffer for single‑producer/single‑consumer byte streams.  
- Automate backup retention and generate monthly sales revenue reports.

#### Responsibilities
- **GPU Acceleration** – two‑pass histogram kernels (`histogram_partial`, `histogram_reduce`).  
- **Concurrent Buffering** – power‑of‑two ring buffer API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`).  
- **Maintenance Automation** – Bash backup rotation (`rotate_backups.sh`) and Python sales reporting (`sales_report.py`).

#### Key units
- `histogram_partial` / `histogram_reduce` kernels – local accumulation and global reduction.  
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`.  
- `rotate_backups.sh` – backup rotation script.  
- `sales_report.py` – CLI tool; `parse_orders`, `revenue_by_month_and_region`, `main`; `Order` dataclass.

#### Data flow
1. **Histogram** – `histogram_partial` reads input bytes → updates local bins → writes partials; `histogram_reduce` aggregates partials → final histogram.  
2. **Ring Buffer** – `ringbuf_init` allocates buffer; producer calls `ringbuf_write`; consumer calls `ringbuf_read`; metrics via `ringbuf_used` / `ringbuf_free_space`; `ringbuf_free` cleans up.  
3. **Scripts** – `rotate_backups.sh` validates paths, tars source, timestamps, deletes excess archives; `sales_report.py` parses CSVs into `Order`, aggregates revenue by month/region, outputs TSV.

#### Dependencies
- OpenCL 1.2 built‑ins for kernels.  
- Standard C library (`malloc`, `free`, `memcpy`, etc.) for ring buffer.  
- Bash utilities (`tar`, `date`, `du`, `basename`, `ls`).  
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`).

#### Cross-cutting
- Atomicity & lock‑free design in ring buffer.  
- Power‑of‑two modulo for efficient wrap‑around.  
- Two‑pass reduction pattern in histogram kernels.  
- Error handling via status enums, `set -euo pipefail`, and `ValueError`.  
- Console logging for status and cleanup actions.

---

#### sql/
> Database packages that provide core persistence, business logic, and reporting for a library, an e‑commerce shop, and a warehouse inventory system.

#### Purpose
- Persist and enforce domain rules for library catalog, shop orders, and warehouse stock.  
- Expose read‑only reporting views and audit trails.  
- Coordinate nightly data ingestion and inventory reconciliation.

#### Responsibilities
- **Library** – schema, checkout/return logic, late‑fee calculation, audit trail, reporting views.  
- **Shop** – order‑item CRUD, inventory reservation, order total recalculation via stored procedure.  
- **Warehouse** – nightly supplier feed ingestion, stock reconciliation, sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, triggers `loan_audit`; `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, triggers audit; `fn_late_fee` computes charges; reporting views aggregate loan, member, book, and author data.  
2. **Shop** – `add_order_item` locks the `product` row, checks stock, inserts/updates `order_item`, decrements `product.stock`, and recomputes `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows after a timestamp, `MERGE`s into `StockLevel`, records inserted/updated counts, logs the run in `SyncLog`, and ensures atomicity with a single transaction.

#### Dependencies
- Internal tables and triggers within each package.  
- PostgreSQL core functions for the library; MySQL 8 for the shop; SQL Server system functions for the warehouse.  
- Cross‑package: none; each operates on its own schema.

#### Cross-cutting
- Integrity via foreign keys, check constraints, and unique indexes.  
- Audit trails (`loan_audit`, `SyncLog`) capture every state change.  
- Concurrency controls (`FOR UPDATE`, transaction isolation, trigger‑based audit) prevent race conditions.  
- Business rules encapsulated in stored functions/procedures (late‑fee policy, stock reservation, atomic sync).
