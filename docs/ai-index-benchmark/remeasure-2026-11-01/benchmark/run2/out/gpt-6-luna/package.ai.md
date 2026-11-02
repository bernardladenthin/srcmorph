### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:45:38Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Combines AI-assisted source documentation settings and providers with byte-processing utilities, operational reports, and SQL workflows for lending, purchasing, and inventory synchronization.

#### Purpose
- Configure and run AI-generated source documentation through local or OpenAI-compatible models.
- Provide reusable byte-processing utilities and scripts alongside database operations for library lending, webshop orders, and warehouse stock.

#### Responsibilities
- **AI documentation (`java/`):** Configure source selection, prompts, models, facts, and oversized-file handling; choose generation providers; clean completion text and expose timing metrics.
- **Byte processing (`other/kernels/`, `other/ringbuf/`):** Compute byte histograms with OpenCL kernels and move bytes through a circular FIFO buffer.
- **Operational scripts and reporting (`other/scripts/`):** Rotate compressed backups and validate, filter, aggregate, and print regional sales totals from CSV files.
- **Database workflows (`sql/library/`, `sql/shop/`, `sql/warehouse/`):** Manage loans and audit reports, reserve stock for order items, and synchronize supplier receipts into warehouse inventory.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold source-indexing and AI generation settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate source-selection rules, extract configured counts, and select oversized-file behavior.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — define generation operations and select the configured backend.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — run local inference, prepare compatible requests, and clean completion output.
- `histogram_partial`, `histogram_reduce` — compute per-work-group byte counts and combine them into a 256-bin histogram.
- `ringbuf_t`, `ringbuf_write`, `ringbuf_read` — represent and operate on the byte FIFO buffer; occupancy helpers report used and free capacity.
- `rotate_backups.sh` — create timestamped compressed archives and enforce retention.
- `parse_orders`, `revenue_by_month_and_region`, `main` — validate and filter CSV orders, aggregate monthly regional revenue, and print results.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — process library loans, restore availability, and calculate capped late fees.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report lending status, member activity, and book popularity.
- `add_order_item` — add or increment an order line, reserve product stock, and recalculate the order total.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — merge eligible supplier quantities into stock and record synchronization outcomes.

#### Data flow
- **AI generation:** File context and configured conditions select inputs; facts and run/model settings shape an `AiGenerationRequest`; the selected provider generates text, which completion parsing cleans.
- **Byte processing:** Input bytes flow through OpenCL partial histograms and reduction, or through circular-buffer writes and reads with occupancy updates.
- **Reports and backups:** CSV rows are validated and currency-filtered before aggregation and threshold filtering; backup directories are archived, finalized as timestamped `.tar.gz` files, then pruned by retention.
- **Database operations:** Library checkout and return update loans and availability with audit events; webshop item updates reserve stock and refresh totals; warehouse synchronization groups supplier receipts by warehouse and SKU, merges stock, and logs results.

#### Dependencies
- AI providers use `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`; local inference depends on llama.cpp JNI types, while compatible requests use Jackson and injected environment lookup for API keys.
- Histogram kernels use OpenCL built-ins and configured launch dimensions; the ring buffer uses `ringbuf.h` and standard C allocation and memory-copy facilities.
- Backup rotation uses Bash utilities including `tar`, `date`, `ls`, `du`, and `rm`; sales reporting uses Python standard-library CSV, date, decimal, argument-parsing, and collection modules.
- SQL workflows span PostgreSQL library objects, MySQL 8 shop objects, and SQL Server warehouse objects; `dbo.usp_SyncStock` reads `dbo.SupplierFeed`, updates `dbo.StockLevel`, and writes `dbo.SyncLog`.

#### Cross-cutting
- AI configuration uses mutable JavaBeans; condition and fact-counter validation report `IllegalArgumentException`, and providers declare `IOException` where applicable.
- Ring-buffer capacity must be a nonzero power of two, with one slot reserved to distinguish full from empty; histogram kernels depend on configured work sizes.
- Sales amounts use `Decimal` and malformed-row errors include file and line context; backup rotation uses strict Bash settings and an exit trap for temporary archives.
- Database workflows enforce domain constraints; shop and warehouse updates use transactional locking or transaction control, shop amounts are stored in cents, and library and warehouse operations record audit or run outcomes.
