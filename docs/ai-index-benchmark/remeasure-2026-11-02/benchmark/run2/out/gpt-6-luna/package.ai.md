### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:46:47Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Combines AI-assisted source generation, byte-processing utilities, operational scripts, and transactional data workflows for lending, order fulfillment, and warehouse inventory.

#### Purpose
- Provides AI configuration and model-generation support alongside low-level byte tools and standalone operational utilities.
- Defines database workflows for library lending, shop orders, and supplier-driven stock synchronization.

#### Responsibilities
- **AI source analysis and generation (`java/`):** Configure source selection, prompts, model behavior, facts, and oversized-input policies; route requests to local or OpenAI-compatible providers.
- **Byte processing (`other/`):** Compute byte histograms with OpenCL kernels and provide a bounded FIFO ring buffer in C.
- **Operations and reporting (`other/`):** Rotate compressed backups and generate monthly regional sales summaries filtered by currency.
- **Database operations (`sql/`):** Manage library catalog and lending, shop orders and stock reservation, and warehouse synchronization with outcome logging.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold run-wide and per-model AI settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — match source metadata, format source facts, and represent oversized-input policies.
- `AiGenerationProviderFactory`, `AiGenerationProvider` — select and define AI generation backends.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — support local inference, remote chat-completion requests, and completion cleanup.
- `histogram_partial`, `histogram_reduce`, `BINS` — calculate and combine byte-frequency counts.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — represent and operate on a bounded FIFO byte buffer.
- `rotate_backups.sh`, `Order`, `parse_orders`, `revenue_by_month_and_region` — rotate archives and parse and aggregate sales records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — process library lending, returns, and overdue fees.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report library loan status and activity.
- `product`, `customer_order`, `order_item`, `add_order_item` — model shop orders and add items while reserving stock.
- `dbo.usp_SyncStock(@Since)`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — synchronize warehouse stock from supplier quantities and record outcomes.

#### Data flow
- AI settings and source conditions feed provider selection; local or remote inference returns completion text and, when supported, timings.
- Byte input flows through OpenCL partial histograms and reduction, or through ring-buffer writes and reads in FIFO order.
- Backup scripts archive a source directory before enforcing retention; sales-report scripts parse matching CSV records, aggregate revenue, filter totals, and print results.
- Library routines update loans and inventory, with triggers recording audit events; reports derive lending statistics from stored records.
- Shop item placement validates and locks stock, updates order lines and inventory, then recalculates order totals.
- Warehouse synchronization aggregates eligible supplier feed rows into stock levels and logs results; failures roll back and are logged before rethrowing.

#### Dependencies
- AI generation uses shared request, prompt, timing, and configuration types; local llama.cpp JNI support; and Jackson plus chat-response and usage types for remote support.
- Histogram kernels depend on OpenCL; the ring buffer uses C standard-library allocation and copying.
- Backup rotation uses Bash and system utilities; sales reporting uses Python standard-library CSV, date, decimal, argument-parsing, and collection support.
- Library SQL routines use `author`, `book`, `member`, and `loan`, with branch inventory and audit objects; shop order items reference `customer_order` and `product`.
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed`, updates `dbo.StockLevel`, and writes `dbo.SyncLog`.

#### Cross-cutting
- AI configuration uses mutable JavaBeans; provider failures use `IOException`, and unsupported provider keys use `IllegalArgumentException`.
- Local AI model initialization is lazy and cached; HTTP API keys are restricted from cleartext transmission except to loopback hosts.
- The ring buffer requires a nonzero power-of-two capacity, reserves one slot, and targets single-producer/single-consumer use; histogram kernels use barriers and atomic increments.
- SQL constraints and foreign keys enforce relationships; transactional routines handle invalid operations, and inventory updates use locking or transactions.
- Backup scripts clean up incomplete archives on exit; sales parsing reports malformed matching rows with file and line context.
