### ai
- H: 1.0
- C: 321EBA94
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:14:25Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted documentation generation, byte-processing and operational utilities, and database workflows for library lending, commerce, and warehouse inventory.

#### Purpose
- Combines configurable source-to-documentation generation with utilities for GPU byte histogramming, buffering, backups, and sales reporting.
- Defines database schemas and operations for library loans, shop orders, and supplier-driven stock synchronization.

#### Responsibilities
- **AI documentation (`java/`):** Configure source selection, metadata conditions, facts, prompts, and models; route requests to mock, local llama.cpp JNI, or OpenAI-compatible inference; process completions.
- **Byte and operational utilities (`other/`):** Compute byte histograms, buffer byte streams, rotate compressed backups, and aggregate order revenue by month and region.
- **Database workflows (`sql/`):** Manage library catalog, copies, loans, fees, audits, and reports; reserve shop stock for order items; synchronize warehouse inventory from supplier feeds.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionEvaluator`, `AiFactExtractor` — configure generation and source rules, apply metadata conditions, and extract exact source facts.
- `AiGenerationProviderFactory`, `AiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — select inference backends, execute requests, and process generated text.
- `histogram_partial`, `histogram_reduce` — compute and combine GPU byte-frequency counts.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — manage bounded circular byte queues.
- `rotate_backups.sh`, `Order`, `parse_orders`, `revenue_by_month_and_region` — rotate directory archives and parse and report sales data.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — represent library catalog, membership, inventory, and borrowing.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit` — handle lending operations, overdue fees, and loan event recording.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report lending activity and catalog popularity.
- `product`, `customer_order`, `order_item`, `add_order_item` — represent shop orders and update lines while reserving product stock.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — store supplier quantities and inventory, and synchronize and log stock updates.

#### Data flow
- AI configuration and metadata conditions determine source scope; fact extraction prepares source facts, provider routing executes generation, and completion parsing removes a leading thinking block.
- GPU input bytes become per-work-group partial histograms and then final counts; ring-buffer writes and reads transfer only available or buffered bytes.
- Backup runs create timestamped compressed archives and apply retention cleanup; sales CSVs become validated orders, monthly regional totals, and filtered tabular reports.
- Library routines update loan and inventory records, with triggers writing audit events; reports derive from catalog and lending data.
- Shop order-item operations lock and check products, update lines and stock, then recalculate order totals; warehouse synchronization aggregates supplier feeds, merges stock levels, and logs outcomes.

#### Dependencies
- AI generation uses shared request, prompt, file-context, condition, and timing types; local inference uses llama.cpp JNI support, while OpenAI-compatible requests use Jackson and chat-response usage data.
- GPU kernels depend on OpenCL C work-group facilities; ring-buffer utilities use C allocation and memory-copy facilities; backup and sales utilities use Bash/system commands and Python standard-library modules.
- Library objects relate authors, books, members, branches, copies, and loans; shop order lines reference orders and products; warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- Database workflows use PostgreSQL features for library and shop operations and SQL Server transactions and `MERGE` for warehouse synchronization.

#### Cross-cutting
- Configuration and validation differ by domain: AI conditions and fact counters are explicitly validated; lending and order routines check eligibility, availability, or quantities.
- Inventory is represented differently across workflows: branch-aware library copies, shop stock decremented per order line, and warehouse stock aggregated from supplier feeds.
- Operational safeguards include ring-buffer power-of-two capacity, temporary-archive cleanup traps, transactional warehouse updates with error logging and rethrow, and input validation with contextual CSV parse errors.
