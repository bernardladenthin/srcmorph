### ai
- H: 1.0
- C: 5E27BCB7
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:03Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted source documentation, byte-processing and operational utilities, and relational workflows for library circulation, online orders, and warehouse inventory.

#### Purpose
- Groups tools for configuring AI-generated source documentation, processing and reporting data, and managing lending, order fulfillment, and stock synchronization.
- Includes Java, OpenCL, C, shell, Python, PostgreSQL, MySQL, and SQL Server components.

#### Responsibilities
- **AI documentation and inference (`java/`):** Configures source selection, conditions, facts, prompts, models, and generation settings; routes requests to llama.cpp, OpenAI-compatible, or mock providers.
- **Byte processing (`other/`):** Computes byte histograms with OpenCL and provides a bounded SPSC ring buffer for byte streams.
- **Operational reporting and backups (`other/`):** Rotates compressed directory backups and aggregates currency-filtered CSV orders by month and region.
- **Library circulation (`sql/`):** Defines catalog, member, branch, copy, and loan data; supports checkout, returns, overdue fees, audit, and reports.
- **Shop fulfillment (`sql/`):** Manages products and orders, reserves stock when adding order items, and recalculates order totals.
- **Warehouse synchronization (`sql/`):** Aggregates recent supplier feed quantities into stock balances and records synchronization outcomes.

#### Key units
- `SrcMorphConfiguration` / `AiGenerationConfig` — source scope, documentation-generation definitions, and per-step inference settings.
- `AiConditionEvaluator` / `AiFactExtractor` — validate and match source conditions, and count configured regex facts.
- `AiGenerationProviderFactory` / `AiGenerationProvider` — resolve inference backends and define the generation contract.
- `LlamaCppJniAiGenerationProvider` / `OpenAiCompatibleProviderSupport` — run local inference or prepare OpenAI-compatible chat-completions metadata.
- `AiCompletionParser` / `AiOversizeStrategy` — clean generated reasoning prefixes and parse oversized-file handling choices.
- `histogram_partial` / `histogram_reduce` — calculate per-work-group byte counts and reduce them to 256-bin totals.
- `ringbuf_t` / `ringbuf_write` / `ringbuf_read` — represent and transfer bytes through a fixed-capacity circular buffer.
- `rotate_backups.sh` — create dated compressed archives and prune older backups according to retention settings.
- `Order` / `parse_orders` / `revenue_by_month_and_region` — parse filtered CSV orders and aggregate revenue for CLI reporting.
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` — process library checkout, returns, and capped overdue charges.
- `author`, `book`, `member`, `branch`, `book_copy`, `loan`, `loan_audit` — library catalog, lending, and audit records.
- `overdue_loans` / `member_loan_stats` / `popular_books` — library overdue, member activity, and popularity reports.
- `add_order_item(p_order_id, p_sku, p_qty)` — validate stock, update order lines, reserve inventory, and recalculate totals.
- `product` / `customer_order` / `order_item` — shop catalog, order headers, and order lines.
- `dbo.usp_SyncStock(@Since)` — apply aggregated supplier quantities to stock and log synchronization results.
- `dbo.SupplierFeed` / `dbo.StockLevel` / `dbo.SyncLog` — warehouse input, current balances, and run history.

#### Data flow
- AI source configuration supplies selection rules, prompts, model and field-generation settings, and facts; conditions match file context, facts count regex matches, and provider routing generates and cleans completion text.
- Local llama.cpp generation combines request data, prompt support, and provider configuration; OpenAI-compatible support prepares request and endpoint/auth metadata.
- Byte input flows through per-work-group histogramming and reduction; ring-buffer writes and reads transfer available bytes with wraparound.
- Backup directories become dated `.tar.gz` archives followed by retention pruning; CSV orders become filtered records, monthly/regional totals, and tab-separated output.
- Library checkout and return routines update loans and copy availability, with triggers recording events and views deriving reports.
- Shop item additions resolve SKUs, lock and validate stock, update order lines, decrement inventory, and recalculate totals.
- Warehouse synchronization filters feed rows by `ReceivedAt`, aggregates by warehouse and SKU, merges stock balances, and logs results; failures roll back stock changes, log the error, and rethrow.

#### Dependencies
- AI configuration uses condition, file-context, fact, prompt, model, calibration, and field-generation types; providers use `AiGenerationRequest`, `AiPromptSupport`, llama JNI types, Jackson, and llama response types.
- Histogram kernels use OpenCL C 1.2; the ring buffer uses C allocation and copy routines; backup rotation uses Bash and system commands; order reporting uses Python standard-library CSV, CLI, date, decimal, and aggregation facilities.
- Library workflows use PostgreSQL routines, triggers, views, and relational keys; shop tables use MySQL 8 foreign keys; warehouse synchronization uses SQL Server tables and built-ins.

#### Cross-cutting
- Database constraints enforce relational integrity; multi-step stock and lending operations use transactions or locks, with failures surfaced as SQL errors or signals.
- Library loan events and warehouse runs preserve operational history through audit records.
- AI configuration is generally mutable and unsynchronized; local model initialization and cached access can race on concurrent first calls.
- AI validation reports invalid conditions and fact counters with `IllegalArgumentException`; providers may throw `IOException`.
- Ring-buffer capacity must be a nonzero power of two, with one slot reserved to distinguish full from empty.
- Backup creation removes partial archives on exit; the sales reporter returns status `1` and writes `no orders found` when no matching orders exist.
