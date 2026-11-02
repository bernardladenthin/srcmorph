### ai
- H: 1.0
- C: 5E27BCB7
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:44:53Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A Maven‑based AI‑powered source‑code transformation engine that validates, extracts facts, and orchestrates local or remote LLM generation with fine‑grained configuration.

#### Purpose
- Provide a configurable pipeline for AI‑driven source‑code morphing.
- Validate file‑level conditions, handle oversized inputs, and generate deterministic fact headers.
- Abstract multiple AI back‑ends (local llama.cpp, OpenAI‑compatible APIs, mocks) with consistent request/response handling and timing metrics.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `AiGenerationConfig`.
- **File Selection & Fact Extraction** – evaluate `AiCondition` trees against `AiFileContext`, count regex matches, build facts blocks.
- **Oversize Handling** – decide between FAIL, SAMPLE, MAP_REDUCE, or DETERMINISTIC strategies.
- **Provider Abstraction** – `AiGenerationProvider`, `AiGenerationProviderFactory`, concrete providers (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Request/Response Processing** – `AiCompletionParser`, `OpenAiCompatibleProviderSupport`, `AiPromptSupport`.
- **Timing & Metrics** – `AiGenerationTimings` captures token counts, TPS, and predicted metrics.

#### Key units
- **`SrcMorphConfiguration`** – mutable bean holding paths, providers, prompts, models, field rules, and facts.
- **`AiConditionEvaluator`** – stateless validator/evaluator for `AiCondition` trees.
- **`AiFactExtractor`** – builds deterministic facts header from regex counters.
- **`AiOversizeStrategy`** – enum of oversize handling modes.
- **`AiGenerationConfig`** – JavaBean with AI provider parameters for a run.
- **`AiGenerationProvider`** – interface for generating text and optional timings.
- **`AiGenerationProviderFactory`** – resolves provider keys and creates instances.
- **`LlamaCppJniConfig`** – immutable llama.cpp settings.
- **`LlamaCppJniAiGenerationProvider`** – local inference provider using `LlamaModel`.
- **`OpenAiCompatibleProviderSupport`** – helper for OpenAI‑compatible chat‑completion calls.
- **`AiCompletionParser`** – removes leading thinking blocks from raw completions.

#### Data flow
1. Load `SrcMorphConfiguration` (Maven plugin, Jackson, or code).  
2. Validate conditions with `AiConditionEvaluator.validate()`.  
3. Match files via `AiConditionEvaluator.matches()`.  
4. Handle oversize via `AiOversizeStrategy`; deterministic mode uses `AiFactExtractor.factsBlock()`.  
5. Build `AiGenerationRequest` (prompt key, source text, header).  
6. Resolve provider with `AiGenerationProviderFactory.effectiveProvider()`.  
7. Generate text: local provider loads `LlamaModel` and calls `chatComplete`; remote provider builds HTTP body, sends request, parses response.  
8. Post‑process with `AiCompletionParser.parseCompletion()` to strip thinking blocks.  
9. Persist or return transformed source.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.  
- **External**: `java.time`, `java.util.regex`, `lombok`, `org.jspecify.annotations`, `net.ladenthin.llama` (JNI model, parameters, JSON parser), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`, `java.util`.  
- **Provider‑specific**: llama.cpp JNI libraries, OpenAI‑compatible HTTP APIs, mock implementations.

#### Cross‑cutting
- Thread‑safe stateless/immutable helpers (`AiConditionEvaluator`, `AiFactExtractor`, `LlamaCppJniConfig`).  
- Validation defaults in configuration beans; errors surface as `IllegalArgumentException`/`IllegalStateException`.  
- Timing instrumentation via `AiGenerationTimings` for both local and remote providers.  
- Logging of truncation, cache reuse, and malformed thinking blocks.

---

> A collection of low‑latency byte‑stream utilities: GPU‑accelerated histograms, a lock‑free ring buffer, and auxiliary shell/Python scripts for maintenance.

#### Purpose
- Provide efficient, reusable components for byte‑stream manipulation and auxiliary tooling.

#### Responsibilities
- **GPU‑accelerated histogram** – two‑pass OpenCL kernels for 256‑bin byte frequency.  
- **Lock‑free FIFO** – power‑of‑two circular buffer for single‑producer/single‑consumer byte streams.  
- **Utility scripts** – backup rotation and CSV‑based sales reporting.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels).  
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` (C API).  
- `rotate_backups.sh`, `sales_report.py` (maintenance scripts).

#### Data flow
1. Histogram: byte array → per‑group local histograms → global reduction → 256‑bin result.  
2. Ring buffer: `ringbuf_init` → `ringbuf_write` → `ringbuf_read` → state queries (`ringbuf_used`, `ringbuf_free_space`).  
3. Scripts: `rotate_backups.sh` archives and cleans old backups; `sales_report.py` parses CSV orders and outputs revenue reports.

#### Dependencies
- OpenCL built‑ins (`get_local_id`, `atomic_inc`).  
- Standard C library (`malloc`, `memcpy`).  
- Shell utilities (`tar`, `date`).  
- Python modules (`csv`, `decimal`).

#### Cross‑cutting
- Power‑of‑two size enforcement and modulo arithmetic in ring buffer.  
- Two‑pass reduction pattern with local memory aggregation in kernels.  
- Non‑blocking semantics: write/read return actual bytes processed.  
- Explicit error codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) and clear exit codes in scripts.

---

> A unified data‑management toolkit that models a multi‑branch library, an e‑commerce shop, and a warehouse inventory system, providing schema, business logic, and synchronization routines across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Deliver a cohesive set of database objects for library lending, online sales, and warehouse stock management.  
- Enable consistent data integrity, reporting, and transactional operations across three distinct domains.

#### Responsibilities
- **Library** – schema, reporting views, checkout/return logic, audit trail, and copy‑based inventory migration.  
- **Shop** – product catalog, order tracking, atomic order‑item addition with stock reservation.  
- **Warehouse** – nightly supplier feed ingestion, stock level upsert, and sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions: `fn_checkout_book`, `fn_late_fee`.  
  - Procedure: `sp_return_book`.  
  - Trigger: `trg_loan_audit` (calls `trg_loan_audit_fn`).  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, triggers audit; `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, logs audit; `fn_late_fee` computes penalties; views aggregate loan data for reporting.  
2. **Shop** – `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows since `@Since`, merges into `StockLevel` (update or insert), counts actions, logs the run in `SyncLog`, and rolls back on error.

#### Dependencies
- **Internal** – cross‑package tables (`author`, `book`, `product`, `StockLevel`, etc.) and shared functions (`fn_late_fee`).  
- **External** – PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), MySQL 8 features (`ENUM`, `SIGNAL`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_popular_books_id`, `idx_book_copy_book`, and shop/warehouse specific indexes.

#### Cross‑cutting
- Strong referential integrity with `ON DELETE CASCADE` and foreign keys.  
- Central audit mechanisms (`loan_audit`, `SyncLog`).  
- Transactional consistency: each procedure runs in a single transaction, rolling back on failure.  
- Consistent naming conventions (`*_id`, `*_cents`, `*_qty`) and type safety across schemas.  
- Migration strategy in the library replaces legacy copy counters with a normalized `book_copy` table, preserving branch awareness.
