### ai
- H: 1.0
- C: F65CE4D9
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:34:51Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides configurable AI-assisted source documentation, low-level data utilities and operational reports, and database workflows for lending, order fulfillment, and inventory synchronization.

#### Purpose
- Groups source-aware AI generation configuration and provider routing with byte-processing tools, backup and sales-reporting utilities, and SQL-backed business workflows.
- Covers library circulation, shop order stock reservation, and warehouse supplier-feed synchronization.

#### Responsibilities
- **AI documentation generation (`java/`):** Configure source selection, file conditions, prompts, model settings, output metadata, and provider routing; extract source facts and handle oversized files.
- **Provider execution and completion handling (`java/`):** Dispatch to local llama.cpp or OpenAI-compatible generation paths, build requests, and clean generated text.
- **Byte utilities (`other/`):** Compute OpenCL byte histograms and provide bounded FIFO ring-buffer operations.
- **Operations and sales reporting (`other/`):** Create and rotate compressed backups; validate order CSVs, aggregate revenue by month and region, and filter reports by currency and threshold.
- **Database workflows (`sql/`):** Manage library lending and auditing, reserve shop inventory for order items, and synchronize warehouse stock from supplier feeds.

#### Key units
- `SrcMorphConfiguration` / `AiGenerationConfig` — hold run-level source, output, provider, and model-generation settings.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate nested file-selection conditions.
- `AiFactExtractor` / `AiOversizeStrategy` — format source match counts and resolve oversized-file handling.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — define generation lifecycle and choose a provider.
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — configure and run local model inference.
- `OpenAiCompatibleProviderSupport` / `AiCompletionParser` — prepare compatible chat requests and strip a leading Gemma-4 thinking block.
- `histogram_partial` / `histogram_reduce` — compute and combine byte-value histogram counts.
- `ringbuf_t` and `ringbuf_*` functions — manage FIFO byte storage, transfers, and capacity queries.
- `rotate_backups.sh` — create UTC-dated compressed archives and apply retention limits.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — parse order data, aggregate sales, and emit reports.
- Library SQL objects `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit` — implement lending, returns, late-fee calculation, and loan auditing.
- Library views `overdue_loans`, `member_loan_stats`, `popular_books` — report circulation and loan activity.
- Shop tables `product`, `customer_order`, `order_item` and routine `add_order_item` — store orders and lines while reserving stock and recalculating totals.
- Warehouse objects `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — aggregate supplier quantities, update balances, and record synchronization outcomes.

#### Data flow
- AI run and model settings select eligible source files, supply source facts and generation requests, and route prompts to a backend; returned completion text may be parsed and accompanied by timing metrics.
- Histogram input bytes become per-work-group partial counts and then a 256-bin result; ring-buffer users initialize storage, transfer bytes in FIFO order, and query occupancy.
- Backup input directories become timestamped archives followed by retention cleanup; sales CSVs are validated and currency-filtered before monthly regional aggregation and threshold filtering.
- Library checkout and return workflows validate and mutate loan and copy state, with audit records; shop item additions update order lines, stock, and totals.
- Warehouse synchronization filters feed rows by cutoff, aggregates quantities by warehouse and SKU, merges stock balances, and logs row counts or failures.

#### Dependencies
- AI generation uses configuration and request types including `AiModelDefinition`, `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`; path matching delegates to `AiSourceExcludeFilter`.
- Local inference depends on `net.ladenthin:llama`; compatible API support uses Jackson, `ChatResponse`, and `Usage`.
- OpenCL kernels use work-item and work-group built-ins, barriers, and atomic operations; ring-buffer code uses `ringbuf.h` and standard C allocation and memory-copy functions.
- Backup rotation uses Bash and system utilities; sales reporting uses Python standard-library CSV, argument-parsing, date, and decimal modules.
- SQL workflows use their domain tables and related constraints; warehouse synchronization uses SQL Server T-SQL, including `MERGE`.

#### Cross-cutting
- The package spans Java, OpenCL/C, Bash, Python, PostgreSQL, MySQL 8, and SQL Server T-SQL components.
- AI configuration is generally mutable; condition and fact validation is explicit, while provider implementations may report `IOException`.
- Ring-buffer use is single-producer/single-consumer, requires nonzero power-of-two capacity, and reserves one slot.
- Database constraints protect relationships; inventory-changing routines handle stock and error cases, with transactional behavior noted for warehouse synchronization and the library migration.
