### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:03:30Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides AI-assisted source documentation tooling alongside byte-processing utilities, operational reporting, and database workflows for lending, commerce, and inventory.

#### Purpose
- Configures source-aware AI documentation generation, including model selection, source filtering, fact extraction, and oversized-file handling.
- Groups reusable byte-processing primitives and operational tools with SQL workflows for library circulation, shop orders, and warehouse stock synchronization.

#### Responsibilities
- **AI documentation generation (`java/`):** Manages run and model configuration, evaluates source-selection conditions, extracts regex-based facts, applies oversized-file policies, and routes completion requests to local llama.cpp or OpenAI-compatible providers.
- **Byte processing (`other/`):** Computes byte histograms with OpenCL kernels and provides a bounded circular byte buffer.
- **Operations and sales reporting (`other/`):** Creates dated compressed backups with retention cleanup; parses order CSVs and reports monthly regional revenue by currency and minimum-revenue filter.
- **Library circulation (`sql/library/`):** Models catalog, members, branches, copies, and loans; supports checkout, returns, late fees, audit history, and circulation reports.
- **Shop orders and inventory (`sql/shop/`):** Stores products and orders, adds order items with stock reservation, and recalculates order totals.
- **Warehouse synchronization (`sql/warehouse/`):** Applies aggregated supplier feed quantities to stock levels and records synchronization outcomes.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — shared run metadata and per-model generation, provider, hardware, and caching settings.
- `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy` — match source conditions, format regex fact counts, and parse oversized-file policies.
- `AiGenerationProviderFactory`, `AiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` — select providers, execute local or compatible completion, prepare requests, and clean completion text.
- `histogram_partial`, `histogram_reduce`, `BINS` — count byte values per OpenCL work-group and reduce counts into a 256-bin histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_read`, `ringbuf_write`, `ringbuf_used`, `ringbuf_free_space` — manage circular-buffer storage, transfer bytes, and report occupancy.
- `rotate_backups.sh` — creates UTC-dated gzip archives and removes excess older matching backups.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — validate and parse sales CSVs, aggregate monthly regional revenue, and run the report CLI.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, circulation, and copy-level inventory tables.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit`, `trg_loan_audit` — perform circulation operations, calculate capped overdue fees, and record loan events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — summarize library circulation and borrowing.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog and order records, with stock-reserving item addition.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since)` — warehouse feed, inventory, synchronization log, and stock-update procedure.

#### Data flow
- **AI generation:** Run and model settings feed source matching and fact extraction; the provider factory selects a backend, which renders prompts and returns completion text and optional timings.
- **Byte processing:** Input bytes produce per-work-group histogram counts followed by reduction; ring-buffer writes and reads move available bytes through bounded storage with wraparound.
- **Operations and reporting:** Backup settings drive temporary archive creation, finalization, and retention cleanup; CSV rows are validated into `Order` records, aggregated by month and region, filtered, and printed.
- **Library:** Catalog and member records feed loans; checkout and return validate state and update circulation, with triggers recording audit events and reports summarizing activity.
- **Shop:** `add_order_item` locks the SKU-matched product, checks and decrements stock, inserts or increments an order line at the current price, and recalculates the order total.
- **Warehouse:** `usp_SyncStock` filters recent supplier rows, aggregates by warehouse and SKU, merges into stock levels, counts insert/update actions, and logs the run.

#### Dependencies
- AI configuration and generation use source types such as `AiFileContext`, `AiCondition`, `AiFactCounter`, and `AiModelDefinition`, plus `AiPromptSupport`, llama.cpp JNI, Jackson, HTTP/URL facilities, and compatible chat-response types.
- OpenCL histogram kernels use work-item and work-group operations, local memory, barriers, and atomic increments; the ring buffer uses `ringbuf.h` and C allocation/copy utilities.
- Backup automation uses Bash and system commands; sales reporting uses Python standard-library CSV, argument parsing, date, decimal, dataclass, and collection utilities.
- Library routines and reports depend on the library catalog, circulation, and audit tables; shop operations use `customer_order`, `product`, and `order_item`; warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- AI configuration is generally mutable and unsynchronized; local model initialization is lazy, while the local provider has no stated synchronization between generation and `close()`.
- AI condition and fact-counter validation is explicit; provider generation declares `IOException`, and unknown provider keys raise `IllegalArgumentException`.
- Ring-buffer capacities must be powers of two, with one slot reserved to distinguish full from empty; histogram counting uses cooperative initialization, barriers, and atomic increments.
- Backup handling uses Bash strict mode and an `EXIT` trap for temporary-file cleanup; sales amounts use `Decimal`, and malformed input raises `ValueError`.
- SQL constraints and indexes enforce relationships and valid states; shop stock checks use `FOR UPDATE`, warehouse synchronization is transactional with rollback on failure, and library routines reject invalid circulation requests.
