### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:51:17Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Supports source-code indexing and AI-generated summaries alongside byte utilities, backup and sales tools, and database workflows for lending, order fulfillment, and inventory.

#### Purpose
- Configure source selection, facts, prompts, and model-backed generation for code indexing.
- Provide supporting byte-processing and operational tools, plus SQL data models and workflows for library, shop, and warehouse operations.

#### Responsibilities
- **AI indexing and generation (`java/`):** Define indexing configuration, validate file-selection conditions, extract regex-based facts, and route completion requests to mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Byte and operational utilities (`other/`):** Compute byte histograms with OpenCL, manage a bounded C ring buffer, rotate compressed backups, and report monthly regional sales from CSV.
- **Database operations (`sql/`):** Model library catalogs and circulation, shop orders and stock, and warehouse supplier-feed synchronization; provide reports, routines, migrations, and seed data.

#### Key units
- `SrcMorphConfiguration` — holds source filters, prompts, model and field routing, facts, fallback, and output settings.
- `AiConditionEvaluator` / `AiFactExtractor` — evaluate source-file conditions and format regex-match facts.
- `AiGenerationProviderFactory` / `LlamaCppJniAiGenerationProvider` / `OpenAiCompatibleProviderSupport` — select and support AI completion backends.
- `histogram_partial` / `histogram_reduce` — calculate and combine 256-bin byte-frequency counts.
- `ringbuf_t` and `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — manage bounded byte-stream storage and transfers.
- `rotate_backups.sh` — create timestamped compressed archives and prune older backups.
- `parse_orders` / `revenue_by_month_and_region` — parse sales records and aggregate revenue by month and region.
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` — implement library checkout, return, and late-fee workflows.
- `overdue_loans`, `member_loan_stats`, `popular_books` — provide library circulation reports.
- `add_order_item` — validate and lock a shop product, update order lines and stock, and recalculate order totals.
- `dbo.usp_SyncStock(@Since)` — merge recent supplier-feed quantities into warehouse stock and log synchronization results.

#### Data flow
- Source filters and facts feed the indexing workflow; generation requests pass through provider selection to local or OpenAI-compatible inference.
- Input bytes flow through OpenCL partial histograms and reduction; ring-buffer clients exchange bytes through bounded partial reads and writes.
- Backup inputs produce timestamped archives followed by retention pruning; CSV orders are validated, aggregated by month and region, filtered by minimum revenue, and reported.
- Library seed and lending operations update circulation records and inventory, with loan events audited; reports derive summaries from loan data.
- Shop order-item updates lock product stock, adjust lines and inventory, then recalculate totals; warehouse synchronization groups recent feed quantities by warehouse and SKU, merges stock, and logs outcomes.

#### Dependencies
- AI support uses internal configuration and request types, llama.cpp JNI, Jackson, and Java regex, time, I/O, and URL APIs.
- OpenCL kernels use OpenCL C 1.2; the ring buffer uses C standard allocation and copy functions.
- Backup and sales tools use Bash utilities (`tar`, `date`, `mv`, `du`, `ls`, `rm`) and Python standard-library CSV, argument parsing, date, and decimal utilities.
- SQL workflows depend on their library, shop, and warehouse tables; the library migration also uses PostgreSQL `generate_series`, while warehouse synchronization uses SQL Server functions.

#### Cross-cutting
- AI configuration is mostly mutable JavaBeans; condition trees and fact definitions have explicit validation. The llama.cpp model is lazily cached without synchronization, and generation is not coordinated with `close()`.
- Ring-buffer operation assumes one producer and one consumer and reserves a slot to distinguish full from empty; histogram kernels use local-memory partial counts and atomic increments.
- Sales parsing reports contextual errors; backup cleanup handles unfinished `.partial` archives.
- SQL constraints and indexes enforce relationships and access patterns; shop operations use integer cents and report invalid or insufficient stock, while warehouse synchronization is transactional with rollback, error logging, and rethrow on failure.
