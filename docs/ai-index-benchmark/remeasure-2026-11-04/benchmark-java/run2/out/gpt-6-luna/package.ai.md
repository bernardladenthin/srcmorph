### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:32:24Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted project indexing alongside byte-processing utilities, operational reporting, and database workflows for lending, order fulfillment, and stock synchronization.

#### Purpose
- Configure and execute AI generation for project-index content using mock, local llama.cpp, or OpenAI-compatible providers.
- Supply supporting utilities and business operations for byte analysis, backup and sales reporting, library lending, shop orders, and warehouse inventory.

#### Responsibilities
- **AI index generation (`java/`):** Configure source scope, conditions, facts, prompts, models, and oversized-file policies; select providers, generate completions, and parse output.
- **Byte utilities (`other/`):** Compute 256-bin byte histograms and provide bounded circular-buffer reads and writes.
- **Operational scripts (`other/`):** Create and rotate compressed backups; validate order CSV data and report monthly regional revenue.
- **Library lending (`sql/library/`):** Maintain catalogue, members, branches, copies, loans, audit events, checkout/return operations, fees, and reports.
- **Shop orders (`sql/shop/`):** Manage products and customer order lines, reserve inventory, and recalculate order totals.
- **Warehouse synchronization (`sql/warehouse/`):** Aggregate supplier-feed quantities into stock levels and record synchronization outcomes.

#### Key units
- `SrcMorphConfiguration` — defines AI run scope, prompts, models, routing rules, facts, and output metadata.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — evaluate file-context conditions, derive source facts, and parse oversized-file policies.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — define the generation contract and select mock, llama.cpp JNI, or OpenAI-compatible backends.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — run local inference, prepare and safeguard compatible API requests, and clean completion text.
- `histogram_partial`, `histogram_reduce`, `BINS` — compute and combine byte counts into a 256-bin histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — manage fixed-capacity circular byte storage and transfers.
- `rotate_backups.sh`, `parse_orders`, `revenue_by_month_and_region` — rotate dated archives and validate and aggregate sales data.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — handle library checkout, returns, and overdue fees.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report overdue loans, member activity, and popular titles.
- `add_order_item` — add or increment a shop order line while checking and reserving product stock.
- `dbo.usp_SyncStock`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — synchronize supplier quantities into warehouse stock and record outcomes.

#### Data flow
- AI configuration scopes and prepares source context; condition evaluation and fact extraction feed the selected provider, whose completion is parsed and returned with optional timings.
- Histogram input bytes become work-group partial counts and then a final histogram; ring-buffer writes and reads advance producer and consumer indices within capacity.
- Backup scripts archive a source directory and prune retained archives; sales scripts validate CSV rows, aggregate `Order` records by month and region, then filter and print totals.
- Library checkout and return operations update loan and inventory state and record audit events; reports derive lending activity from catalogue, member, and loan data.
- Shop item addition validates SKU and stock, updates order lines, decrements inventory, and recalculates totals; warehouse synchronization aggregates eligible feed rows, merges stock, and logs success or failure.

#### Dependencies
- AI generation uses `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, configuration and file-context types, llama.cpp JNI support, Jackson, and OpenAI-compatible response types.
- Histogram kernels use OpenCL C 1.2; the ring buffer uses standard allocation and memory-copy facilities. Backup rotation uses Bash and system utilities; sales reporting uses Python standard-library modules.
- SQL workflows use PostgreSQL library objects, MySQL 8 shop objects, and SQL Server warehouse objects, with relational records linked through foreign keys and domain relationships.

#### Cross-cutting
- AI configuration is mostly mutable JavaBeans; provider generation exposes `IOException`, and lazy local-model initialization is unsynchronized.
- OpenAI-compatible requests restrict base URLs to HTTP(S) and protect API keys from transmission over plain HTTP to non-loopback hosts.
- Ring-buffer capacity must be a power of two with one slot reserved; sales amounts use `Decimal` and invalid matching rows produce path- and line-aware errors.
- SQL constraints and indexes enforce relationships and domain checks; library loans and warehouse synchronization retain audit or operational records, while warehouse failures roll back, log, and rethrow.
