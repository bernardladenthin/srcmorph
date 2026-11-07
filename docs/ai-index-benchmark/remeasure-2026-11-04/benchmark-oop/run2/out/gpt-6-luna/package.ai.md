### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:39:35Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted source summarization, byte and operational utilities, and database workflows for lending, retail orders, and warehouse inventory.

#### Purpose
- Configures source discovery and AI completion for generating `.ai.md` summaries.
- Provides byte-processing tools and standalone backup and sales-report utilities, alongside SQL workflows for library lending, retail fulfillment, and stock reconciliation.

#### Responsibilities
- **AI summarization and generation:** Configure source selection, facts, model inference, oversized-file handling, and local, remote, or mock completion providers.
- **Byte processing:** Build 256-bin histograms with OpenCL kernels and provide a FIFO byte ring buffer in C.
- **Operational utilities:** Create and rotate compressed backups; validate sales CSV data, aggregate revenue by month and region, and report qualifying totals.
- **Database workflows:** Manage library catalog and loan lifecycles, retail order items and stock reservation, and warehouse supplier-receipt synchronization.
- **Schema and reporting:** Seed and migrate library inventory; expose loan, overdue, member-activity, and popular-book reports.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — run-level source/output settings and per-model inference/provider options.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate source rules, format source facts, and select oversized-file handling.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — provider contract and selection for mock, llama.cpp JNI, or OpenAI-compatible generation.
- `AiCompletionParser`, `AiGenerationTimings` — clean completion text and represent timing and token-count data.
- `histogram_partial`, `histogram_reduce`, `BINS` — produce and combine per-work-group byte counts into 256-bin histograms.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — manage bounded FIFO byte storage and transfers.
- `rotate_backups.sh` — create dated compressed archives and prune older backups.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — parse currency-filtered sales CSVs, aggregate revenue, and run the report CLI.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — represent library catalog, members, borrowing, and branch-level inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit` — handle library loan lifecycle, fees, and event auditing.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report library loan status and activity.
- `product`, `customer_order`, `order_item`, `add_order_item` — represent retail orders and update order lines while reserving stock.
- `dbo.usp_SyncStock`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — reconcile warehouse receipts with stock balances and log synchronization outcomes.

#### Data flow
- Source contexts and condition trees feed AI configuration and provider selection; an `AiGenerationRequest` yields completion text, which is cleaned before return.
- Histogram input bytes become per-group partial counts, then a final histogram; ring-buffer writes and reads transfer bytes in FIFO order.
- Backup utility archives a source directory to a dated compressed file, then prunes older matching archives. Sales CSVs are validated and currency-filtered, aggregated by month and region, then filtered by minimum revenue.
- Library checkout and return routines validate eligibility and availability, update loans and inventory, and record audit events; reports derive from library tables.
- Retail order-item updates check and lock product stock, insert or increment a line, decrement inventory, and recalculate order totals.
- Warehouse synchronization aggregates supplier receipts by warehouse and SKU, merges quantities into stock levels, and logs success or failure.

#### Dependencies
- AI generation uses condition, file-context, fact-counter, prompt/model, and request types; external support includes llama.cpp JNI, Jackson, OpenAI-compatible chat responses, and `Java8CompatibilityHelper`.
- OpenCL kernels use work-item/group indexing, barriers, and atomic operations; C ring-buffer code uses standard allocation and memory-copy routines.
- Backup and sales tools depend on Bash/external archive and file commands, and Python standard-library CSV, argument-parsing, date, decimal, and aggregation facilities.
- SQL workflows use their respective library, retail, and warehouse tables; the library migration uses PostgreSQL `generate_series`.

#### Cross-cutting
- AI configuration beans are mutable and unsynchronized; local model initialization is lazy and unsynchronized, while provider generation declares `IOException`.
- Ring-buffer capacity must be a nonzero power of two, with one slot reserved to distinguish full from empty; its API describes single-producer/single-consumer use.
- Library and retail SQL use integrity constraints, transactions, and concurrency controls; warehouse synchronization rolls back, logs failure details, and rethrows errors.
- SQL spans PostgreSQL, MySQL 8, and SQL Server; utility failures use domain-specific validation and exit behavior.
