### ai
- H: 1.0
- C: 973AD197
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:24:41Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted source documentation, byte and backup utilities, regional sales reporting, and transactional library, shop, and warehouse operations.

#### Purpose
- Provides source-selection and model-generation configuration with local and OpenAI-compatible backend support.
- Bundles independent byte-processing and operations utilities with SQL workflows for lending, order inventory, and stock synchronization.

#### Responsibilities
- **AI documentation support (`java/`):** Evaluate source-file conditions, configure generation and oversized-input behavior, validate and extract regex-based facts, and select inference providers.
- **Byte utilities (`other/`):** Compute byte histograms with OpenCL kernels and manage bounded byte transfers through a single-producer/single-consumer ring buffer.
- **Operations and reporting (`other/`):** Create and prune compressed backups; parse currency-filtered order CSVs and report monthly revenue by region.
- **Database workflows (`sql/`):** Manage library lending and audit/reporting, reserve shop product stock while updating order totals, and synchronize warehouse stock from supplier feeds.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — carry source-selection, generation, routing, fallback, and inference settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate file conditions, count regex matches, and parse oversized-input strategies.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — define text-generation behavior and select mock, llama.cpp JNI, or OpenAI-compatible backends.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — run local inference, prepare compatible requests, and clean completion text.
- `histogram_partial`, `histogram_reduce` — count input bytes per work group and reduce partial results to a 256-bin histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — represent and operate on the bounded circular byte queue.
- `rotate_backups.sh`, `parse_orders`, `revenue_by_month_and_region` — rotate compressed archives and parse and aggregate regional sales.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate lending operations, process returns, and calculate overdue fees.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan checkout and return events.
- `add_order_item` — reserve shop stock, update an order line, and recalculate the order total.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — merge recent supplier quantities into stock balances and log synchronization outcomes.

#### Data flow
- **AI generation:** Source filters and conditions identify eligible files; configuration and facts are evaluated; the provider factory selects a backend, which generates completion text and available timings.
- **Byte processing:** Input bytes feed per-work-group histogram counts and reduction, or are written to and read from the ring buffer.
- **Operations:** Backup scripts archive a source directory and prune older backups; the sales reporter parses matching-currency CSV rows, aggregates by month and region, filters by minimum revenue, and prints results.
- **Database operations:** Library checkout and return routines update loan and inventory state with audit events; shop order-item insertion locks and decrements stock while updating order totals; warehouse synchronization aggregates feeds, updates stock, and logs outcomes.

#### Dependencies
- **AI:** Shared source, prompt, model, request, and timing types; `AiSourceExcludeFilter`; llama.cpp JNI types; Jackson for OpenAI-compatible request construction.
- **Utilities:** OpenCL C 1.2 facilities; C allocation and memory-copy functions; Bash archive/filesystem utilities; Python standard-library CSV, date, decimal, and argument-parsing modules.
- **Databases:** PostgreSQL library tables, routines, triggers, and reports; MySQL 8 with InnoDB for shop orders and products; SQL Server warehouse tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Components are independent across the package; no shared runtime data flow is described.
- AI configuration is generally mutable, with explicit validation in condition and fact handling; provider operations declare `IOException` where specified, and thread-safety guarantees are limited.
- Ring-buffer capacity must be a nonzero power of two and reserves one slot to distinguish full from empty.
- Database constraints and transaction or locking behavior protect relationships and inventory updates; warehouse failures roll back, log the error with zero counts, and rethrow it.
