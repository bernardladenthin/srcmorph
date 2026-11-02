### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:30:17Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides source-documentation generation tools, byte-processing and operational utilities, and database workflows for circulation, commerce, and inventory.

#### Purpose
- Combines Java configuration and AI-backed source analysis with standalone utilities and SQL business workflows.
- Covers library lending, webshop order fulfillment, warehouse replenishment, byte handling, backup rotation, and sales reporting.

#### Responsibilities
- **Source analysis and AI generation (`java/`):** Configure source scope, prompts, model settings, and file selection; extract source facts; route requests to mock, local llama.cpp, or OpenAI-compatible providers.
- **Byte processing (`other/`):** Compute byte-frequency histograms with OpenCL kernels and provide a fixed-capacity ring buffer.
- **Operational reporting (`other/`):** Rotate compressed backups and parse CSV orders into currency-filtered monthly regional revenue reports.
- **Database workflows (`sql/`):** Manage library catalog and circulation, shop orders and stock, and transactional warehouse stock synchronization.

#### Key units
- `SrcMorphConfiguration` / `AiGenerationConfig` — define run-level source and prompt settings and per-model generation options.
- `AiConditionEvaluator` / `AiFactExtractor` — match file contexts against configured conditions and produce source fact counts.
- `AiGenerationProviderFactory` / `LlamaCppJniAiGenerationProvider` / `OpenAiCompatibleProviderSupport` — select and operate AI generation backends.
- `histogram_partial` / `histogram_reduce` — compute and combine 256-bin byte histograms.
- `ringbuf_t` — represents the circular byte buffer and its read/write state.
- `rotate_backups.sh` / `parse_orders` / `revenue_by_month_and_region` — handle backup retention and sales parsing and aggregation.
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` — implement library lending and overdue-fee operations.
- `add_order_item` — adds shop order quantities while checking stock and recalculating totals.
- `dbo.usp_SyncStock` — merges recent supplier-feed quantities into warehouse stock and records synchronization outcomes.

#### Data flow
- **AI generation:** Run configuration selects source files and prompts; condition and fact utilities prepare inputs; provider routing sends requests to local or remote inference and returns cleaned completions and timing data.
- **Utilities:** Bytes flow through histogram partial counts and reduction, or through ring-buffer writes and reads; backup and sales scripts transform directories and CSV inputs into retained archives and revenue reports.
- **Database operations:** Library checkout and return update loan and availability records and feed audit/reporting; shop item addition updates order lines, stock, and totals; warehouse synchronization aggregates feed rows, merges stock, and logs results.

#### Dependencies
- Java generation uses source-selection and prompt/fact types, llama.cpp interfaces, and Jackson JSON support for remote requests.
- Byte utilities depend on OpenCL primitives and C allocation/copy facilities; operational scripts use Bash/system commands and Python standard-library CSV and aggregation modules.
- SQL workflows use PostgreSQL library tables, MySQL shop tables, and SQL Server supplier-feed, stock-level, and sync-log tables.

#### Cross-cutting
- Configuration, validation, and error handling vary by domain: Java conditions and fact utilities validate inputs; sales reporting rejects malformed data; SQL workflows enforce constraints and domain-specific failures.
- Inventory updates accompany library circulation and shop ordering; warehouse synchronization is transactional, with rollback and error logging.
- Ring-buffer capacity must be a nonzero power of two; local model initialization is lazy and unsynchronized.
