### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:54:17Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted source summarization, byte and operations utilities, and database workflows for lending, order fulfillment, and warehouse inventory.

#### Purpose
- Configure AI generation for source summaries, including file selection, provider choice, and oversized-input handling.
- Provide standalone byte-processing and reporting tools alongside SQL-backed library, shop, and warehouse operations.

#### Responsibilities
- **AI generation (`java/`):** Configure source and model settings, evaluate file conditions, extract source facts, route requests to mock, llama.cpp JNI, or OpenAI-compatible providers, and clean generated completions.
- **Byte processing (`other/`):** Count byte frequencies with OpenCL histogram kernels and provide fixed-capacity circular buffering through `ringbuf_t`.
- **Command-line operations (`other/`):** Create and prune compressed backups; parse currency-filtered order CSVs and report monthly regional revenue.
- **Database workflows (`sql/`):** Manage library catalog and lending, shop order lines and stock reservation, and supplier-driven warehouse stock synchronization, with supporting audits and reports.

#### Key units
- `SrcMorphConfiguration` / `AiGenerationConfig` — hold source-selection, provider-routing, model, and generation settings.
- `AiConditionEvaluator` / `AiFactExtractor` / `AiOversizeStrategy` — evaluate file eligibility, count configured source facts, and represent oversized-input handling.
- `AiGenerationProviderFactory` / `AiGenerationProvider` — select and define completion-generation providers; `AiCompletionParser` cleans generated text.
- `histogram_partial` / `histogram_reduce` / `BINS` — compute and combine 256-bin byte histograms.
- `ringbuf_t` / `ringbuf_init` / `ringbuf_write` / `ringbuf_read` — represent and operate a bounded circular byte buffer.
- `rotate_backups.sh` — create timestamped compressed archives and apply retention pruning.
- `Order` / `parse_orders` / `revenue_by_month_and_region` — model, filter, aggregate, and report sales data.
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` — implement library checkout, return, and late-fee operations.
- `loan_audit` / `trg_loan_audit` / `overdue_loans` / `member_loan_stats` / `popular_books` — record lending events and provide loan-related reports.
- `add_order_item` — place or increment order lines while reserving product stock and recalculating order totals.
- `dbo.usp_SyncStock` — merge aggregated supplier quantities into warehouse stock and record synchronization outcomes.

#### Data flow
- AI settings and file context determine eligible source inputs; fact extraction and oversize strategy support requests routed through the provider factory to completion text and optional timing data.
- Histogram inputs are counted into work-group partials and reduced to final bins; ring-buffer writes and reads transfer available bytes while updating circular indices.
- Backup settings drive archive creation followed by retention pruning; sales CSV rows become currency-matched orders, then monthly regional totals and threshold-filtered output.
- Library records feed checkout and return routines, audit events, and reports; shop item placement updates order lines, inventory, and totals; warehouse feed rows are filtered and aggregated before stock merging and sync logging.

#### Dependencies
- AI generation uses internal condition, request, prompt, fact, and provider-support types; external integrations include llama.cpp JNI and Jackson.
- OpenCL kernels use work-item, work-group, barrier, and atomic built-ins; ring-buffer code uses `ringbuf.h` and C allocation and memory-copy facilities.
- Backup rotation uses Bash utilities including `tar`, `date`, `mv`, and `rm`; sales reporting uses Python standard-library CSV, date, decimal, and aggregation facilities.
- Library routines and reports depend on catalog, member, branch, copy, and loan tables; shop logic uses `product`, `customer_order`, and `order_item`; warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- AI configuration uses mutable beans and defaults; providers are `AutoCloseable`, and llama.cpp model initialization is lazy and unsynchronized.
- Ring buffers use power-of-two capacity, masked wraparound, and a reserved slot; volatile indices are intended for single-producer/single-consumer use.
- SQL integrity relies on relational constraints and indexes; transactions, audit records, and domain-specific error handling protect operational workflows.
- Monetary values are stored in cents in the SQL domains, and warehouse synchronization uses UTC timestamps.
