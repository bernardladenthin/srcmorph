### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:24:12Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Covers AI-assisted document generation alongside byte-processing utilities, operational reporting, and database workflows for lending, order fulfillment, and stock synchronization.

#### Purpose
- Provides shared AI configuration and generation support, plus utilities and operational workflows across several domains.
- Includes GPU byte histograms, bounded FIFO buffering, backup rotation, revenue reporting, and SQL-backed library, shop, and warehouse operations.

#### Responsibilities
- **AI document generation (`java/`):** Configure file selection and prompts, evaluate conditions and source facts, route generation requests to mock, llama.cpp, or OpenAI-compatible providers, and clean completion text.
- **Byte and operations utilities (`other/`):** Compute byte-frequency histograms, manage circular-buffer reads and writes, rotate compressed backups, and report monthly revenue by region.
- **Database workflows (`sql/`):** Manage library catalog and lending, reserve shop inventory for order lines, and synchronize warehouse stock from supplier feeds with execution logging.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionEvaluator`, `AiFactExtractor` — configure source selection and generation, evaluate file conditions, and validate and format source facts.
- `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` — define generation, select backends, run local inference, and support remote-compatible requests.
- `AiOversizeStrategy`, `LlamaCppJniConfig`, `AiCompletionParser` — represent oversized-input policies, configure local inference, and remove leading reasoning blocks from generated text.
- `histogram_partial`, `histogram_reduce`, `BINS` — compute and combine per-work-group counts into a 256-bin byte histogram.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — represent and operate a bounded circular byte buffer.
- `rotate_backups.sh`, `parse_orders`, `revenue_by_month_and_region`, `main` — rotate archives and parse, aggregate, and report currency-filtered order revenue.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit`, `trg_loan_audit` — perform library lending operations, calculate fees, and audit loan events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report on overdue lending, member activity, and book popularity.
- `add_order_item` — add or increase a shop order line while reserving product stock and updating the order total.
- `dbo.usp_SyncStock(@Since DATETIME2(0))`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — merge recent supplier quantities into warehouse stock and record synchronization outcomes.

#### Data flow
- **AI generation:** Configuration feeds file-condition evaluation and fact formatting; the provider factory resolves model-level or run-level backend settings and routes generation requests. Local inference parses generated text; OpenAI-compatible support prepares remote request and response data.
- **Byte processing:** Histogram kernels count input bytes in work groups and reduce partial counts; ring-buffer operations copy available bytes while advancing wrapping indices.
- **Operations reporting:** Backup rotation archives a source directory and removes older matching archives; revenue processing validates CSV data, filters by currency, aggregates by month and region, and prints qualifying results.
- **Database operations:** Library checkout and return update loans and copy availability, with loan events audited; shop order-line processing locks a stocked SKU, adjusts stock, and recalculates totals; warehouse synchronization aggregates recent supplier quantities, merges stock levels, and logs outcomes.

#### Dependencies
- **AI:** Uses shared generation requests, timings, prompt support, configuration and file-context types; integrates llama.cpp JNI, OpenAI-compatible and mock providers, Jackson JSON nodes, and response/timing utilities.
- **Utilities:** Uses OpenCL C 1.2 and caller-provided local memory for histograms; standard C allocation and copy operations for the ring buffer; Bash system commands and Python standard-library modules for scripts.
- **SQL:** Library routines operate on PostgreSQL catalog, member, branch, copy, and loan records; MySQL shop procedures use `product`, `customer_order`, and `order_item`; SQL Server warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- AI configuration objects are generally mutable and unsynchronized; llama.cpp initialization is lazy and may race on concurrent first calls.
- AI validation is explicit, generation failures use `IOException`, and providers without timing data default to zero timings; OpenAI-compatible endpoints require HTTP or HTTPS, with API-key restrictions for non-loopback plain HTTP.
- Ring-buffer capacity must be a nonzero power of two, with one slot reserved and volatile producer/consumer indices; histogram work-items coordinate through local memory and barriers.
- Revenue aggregation uses `Decimal` and includes file context in input errors; backup rotation uses strict Bash mode and cleans partial archives on exit.
- SQL platforms differ by domain: PostgreSQL for library lending, MySQL 8 for shop orders, and SQL Server for warehouse synchronization; transaction handling is defined for library sample-data loading and warehouse synchronization, but not explicitly for the shop procedure.
