### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:04:19Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A configuration‑driven AI source‑morphing engine that indexes code, evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI generation for code summaries and transformations.

#### Purpose
- Central configuration holder for the srcmorph AI code‑generation tool.  
- Provides utilities to evaluate file‑selection conditions and extract deterministic facts for AI summarisation.  
- Defines strategies for handling oversized source files.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates runtime parameters (paths, AI providers, model settings, prompts, field‑generation rules, fact definitions).  
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical trees (`and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`) against `AiFileContext`.  
- **Fact Extraction** – `AiFactExtractor` builds deterministic facts blocks from regex counters and validates counter configurations.  
- **Oversize Handling** – `AiOversizeStrategy` enumerates routing behaviours for files exceeding model context limits.  
- **Generation Configuration** – `AiGenerationConfig` holds AI provider‑specific parameters (model path, sampling, retry, stop strings, caching, GPU/CPU settings, etc.).

#### Key units
- `SrcMorphConfiguration` – mutable bean for all srcmorph settings.  
- `AiConditionEvaluator` – stateless validator/evaluator of `AiCondition` trees.  
- `AiFactExtractor` – static utility for building facts blocks.  
- `AiOversizeStrategy` – enum of routing strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).  
- `AiGenerationConfig` – data holder for AI generation parameters.  
- `AiGenerationProvider` – interface for generating text from LLMs.  
- `AiGenerationProviderFactory` – resolves provider keys and builds concrete providers.  
- `LlamaCppJniConfig` – immutable config for llama.cpp provider.  
- `LlamaCppJniAiGenerationProvider` – local GGUF inference implementation.  
- `OpenAiCompatibleProviderSupport` – HTTP request/response handling for OpenAI‑compatible APIs.  
- `AiCompletionParser` – cleans raw LLM output.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven, JSON/YAML, or programmatic input.  
2. **File Routing** – `AiConditionEvaluator` checks each source file against configured `AiCondition` trees to decide inclusion.  
3. **Fact Generation** – For included files, `AiFactExtractor` produces a deterministic facts block used to control AI summarisation size.  
4. **Oversize Decision** – If a file exceeds context limits, `AiOversizeStrategy` determines whether to fail, sample, map‑reduce, or deterministically truncate.  
5. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` resolves the provider key; `create` returns the matching provider.  
6. **AI Generation** – Provider’s `generate` or `generateWithTimings` is invoked:  
   - *Local*: `LlamaCppJniAiGenerationProvider` loads a GGUF model via JNI, runs chat completions, parses JSON, and reports timings.  
   - *Cloud*: `OpenAiCompatibleProviderSupport` builds JSON body, endpoint, API key, sends HTTP request, parses `ChatResponse`, and extracts metrics.  
7. **Post‑processing** – `AiCompletionParser.parseCompletion` removes internal reasoning blocks from the raw completion before persisting.

#### Dependencies
- **Internal** – `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `Java8CompatibilityHelper`.  
- **External** – `java.time`, `java.util`, `java.util.regex`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`, `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`.

#### Cross‑cutting
- **Thread safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; providers are stateless or lazily initialized; factories are thread‑safe.  
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness, throwing `IllegalArgumentException` on errors.  
- **Immutability** – `LlamaCppJniConfig` and `OpenAiCompatibleProviderSupport` are immutable; builders are single‑threaded.  
- **Error handling** – Validation errors surface as unchecked exceptions; I/O errors propagate as `IOException`; unknown provider keys trigger `IllegalArgumentException`.  
- **Logging & metrics** – Providers log cache reuse, truncation warnings, and timing metrics; `AiGenerationTimings` captures token usage and latency.

---

#### Purpose (other)
- Deliver high‑performance byte‑histogram generation via OpenCL.  
- Offer a lightweight, non‑blocking circular buffer for single‑producer/single‑consumer byte streams.  
- Supply shell and Python tools for routine backup rotation and revenue analysis tasks.

#### Responsibilities (other)
- **GPU Kernels** – compute 256‑bin histograms in two passes (partial per‑group, global reduction).  
- **Ring Buffer** – manage fixed‑size, power‑of‑two circular storage with non‑blocking read/write and usage metrics.  
- **Utilities** – automate directory backup rotation and generate monthly sales revenue reports from CSV data.

#### Key units (other)
- `histogram_partial`, `histogram_reduce` (OpenCL kernels).  
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – core ring‑buffer API.  
- `rotate_backups.sh` – Bash script for timestamped tarball creation and retention cleanup.  
- `sales_report.py` – Python module exposing `parse_orders`, `revenue_by_month_and_region`, and CLI `main`.

#### Data flow (other)
1. **Histogram**: input byte stream → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates into final 256‑bin histogram.  
2. **Ring Buffer**: `ringbuf_init` allocates buffer → `ringbuf_write` copies producer data → `ringbuf_read` retrieves consumer data → usage queried via `ringbuf_used`/`ringbuf_free_space`.  
3. **Utilities**: `rotate_backups.sh` validates paths, creates `*.tar.gz`, moves to backup dir, deletes excess archives; `sales_report.py` parses CSVs, filters orders, aggregates revenue by month/region, outputs TSV.

#### Dependencies (other)
- **OpenCL** built‑ins (`get_local_id`, `atomic_inc`, etc.) for kernels.  
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.  
- Shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`, `mkdir`, `basename`, `dirname`) for backup script.  
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales reporting.

#### Cross‑cutting (other)
- Two‑pass reduction pattern and atomic increments in GPU kernels.  
- Power‑of‑two sizing and bitwise masking for ring‑buffer wrap‑around.  
- Single‑producer/single‑consumer design eliminates locks in ring buffer.  
- Robust error handling: ring buffer returns `RINGBUF_*` codes; shell script uses `set -euo pipefail`; Python raises `ValueError` on malformed CSV.

---

#### Purpose (sql)
- Persist and enforce business rules for library catalog, member, and loan data.  
- Atomically manage order line items, inventory reservations, and order totals in an e‑commerce context.  
- Synchronize nightly supplier feeds with warehouse stock levels while maintaining an audit trail.

#### Responsibilities (sql)
- **Library** – data definition, borrowing logic, audit logging, reporting, migration, seeding.  
- **Shop** – order line CRUD, inventory locking, total recalculation.  
- **Warehouse** – supplier feed ingestion, stock reconciliation, sync logging.

#### Key units (sql)
- **Tables**  
  - `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy` (PostgreSQL).  
  - `product`, `customer_order`, `order_item` (MySQL).  
  - `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` (SQL Server).  
- **Functions / Procedures**  
  - `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn` (PostgreSQL).  
  - `add_order_item(p_order_id, p_sku, p_qty)` (MySQL).  
  - `dbo.usp_SyncStock(@Since DATETIME2)` (SQL Server).  
- **Views / Materialized Views**  
  - `overdue_loans`, `member_loan_stats`, `popular_books` (PostgreSQL).

#### Data flow (sql)
1. **Library** – checkout → validate member & copies → insert `loan` → trigger logs `CHECKOUT`; return → update `loan` → trigger logs `RETURN`.  
2. **Shop** – `add_order_item` locks `product`, validates stock, inserts/updates `order_item`, decrements `product.stock`, recomputes `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows, merges into `StockLevel` (insert or update), records counts and errors in `SyncLog`.

#### Dependencies (sql)
- **Internal** – foreign keys among library tables; MySQL tables linked by primary/foreign keys; SQL Server tables referenced by the sync procedure.  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`, `FILTER`, `HAVING`); MySQL 8 features (`SELECT … FOR UPDATE`, `ON DUPLICATE KEY UPDATE`, `ENUM`); SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`).

#### Cross‑cutting (sql)
- **Audit & Logging** – `loan_audit` and `SyncLog` capture every state change.  
- **Constraints & Checks** – enforce copy counts, due dates, enum statuses, and birth year ranges.  
- **Transactionality** – each operation runs within a single transaction; MySQL procedure and SQL Server sync use implicit/explicit transaction control.  
- **Concurrency** – MySQL row‑level locking on `product`; PostgreSQL trigger ensures atomic audit logging.  
- **Refresh / Maintenance** – `popular_books` materialized view requires periodic refresh; migration script updates schema for branch and copy tracking.
