### ai
- H: 1.0
- C: C91EB333
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:52:32Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides source-summary generation capabilities alongside byte-processing utilities, operational reporting tools, and database workflows for circulation, ordering, and inventory.

#### Purpose
- Configures AI-backed source-summary generation, including file selection, prompt/model settings, output metadata, and oversize handling.
- Groups supporting byte utilities, backup and sales-report commands, and SQL workflows for library circulation, shop orders, and warehouse stock.

#### Responsibilities
- **Summary generation (`java/`):** Select source files, evaluate conditions, extract configured facts, route requests to AI providers, and clean completion text.
- **Byte processing (`other/`):** Compute byte-frequency histograms with OpenCL and provide a wraparound FIFO byte ring buffer.
- **Operational tools (`other/`):** Create timestamped compressed backups with retention and report currency-filtered monthly revenue by region.
- **Relational workflows (`sql/`):** Manage library catalog, loans, audits, and reports; validate shop order lines and reserve stock; synchronize warehouse inventory from supplier feeds.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — define source scopes, generation steps, model settings, and output metadata.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate file-selection rules, format regex-based source facts, and parse oversize policies.
- `AiGenerationProviderFactory`, `AiGenerationProvider` — select and define the generation-provider contract; `AiCompletionParser` cleans returned text.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` — support local llama.cpp inference and OpenAI-compatible chat completions.
- `histogram_partial`, `histogram_reduce` — produce per-byte histograms and combine partial counts.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — manage FIFO byte storage and transfers with wraparound.
- `rotate_backups.sh`, `parse_orders`, `revenue_by_month_and_region` — rotate compressed archives and validate, filter, and aggregate sales records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — process library circulation and calculate overdue charges; `loan_audit` and its trigger record loan transitions.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report library circulation activity.
- `add_order_item` — validates and reserves shop stock while updating order lines and totals.
- `dbo.usp_SyncStock(@Since)` — aggregates supplier feeds into warehouse stock levels and records synchronization outcomes.

#### Data flow
- Source configuration selects files; fact counters scan source text; generation settings and prompts feed a selected provider, whose completion is cleaned and may include timing metrics.
- Histogram kernels accumulate local byte bins and reduce partials; the ring buffer accepts writes and drains reads through masked index wraparound.
- Backup rotation archives a source directory and applies retention; sales reporting parses and filters CSV orders, aggregates monthly regional revenue, and emits filtered results.
- Library checkout and return update loan state and availability, with audit triggers recording transitions; reports aggregate circulation records.
- Shop order-item processing validates SKU and stock, updates order lines, reserves product stock, and recalculates totals; warehouse sync aggregates recent feed rows, updates stock, and logs success or failure.

#### Dependencies
- Java generation configuration uses condition, prompt, model, calibration, and fact-definition types; providers use generation request, prompt support, and timing types.
- AI provider integrations include mock, llama.cpp JNI, Jackson JSON support, and OpenAI-compatible APIs.
- OpenCL kernels use work-group built-ins, local memory, atomics, and barriers; the C ring buffer uses `ringbuf.h` and standard allocation and memory-copy functions.
- Shell backup tooling uses `tar`, `date`, `mv`, and `rm`; sales reporting uses Python standard-library CSV, CLI, date, decimal, and aggregation facilities.
- Library SQL routines use catalog, member, loan, branch, and copy records; shop ordering uses `product`, `customer_order`, and `order_item`; warehouse sync uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Integrity checks span configured conditions and facts, ring-buffer capacity, CSV validation, and relational keys, checks, and indexes.
- Failure handling includes Java argument and I/O exceptions, nonzero command-line exit codes, SQL errors, and warehouse transaction rollback with error logging.
- Concurrency controls include OpenCL work-group synchronization, row locking during shop stock reservation, and transactional warehouse stock updates.
- Generation defaults include mock provider selection and `FAIL` oversize handling; the ring buffer requires power-of-two capacity and reserves one slot to distinguish full from empty.
