### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:06Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted source indexing alongside byte-processing utilities, operational reporting, and transactional workflows for library lending, shop orders, and warehouse inventory.

#### Purpose
- Combines source-selection and AI summarization support with reusable byte tools, backup and sales-report scripts, and database operations for lending, purchasing, and stock management.
- Covers distinct domains across Java, OpenCL/C, Bash/Python, PostgreSQL, MySQL, and SQL Server components.

#### Responsibilities
- **AI indexing and generation (`java/`):** Configure source eligibility, facts, prompts, models, output behavior, and oversize handling; generate summaries through local llama.cpp or OpenAI-compatible providers.
- **Byte processing and operations (`other/`):** Compute parallel byte histograms, buffer byte streams in a circular FIFO, rotate compressed backups, and aggregate monthly regional sales from CSV exports.
- **Transactional business data (`sql/`):** Maintain library catalog and loan workflows with auditing and reports; reserve shop inventory during order updates; synchronize warehouse stock from supplier receipts.

#### Key units
- `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor` — configure source indexing, evaluate file conditions, and extract regex-based facts.
- `AiGenerationProviderFactory`, `AiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — select generation backends, produce and clean completions, and support timing.
- `histogram_partial`, `histogram_reduce`, `ringbuf_t` — count byte frequencies and provide circular-buffer storage and read/write operations.
- `rotate_backups.sh`, `parse_orders`, `revenue_by_month_and_region`, `main` — retain compressed backups and filter and report sales totals.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `overdue_loans`, `member_loan_stats`, `popular_books` — process and report library lending activity.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan state transitions.
- `add_order_item` — update shop order lines, reserve product stock, and recalculate order totals.
- `dbo.usp_SyncStock(@Since)` — aggregate supplier receipts into warehouse stock levels and log synchronization outcomes.

#### Data flow
- Source files and metadata are selected and evaluated; configured facts and generation settings feed a provider, which returns completion text for parsing.
- Byte inputs flow through parallel partial histograms and reduction; stream bytes move through `ringbuf_t` storage. CSV orders are currency-filtered, grouped by month and region, thresholded, and printed.
- Library catalog and member records feed checkout, return, auditing, and reports; shop order-item changes validate and reserve product inventory; warehouse receipts are timestamp-filtered, aggregated, merged into stock, and logged.
- Backup scripts archive a source directory before pruning older matching archives to the retention limit.

#### Dependencies
- AI indexing uses shared project types such as `AiCondition`, `AiFileContext`, `AiGenerationRequest`, `AiGenerationConfig`, and `AiPromptSupport`; backend support uses llama.cpp JNI and Jackson.
- Byte components use OpenCL C 1.2 and standard C allocation and memory-copy facilities; scripts use Bash/system utilities and Python standard-library CSV, date, decimal, and aggregation facilities.
- Database routines operate on library catalog/loan tables, shop `customer_order`, `order_item`, and `product`, and warehouse `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- SQL components target PostgreSQL (library), MySQL 8 (shop), and SQL Server (warehouse).

#### Cross-cutting
- AI settings are generally mutable JavaBeans; validation errors include invalid condition structures, regexes, and oversize tokens. Local model initialization is lazy and cached but unsynchronized.
- Ring buffers require nonzero power-of-two capacity and document single-producer/single-consumer ownership; histogram work-groups synchronize local-memory operations.
- Inventory changes are coupled to business operations; database constraints and auditing protect relationships and state transitions, with transactional rollback and error logging in warehouse synchronization.
