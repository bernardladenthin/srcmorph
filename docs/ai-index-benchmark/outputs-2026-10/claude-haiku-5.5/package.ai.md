### ai
- H: 1.0
- C: 1BB7C853
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:33:46Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-language workspace combining an AI-driven source-file summarisation engine (Java), a GPU and ring-buffer toolkit with backup and sales-report utilities (C/OpenCL/Bash/Python), and three independent SQL database artifacts for library lending, web shop orders, and warehouse stock sync.

#### Purpose
- Hosts three unrelated subsystems: AI source summarisation (`java/`), low-level and scripting utilities (`other/`), and database schemas and routines (`sql/`).
- Subfolders are independent; no cross-folder collaboration is described.

#### Responsibilities
- **AI summarisation (`java/`)**: file selection conditions, oversize handling, fact extraction, and pluggable generation providers (mock, llama.cpp JNI, OpenAI-compatible).
- **Compute and data structures (`other/`)**: OpenCL two-pass byte histogram kernels and a C SPSC byte ring buffer (`ringbuf_t`).
- **Operational scripts (`other/`)**: Bash backup rotation to the newest `KEEP` archives; Python monthly revenue reporting by region.
- **Library lending (`sql/`, PostgreSQL)**: catalog, members, loans, checkout/return routines, late fees, reporting views, audit trigger.
- **Web shop orders (`sql/`, MySQL 8)**: products with stock, orders, line items, and `add_order_item` stock reservation.
- **Warehouse stock sync (`sql/`, T-SQL)**: nightly `usp_SyncStock` MERGE of supplier feed into `StockLevel`, with `SyncLog` auditing.

#### Key units
- Java: `SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`.
- Other: `histogram_partial`, `histogram_reduce`, `ringbuf_t`, `ringbuf_write`, `ringbuf_read`, `rotate_backups.sh`, `parse_orders`, `revenue_by_month_and_region`, `Order`.
- SQL: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit`, `add_order_item`, `customer_order`, `order_item`, `dbo.usp_SyncStock`, `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.

#### Data flow
- Java: config → file conditions and fact headers → oversize strategy → provider factory → generated text → completion parser.
- Other GPU: bytes → `histogram_partial` → partials → `histogram_reduce` → 256-bin histogram.
- Other ring buffer: producer `ringbuf_write` → `data` → consumer `ringbuf_read`.
- Other backup: directories → `tar -czf` to `.partial` → atomic `mv` → prune older archives.
- Other sales: CSV → `parse_orders` → `Order` → `revenue_by_month_and_region` → threshold report.
- SQL library: checkout inserts `loan`, trigger writes `loan_audit`; return restores stock.
- SQL shop: `add_order_item` locks `product`, updates `order_item`, decrements stock, recalculates `customer_order.total_cents`.
- SQL warehouse: `SupplierFeed` aggregated → MERGE → `StockLevel`; counts → `SyncLog`.

#### Dependencies
- Java: llama.cpp bindings (`net.ladenthin.llama`), Jackson, Lombok, jspecify, internal `AiFileContext`, `AiPromptSupport`, and related config types.
- Other: OpenCL C 1.2 built-ins, C stdlib, Bash tools (`tar`, `ls`, `mv`), Python stdlib (`csv`, `decimal`, `dataclasses`).
- SQL: PostgreSQL built-ins, MySQL 8 InnoDB, T-SQL `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Error handling: Java `IllegalArgumentException`/`IOException`; C status codes `RINGBUF_EINVAL`/`RINGBUF_ENOMEM`; Python `ValueError`; Bash exit 2 on usage errors; SQL `RAISE`/`SIGNAL`/`THROW`.
- Configuration: Java mutable JavaBeans; Bash and Python via CLI arguments; `KEEP` defaults to `7`, currency to `EUR`.
- Concurrency: Java `LlamaCppJniAiGenerationProvider.model()` lazy init not thread-safe; ring buffer is SPSC without locks; GPU uses barriers within groups; SQL shop uses `FOR UPDATE` row locks.
- Validation gaps: no buffer-size or NULL checks in C/OpenCL; Python `OSError` not caught; Bash `ls` under `pipefail` may abort pruning.
- Transactions: explicit in PostgreSQL scripts and around the T-SQL MERGE and log insert.
