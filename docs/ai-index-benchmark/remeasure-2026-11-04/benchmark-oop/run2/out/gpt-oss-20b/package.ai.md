### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:42:43Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A modular AI‑driven source‑code transformation engine that selects files, extracts facts, and generates code changes via pluggable local or cloud providers.

#### Purpose
- Orchestrate AI‑based source‑code analysis and generation for the srcmorph tool.
- Provide configurable file selection, fact extraction, and generation pipelines.

#### Responsibilities
- **Configuration & Selection** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiConditionGroup`.
- **Fact Extraction** – `AiFactExtractor`, `AiFactCounter`.
- **Generation Pipeline** – `AiGenerationConfig`, `AiOversizeStrategy`, `AiGenerationProviderFactory`.
- **Provider Implementation** – `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`.

#### Key units
- `SrcMorphConfiguration` – mutable holder for file paths, prompts, and model metadata.
- `AiConditionEvaluator` – validates and evaluates file‑selection trees.
- `AiFactExtractor` – regex‑based fact counter for AI prompts.
- `AiGenerationConfig` – JavaBean for AI model, sampling, caching, and DRY settings.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized files.
- `AiGenerationProvider` – interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves provider keys to concrete implementations.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp JNI provider with lazy model loading.
- `OpenAiCompatibleProviderSupport` – builds JSON payloads and normalises endpoints for OpenAI‑compatible APIs.
- `AiCompletionParser` – strips internal “thinking” blocks from LLM outputs.
- `LlamaCppJniConfig` – immutable record of 37 llama.cpp tuning knobs.

#### Data flow
1. **Setup** – `SrcMorphConfiguration` supplies file selection rules, prompts, and AI settings.  
2. **Selection** – `AiConditionEvaluator` checks each file against `AiConditionGroup`; selected files may trigger line‑count checks.  
3. **Fact Extraction** – `AiFactExtractor` scans source text with `AiFactCounter` patterns, producing a facts block for prompts.  
4. **Provider Selection** – `AiGenerationProviderFactory` creates the appropriate `AiGenerationProvider`.  
5. **Generation** – Provider receives an `AiGenerationRequest`; for OpenAI‑compatible providers, `OpenAiCompatibleProviderSupport` builds the request body; for local llama.cpp, native chat completion is invoked.  
6. **Response Handling** – `AiCompletionParser` cleans the raw output; provider returns plain text or `AiGenerationTimings`.  
7. **Output** – Generated code is written to the output directory defined in `SrcMorphConfiguration`.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`.
- **External**: `java.time.Instant`, `java.util.regex`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`), Jackson `ObjectMapper`, `net.ladenthin.llama` (model, args, JSON parser), `net.ladenthin.srcmorph.support`.
- **Configuration Sources**: Maven plugin parameters or external config files populate `SrcMorphConfiguration` and `AiGenerationConfig`.

#### Cross‑cutting
- Thread‑safe, stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`).  
- Defensive copying in `AiConditionGroup.setConditions`.  
- Immutable configuration objects (`LlamaCppJniConfig`, provider support classes).  
- Robust error handling with clear exception messages for invalid configs or missing markers.  
- Timing metrics via `generateWithTimings` default implementation.  
- Logging of truncation, prompt‑cache reuse, and provider status (details omitted in summaries).

---

#### Purpose
- Deliver high‑performance byte‑histogram computation via OpenCL.  
- Offer a reusable, lock‑free circular buffer for single‑producer/single‑consumer byte streams.  
- Supply shell and Python utilities for backup rotation and sales data aggregation.

#### Responsibilities
- **GPU Histogram Engine** – two‑pass reduction kernels for 256‑bin byte histograms.  
- **Ring Buffer API** – power‑of‑two circular buffer with init, free, read, write, and capacity queries.  
- **Utility Scripts** – Bash backup rotation and Python monthly sales reporting.

#### Key units
- `histogram_partial` / `histogram_reduce` kernels (OpenCL) – build and aggregate per‑group histograms.  
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – core ring buffer types and operations.  
- `rotate_backups.sh` – backup creation and pruning script.  
- `sales_report.py` – CSV parser, `revenue_by_month_and_region`, CLI entry point.

#### Data flow
1. **Histogram**: byte stream → `histogram_partial` (per‑group local bins) → `histogram_reduce` (global sum) → final 256‑bin histogram.  
2. **Ring Buffer**: producer writes via `ringbuf_write`; consumer reads via `ringbuf_read`; indices wrap modulo power‑two capacity; metrics via `ringbuf_used` / `ringbuf_free_space`.  
3. **Utilities**: source directory → `tar` archive → backup directory; old archives removed. CSV files → `parse_orders` → aggregated revenue → printed report.

#### Dependencies
- OpenCL built‑ins (`get_local_id`, `atomic_inc`, `barrier`, etc.) for histogram kernels.  
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer implementation.  
- Bash utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`) for backup script.  
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross‑cutting
- **Power‑of‑two constraint** in ring buffer capacity checks.  
- **Lock‑free design**: single‑producer/single‑consumer model, no mutexes.  
- **Atomic operations** and **barriers** for intra‑group synchronization in OpenCL kernels.  
- **Consistent error handling**: `ringbuf_status_t` codes, Bash `set -euo pipefail`, Python `ValueError` for CSV parsing.  
- **UTC timestamps** for backup naming and report dates.

---

#### Purpose
- Persist and enforce business rules for library catalog, shop orders, and warehouse stock.  
- Provide reporting, audit logging, and transactional integrity across each domain.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, branch‑aware lending, reporting, and audit.  
- **Shop** – product catalog, order processing, stock reservation, and order total calculation.  
- **Warehouse** – supplier feed ingestion, stock level maintenance, and sync run auditing.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions: `fn_checkout_book`, `fn_late_fee`.  
  - Procedure: `sp_return_book`.  
  - Trigger: `trg_loan_audit` on `loan`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock`.

#### Data flow
- **Library** – checkout → `fn_checkout_book` → `loan` insert + audit; return → `sp_return_book` → `loan` update + audit; reporting views aggregate loan/member/book data.  
- **Shop** – `add_order_item` locks product, validates stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
- **Warehouse** – `usp_SyncStock` aggregates new `SupplierFeed` rows, merges into `StockLevel`, logs counts/errors in `SyncLog`.

#### Dependencies
- **Internal** – cross‑package tables referenced by procedures and triggers.  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL 8 features (`FOR UPDATE`, ENUM), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **Indexes** – e.g., `idx_book_author`, `idx_loan_member`, `idx_order_customer`, `idx_book_copy`.

#### Cross‑cutting
- **Integrity & Constraints** – data validity enforced via checks (e.g., `copies_free <= copies_total`, `due_on > loaned_on`).  
- **Audit Trails** – `loan_audit` and `SyncLog` capture every state change.  
- **Concurrency Control** – row locks in `add_order_item`; transactional boundaries in `usp_SyncStock`.  
- **Materialized View Refresh** – `popular_books` requires periodic refresh.  
- **Branch‑aware Lending** – post‑migration loans reference `book_copy` tied to `branch`.
