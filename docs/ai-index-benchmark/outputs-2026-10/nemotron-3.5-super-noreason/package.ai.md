### ai
- H: 1.0
- C: 88B7E655
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:05:31Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Evaluates source-file conditions and generates AI summaries for source morphing, aggregates monthly regional revenue from order CSVs, manages timestamped directory backups, computes 256-bin byte histograms via OpenCL, and synchronizes warehouse stock across PostgreSQL, MySQL, and SQL Server schemas.

#### Purpose
- The `java/` package provides configuration, condition evaluation, fact extraction, and pluggable AI text-generation providers for srcmorph source morphing.
- The `other/` package provides image byte-histogram computation, circular byte buffering, sales reporting, and backup rotation utilities.
- The `sql/` package provides a PostgreSQL lending library, a MySQL web shop catalog/order system, and a T-SQL warehouse stock synchronization procedure.

#### Responsibilities
- Condition tree evaluation and validation for file selection (`AiConditionEvaluator`, `AiConditionGroup`).
- Deterministic fact counting and facts-block assembly for AI input (`AiFactExtractor`).
- Mutable JavaBean configuration for a single AI generation step (`AiGenerationConfig`) and for an entire srcmorph run (`SrcMorphConfiguration`).
- Oversize-file handling strategy parsing (`AiOversizeStrategy`).
- Provider contract, factory, local llama.cpp JNI and OpenAI-compatible provider implementations, and completion parsing to strip thinking blocks (`AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`).
- Parallel 256-bin byte histogram computation using two-pass OpenCL reduction (`histogram_partial`, `histogram_reduce`).
- Fixed-capacity circular byte buffer for producer-consumer streaming (`ringbuf_t`, `ringbuf_status_t`).
- Monthly regional revenue aggregation from order CSV exports (`sales_report.py`).
- Timestamped directory backups with per-directory retention enforcement (`rotate_backups.sh`).
- PostgreSQL lending library schema, checkout/return routines, late fee calculation, and reporting views.
- MySQL web shop product catalog, customer orders, order line items, and stock reservation.
- T-SQL warehouse stock synchronization from supplier feeds with sync logging.

#### Key units
- `AiConditionEvaluator` — recursively evaluates and/or/not condition trees against `AiFileContext` facts; validates tree configuration; detects line-count requirements.
- `AiConditionGroup` — nullable container for child `AiCondition` elements in and/or combinators, with defensive copy on set.
- `AiFactExtractor` — stateless utility that counts regex matches for fact labels and builds a labeled facts block string.
- `AiGenerationConfig` — mutable JavaBean holding model path, sampling, retry, trimming, and stop-string parameters for one AI generation step.
- `SrcMorphConfiguration` — central mutable JavaBean for all srcmorph run parameters, including output paths, filters, provider/model settings, prompts, fields, facts, and header version metadata.
- `AiOversizeStrategy` — enum parsing `<onOversize>` config tokens into `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC`.
- `AiGenerationProvider` — interface defining `generate`, `generateWithTimings`, and `close`; extends `AutoCloseable`.
- `AiGenerationProviderFactory` — stateless factory resolving mock, llama.cpp JNI, or OpenAI-compatible providers.
- `LlamaCppJniAiGenerationProvider` — final provider implementing local GGUF generation with lazy native model loading.
- `LlamaCppJniConfig` — immutable value type holding thirty-seven generation parameters via a fluent Builder.
- `OpenAiCompatibleProviderSupport` — package-private helper for OpenAI-compatible request bodies, endpoints, API keys, and timings.
- `AiCompletionParser` — stateless parser stripping Gemma-4 thinking blocks from raw completion text.
- `histogram_partial` — OpenCL kernel counting bytes per work-group using local memory and atomics, writing partial histograms to global memory.
- `histogram_reduce` — OpenCL kernel summing partial histograms across work-groups into a final 256-bin output.
- `ringbuf_t` — struct with `data`, `capacity`, volatile `head`, and volatile `tail` indices.
- `ringbuf_status_t` — enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — ring buffer lifecycle and streaming operations.
- `sales_report.py` — `Order` dataclass, `parse_orders`, `revenue_by_month_and_region`, and `main` CLI entry point.
- `rotate_backups.sh` — shell utility taking `<source_dir> <backup_dir> [keep=7]` for tar-based archiving and retention cleanup.
- Library tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; routines: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger: `trg_loan_audit_fn`; views: `overdue_loans`, `member_loan_stats`, `popular_books`.
- Shop tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- Warehouse tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure: `stock_sync_tsql.sql`.

#### Data flow
- `SrcMorphConfiguration` supplies run-level scope (subtrees, excludes, extensions, size bounds) and AI definitions to the engine.
- `AiConditionEvaluator.matches` consumes `AiCondition` trees and `AiFileContext` to decide whether a file is in scope; `usesLines` signals whether the indexer must read file contents for line counting.
- `AiFactExtractor.factsBlock` consumes `List<AiFactCounter>` and source text to produce a deterministic facts block prepended to AI summaries.
- `AiGenerationConfig` carries per-step generation parameters from Maven configuration to AI provider implementations.
- `AiOversizeStrategy.fromConfig` converts the `<onOversize>` token into a strategy constant for oversized-file handling.
- `AiGenerationProviderFactory.effectiveProvider` selects the provider key from model or run configuration, defaulting to mock when blank; `create` instantiates the chosen provider, injecting `LlamaCppJniConfig` and `AiPromptSupport`.
- `LlamaCppJniAiGenerationProvider.generate` builds model parameters from config, lazily loads the native `LlamaModel`, calls `chatComplete`, and pipes the response through `AiCompletionParser.parseCompletion` to strip thinking blocks.
- `OpenAiCompatibleProviderSupport.requestBody` serializes a Jackson JSON chat-completions body; `apiKeyFor` guards against cleartext keys over non-loopback HTTP; `timings` derives `AiGenerationTimings` from `ChatResponse` usage and wall-clock seconds.
- Histogram: input byte stream feeds `histogram_partial` to produce per-work-group partial histograms, which `histogram_reduce` sums into a single 256-bin histogram.
- Ring buffer: producers write bytes via `ringbuf_write` into the backing array in up to two wrapped chunks; consumers read via `ringbuf_read` in up to two wrapped chunks; callers check `ringbuf_used` and `ringbuf_free_space` to bound operations.
- Sales report: CSV rows are parsed into `Order` objects, aggregated by `("%Y-%m", region)` in a `defaultdict`, filtered by `--min-revenue`, and printed as `month\tregion\trevenue currency`.
- Backup rotation: a source directory is tarred into a `.partial` temp file, renamed to a UTC-timestamped archive, then archives beyond the keep limit are deleted newest-first.
- Library: seed data populates core tables; migration adds branch and book_copy; `fn_checkout_book` validates availability and inserts loans with audit; `sp_return_book` marks returns and restores copies; `fn_late_fee` computes fees; reporting views aggregate overdue loans, member stats, and popular books.
- Shop: `add_order_item` locks product stock by SKU, validates availability, upserts order items, decrements stock, and recomputes order totals.
- Warehouse: `stock_sync_tsql.sql` reads `SupplierFeed` rows since a cutoff, aggregates by `WarehouseId` and `Sku`, `MERGE`s into `StockLevel`, counts inserts/updates, and logs results or errors to `SyncLog`.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `net.ladenthin.srcmorph.support.ConvertToRecord`.
- External (Java): `java.io.File`, `java.io.IOException`, `java.net.URL`, `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `java.util.Map`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`, `java.lang.AutoCloseable`, `net.ladenthin.llama.*` (LlamaModel, ModelParameters, ChatResponse, Timings, Usage, ChatResponseParser, FlashAttn, ReasoningFormat), Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`), Lombok (`@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`, `@SuppressWarnings`), `org.jspecify.annotations.Nullable`.
- External (C/OpenCL): OpenCL C 1.2 builtins and `BINS` macro (256) for histogram kernels; `ringbuf.h` / `ringbuf.c`, `stdlib.h`, `string.h`, `<stddef.h>`, `<stdint.h>` for the ring buffer.
- External (Python): `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation` for `sales_report.py`.
- External (Bash): Bash builtins and external `tar`, `mv`, `rm`, `ls`, `du`, `cut`, `mkdir` for `rotate_backups.sh`.
- External (SQL): PostgreSQL (BIGSERIAL, SMALLSERIAL, PL/pgSQL, generate_series, materialized views, partial indexes); MySQL 8 InnoDB with utf8mb4; SQL Server (SYSUTCDATETIME, ERROR_MESSAGE, @@TRANCOUNT).

#### Cross-cutting
- Mutable JavaBeans (`AiGenerationConfig`, `SrcMorphConfiguration`) are not thread-safe and intended for single-threaded population before use.
- Stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`, `AiCompletionParser`) are thread-safe with no shared mutable state.
- `AiConditionGroup` and `AiGenerationConfig` defensively copy or null-coerce mutable collections in setters; list getters return unmodifiable views where applicable.
- `validate` methods fail fast with `IllegalArgumentException` on misconfigured condition trees, fact counters, or unrecognized oversize strategy tokens.
- Lombok `@ToString` used across `AiCompletionParser`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, and `OpenAiCompatibleProviderSupport`; `LlamaCppJniConfig` also uses `@EqualsAndHashCode` and `@ConvertToRecord`.
- Nullability expressed via `org.jspecify.annotations.Nullable` and `Java8CompatibilityHelper.isBlank` for blank-string handling.
- `AiGenerationProvider` and `LlamaCppJniAiGenerationProvider` implement `AutoCloseable` for native resource cleanup.
- `LlamaCppJniAiGenerationProvider` is not thread-safe due to unsynchronized lazy model initialization; other units are stateless or effectively immutable and safe for concurrent use.
- `IllegalArgumentException` is thrown by the factory and `OpenAiCompatibleProviderSupport.endpoint` for unrecognized providers or invalid URL schemes; `IOException` surfaces JSON serialization, missing thinking-block end markers, and cleartext API key violations.
- Two-pass reduction pattern in histogram kernels with local memory fences and out-of-range bin guards.
- Ring buffer reserves one slot to distinguish full from empty, yielding capacity - 1 usable bytes; documented as thread-unsafe despite header claims of thread-safety.
- Both script units validate inputs and exit non-zero on failure; `sales_report.py` returns 1 with `no orders found` on stderr, `rotate_backups.sh` returns 2 with a usage message and uses an EXIT trap to remove `.partial` temp files.
- Library: integrity constraints on copies, due dates, copy status, and audit actions; indexes on author, member, open loans, book copies, and popular books; transactional seed and migration scripts; migration retires per-book copy counters in favor of physical copies.
- Shop: `SELECT ... FOR UPDATE` row locking for stock reservation; SQLSTATE '45000' errors for missing product or insufficient stock; `INSERT ... ON DUPLICATE KEY UPDATE` upsert; monetary values in integer cents.
- Warehouse: transactional rollback with error logging and `THROW`; logical join on `WarehouseId` and `Sku` without foreign keys.
