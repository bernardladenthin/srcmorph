### ai
- H: 1.0
- C: 973AD197
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:07Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package provides a configurable framework for AI-assisted source modification, governing how AI models are selected, conditioned, and applied to source files during build-time processing.

#### Purpose
- Serves as the central configuration and orchestration layer for AI-driven source transformation, integrating condition evaluation, fact extraction, generation parameters, and oversize handling into a single build-time pipeline.
- Orchestrates AI-assisted code transformation by selecting and configuring generation providers, parsing LLM outputs, and handling provider-specific logic such as prompt rendering, response parsing, and API key management.

#### Responsibilities
- **AI-driven source transformation**: Central configuration and orchestration for AI model selection, condition evaluation, fact extraction, generation parameters, and oversize handling during build-time processing.
- **Provider selection and instantiation**: Factory logic resolving provider names and creating instances of `AiGenerationProvider` implementations (mock, LlamaCppJni, OpenAiCompatible).
- **Text generation and completion parsing**: Core generation methods accepting `AiGenerationRequest` objects; strips model-internal thinking blocks from raw LLM completion text.
- **Configuration management**: Immutable GGUF model parameters via fluent builder (`LlamaCppJniConfig`); OpenAI-compatible request body construction and endpoint validation.
- **Prompt support**: Integration with `AiPromptSupport` for prompt rendering and chat template construction.
- **Migration and schema evolution**: Database schema updates for library and web shop systems (authors, books, members, loans, products, orders).

#### Key units
- `AiConditionEvaluator`: Recursive tree walker evaluating `and`/`or`/`not` combinators, extensions, size, lines, and path globs against `AiFileContext`.
- `AiConditionGroup`: Defensive container for child `AiCondition` instances used as combinator bodies.
- `AiFactExtractor`: Utility producing `FACTS_HEADER`/`FACTS_SUFFIX` blocks from `AiFactCounter` configurations.
- `AiGenerationConfig`: Mutable JavaBean carrying model path, sampling parameters (temperature, top-p, top-k), retry policy, and trimming limits.
- `AiOversizeStrategy`: Enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for handling files exceeding model context windows.
- `SrcMorphConfiguration`: Root mutable JavaBean mirroring Maven `@Parameter` names, holding all per-run parameters.
- `AiGenerationProvider`: Interface defining `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)`.
- `AiGenerationProviderFactory`: Final class selecting and instantiating providers by name, defaulting to mock.
- `LlamaCppJniAiGenerationProvider`: Final class implementing `AiGenerationProvider` and `AutoCloseable`; lazily loads native `LlamaModel` via JNI.
- `LlamaCppJniConfig`: Final immutable class with thirty-seven fields (model path, context size, threads, GPU layers, cache settings, reasoning budget) constructed via fluent `Builder`.
- `OpenAiCompatibleProviderSupport`: Final class providing pure logic for OpenAI-compatible model interactions: request body construction, endpoint validation, API key lookup, loopback checking, and timing computation.
- `AiCompletionParser`: Final class stripping `<|channel>thought...<channel|>` thinking blocks from raw LLM completion text.
- `AiPromptSupport`: Interface/collaborator for prompt rendering and building chat template kwargs.
- `histogram_partial` / `histogram_reduce`: OpenCL kernels computing 256-bin byte histograms via parallel reduction.
- `ringbuf_t` / `ringbuf_status_t`: Lock-free ring buffer struct and status enum with volatile head/tail indices.
- `rotate_backups.sh`: Shell script creating dated `.tar.gz` archives and pruning old backups.
- `sales_report.py`: Python script parsing CSV order files and outputting aggregated revenue by month and region.
- `author`/`book`/`member`/`loan` (01_schema.sql): Core relational tables with PK/FK constraints for lending library.
- `fn_checkout_book`/`sp_return_book`/`fn_late_fee` (03_routines.sql): PL/pgSQL routines for lending workflow and fee calculation.
- `trg_loan_audit` (03_routines.sql): Trigger writing audit entries on loan insert or return update.
- `overdue_loans`/`member_loan_stats`/`popular_books` (02_views.sql): Reporting views for overdue status, member stats, and popular books.
- `product`/`customer_order`/`order_item` (webshop schema): Tables for catalog, orders, and order items with `add_order_item` stored procedure.
- `dbo.StockLevel`/`dbo.SupplierFeed`/`dbo.SyncLog` (sql/): T-SQL merge and sync logging logic.
- `branch`/`book_copy` (04_migration_v2.sql): Migration objects for multi-branch support and copy-id tracking.

#### Data flow
1. `SrcMorphConfiguration` populated via Maven plugin, JSON/YAML, or direct Java construction.
2. `AiConditionEvaluator.matches()` walks `AiCondition` trees using file facts to produce boolean inclusion decisions.
3. `AiFactExtractor.factsBlock()` computes count facts from `AiFactCounter` patterns, prepending `FACTS_HEADER`/`FACTS_SUFFIX`.
4. `AiGenerationConfig` supplies sampling parameters to AI provider implementations.
5. `AiOversizeStrategy` dictates fallback behavior (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) when files exceed model context windows.
6. `AiGenerationProviderFactory.create()` resolves provider names and returns `AiGenerationProvider` instances (mock, LlamaCppJni, OpenAiCompatible).
7. Provider `generate()`/`generateWithTimings()` methods invoke model inference; Llama.cpp JNI path lazily initializes native `LlamaModel`, builds chat template kwargs via `AiPromptSupport`, calls `model.chatComplete()`, and parses responses via `ChatResponseParser` then `AiCompletionParser.parseCompletion()`.
8. OpenAI-compatible path constructs JSON request bodies via `ObjectMapper`, validates endpoints, looks up API keys, and computes timings from `ChatResponse` usage.
9. Completion text passes through `AiCompletionParser.parseCompletion()` to strip thinking blocks regardless of backend.
10. `provider.close()` releases native handles (e.g., `LlamaModel` in JNI provider).
11. Database checkout: `fn_checkout_book` validates member status and book availability, inserts `loan`, decrements `copies_free`, fires `trg_loan_audit`.
12. Database return: `sp_return_book` sets `returned_on`, increments `copies_free`, fires `trg_loan_audit`.
13. Database late fee: `fn_late_fee` computes 50 cents/day overdue, capped at 2000 cents.
14. Webshop `add_order_item` procedure: locks `product` row, validates stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
15. SQL merge: `StockSync` procedure MERGEes `dbo.SupplierFeed` into `dbo.StockLevel`, logs counts to `dbo.SyncLog`, rolls back on error.
16. Histogram: Input bytes → `histogram_partial` (work-group partial counts) → `histogram_reduce` (global 256-bin output).
17. Ring buffer: Producer calls `ringbuf_write` (bitmasked wrap-around, advance head); consumer calls `ringbuf_read` (bitmasked wrap-around, advance tail); occupancy derived from head-tail difference masked by `capacity - 1`.
18. Backup rotation: Script generates timestamped `.tar.gz` archives, moves to backup directory, prunes excess archives keeping newest N.
19. Sales report: Reads CSV files, validates columns/data, aggregates `Decimal` revenues per `(month, region)`, prints tab-separated summaries to stdout.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.config.AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiCalibration`, `AiSourceExcludeFilter`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `AiGenerationTimings`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, provider implementations, `AiCompletionParser`, `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `ReasoningFormat`, `ModelParameters`, `ChatResponseParser`, `ChatResponse`, `Timings`, `FlashAttn`, `com.fasterxml.jackson.databind.ObjectMapper`, `org.jspecify.annotations.Nullable`, `lombok.ToString`, `lombok.EqualsAndHashCode`.
- External/JNI: `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ModelParameters`, `ChatResponse`, `Timings`, `FlashAttn`.
- External/IO: `java.io.IOException`, `java.util.regex.Pattern`, `java.util.function.Function`, `java.net.URL`, `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `java.util.regex.Pattern`.
- External/C: OpenCL API types (`uchar`, `uint`, `atomic_inc`, `barrier`), `ringbuf.h`, `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`, C11 or later.
- External/Python: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`.
- External/Shell: `tar`, `mv`, `ls -1t`, `rm`, `du -h`, `date -u`, bash with `set -euo pipefail`.
- External/PostgreSQL: `generate_series` for migration data population.
- External/MySQL: `ENUM` for order status, `ON DELETE CASCADE`, `UNIQUE KEY uk_product_sku`.
- External/T-SQL: `MERGE` operation, table variables (`@Changes`), transaction management.

#### Cross-cutting
- **Null-safety**: `@Nullable` annotations pervade `getConditions`, `getModelDirectory`, `getLlamaModelPath`, `getProjectName`, and list-returning getters; `setConditions` and `setDrySequenceBreakers`/`setStopStrings` accept `@Nullable List<String>`, null resetting to empty.
- **Immutability**: `AiConditionEvaluator` and `AiOversizeStrategy` are effectively immutable/thread-safe; `AiGenerationConfig` and `SrcMorphConfiguration` are mutable JavaBeans with no thread-safety guarantees.
- **Defaults**: Static final defaults dot `AiGenerationConfig` (context size 32768, max output 128, temperature 0.15f, etc.) and `AiOversizeStrategy` (`DEFAULT` = `FAIL`); `SrcMorphConfiguration` constructor applies `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, etc.
- **Configuration mirroring**: `SrcMorphConfiguration` field names intentionally mirror Maven `@Parameter` names so JSON/YAML config keys read identically to existing plugin XML.
- **Builder pattern**: `LlamaCppJniConfig` uses a fluent `Builder` with thirty-seven optional setters, all starting from `AiGenerationConfig.DEFAULT_*` defaults; builder not thread-safe, produced config immutable.
- **Immutability**: `LlamaCppJniConfig` final with private final fields and record-style accessors; `AiCompletionParser` no threading concerns.
- **Exception handling**: `AiCompletionParser` throws `IOException` if thinking block starts but never closes; `AiGenerationProviderFactory` throws `IllegalArgumentException` for unrecognized provider names; `OpenAiCompatibleProviderSupport.endpoint()` throws `IllegalArgumentException` for non-http(s) base URLs; `OpenAiCompatibleProviderSupport.apiKeyFor()` throws `IOException` if API key sent over plain http to non-loopback host.
- **Null safety**: `Objects.requireNonNull` checks on `config` and `promptSupport` in `LlamaCppJniAiGenerationProvider` constructor; `AiCompletionParser.parseCompletion()` returns empty string on null input; `OpenAiCompatibleProviderSupport.apiKey()` returns null when env var unset/blank.
- **Timing computation**: `OpenAiCompatibleProviderSupport.timings()` computes decode rate as completion tokens / seconds, defaulting to 0 when seconds <= 0 or usage unavailable.
- **Thinking block markers**: `AiCompletionParser` uses `<|channel>thought` as start and `<channel|>` as end markers; constants searchable across the package.
- **Parallel reduction pattern**: Histogram uses two-pass accumulation and synchronization to balance work-group partial results and global sums.
- **Power-of-two constraint**: All ring buffer capacity checks, bitmask operations (`& (capacity - 1)`), and usable-byte calculations (`capacity - 1`) stem from power-of-two requirement; invalid capacities rejected.
- **Single-producer/single-consumer model**: Ring buffer uses `volatile` on head/tail indices for serialization without mutexes, assuming one writer and one reader.
- **One-slot-empty reservation**: Usable capacity always `capacity - 1`, last slot permanently reserved to distinguish full and empty states.
- **Input validation**: All scripts validate inputs and exit with error codes on invalid data; `rotate_backups.sh` uses EXIT trap to clean up partial archive temp files.
- **Error handling**: Ring buffer write/read return actual byte count copied (may be less than requested); `sales_report.py` exits with code 1 and prints `"no orders found"` to stderr when no matching orders exist.
- **Check constraints**: `book.copies_free ≤ copies_total`; `loan.due_on > loaned_on`; `loan_audit.action IN ('CHECKOUT','RETURN')`.
- **Trigger logic**: Single trigger handles both INSERT (CHECKOUT) and UPDATE of `returned_on` (RETURN) via NEW/OLD distinction.
- **Naming conventions**: Consistent BIGSERIAL PKs, VARCHAR lengths, FK naming (`idx_*`, `book_copy`, `member_since`).
- **Uses MySQL 8 ENUM** for order `status` with default `'NEW'`.
- **Relies on UNIQUE KEY** `uk_product_sku` to guarantee SKU uniqueness and enable duplicate-key logic within procedure.
- **Uses T-SQL MERGE** with table variable (`@Changes`) to capture outcomes for subsequent logging.
- **Explicit transaction management** with rollback and error logging into `dbo.SyncLog` before re-throwing.
