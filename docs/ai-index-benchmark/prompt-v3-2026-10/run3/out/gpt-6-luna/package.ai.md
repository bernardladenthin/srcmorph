### ai
- H: 1.0
- C: 79B83DF2
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:39:08Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted source documentation, byte and sales utilities, and database workflows for library lending, shop inventory, and warehouse stock synchronization.

#### Purpose
- Combines AI generation configuration and provider integrations with independent utilities for byte processing, backup rotation, and sales reporting.
- Defines SQL-backed operations for library loans, order fulfillment, and supplier-driven inventory updates.

#### Responsibilities
- **AI documentation and generation:** Configure source selection, prompts, facts, model routing, and oversized-source handling; route generation requests to mock, local llama.cpp, or OpenAI-compatible providers.
- **Byte and operations utilities:** Compute byte histograms, manage bounded FIFO ring buffers, rotate compressed backups, and aggregate currency-filtered sales by month and region.
- **Database workflows:** Manage library catalog, checkout, returns, auditing, and reports; reserve shop inventory for order items; synchronize warehouse stock from supplier feeds.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold workflow-wide and per-model AI settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate source rules, count configured source facts, and resolve oversized-source policies.
- `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` — define and route generation across mock, local, and compatible remote backends.
- `histogram_partial`, `histogram_reduce` — produce and combine partial byte-value counts into a 256-bin histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_free` — provide bounded FIFO byte storage and lifecycle operations.
- `rotate_backups.sh` — create timestamped compressed backups and remove older matching archives.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — validate and filter sales CSV records, aggregate revenue, and print threshold-filtered results.
- `author`, `book`, `member`, `loan`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — model library lending and implement checkout, return, and fee operations.
- `loan_audit`, `trg_loan_audit`, `overdue_loans`, `member_loan_stats`, `popular_books` — capture loan transitions and report lending activity.
- `branch`, `book_copy`, `04_migration_v2.sql` — represent branch-level copy inventory and migrate from book-level counts.
- `product`, `customer_order`, `order_item`, `add_order_item` — model shop orders and process stock-reserving item changes.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — supply, update, and log warehouse stock synchronization.

#### Data flow
- **AI generation:** Configuration and file context feed source-condition evaluation and fact extraction; the provider factory selects a backend, which generates text and returns timing metrics where available.
- **Utilities:** Bytes flow through histogram partial counts and reduction, or through ring-buffer FIFO reads and writes. Backup inputs produce temporary then timestamped archives. Sales CSV rows become validated, currency-filtered orders, then monthly regional totals and tabular output.
- **Database workflows:** Library routines update loan and availability state, with triggers auditing transitions and views reporting activity. Shop item processing validates stock, updates line items and inventory, and recalculates order totals. Warehouse synchronization filters and aggregates supplier quantities, merges stock updates, and logs outcomes.

#### Dependencies
- AI generation uses internal request, prompt, model, fact, condition, and file-context types; external integrations include llama.cpp JNI, Jackson, Lombok, and Java APIs.
- Histogram kernels depend on OpenCL C 1.2; ring-buffer code uses its C API and standard allocation and copy functions.
- Backup rotation uses Bash and system utilities; sales reporting uses Python standard-library CSV, date, decimal, argument-parsing, and aggregation facilities.
- SQL workflows use domain tables and routines across PostgreSQL, MySQL, and SQL Server; warehouse synchronization depends on `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Validation and explicit failure handling recur across the package: AI condition trees and regex counters are validated; sales parsing raises contextual `ValueError`s; SQL routines handle unavailable or invalid resources.
- Data integrity is enforced with SQL constraints and foreign keys; database transactions protect migrations and stock updates.
- Notable operational constraints include unsynchronized llama.cpp handle access, ring-buffer power-of-two capacity with one reserved slot, and backup cleanup of partial archives on failure.
- AI provider settings commonly use defaults and null handling; OpenAI-compatible API keys are restricted to HTTPS or recognized loopback endpoints.
