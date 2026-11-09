### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:00:38Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports AI-generated source documentation alongside byte-processing utilities, backup and sales reporting, and library, shop, and warehouse data workflows.

#### Purpose
- Configure AI source-documentation generation with local or OpenAI-compatible providers.
- Provide operational utilities and SQL models/routines for byte handling, backup rotation, sales reporting, lending, order fulfillment, and inventory synchronization.

#### Responsibilities
- **AI documentation (`java/`):** Configure source selection and generation, validate conditions and fact counters, choose inference providers, and normalize completions.
- **Byte utilities (`other/kernels/`, `other/ringbuf/`):** Compute byte histograms in parallel and provide bounded FIFO byte storage.
- **Operational scripts (`other/scripts/`):** Rotate compressed backups and aggregate currency-filtered orders into monthly regional revenue reports.
- **Relational workflows (`sql/`):** Manage library catalog and lending, shop orders and stock reservation, and supplier-driven warehouse inventory updates with reporting and audit logs.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold run-wide source rules and per-model generation settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate source conditions, format fact counts, and represent oversized-input policies.
- `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` — define, select, and support local or remote generation backends.
- `histogram_partial`, `histogram_reduce`, `BINS` — produce and combine 256-bin byte-frequency counts.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — represent and operate a bounded FIFO byte buffer.
- `rotate_backups.sh` — create timestamped compressed backups and enforce retention.
- `parse_orders`, `revenue_by_month_and_region` — parse order CSV data and aggregate monthly regional revenue.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit` — implement lending operations, fee calculation, and event auditing.
- `add_order_item(p_order_id, p_sku, p_qty)` — validate stock, update order lines and inventory, and recalculate order totals.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — merge recent supplier quantities into stock levels and log synchronization outcomes.

#### Data flow
- **AI generation:** Run and model configuration guide source-condition checks and fact formatting; the provider factory selects a backend, which consumes an `AiGenerationRequest` and returns completion text and optional timings.
- **Byte processing:** Input bytes yield work-group partial histograms that are reduced into final counts; ring-buffer clients initialize storage, transfer bytes with wraparound, and query occupancy.
- **Scripts:** A source directory becomes a timestamped archive that is pruned to the retention limit; CSV rows become validated, currency-filtered orders, monthly regional totals, and threshold-filtered output.
- **Database workflows:** Library routines update lending state and feed audits and reports; shop item insertion validates and reserves stock before recalculating totals; warehouse synchronization aggregates supplier rows, merges stock, and logs success or failure.

#### Dependencies
- AI generation uses domain types such as `AiPromptDefinition`, `AiModelDefinition`, `AiGenerationRequest`, and `AiFileContext`; local inference integrates llama.cpp support, while remote requests use Jackson JSON and chat-response data.
- OpenCL C work-group operations support histogram kernels; the ring buffer uses standard C allocation and copy facilities.
- Backup rotation uses Bash and system utilities; the sales report uses Python standard-library CSV, date, decimal, and aggregation facilities.
- PostgreSQL library and shop objects use related catalog, lending, order, and product tables; SQL Server warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- AI configuration is generally mutable; local model creation is lazy and cached, while provider lifecycle uses `AutoCloseable`.
- The ring buffer requires power-of-two capacity, reserves one slot to distinguish full from empty, and describes single-producer/single-consumer use.
- Database constraints enforce relationships; shop updates lock product rows, and warehouse synchronization uses a transaction with rollback and error logging.
- Input validation is domain-specific: AI conditions and fact definitions are validated, sales parsing raises `ValueError` for invalid data, and lending and shop routines report operational failures.
