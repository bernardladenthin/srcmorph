### ai
- H: 1.0
- C: B05AF932
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:52:57Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Multi-domain infrastructure toolkit providing AI-driven source documentation generation, GPU-accelerated byte-stream analytics with lock-free streaming, operational automation scripts, and transactional SQL persistence for library, e-commerce, and warehouse inventory systems across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Deliver an AI-powered Maven plugin pipeline that generates `.ai.md` source summaries via pluggable local (llama.cpp JNI) or remote (OpenAI-compatible) LLM backends with condition-based file selection and oversize handling
- Supply low-level systems primitives (OpenCL 256-bin histogram kernels, SPSC ring buffer) and operational CLI utilities (backup rotation, monthly sales reporting)
- Define atomic, auditable database schemas and stored procedures for library lending (PostgreSQL), retail order management (MySQL), and warehouse stock synchronization (SQL Server)

#### Responsibilities
- **AI documentation pipeline** (`java/`): Bind Maven XML to `SrcMorphConfiguration`/`AiGenerationConfig`; evaluate `AiCondition` AND/OR/NOT trees via `AiConditionEvaluator` against `AiFileContext` to filter files; route oversize files via `AiOversizeStrategy` with `AiFactExtractor` deterministic prepends; generate summaries through `AiGenerationProvider` backends (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `MockAiGenerationProvider`) resolved by `AiGenerationProviderFactory`, with `AiCompletionParser` stripping thinking blocks
- **Systems primitives & ops** (`other/`): Two-pass OpenCL histogram computation (`histogram_partial` atomic local accumulation, `histogram_reduce` global summation); lock-free power-of-two SPSC `ringbuf_t` with volatile head/tail; UTC-timestamped `rotate_backups.sh` retention pruning; `sales_report.py` CSV parsing and monthly/region revenue aggregation
- **Transactional SQL domains** (`sql/`): Library circulation lifecycle (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit`) with reporting views (`overdue_loans`, `member_loan_stats`, `popular_books`); e-commerce order line-item management (`add_order_item` with `FOR UPDATE` stock locking and total recalculation); warehouse nightly sync (`dbo.usp_SyncStock` MERGE into `dbo.StockLevel` with `dbo.SyncLog` metrics)

#### Key units
- `SrcMorphConfiguration` / `AiGenerationConfig` — Maven-bound JavaBeans for pipeline directories/filters and inference parameters (model path, context size, sampling, GPU offload, remote provider settings)
- `AiConditionEvaluator` / `AiCondition` / `AiConditionGroup` / `AiFileContext` — stateless boolean tree evaluation for file inclusion by extension, size, lines, modification date, path-glob
- `AiGenerationProvider` / `AiGenerationProviderFactory` — AutoCloseable backend contract (`generate`, `generateWithTimings`, `close`) and factory resolving `mock`, `llamacpp-jni`, `openai-compatible` keys with model-level override
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — local GGUF inference via JNI `LlamaModel` with lazy `ModelParameters` and immutable 41-field config builder
- `OpenAiCompatibleProviderSupport` — remote HTTPS `/chat/completions` client enforcing API-key security (HTTPS/loopback only) and computing usage-based `AiGenerationTimings`
- `AiOversizeStrategy` / `AiFactExtractor` / `AiFactCounter` — oversize routing enum (`FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC`) and deterministic regex match-count block generation
- `AiCompletionParser` — stateless parser stripping Gemma-4 thinking blocks from raw completions
- `histogram_partial` / `histogram_reduce` / `BINS` — OpenCL kernels and constant for 256-bin atomic histogram of uchar streams
- `ringbuf_t` / `ringbuf_status_t` — lock-free SPSC circular buffer struct with `ringbuf_init/free/write/read/used/free_space` API
- `rotate_backups.sh` / `sales_report.py` — Bash backup rotation CLI and Python sales report CLI with `Order` dataclass and `revenue_by_month_and_region` aggregator
- PostgreSQL routines: `fn_checkout_book(p_member_id, p_book_id, p_days)`, `sp_return_book(p_loan_id)`, `fn_late_fee(p_due_on, p_returned_on)`, trigger `trg_loan_audit`
- MySQL procedure: `add_order_item(p_order_id, p_sku, p_qty)` with `SIGNAL SQLSTATE '45000'` on insufficient stock
- SQL Server procedure: `dbo.usp_SyncStock(@Since DATETIME2(0))` with `MERGE` and `OUTPUT $action`

#### Data flow
- **Java AI**: Maven XML → `SrcMorphConfiguration` setters → `AiConditionEvaluator.matches()` file selection → oversize check via `AiOversizeStrategy.fromConfig` → `AiFactExtractor.factsBlock` prepend → `AiGenerationProviderFactory.create()` → `AiGenerationRequest` → local path (`LlamaCppJniConfig` → `ModelParameters` → `LlamaModel.chatComplete`) or remote path (`OpenAiCompatibleProviderSupport.requestBody()` → HTTPS POST → `ChatResponse`) → `AiCompletionParser.parseCompletion()` → `.ai.md` output with `AiGenerationTimings`
- **Histogram**: `input`/`length` → `histogram_partial` (grid-stride, `atomic_inc` local bins → per-group partials) → `histogram_reduce` (sum across `group_count`) → final `histogram[256]`
- **Ring buffer**: producer `ringbuf_write` (clamp, two `memcpy` chunks, bitmask head advance) ↔ consumer `ringbuf_read` (clamp, two `memcpy` chunks, tail advance)
- **Backups**: source dir → `tar -czf` to `.partial` → atomic `mv` to `${name}_<UTC>.tar.gz` → `ls -1t` newest-first → `rm` beyond `KEEP`
- **Sales**: CSV paths → `csv.DictReader` → currency-filtered `Order` objects → `defaultdict` keyed by `(YYYY-MM, region)` → sorted TSV stdout
- **Library SQL**: `fn_checkout_book` validates `member.is_blocked` → decrements `book.copies_free` or `book_copy` status → inserts `loan` → `trg_loan_audit` logs 'CHECKOUT'; `sp_return_book` sets `returned_on` → trigger logs 'RETURN' → `fn_late_fee` computes `LEAST(GREATEST(returned_on - due_on, 0) * 50, 2000)` cents
- **Shop SQL**: `add_order_item` locks `product` via `SELECT ... FOR UPDATE` → validates `stock >= p_qty` → upserts `order_item` → decrements `product.stock` → updates `customer_order.total_cents` from `SUM(quantity * unit_price_cents)`
- **Warehouse SQL**: `usp_SyncStock` aggregates `SupplierFeed` where `ReceivedAt >= @Since` → `MERGE` into `StockLevel` (increment `OnHand` or insert) → captures `$action` counts → writes to `SyncLog`

#### Dependencies
- Java: `net.ladenthin:llama` JNI (`LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`), Jackson Databind (`ObjectMapper`, `ObjectNode`), Lombok, `org.jspecify.annotations.Nullable`, Maven plexus reflection; cross-package `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`/`AiPromptDefinition`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`/`Java8CompatibilityHelper`/`ConvertToRecord`
- C/OpenCL: OpenCL 1.2 built-ins (`atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`, work-group query functions); C stdlib (`stdlib.h`, `string.h`, `stdint.h`, `stddef.h`)
- Python/Bash: Python stdlib (`argparse`, `csv`, `dataclasses`, `datetime.date`, `decimal.Decimal`, `collections.defaultdict`); Coreutils (`date`, `ls`, `mv`, `rm`, `tar`, `basename`, `du`); Bash builtins (`set`, `trap`, `mapfile`)
- SQL platforms: PostgreSQL (PL/pgSQL, partial indexes, materialized views, `generate_series`), MySQL 8 (InnoDB, utf8mb4, `SIGNAL SQLSTATE`, `ON DUPLICATE KEY UPDATE`, `FOR UPDATE`), SQL Server (T-SQL `MERGE` with `OUTPUT $action`, `SYSUTCDATETIME()`, `THROW`, `@@TRANCOUNT`)
- External tooling: `docs/ai-index-benchmark/tools/generate-seed-sql.py` for library seed fixtures

#### Cross-cutting
- Atomicity & transactional boundaries: explicit `BEGIN`/`COMMIT` (PostgreSQL/MySQL) and `BEGIN TRANSACTION`/`TRY...CATCH`/`ROLLBACK`/`THROW` (SQL Server); `.partial`-then-rename backup writes; `atomic_inc` + `barrier` in OpenCL kernels
- Audit & logging: trigger-based immutable `loan_audit`, `SyncLog` execution metrics with `RowsInserted`/`RowsUpdated`/`ErrorMessage`, Lombok `@ToString` diagnostics with `@ToString.Exclude` on sensitive provider fields
- Security: `OpenAiCompatibleProviderSupport.apiKeyFor()` blocks cleartext API keys to non-loopback HTTP hosts; `IOException` on security violations and unterminated thinking blocks
- Determinism & validation: fail-fast `IllegalArgumentException` from `AiConditionEvaluator.validate`/`AiFactExtractor.validate` on invalid ranges, blank globs, bad regex, unparseable ISO-8601 dates; deterministic `AiFactExtractor` regex counts; UTC `%Y%m%dT%H%M%SZ` timestamps and ISO-8601 order dates
- Concurrency: `LlamaCppJniAiGenerationProvider` explicitly single-threaded with unsynchronized lazy model init; volatile SPSC `ringbuf_t` with one-slot-empty invariant; `FOR UPDATE` row-level locking in MySQL to prevent overselling
- Numeric conventions: integer cents throughout (`price_cents`, `total_cents`, `unit_price_cents`, late fees capped at 2000 cents); power-of-two bitmask arithmetic (`BINS=256`, ringbuf capacity `& (n-1)`) instead of modulo
- Null-tolerance & immutability: `@Nullable` throughout with `DEFAULT_*` fallbacks; defensive copies on write, unmodifiable views on read; immutable value objects (`LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`) vs mutable single-threaded JavaBeans (`SrcMorphConfiguration`, `AiGenerationConfig`)
