### ai
- H: 1.0
- C: AB388750
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:52:10Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted source documentation alongside byte-processing utilities, operational reporting, and database workflows for library circulation, shop orders, and warehouse inventory.

#### Purpose
- Centralizes AI generation configuration, source selection, and provider execution for documentation workflows.
- Groups utilities and data workflows for byte analysis and transfer, backups and sales reporting, library lending, shop ordering, and warehouse stock synchronization.

#### Responsibilities
- **AI documentation:** Configure source filters, models, prompts, generation options, and output metadata; evaluate file conditions and extract configured source facts; route requests to mock, local llama.cpp, or OpenAI-compatible providers.
- **Byte processing:** Compute 256-bin byte histograms with OpenCL kernels and manage fixed-capacity circular buffers with partial reads, writes, and wraparound.
- **Operations and sales reporting:** Create timestamped compressed backups with retention cleanup; parse currency-matched order CSVs and aggregate monthly revenue by region.
- **Database workflows:** Manage library catalog and circulation records, checkout and return operations, audit events, fees, and reports; reserve shop inventory while adding order items; synchronize warehouse stock from supplier feeds.

#### Key units
- `SrcMorphConfiguration` / `AiGenerationConfig` — source scope, model routing, generation settings, and documentation metadata.
- `AiConditionEvaluator` / `AiFactExtractor` — evaluate file-selection rules and format regex-based source facts.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — define generation and lifecycle operations and select the configured backend.
- `AiCompletionParser` / `OpenAiCompatibleProviderSupport` — clean generated completions and prepare or interpret compatible chat-completion data.
- `histogram_partial` / `histogram_reduce` — count bytes into work-group partial histograms and combine the results.
- `ringbuf_t` / `ringbuf_write` / `ringbuf_read` — represent and operate on the bounded circular byte buffer.
- `rotate_backups.sh` — produce timestamped archives and enforce backup retention.
- `parse_orders` / `revenue_by_month_and_region` — parse filtered CSV orders and aggregate regional monthly revenue.
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` — implement library checkout, return, and overdue-fee operations.
- `loan_audit` / `overdue_loans` / `member_loan_stats` / `popular_books` — capture circulation events and report lending activity.
- `add_order_item` — add or increment a shop order line while reserving stock and recalculating the total.
- `dbo.usp_SyncStock` — aggregate supplier-feed quantities into warehouse stock and log synchronization outcomes.

#### Data flow
- AI settings configure source filtering and request generation; conditions match file context, fact counters produce a facts block, and the selected provider generates text that may be cleaned or accompanied by timing metrics.
- Histogram inputs become work-group-local counts, partial results, and a reduced final histogram; ring-buffer writes and reads transfer bytes through available capacity, splitting at the buffer boundary.
- Backup arguments produce a temporary archive, then a final compressed backup followed by retention cleanup; sales CSVs are validated and currency-filtered before aggregation and threshold filtering.
- Library checkout and return operations update loans and copy state, with audit records and circulation reports derived from the resulting records.
- Shop item addition validates and locks a product, updates order lines and stock, then recalculates the order total; warehouse synchronization aggregates eligible feeds, merges stock changes, and logs results.

#### Dependencies
- AI configuration and generation collaborate with `AiFileContext`, condition and model definitions, `AiPromptSupport`, and provider-specific support; local inference uses llama.cpp JNI types, while compatible support uses Jackson and chat-response usage data.
- OpenCL primitives support histogram computation; the C ring buffer uses standard allocation and memory-copy facilities; backup rotation uses Bash and system utilities; sales reporting uses Python standard-library CSV, date, decimal, and CLI facilities.
- Library SQL relationships connect books, authors, members, branches, copies, and loans; shop order lines reference products and orders; warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- AI configuration is generally mutable and unsynchronized; provider generation and close failures use `IOException`, and local model initialization is lazy and unsynchronized.
- The ring buffer requires a nonzero power-of-two capacity, reserves one slot to distinguish full from empty, and assumes single-producer/single-consumer ownership; histogram kernels use barriers and local-memory atomics.
- SQL spans PostgreSQL, MySQL, and SQL Server; workflows use constraints, transactions, and row locking where applicable.
- Backup timestamps use UTC; sales amounts use `Decimal`, with invalid dates or amounts reported with file and line context.
