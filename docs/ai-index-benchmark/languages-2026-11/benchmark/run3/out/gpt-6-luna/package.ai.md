### ai
- H: 1.0
- C: CB7EA24B
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:02:29Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted source analysis alongside byte-processing utilities, operational scripts, and database workflows for lending, order fulfillment, and inventory synchronization.

#### Purpose
- Combines source selection and AI generation support with standalone utilities and SQL operations across library, shop, and warehouse domains.
- Provides configurable command-line and database workflows for processing files, bytes, backups, sales data, loans, orders, and stock.

#### Responsibilities
- **AI source analysis and generation (`java/`):** Configure file scope, prompts, models, facts, and oversized-file policies; evaluate file conditions and route generation requests to mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Byte and operational utilities (`other/`):** Compute GPU byte histograms, buffer byte streams, rotate dated backups, and aggregate currency-filtered order revenue by month and region.
- **Library circulation (`sql/`):** Manage catalog, membership, copies, loans, checkout and returns, overdue reports, borrowing statistics, popular books, late fees, and loan auditing.
- **Shop order fulfillment (`sql/`):** Add or increment order lines while validating and reserving product stock and updating order totals.
- **Warehouse inventory (`sql/`):** Synchronize supplier quantities into stock levels and record synchronization outcomes.

#### Key units
- `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor` — configure AI source scope and evaluate file conditions and whole-source facts.
- `AiGenerationProvider`, `AiGenerationProviderFactory`, `AiGenerationConfig`, `AiOversizeStrategy` — define generation settings, provider selection, and oversized-file policies.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — support local inference, compatible chat-completion requests, and completion cleanup.
- `histogram_partial`, `histogram_reduce`, `ringbuf_t` — implement two-stage GPU byte histograms and bounded wraparound byte buffering.
- `rotate_backups.sh`, `Order`, `parse_orders`, `revenue_by_month_and_region` — rotate compressed backups and parse, aggregate, and report sales data.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — represent library catalog, membership, circulation, and branch inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `overdue_loans`, `member_loan_stats`, `popular_books` — provide library lending operations and reports.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan lifecycle events.
- `product`, `customer_order`, `order_item`, `add_order_item` — represent shop orders and update order lines with stock reservation.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — model supplier inputs, warehouse stock, sync logging, and inventory synchronization.

#### Data flow
- AI configuration supplies source filters, models, prompts, and policies; conditions and facts prepare file context before the provider factory selects a generator. Providers return completion text and, where available, timing or cache metrics.
- Histogram input bytes flow through per-work-group counting and reduction; ring-buffer reads and writes copy data across wrap boundaries while updating buffer state.
- Backup arguments determine source, destination, and retention; the script creates an archive and prunes older backups. Sales CSV rows are validated and currency-filtered before monthly regional aggregation and reporting.
- Library checkout and return validate loan state and update circulation inventory and audit records; reports derive overdue and borrowing data from loans.
- Shop order-line updates check and lock available product stock, change order lines, decrement stock, and recalculate totals. Warehouse sync aggregates supplier quantities, merges stock changes, and logs results.

#### Dependencies
- AI generation depends on source/configuration types and provider support including `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, Jackson, llama JNI types, and Java regex and collection APIs.
- GPU histogram kernels use OpenCL; the C ring buffer uses allocation and memory-copy facilities. Backup and sales tools use Bash/system commands and Python standard-library modules.
- Library SQL routines and reports use loan, member, book, and author records; migration introduces `branch` and `book_copy` and uses PostgreSQL `generate_series()`.
- Shop order processing reads and writes `product`, `order_item`, and `customer_order`; warehouse sync uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- AI configuration beans are generally mutable and unsynchronized; condition evaluation and fact extraction are stateless. Provider errors and invalid configuration use explicit exceptions, and generation timings share `AiGenerationTimings`.
- The GPU histogram uses two-pass accumulation and reduction; the ring buffer uses power-of-two capacity and mask-based wraparound with a reserved slot.
- SQL domains enforce defined integrity constraints and update inventory within operational workflows; routines use explicit rejection, rollback, logging, or rethrow behavior for failures.
