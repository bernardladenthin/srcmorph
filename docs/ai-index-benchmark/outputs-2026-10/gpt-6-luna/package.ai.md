### ai
- H: 1.0
- C: 69947EBB
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:23:41Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted source documentation alongside low-level byte utilities, operational reporting and backup tools, and database workflows for library circulation, shop orders, and warehouse inventory.

#### Purpose
- Configure and route AI text generation for source documentation, with local, mock, and OpenAI-compatible backends.
- Bundle supporting byte-processing utilities and operational workflows for reporting, backups, circulation, ordering, and stock synchronization.

#### Responsibilities
- **AI documentation and generation:** Configure source eligibility, prompts, facts, model options, oversized-file policies, and provider selection; clean and time generated completions.
- **Byte processing:** Count byte frequencies with OpenCL histogram kernels and provide FIFO byte-stream storage through a C ring buffer.
- **Operations and reporting:** Rotate dated compressed backups and aggregate filtered order CSV data into monthly regional revenue reports.
- **Database workflows:** Manage library catalog and circulation, shop order lines and stock, and warehouse inventory synchronization with operational records and reports.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold source-selection, generation-routing, inference, and documentation metadata settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — match source conditions, format validated facts, and parse oversized-file policies.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — define generation behavior and select a backend.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `MockAiGenerationProvider` — implement local, compatible-API, and mock generation routes.
- `AiCompletionParser`, `AiGenerationTimings`, `LlamaCppJniConfig` — process completion text, carry timing data, and configure local inference.
- `histogram_partial`, `histogram_reduce`, `BINS` — count bytes in work groups and reduce partial counts into a 256-bin histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — manage FIFO byte storage and capacity queries.
- `rotate_backups.sh` — create dated compressed archives and prune backups according to retention settings.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — parse order data and produce monthly regional revenue reports.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — perform library checkout, return, and late-fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record library loan events; `overdue_loans`, `member_loan_stats`, `popular_books` provide circulation reporting.
- `product`, `customer_order`, `order_item`, `add_order_item` — represent shop catalog and order data and manage order lines, stock deductions, and totals.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — supply warehouse quantities, maintain stock, log synchronization, and run stock updates.

#### Data flow
- AI source metadata and condition trees determine file eligibility; configured counters and source text produce facts, while generation settings route requests to a provider and completions pass through text parsing.
- Byte input is counted into per-work-group histograms and reduced; separately, ring-buffer writes and reads transfer bytes through wraparound storage.
- Backup rotation archives a source directory before retention pruning; sales CSVs are validated and currency-filtered before monthly regional aggregation and output.
- Library checkout and return update circulation and copy inventory with audit records; shop order-line changes validate and reserve stock before recalculating totals; warehouse synchronization aggregates recent feed quantities into stock levels and logs outcomes.

#### Dependencies
- AI configuration and provider components collaborate through `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and related condition, model, and prompt types; path matching uses `AiSourceExcludeFilter`.
- AI provider support uses llama.cpp JNI, Jackson JSON APIs, and Java I/O, URL, collection, regex, and time APIs.
- OpenCL kernels use work-item/group operations, local memory, barriers, and atomic increments; the C ring buffer uses standard allocation and memory-copy facilities.
- Backup rotation uses Bash and system utilities; sales reporting uses Python standard-library CSV, CLI, date, decimal, and aggregation facilities.
- Library relationships connect authors, books, members, branches, copies, and loans; shop order procedures use `product`, `customer_order`, and `order_item`.
- Warehouse synchronization uses SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Configuration is generally mutable and unsynchronized; `LlamaCppJniConfig` is immutable, while lazy local-model initialization is unsynchronized.
- Inventory changes are paired with operational records: loan audits, shop order lines and totals, or warehouse synchronization logs.
- Concurrency and transaction controls include ring-buffer producer-/consumer-owned indices, locked shop product rows, and transactional warehouse updates.
- Error handling includes provider `IOException`, backup cleanup on failure, contextual CSV parsing errors, circulation validation, shop stock/SKU errors, and warehouse rollback with logged errors and rethrowing.
