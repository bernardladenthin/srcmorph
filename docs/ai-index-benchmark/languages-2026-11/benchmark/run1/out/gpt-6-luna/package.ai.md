### ai
- H: 1.0
- C: 245F2262
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:17:36Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted source documentation, low-level byte and operational utilities, and transactional workflows for lending, retail orders, and warehouse inventory.

#### Purpose
- Configures source selection and AI text generation while providing independent utilities for byte processing, backups, and revenue reporting.
- Defines database schemas and workflows for library circulation, shop fulfillment, and warehouse stock synchronization.

#### Responsibilities
- **AI configuration and generation (`java/`):** Holds run- and model-level settings, evaluates source conditions, extracts configured facts, parses oversized-file policies, and routes generation to mock, llama.cpp JNI, or OpenAI-compatible support.
- **Byte processing (`other/`):** Computes byte histograms with OpenCL kernels and provides a bounded single-producer/single-consumer ring buffer.
- **Operational tools (`other/`):** Rotates compressed backups with retention limits and reports monthly regional revenue from validated, currency-filtered order CSVs.
- **Library circulation (`sql/`):** Manages catalog, members, loans, checkout and return operations, late fees, reports, audit events, and branch-assigned book copies.
- **Retail order fulfillment (`sql/`):** Adds order items while reserving product stock and recalculating order totals.
- **Warehouse inventory (`sql/`):** Aggregates recent supplier feeds into stock levels and records synchronization outcomes.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — carry run-level and per-model source, prompt, provider, and generation settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate file metadata conditions, format source-match facts, and parse oversized-file policies.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — define generation-provider behavior and select a provider from effective configuration.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — support local inference, OpenAI-compatible request preparation, and leading reasoning-block cleanup.
- `histogram_partial`, `histogram_reduce`, `BINS` — produce and combine 256-bin byte counts.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — represent and operate the fixed-capacity circular buffer.
- `rotate_backups.sh` — creates timestamped gzip archives and enforces retention.
- `Order`, `parse_orders`, `revenue_by_month_and_region` — parse order records, aggregate revenue, and produce threshold-filtered reports.
- `author`, `book`, `member`, `loan`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — model library circulation and perform borrowing, returns, and fee calculation.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report overdue loans, member borrowing activity, and popular books.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events.
- `branch`, `book_copy` — represent locations and individual book inventory introduced by migration.
- `product`, `customer_order`, `order_item`, `add_order_item` — model shop ordering and add items while reserving stock.
- `dbo.usp_SyncStock(@Since DATETIME2(0))`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — synchronize supplier quantities into warehouse inventory and log results.

#### Data flow
- AI settings feed file-condition evaluation and source-fact extraction; the provider factory selects a generation backend, which consumes `AiGenerationRequest` and produces text. Local inference parses model output and timing, while OpenAI-compatible support prepares request data.
- Byte input feeds `histogram_partial`, whose work-group counts are combined by `histogram_reduce`; separately, ring-buffer writes enqueue bytes for reads.
- Backup processing archives a source directory and removes older matching archives beyond the retention limit. The reporting tool reads CSV orders, validates and filters them, aggregates by month and region, then prints qualifying totals.
- Library seed data populates catalog, member, and loan records; circulation routines update loans and inventory, triggers audit events, and reports derive lending statistics. Migration turns book counts into branch-assigned copies and associates loans and members with locations.
- `add_order_item` locks a product with sufficient stock, inserts or increments an order line, decrements stock, and recalculates the order total.
- `dbo.usp_SyncStock` filters supplier feeds by `@Since`, aggregates by warehouse and SKU, merges quantities into `StockLevel`, and logs outcomes; failures roll back, log the error, and rethrow.

#### Dependencies
- AI configuration and generation use `AiFileContext`, condition and prompt definitions, `AiGenerationRequest`, `AiPromptSupport`, timing types, llama JNI support, Jackson JSON nodes, and `ChatResponse`/`Usage`. OpenAI-compatible support prepares request data but does not perform network I/O.
- Histogram kernels use OpenCL C 1.2; the ring buffer uses standard C allocation and copy facilities. Backup rotation relies on Bash and `tar`, `date`, `ls`, `mv`, and `rm`; the sales report uses Python standard-library CSV, CLI, date, decimal, and aggregation facilities.
- Library routines use `author`, `book`, `member`, and `loan`; migration uses `branch`, `book_copy`, and PostgreSQL `generate_series`.
- Shop order processing reads and updates `product`, `customer_order`, and `order_item`. Warehouse synchronization reads `dbo.SupplierFeed`, updates `dbo.StockLevel`, and writes `dbo.SyncLog`.

#### Cross-cutting
- AI configuration is generally mutable JavaBeans; condition and fact validation raise `IllegalArgumentException`, provider failures use `IOException` where declared, and local model initialization/closure is unsynchronized.
- The ring buffer reserves one slot to distinguish full from empty and uses power-of-two capacities for masked wraparound; invalid capacity and allocation failure have distinct statuses. Histogram kernels use local-memory atomics and work-group synchronization.
- Database inventory changes are protected by transactions or locking: library seed and migration transactions, MySQL InnoDB row locks, and warehouse transaction rollback with error logging.
- SQL spans PostgreSQL for library circulation, MySQL 8 with InnoDB and `utf8mb4` for shop ordering, and SQL Server-style `dbo` objects for warehouse synchronization.
