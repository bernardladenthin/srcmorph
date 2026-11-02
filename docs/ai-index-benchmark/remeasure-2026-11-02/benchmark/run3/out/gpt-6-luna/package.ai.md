### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:00:45Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Combines source-summary generation, byte-processing and operational reporting utilities, and transactional data workflows for library lending, webshop fulfillment, and warehouse inventory.

#### Purpose
- Provides tools for selecting and summarizing source files with configurable AI generation, alongside low-level byte utilities and operational scripts.
- Defines database models and workflows for library circulation, online orders, and warehouse stock synchronization.

#### Responsibilities
- **AI source summarization:** Configure file selection, fact extraction, prompts, model settings, oversized-file strategies, provider routing, and completion handling.
- **Byte utilities and operations:** Compute byte histograms, buffer byte streams, rotate compressed backups, and report monthly regional sales.
- **Library circulation:** Model catalogs, branches, members, copies, and loans; implement checkout, returns, late fees, audit events, and lending reports.
- **Shop fulfillment:** Model products and orders, reserve stock when adding order items, and recalculate order totals.
- **Warehouse synchronization:** Aggregate supplier feeds into warehouse stock balances and log synchronization outcomes.
- **Database setup and evolution:** Migrate library inventory to branch-level copies and provide sample data.

#### Key units
- `SrcMorphConfiguration` — central configuration for source indexing and `.ai.md` generation.
- `AiConditionEvaluator`, `AiFactExtractor` — evaluate file-selection rules and derive configured facts from source text.
- `AiGenerationProviderFactory`, `AiGenerationProvider` — select and define AI generation backends.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` — provide local inference and OpenAI-compatible request support.
- `histogram_partial`, `histogram_reduce` — compute and combine byte-frequency counts.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — implement a bounded FIFO byte buffer.
- `rotate_backups.sh` — create timestamped compressed backups and prune older archives.
- `parse_orders`, `revenue_by_month_and_region` — parse sales orders and aggregate regional monthly revenue.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — represent library catalog, circulation, and branch inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — implement library checkout, return, and late-fee operations.
- `overdue_loans`, `member_loan_stats`, `popular_books` — provide library lending reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — model webshop orders and add items while reserving stock.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since)` — represent warehouse feeds, balances, sync logs, and synchronization.

#### Data flow
- AI summarization routes source files through selection rules and optional fact extraction, then applies generation settings and provider selection to produce completions.
- Byte histograms flow from input bytes through per-work-group partial counts to a reduced result; ring-buffer operations move bytes through bounded storage.
- Backup inputs produce timestamped archives, after which older matching archives are pruned; sales CSVs produce currency-filtered monthly regional totals.
- Library circulation updates loans and inventory, records audit events, and exposes derived lending reports.
- Shop item addition validates and reserves product stock, updates order lines, and recalculates totals.
- Warehouse synchronization filters supplier feeds by cutoff, aggregates by warehouse and SKU, merges stock balances, and logs results.

#### Dependencies
- AI generation uses shared request, prompt, timing, and configuration types; backends include mock, llama.cpp JNI, and OpenAI-compatible providers.
- Byte processing uses OpenCL; the ring buffer uses C allocation and copying libraries; operational tools use Bash/system commands and Python standard-library modules.
- Database workflows span PostgreSQL library objects, MySQL 8 shop objects, and SQL Server warehouse objects, each operating on its domain’s related tables and routines.

#### Cross-cutting
- AI configuration is primarily mutable JavaBeans; invalid conditions and fact settings are rejected, and providers share an `AutoCloseable` contract.
- The ring buffer requires a nonzero power-of-two capacity, reserves one slot, and documents single-producer/single-consumer use.
- Database constraints protect related records and inventory; transactional routines validate operations, with shop stock locking and warehouse rollback/error logging.
