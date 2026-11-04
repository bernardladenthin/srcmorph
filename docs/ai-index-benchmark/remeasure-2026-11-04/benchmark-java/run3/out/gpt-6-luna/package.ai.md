### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:47:19Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted documentation generation, byte-stream and operational utilities, and database workflows for lending, order fulfillment, and warehouse inventory.

#### Purpose
- Select source material and generate AI documentation through configurable model providers.
- Combine low-level processing and operations tools with SQL-backed library, retail, and warehouse workflows.

#### Responsibilities
- **AI documentation (`java/`):** Evaluate source-selection conditions, extract prompt facts, configure generation runs and oversized-input policies, and route requests to mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Byte processing and operations (`other/`):** Compute byte histograms with OpenCL, buffer bytes through a single-producer/single-consumer ring buffer, rotate compressed backups, and report monthly regional sales.
- **Database workflows (`sql/`):** Manage library catalog, memberships, loans, audit and reports; reserve shop inventory for order items; synchronize supplier quantities into warehouse stock.

#### Key units
- `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor` — configure documentation runs, match source metadata, and format regex-derived source facts.
- `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — select generation backends, obtain completion text and timings, and clean local-model responses.
- `histogram_partial`, `histogram_reduce` — compute and combine work-group byte-frequency counts.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — manage bounded circular byte storage and transfers.
- `rotate_backups.sh`, `parse_orders`, `revenue_by_month_and_region` — rotate retained archives and aggregate currency-filtered sales.
- `author`, `book`, `member`, `loan`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — represent library lending data and its checkout, return, and fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit`, `overdue_loans`, `member_loan_stats`, `popular_books` — record loan events and report lending activity.
- `branch`, `book_copy` — represent branch locations and individually tracked copies.
- `product`, `customer_order`, `order_item`, `add_order_item` — store shop products and orders, and update order lines while reserving stock.
- `dbo.usp_SyncStock(@Since)`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — merge recent supplier quantities into warehouse stock and record sync outcomes.

#### Data flow
- Source metadata and text are evaluated and mined for prompt facts; configuration and provider settings determine how generation requests produce completion text and optional timings.
- Histogram inputs become per-work-group counts and then a reduced 256-bin result; ring-buffer writes and reads enqueue and dequeue bytes.
- Backup scripts archive a source directory before applying retention; sales tools parse CSV orders, aggregate by month and region, then filter and print totals.
- Library operations update loans and availability, trigger audit events, and support derived reports; migration creates branch-associated copies from aggregate book counts.
- Shop order-item updates validate product stock, adjust line quantities, decrement inventory, and recalculate order totals; warehouse sync aggregates recent supplier rows by warehouse and SKU, merges stock, and logs results.

#### Dependencies
- AI generation uses internal request, prompt, model, condition, and timing types; llama.cpp JNI support; Jackson request serialization; and supplied environment lookup for API keys.
- Histogram kernels use OpenCL C 1.2; ring-buffer code uses standard allocation and byte-copy routines; scripts use Bash/system commands and Python standard-library CSV, date, decimal, and argument-parsing utilities.
- SQL workflows span PostgreSQL library data, MySQL shop data, and SQL Server warehouse data, each using database-native relationships and operations.

#### Cross-cutting
- AI configuration is generally mutable and unsynchronized; llama.cpp model initialization and close/generation operations are unsynchronized, while providers support cleanup.
- Input validation varies by domain: AI conditions and fact counters are validated; sales parsing reports malformed data; library and shop operations reject invalid or unavailable requests.
- Inventory semantics differ across workflows: library copies are branch-associated, shop stock is held on products, and warehouse stock is keyed by warehouse and SKU.
- SQL failure handling is database-specific: library routines raise exceptions, shop item updates signal SQLSTATE `45000`, and warehouse synchronization rolls back, logs, and rethrows errors.
