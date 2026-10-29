### ai
- H: 1.0
- C: 3EE55F54
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:14:13Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-domain platform that enables AI‑aware source file manipulation, parallel data processing, and relational database operations for lending, e‑commerce, and warehouse management.

#### Purpose
- AI‑aware source file selection, generation parameter tuning, and oversized‑file strategy management for srcmorph document workflows.  
- Parallel histogram computation, fixed‑capacity ring buffering, and CSV‑based revenue aggregation for data‑processing workflows.  
- Relational schema, stored procedures, and audit trails for lending‑library, e‑commerce, and warehouse‑inventory systems.

#### Responsibilities
- AI condition trees walked and validated against file context to determine inclusion matches.  
- Logical <and>/<or> combinators grouping child AiCondition instances for configuration‑level criteria.  
- Regex‑based fact blocks generated from pattern counters over source text.  
- AI generation parameters configured as mutable JavaBeans for provider integration.  
- Oversized source files assigned handling strategies (fail, sample, mapReduce, deterministic).  
- AI completion parsing strips thinking blocks; null input yields empty string, missing end marker throws IOException.  
- Provider contract defines generate/generateWithTimings/close; factory resolves effective provider key and dispatches to concrete back‑ends.  
- Llama.cpp JNI backend runs GGUF inference with lazy native model initialization.  
- OpenAI‑compatible infrastructure builds JSON request bodies, resolves endpoints, validates API keys, and extracts generation timings.  
- OpenCL kernels accumulate partial histograms via atomic_inc and reduce across work‑groups to a final 256‑bin histogram.  
- Fixed‑capacity single‑producer single‑consumer ring buffer with power‑of‑two constraint, wrap‑around index arithmetic, and one‑slot reservation.  
- Dated tar.gz backup archives created and oldest copies pruned beyond a retained count.  
- CSV order data parsed, filtered by currency, and revenue aggregated by month and region.  
- Relational schema designed with CHECK constraints and FK patterns for core entities.  
- Operational stored procedures (checkout/return, order item addition, inventory sync) with transactional boundaries.  
- Reporting and analytics views (overdue loans, member stats, book popularity, order totals).  
- Migration and seed‑data population using generate_series and test‑data scripts.  
- Stock and order lifecycle management including stock decrement, quantity accumulation, and availability enforcement.  
- Sync logging and audit tracking with transactional error rollback and outcome recording.

#### Key units
- AiConditionEvaluator: walks AiCondition trees and validates file inclusion matches against AiFileContext.  
- AiConditionGroup: holds child AiCondition instances as logical <and>/<or> combinators.  
- AiFactExtractor: generates regex‑based fact blocks from pattern counters over source text.  
- AiGenerationConfig: mutable JavaBeans configuration object with many public members for AI generation steps.  
- AiOversizeStrategy: enum defining FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC strategies for files exceeding model context windows.  
- AiCompletionParser: parses raw LLM completion text; returns empty string for null input, strips thinking blocks, throws IOException if thinking block started without end marker.  
- AiGenerationProvider: generation contract interface extending AutoCloseable with generate/generateWithTimings/close.  
- AiGenerationProviderFactory: creates providers by name (PROVIDER_MOCK, PROVIDER_LLAMACPP_JNI, PROVIDER_OPENAI_COMPATIBLE); effectiveProvider resolves key.  
- LlamaCppJniAiGenerationProvider: runs GGUF model inference via llama.cpp; native LlamaModel handle lazily initialized on first generate call.  
- OpenAiCompatibleProviderSupport: final class providing non‑network infrastructure for OpenAI‑compatible model interactions; builds JSON request bodies, resolves endpoints, validates API keys.  
- AiGenerationRequest, AiGenerationTimings: data types carrying generation parameters and token/rate metrics.  
- histogram_partial: OpenCL kernel that accumulates partial histograms into a global buffer per work‑group using atomic_inc and local memory fencing.  
- histogram_reduce: OpenCL kernel that reduces partial histograms into the final 256‑bin histogram with an out‑of‑bounds guard bin >= BINS.  
- BINS 256: constant defining the number of histogram bins.  
- ringbuf_t: struct holding buffer data, capacity, head, tail indices under power‑of‑two capacity constraint.  
- ringbuf_status_t: status type with RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM.  
- ringbuf_init: allocates buffer and validates power‑of‑two capacity.  
- ringbuf_write: copies bytes into buffer with wrap‑around, returns actual bytes written.  
- ringbuf_read: copies bytes from buffer with wrap‑around, returns actual bytes read.  
- ringbuf_used: computes stored byte count via (head - tail) & (capacity - 1).  
- revenue_by_month_and_region: aggregator that totals revenue by (month, region) from order sequences filtered by currency.  
- parse_orders: generator that yields orders filtered by currency from CSV paths.  
- rotate_backups.sh: script that creates dated tar.gz archives and prunes oldest beyond KEEP count.  
- author table (library): stores author identity, name, birth year, country with CHECK constraints.  
- book table (library): manages ISBN, title, author linkage, copies with integrity constraints.  
- member table (library): tracks member email, name, join date, block status, home_branch_id FK.  
- loan table (library): records checkout sequences, due dates, return status, cascade deletion references.  
- overdue_loans view (library): joins loan, member, book to surface active overdue loans with days_overdue computation.  
- member_loan_stats view (library): aggregates per‑member loan totals, open count, last loan date.  
- popular_books view (library): groups by book_id, title, author_name with unique index and times_loaned count.  
- product table (shop): stores SKU, price in cents, stock quantity for order item lookups.  
- customer_order table (shop): tracks order status, total cents, creation timestamp.  
- order_item table (shop): links orders and products with quantity and unit price, composite primary key.  
- add_order_item procedure (shop): transactional routine validates stock, inserts items, updates inventory and order totals.  
- StockLevel table (warehouse): warehouse stock table with PK (WarehouseId, Sku) for inventory tracking.  
- SupplierFeed table (warehouse): inbound feed source filtered by ReceivedAt >= @Since for incremental sync.  
- SyncLog table (warehouse): sync outcome log recording rows inserted, updated, and errors.  
- MERGE procedure (warehouse): nightly sync routine aggregating feed rows and updating StockLevel with change tracking.

#### Data flow
- AiCondition trees and AiFileContext enter AiConditionEvaluator.matches/validate; output boolean match or IllegalArgumentException.  
- Source text and AiFactCounter lists enter AiFactExtractor.factsBlock; output String facts block or empty string.  
- AiGenerationConfig parameters flow into provider creation; concrete provider (LlamaCppJni or OpenAiCompatible) executes generate(AiGenerationRequest) → LLM inference → AiCompletionParser → cleaned text.  
- Oversized‑file size comparison triggers AiOversizeStrategy selection; strategy applied during build‑time processing (fail, sample, mapReduce, deterministic).  
- Histogram: input byte stream → histogram_partial (work‑group partial histograms with atomic_inc and CLK_LOCAL_MEM_FENCE) → histogram_reduce (global reduction into final 256‑bin histogram).  
- Ring buffer: producer calls ringbuf_write(data, len) → data wraps into ringbuf_t.data via head index advancement; consumer calls ringbuf_read(dest, len) → reads via tail index advancement; stored byte count derived from head‑tail mask.  
- Revenue reporting: CSV files → parse_orders (filtered by currency) → revenue_by_month_and_region accumulates (month, region) → output aggregated totals; backup rotation: SOURCE_DIR → tar.gz archive → BACKUP_DIR → prune oldest archives → stdout status.  
- Library checkout: fn_checkout_book validates member eligibility, decrements copies_free, inserts loan, triggers audit log; return: sp_return_book sets returned_on, increments copies_free, triggers audit.  
- Shop order item: add_order_item procedure validates stock with row locking, inserts item or increments quantity via ON DUPLICATE KEY UPDATE, decrements product stock, recomputes order total cents.  
- Warehouse sync: MERGE procedure reads SupplierFeed rows where ReceivedAt >= @Since, grouped by WarehouseId/Sku, aggregates quantities into StockLevel UPDATE/INSERT, captures actions, logs outcomes into SyncLog.

#### Dependencies
- Internal: AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition, AiFactCounter, AiCalibration, AiCompletionParser, AiGenerationProvider, AiGenerationProviderFactory, LlamaCppJniConfig, OpenAiCompatibleProviderSupport, MockAiGenerationProvider, OpenAiCompatibleAiGenerationProvider, AiGenerationRequest, AiGenerationTimings, AiPromptSupport, Java8CompatibilityHelper.  
- External Java: java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List, java.util.ArrayList, java.util.regex.Pattern, java.util.regex.Matcher, java.util.regex.PatternSyntaxException.  
- External modules: lombok.ToString, org.jspecify.annotations.Nullable, com.fasterxml.jackson.databind.ObjectMapper, net.ladenthin.llama.value.ChatResponse, net.ladenthin.llama.value.Usage, net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper.  
- OpenCL C 1.2 runtime and atomic_inc operation for per‑byte accumulation in histogram kernels.  
- ringbuf.h: type and status declarations, <stdlib.h> malloc/free, <string.h> memcpy, <stddef.h> size_t, <stdint.h> uint8_t, C extern "C" linkage for C++ compatibility.  
- sales_report.py: argparse, csv, sys, collections, dataclasses, datetime, decimal; rotate_backups.sh: tar, ls, mapfile, du, basename, dirname, date, mv, rm, set -euo pipefail.  
- Types: uchar, uint, uint8_t; constant: BINS 256; buffers: partial, local_bins, histogram, ringbuf_t.data.  
- External DB: PostgreSQL generate_series for migration, MySQL stored procedure syntax, table variable @Changes, system catalog tables for constraints and sequences.  
- Parameter dependencies: @Since DATETIME2(0) for warehouse incremental sync; CURRENT_DATE defaults in functions; COALESCE patterns across routines.

#### Cross‑cutting
- @Nullable annotation governs null‑handling across AiFactExtractor, AiGenerationConfig, AiOversizeStrategy, AiCompletionParser.  
- IllegalArgumentException thrown by validators for misconfiguration (AiConditionEvaluator, AiFactExtractor, AiOversizeStrategy).  
- Defensive copying in AiConditionGroup.setConditions to prevent external mutation of conditions list.  
- Thread‑safety: AiConditionEvaluator and AiFactExtractor are stateless and thread‑safe; AiGenerationConfig is fully mutable, not safe for concurrent access; AiConditionGroup has mutable state not thread‑safe by default; AiOversizeStrategy is immutable enum, safe for concurrent access; providers control their own synchronization.  
- All validators default null/blank inputs to safe states (DEFAULT for AiOversizeStrategy, empty string for factsBlock).  
- @ToString Lombok annotation on classes/fields (with @ToString.Exclude where needed).  
- IOException thrown for missing thinking blocks, invalid endpoints, unset API keys, non‑loopback key transmission, and non‑2xx HTTP statuses.  
- Switch/dispatch on effective provider key for backend dispatch.  
- Constructor patterns requiring non‑null config and promptSupport; final fields where applicable.  
- Default no‑op close() and delegating generateWithTimings implementations.  
- JSON serialization via ObjectMapper for request bodies.  
- Pattern‑based endpoint validation (http(s) scheme, trailing slash stripping).  
- Loopback host checking (localhost, 127.x.x.x, [::1]) for API key transmission safety.  
- Two‑pass reduction pattern with CLK_LOCAL_MEM_FENCE synchronization between passes in histogram kernels.  
- atomic_inc used for concurrent byte‑level counter increments; out‑of‑bounds guard bin >= BINS prevents out‑of‑range access.  
- Power‑of‑two capacity validation enforced in ringbuf_init; one‑slot reservation pattern to distinguish full from empty buffer state.  
- Error handling via return codes (RINGBUF_EINVAL, RINGBUF_ENOMEM) rather than exceptions in ringbuf; exit codes/status streams for error reporting in backup and revenue modules.  
- Input validation and strict error handling patterns across all subpackages; trap for partial archive cleanup on EXIT in rotate_backups.sh; ValueError with file path and line number context in sales_report.py.  
- Bitmask wrap‑around arithmetic for index management in ringbuf; symmetrical write/read operations with dual‑chunk copy logic for wrap‑around.  
- CHECK constraints recurring: birth_year range, copies_free <= copies_total, due_on > loaned_on, status enum values, stock quantity non‑negative.  
- FK constraint pattern across all core tables and reference integrations.  
- Trigger/audit pattern: trg_loan_audit_fn logs CHECKOUT on INSERT and RETURN on UPDATE of returned_on; similar audit intent in sync logging.  
- Function pattern: fn_ and sp_ prefixed routines with COALESCE and CURRENT_DATE defaults for business logic.  
- View pattern: reporting views with GROUP BY, unique indexes, computed columns (days_overdue, times_loaned, order total cents).  
- Migration pattern: DDL changes, column renames (joined_on -> member_since), data migrations using generate_series CROSS JOIN.  
- Status enum usage: book copy statuses ('AVAILABLE','ON_LOAN','LOST'), loan actions ('CHECKOUT','RETURN'), order item accumulation via ON DUPLICATE KEY UPDATE.  
- Transactional integrity: BEGIN TRANSACTION, COMMIT on success, ROLLBACK on failure; CATCH block error logging and THROW.  
- Row‑level locking: FOR UPDATE on product reads to prevent concurrent stock oversell; SQLSTATE '45000' signals for unknown SKU or insufficient stock.
