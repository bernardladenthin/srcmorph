### ai
- H: 1.0
- C: 3148BC75
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:48:16Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package combines AI-assisted source summarization, low-level data utilities and operational scripts, and relational workflows for lending, commerce, and warehouse inventory.

#### Purpose
- Configure source selection and generate normalized AI completions through local or OpenAI-compatible providers.
- Provide byte-processing utilities, backup and revenue-reporting tools, and SQL workflows for library borrowing, shop fulfillment, and warehouse stock synchronization.

#### Responsibilities
- **AI source processing:** Configure file scope, conditions, prompts, models, facts, and oversized-file policies; route generation requests to local or remote providers and normalize completions.
- **Byte utilities:** Compute byte-value histograms with OpenCL and transfer bytes through a bounded C ring buffer.
- **Operations and reporting:** Create and rotate compressed backups; validate order CSVs and report monthly regional revenue.
- **Relational business workflows:** Maintain library catalog, copies, loans, audits, and reports; reserve shop inventory for order items; synchronize warehouse stock from supplier feeds and log outcomes.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold run-wide source and generation settings and per-model parameters.
- `AiConditionEvaluator`, `AiConditionGroup`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate file filters, format source facts, and resolve oversized-file policies.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — define provider lifecycle and select a backend from effective settings.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — run local inference, handle remote chat-completions requests, and normalize completion text.
- `histogram_partial`, `histogram_reduce`, `BINS` — build and combine byte-value histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — represent and operate a bounded byte buffer.
- `rotate_backups.sh` — create timestamped compressed archives and enforce retention.
- `Order`, `parse_orders`, `revenue_by_month_and_region` — validate order records and aggregate regional monthly revenue.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — represent library catalog, membership, borrowing, and copy inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit`, `overdue_loans`, `member_loan_stats`, `popular_books` — implement borrowing operations, audit, and loan reporting.
- `product`, `customer_order`, `order_item`, `add_order_item` — represent shop orders and reserve stock while updating order totals.
- `dbo.usp_SyncStock(@Since)`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — aggregate supplier quantities, update warehouse stock, and record synchronization results.

#### Data flow
- Source configuration and file context feed condition evaluation and selection; source text and configured counters feed fact-block generation; requests pass through the provider factory to local or remote inference, then completion parsing and timing reporting.
- Histogram input bytes become work-group partial counts and then final bins; ring-buffer writes and reads transfer available bytes across wraparound.
- Backup sources flow through a temporary `.partial` archive to a timestamped `.tar.gz` archive followed by retention cleanup; order CSV rows are validated, currency-matched, aggregated by month and region, filtered, and formatted for output.
- Library checkout and return update loan and copy availability records and trigger auditing; reports derive from loans and related records. Shop item additions validate and reserve stock before recalculating order totals. Warehouse synchronization filters and aggregates feed quantities, merges stock changes, and logs results.

#### Dependencies
- AI generation uses `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, llama.cpp JNI support, Jackson, and Java URL/HTTP utilities.
- OpenCL histogram kernels use work-item and work-group facilities, atomics, and barriers; the C ring buffer uses standard allocation and byte-copy facilities.
- Backup rotation uses Bash and system utilities; revenue reporting uses Python standard-library CSV, argument parsing, date, and decimal modules.
- SQL workflows span PostgreSQL library SQL, MySQL 8 shop SQL, and SQL Server warehouse logic; they depend on their domain tables, with library audit triggers writing to `loan_audit` and warehouse synchronization using `SupplierFeed`, `StockLevel`, and `SyncLog`.

#### Cross-cutting
- AI configuration uses mutable JavaBeans; condition evaluation and fact extraction are stateless. Providers implement `AutoCloseable`; local model loading is lazy and cached.
- The ring buffer requires a nonzero power-of-two capacity, reserves one slot to distinguish full from empty, and permits partial transfers; metadata assigns `head` and `tail` to producer and consumer roles.
- Revenue aggregation uses `Decimal`; malformed required headers, dates, or amounts raise `ValueError`. Backup cleanup removes unfinished partial archives on exit.
- SQL transactions and constraints protect domain updates; concurrency controls include shop `FOR UPDATE` stock checks and warehouse transactional `MERGE` with rollback, error logging, and rethrow.
