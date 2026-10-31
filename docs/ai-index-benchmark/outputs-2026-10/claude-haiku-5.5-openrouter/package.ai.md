### ai
- H: 1.0
- C: CCD7207D
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:37:53Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Multi-part workspace bundling an AI-driven per-file source documentation generator for srcmorph, a GPU byte-histogram plus lock-free ring buffer with backup and sales-report tooling, and three independent SQL schema domains (library lending, web-shop orders, warehouse stock sync).

#### Purpose
- Top-level package of three unrelated subprojects: AI source documentation (`java/`), byte-frequency and data/ops utilities (`other/`), and database schemas and routines (`sql/`).
- Each subtree is self-contained; no runtime code is shared across them.

#### Responsibilities
- AI documentation generation (`java/`): run configuration, file selection and oversize handling, deterministic fact counts, and routing text generation to mock, llama.cpp JNI, or OpenAI-compatible backends.
- Byte analysis and data plumbing (`other/`): OpenCL two-pass histogram, C SPSC byte ring buffer, Bash backup rotation, Python currency-filtered sales reporting.
- Relational domains (`sql/`): PostgreSQL library lending, MySQL 8 web-shop ordering with stock reservation, SQL Server nightly warehouse stock MERGE with audit logging.

#### Key units
- Java config and selection: `SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionEvaluator`, `AiConditionGroup`, `AiOversizeStrategy`, `AiFactExtractor`.
- Java providers: `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`.
- Other GPU and buffers: `histogram_partial`, `histogram_reduce`, `BINS`, `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`.
- Other ops: `rotate_backups.sh`, `Order`, `parse_orders`, `revenue_by_month_and_region`, `sales_report.py` `main`.
- Library SQL: `loan`, `book`, `book_copy`, `member`, `branch`, `loan_audit`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit`, `overdue_loans`, `member_loan_stats`, `popular_books`.
- Shop SQL: `product`, `customer_order`, `order_item`, `add_order_item`.
- Warehouse SQL: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `dbo.usp_SyncStock`.

#### Data flow
- AI docs: Maven/YAML/JSON bind into `SrcMorphConfiguration`; `AiFileContext` is tested by `AiConditionEvaluator`; oversize files go through `AiOversizeStrategy` (SAMPLE, MAP_REDUCE, DETERMINISTIC, FAIL); `AiFactExtractor` prepends fact blocks; the provider chosen by `AiGenerationProviderFactory` returns text, cleaned by `AiCompletionParser`.
- GPU histogram: pass 1 `histogram_partial` fills per-group partials, pass 2 `histogram_reduce` sums them into `histogram`; launch order and group count are mandatory.
- Ring buffer: producer `ringbuf_write` advances `head`, consumer `ringbuf_read` advances `tail`, both returning short counts.
- Backups: `tar -czf` to `.partial`, atomic `mv`, `ls -1t` ranking, prune past `keep`.
- Sales: CSV → `parse_orders` → `Order` stream → `revenue_by_month_and_region` → `min_revenue` filter → report.
- SQL: library checkout and return write loans and audit rows; shop `add_order_item` locks, upserts, decrements stock, recomputes totals; warehouse MERGEs aggregated `SupplierFeed` into `StockLevel` and logs to `SyncLog`.

#### Dependencies
- Java: llama.cpp binding (`net.ladenthin.llama`), Jackson, Lombok, JSpecify, internal `net.ladenthin.srcmorph.support` types.
- Other: OpenCL C built-ins, C standard library, Bash and coreutils (`tar`, `date`, `mv`), Python standard library (`csv`, `decimal`, `dataclasses`, `argparse`).
- SQL: PostgreSQL built-ins, MySQL 8 InnoDB, SQL Server `MERGE`/`OUTPUT`/`THROW`.

#### Cross-cutting
- Errors: Java uses `IllegalArgumentException` for config and fail-fast checks and `IOException` for provider failures; C has status enum only at init; Python raises chained `ValueError`; Bash exits 2 on bad args; SQL uses exceptions, SQLSTATE `45000`, and rollback with rethrow.
- Concurrency: Java bean classes are not thread-safe and the llama.cpp lazy model init is unsynchronized; OpenCL kernels rely on work-group barriers; ring buffer is lock-free SPSC with `volatile` indices and no memory barriers; shop uses `FOR UPDATE`.
- Money: integer cents in library and shop; Python uses `Decimal` with two-decimal display, no currency conversion.
- Time: UTC timestamps in backups and warehouse (`SYSUTCDATETIME()`); library uses `CURRENT_DATE`/`now()`.
- Known gaps: ring buffer lacks pointer validation; kernels lack bounds checks; backup `ls -1t` breaks on newline filenames; library `03_routines.sql` references `book.copies_free`, dropped by `04_migration_v2.sql`.
