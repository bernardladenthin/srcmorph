### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:08:50Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-assisted source documentation alongside byte processing, operational reporting, and database workflows for circulation, order fulfillment, and inventory synchronization.

#### Purpose
- Groups tools for configuring AI completion and source selection, processing byte data, rotating backups, reporting revenue, and managing business records.
- Covers library circulation, shop inventory and orders, and warehouse stock synchronization across PostgreSQL, MySQL, and SQL Server.

#### Responsibilities
- **AI documentation and inference:** Configure source filters, conditions, facts, prompts, generation settings, and oversized-file strategies; route completion requests to mock, local llama.cpp, or OpenAI-compatible providers.
- **Byte and operational utilities:** Compute byte histograms, transfer bytes through a bounded ring buffer, rotate compressed backups, and aggregate monthly regional order revenue by currency.
- **Business database workflows:** Maintain library catalog and circulation records with auditing and reports; place shop order items while reserving stock; synchronize warehouse balances from supplier feeds and log outcomes.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold source-selection, generation, provider, and output settings.
- `AiConditionEvaluator`, `AiFactExtractor` — match configured conditions and produce source facts from regex counters.
- `AiGenerationProviderFactory`, `AiGenerationProvider` — select a completion backend and define its generation contract.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — provide local or remote inference support and normalize completion text.
- `histogram_partial`, `histogram_reduce` — compute and combine byte-frequency counts.
- `ringbuf_t`, `ringbuf_write`, `ringbuf_read` — manage bounded circular byte storage and transfers.
- `rotate_backups.sh` — create timestamped compressed backups and prune older archives.
- `parse_orders`, `revenue_by_month_and_region` — parse currency-matched orders and aggregate monthly regional revenue.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit` — implement library circulation operations, fees, and audit recording.
- `add_order_item` — update a shop order line, reserve product stock, and recalculate the order total.
- `dbo.usp_SyncStock(@Since)` — merge supplier-feed quantities into warehouse stock and record synchronization outcomes.

#### Data flow
- Source configuration and file facts feed condition evaluation and AI provider selection; providers generate completions that are normalized and returned with available timing metrics.
- Byte inputs become per-group histogram counts and then reduced totals; ring-buffer callers write and read queued bytes within fixed capacity.
- Backup rotation validates inputs, creates a temporary archive, finalizes it, and prunes older matching backups; sales reporting parses CSV orders into filtered monthly regional totals.
- Library checkout and return operations update circulation and inventory records and trigger audit events; shop item placement adjusts order lines, stock, and totals; warehouse synchronization aggregates eligible feed rows into stock balances and logs results.

#### Dependencies
- AI generation uses `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, Jackson JSON support, and the llama.cpp JNI binding.
- Histogram processing uses OpenCL C; ring-buffer operations use standard C allocation and copying; backup rotation uses Bash utilities; revenue reporting uses Python standard-library modules.
- Library routines, views, and triggers use PostgreSQL catalog and circulation tables; shop procedures use MySQL order and product tables; SQL Server synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- AI configuration uses mutable JavaBeans; condition and fact utilities reject malformed configuration, while provider generation and resource failures use `IOException`.
- The ring buffer uses power-of-two capacity, reserves one slot to distinguish full from empty, and specifies single-producer/single-consumer ownership of its indices.
- Inventory changes are coupled to circulation, order placement, and supplier synchronization; database domains enforce their own constraints and transaction or audit behavior.
- SQL implementations use distinct dialect-specific mechanisms: PostgreSQL routines and triggers, a MySQL locking procedure, and a SQL Server transactional merge with error logging.
