### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:14:12Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted documentation generation alongside byte-processing, operational reporting, and database workflows for circulation, order fulfillment, and stock synchronization.

#### Purpose
- Combines source-aware AI generation configuration and provider routing with utility workflows for byte analysis, buffering, backups, and sales reporting.
- Defines database schemas and routines for library circulation, shop inventory reservation, and warehouse stock synchronization.

#### Responsibilities
- **AI generation (`java/`):** Select source files using conditions, extract configured facts, apply oversized-source policies, and generate completions through mock, llama.cpp, or OpenAI-compatible providers.
- **Byte utilities and operations (`other/`):** Calculate byte histograms, manage bounded FIFO byte storage, rotate compressed backups, and report monthly regional sales.
- **Database workflows (`sql/`):** Manage library loans and audit/reporting, reserve shop stock for order items, and synchronize warehouse balances from supplier feeds.

#### Key units
- `AiConditionEvaluator`, `AiFactExtractor`, `AiGenerationConfig`, `SrcMorphConfiguration` — evaluate source rules, extract source facts, and hold generation and run settings.
- `AiOversizeStrategy`, `AiGenerationProvider`, `AiGenerationProviderFactory`, `AiCompletionParser` — resolve oversized-source policies and provider selection, then process completions.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` — execute local llama.cpp and OpenAI-compatible generation paths.
- `histogram_partial`, `histogram_reduce`, `BINS` — count byte values in parallel and reduce counts into a 256-bin histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free` — manage bounded byte storage and transfers.
- `rotate_backups.sh` — create timestamped compressed archives and enforce retention.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — parse filtered order CSVs, aggregate revenue, and print qualifying totals.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit`, `overdue_loans`, `member_loan_stats`, `popular_books` — implement library circulation, audit, and reporting.
- `add_order_item` — add or increment shop order lines, reserve product stock, and recalculate order totals.
- `dbo.usp_SyncStock`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — synchronize warehouse quantities and track run outcomes.

#### Data flow
- AI source conditions and facts feed configured generation requests; the provider factory selects a backend, which returns parsed completion text and optional timings.
- Histogram input bytes flow through per-work-group counts and a reduction; ring-buffer writes and reads update wrapped storage indices.
- Backup rotation archives a source directory and prunes older matching archives; sales CSV rows become filtered `Order` records, monthly regional aggregates, and threshold-qualified output.
- Library checkout and return update loans and inventory with audit events; shop item addition locks and deducts stock; warehouse sync aggregates feed rows into balances and records success or failure.

#### Dependencies
- AI configuration and provider collaborators include `AiFileContext`, condition and fact types, `AiGenerationRequest`, `AiPromptSupport`, llama.cpp support, Jackson, and OpenAI-compatible response types.
- Histogram kernels use OpenCL C; ring-buffer implementation uses standard C allocation and memory-copy functions.
- Backup rotation uses Bash and system archive/date/file commands; sales reporting uses Python standard-library CSV, date, decimal, and argument-parsing utilities.
- SQL workflows use PostgreSQL library tables, MySQL 8 shop tables, and SQL Server warehouse tables and procedures.

#### Cross-cutting
- AI configuration is generally mutable; the provider contract supports optional timing and cleanup, while local model initialization is lazy and unsynchronized.
- Ring-buffer capacity must be a nonzero power of two; the API is designed for single-producer/single-consumer use.
- Database constraints and indexes support integrity and lookup; shop stock updates use locking, and warehouse synchronization is transactional with rollback and error logging.
