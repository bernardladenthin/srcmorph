### ai
- H: 1.0
- C: 3BF5828F
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:01:39Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted project-index generation alongside byte processing, operational reporting, and database workflows for library circulation, shop ordering, and warehouse inventory.

#### Purpose
- Combines AI configuration and text-generation providers with low-level data utilities and operational scripts.
- Defines SQL-backed workflows for lending, order and stock management, and supplier-feed synchronization.

#### Responsibilities
- **AI-assisted indexing (`java/`):** Configure source selection, file conditions, facts, prompts, models, and provider settings; generate index content through mock, local llama.cpp, or OpenAI-compatible pathways.
- **Byte processing (`other/`):** Compute parallel 256-bin byte histograms and provide bounded circular buffering for partial reads and writes.
- **Operations and sales reporting (`other/`):** Create and prune compressed backups; parse order CSVs and report currency-filtered monthly revenue by region.
- **Database workflows (`sql/`):** Manage library catalogs and circulation, shop orders and stock reservation, and warehouse inventory updates from supplier feeds.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — configure source filters, generation definitions, provider/model options, and output metadata.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate configured file conditions, validate and format source facts, and parse oversize-file routing policies.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — define generation and resource-management operations, and select a backend from configuration.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — support local inference, shared remote chat-completion requests, and response cleanup.
- `histogram_partial`, `histogram_reduce` — count bytes per work group and combine partial counts into a final histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_read`, `ringbuf_write` — manage circular storage and transfers.
- `rotate_backups.sh`; `Order`, `parse_orders`, `revenue_by_month_and_region` — rotate compressed backups and parse and aggregate sales data.
- `library.author`, `book`, `member`, `loan`, `branch`, `book_copy` — represent library catalog, circulation, and branch inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit` — process lending actions, calculate fees, and record loan transitions.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report circulation status and activity.
- `shop.product`, `customer_order`, `order_item`, `add_order_item` — manage shop products and orders, reserve stock, and update totals.
- `dbo.usp_SyncStock`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — aggregate supplier quantities into warehouse stock and record synchronization outcomes.

#### Data flow
- AI configuration and source facts feed condition evaluation and provider selection; providers consume `AiGenerationRequest` and return text, optionally with timing data.
- Byte inputs flow through OpenCL partial histograms into a reduced 256-bin result; ring-buffer writes and reads update circular storage indices.
- Backup sources are archived to a temporary `.partial` file, finalized as timestamped archives, then pruned; order CSV data is validated, currency-filtered, aggregated by month and region, and emitted as tab-separated output.
- Library circulation routines update loan and inventory records, with triggers recording audit events; reports derive from library data, and migration converts book-level counts into branch-level copies.
- Shop order-item updates lock qualifying product stock, insert or increment an order line, decrement inventory, and recalculate the order total.
- Warehouse synchronization filters supplier records by cutoff, aggregates quantities by warehouse and SKU, merges stock levels, and logs results; failures roll back, log, and rethrow.

#### Dependencies
- Java AI components use shared prompt, model, fact, condition, request, and timing types; local inference uses the `net.ladenthin:llama` JNI binding, while remote support uses Jackson and environment-based API-key lookup.
- OpenCL histogram kernels use OpenCL C 1.2; ring-buffer code uses its C API and standard allocation and byte-copying libraries.
- Backup scripts use Bash and system utilities; the sales reporter uses Python standard-library CSV, date, decimal, aggregation, and CLI modules.
- SQL components are separate PostgreSQL library, MySQL 8 shop, and SQL Server warehouse workflows, each coordinating its domain tables and routines.

#### Cross-cutting
- Java configuration is generally mutable; invalid conditions or facts and unsupported provider keys raise `IllegalArgumentException`. Provider operations and malformed completion blocks can raise `IOException`.
- The ring buffer requires nonzero power-of-two capacity and uses single-producer/single-consumer ownership conventions; histogram work groups synchronize local-bin operations and use atomic increments.
- Backup scripts use strict error handling and clean up partial archives; the sales reporter raises `ValueError` for missing columns or invalid dates and amounts.
- SQL updates use transactions and integrity constraints; shop product-row locking and warehouse rollback-on-failure handling address concurrency and errors.
- Library checkout and return routines use book-level free-copy counts, while the v2 migration establishes branch-level `book_copy` inventory.
