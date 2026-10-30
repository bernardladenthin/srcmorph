### ai
- H: 1.0
- C: 1BB7C853
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:37:45Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted source summarization alongside byte-processing utilities, backup and sales-reporting tools, and SQL workflows for lending, retail orders, and inventory synchronization.

#### Purpose
- Generate source summaries through configurable AI providers and support source eligibility and facts.
- Provide standalone utilities and database workflows for byte handling, operational reporting, circulation, orders, and stock.

#### Responsibilities
- **AI-assisted summarization:** Evaluate file conditions, format source facts, configure generation, route requests to local or OpenAI-compatible providers, and clean completions.
- **Byte and operational utilities:** Compute byte histograms, manage a bounded FIFO buffer, rotate directory backups, and report monthly regional revenue.
- **SQL business workflows:** Manage library circulation and reports, shop orders and stock, and warehouse synchronization with audit and sync logging.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — hold run-level and per-step generation settings.
- `AiConditionEvaluator`, `AiFactExtractor` — evaluate source eligibility and format source-text match counts.
- `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport` — select and support AI generation backends.
- `histogram_partial`, `histogram_reduce` — calculate and combine byte histograms.
- `ringbuf_t`, `ringbuf_write`, `ringbuf_read` — represent and operate on bounded FIFO storage.
- `rotate_backups.sh`, `revenue_by_month_and_region` — rotate archives and aggregate filtered order revenue.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — handle library checkout, returns, and late fees.
- `add_order_item` — update order lines, product stock, and order totals.
- `dbo.usp_SyncStock(@Since)` — merge supplier updates into warehouse stock and log outcomes.

#### Data flow
- Source context and conditions determine eligibility; run settings select a provider, which turns an `AiGenerationRequest` into completion text.
- Byte input becomes partial histogram counts and then a final histogram; ring-buffer writes and reads transfer bytes in FIFO order.
- Backup processing archives a source directory before pruning older archives; sales reporting filters CSV orders by currency and aggregates monthly regional totals.
- Library circulation updates loans and copy availability, with audit events and reports derived from circulation records.
- Shop order updates reserve product stock and recalculate totals; warehouse synchronization groups recent supplier quantities by warehouse and SKU, merges stock, and logs results.

#### Dependencies
- AI generation uses internal context, condition, request, timing, prompt, and configuration types; path filtering uses `AiSourceExcludeFilter`.
- Local inference uses llama.cpp JNI support; compatible chat-completion support uses Jackson and response/usage types.
- Byte processing uses OpenCL; the ring buffer uses its C API and standard headers. Backup and reporting use system utilities and Python standard-library modules.
- SQL workflows use library, shop, and warehouse tables; PostgreSQL `generate_series` and SQL Server `SYSUTCDATETIME()` and `ERROR_MESSAGE()` are noted dependencies.

#### Cross-cutting
- AI providers expose generation, timing, and close contracts; generation APIs declare `IOException`.
- Ring-buffer operations support partial transfers and are designed for single-producer/single-consumer use.
- SQL constraints and indexes enforce relationships; warehouse synchronization rolls back, logs failures, and rethrows.
