### ai
- H: 1.0
- C: 1CE11ABF
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:26:26Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> AI documentation generation with file-scope selection and pluggable LLM inference, plus GPU/ring-buffer/ops utilities and multi-dialect library/shop/warehouse SQL examples.
#### Purpose
- Generate storable AI documentation headers via configurable file selection, facts, oversize handling, and local/remote LLM providers.
- Provide auxiliary OpenCL/C/scripts utilities and PostgreSQL/MySQL/SQL-Server inventory/ordering reference schemas.
#### Responsibilities
- AI run configuration: `SrcMorphConfiguration`, `AiGenerationConfig`, prompt/model/field/fact definitions, base/output dirs, subtrees/excludes/extensions, llama.cpp fallback.
- File selection and deterministic fallback: `AiConditionEvaluator/AiConditionGroup`, `AiFileContext`, `AiFactExtractor`, `AiOversizeStrategy FAIL/SAMPLE/MAP_REDUCE/DETERMINISTIC`.
- LLM provider dispatch and inference: `AiGenerationProvider/AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider/LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`.
- GPU histogram and SPSC transport: `histogram_partial/histogram_reduce/histogram.cl BINS=256`, `ringbuf_t/ringbuf_init/ringbuf_write/ringbuf_read/ringbuf_used`.
- Ops reporting: `rotate_backups.sh tar -czf` rotation, `parse_orders/revenue_by_month_and_region/main` CSV revenue aggregation via `Order`.
- Library/shop/warehouse SQL: `fn_checkout_book/sp_return_book/fn_late_fee`, `add_order_item`, `usp_SyncStock/MERGE`, `overdue_loans/member_loan_stats/popular_books`, `SyncLog/loan_audit`.
#### Key units
- `SrcMorphConfiguration` — run-root bean for scope, providers, prompts/models/facts.
- `AiGenerationConfig` — 45-field LLM knobs with `DEFAULT_*`, tri-state thinking flag.
- `AiConditionEvaluator` — condition-tree matching/validation over `AiFileContext`.
- `AiFactExtractor` — `factsBlock/validate/countMatches` regex-count facts prepend.
- `AiOversizeStrategy` — oversize policy parse via `configValue/fromConfig`.
- `AiGenerationProvider` — `generate/generateWithTimings/close` over `AiGenerationRequest` to `AiGenerationTimings`.
- `LlamaCppJniConfig` — 41-field sampling/KV-cache/batch/GPU/DRY/reasoning snapshot via `builder(modelPath)`.
- `ringbuf_t`, `ringbuf_status_t` — power-of-two FIFO state, `RINGBUF_OK/EINVAL/ENOMEM`.
- `Order` — frozen `order_date/region/amount` sales record.
- `author/book/member/loan/branch/book_copy`, `product/customer_order/order_item`, `StockLevel/SupplierFeed/SyncLog` — library/shop/warehouse tables.
#### Data flow
- AI selection: `AiFileContext(fileName,sizeBytes,lineCount,lastModifiedEpochMilli,relativePath)` + `AiCondition` -> `matches` -> scope; `usesLines` gates line counting.
- AI generation: source + `AiFactCounter` -> facts block prepended to body -> routing/model resolution -> mock/`llamacpp-jni`/`openai-compatible` inference -> thinking-strip/timings parse -> `.ai.md`.
- Oversize branch: source vs context window -> `fromConfig` -> fail/head-sample/chunk map-reduce/deterministic fallback.
- Histogram: `input` strided scan -> `partial[group_id*BINS+bin]` via `atomic_inc` -> per-bin reduce -> `histogram[bin]`.
- Ringbuf: byte chunks `memcpy` with `& (capacity-1)` wrap -> `(head-tail) & (capacity-1)` occupancy.
- Library: blocked/copies check -> copy decrement -> `INSERT loan` -> `loan_audit`; return -> increment + `fn_late_fee`.
- Shop: `p_order_id/p_sku/p_qty` -> `SELECT product FOR UPDATE` -> line upsert -> `stock` decrement -> `total_cents` recompute.
- Warehouse: `@Since` feed `SUM(Quantity)` -> `MERGE` into `StockLevel` -> `OUTPUT $action` -> `SyncLog COMMIT`; failure -> `ROLLBACK` + `THROW`.
#### Dependencies
- Internal: `AiCondition/AiRangeCondition`, `AiFactCounter/AiCalibration/AiModelDefinition/AiPromptDefinition`, `AiGenerationRequest/AiGenerationTimings/LlamaCppJniProviderSupport/MockAiGenerationProvider`; `book→author`, `loan→book/member/book_copy`, `order_item→customer_order/product`, `StockLevel↔SupplierFeed`.
- External: `net.ladenthin.llama LlamaModel/ChatResponseParser/Timings`, `com.fasterxml.jackson.databind`, `lombok`, `java.time.Instant`, `java.util.regex`; OpenCL `atomic_inc/CLK_LOCAL_MEM_FENCE`; C `malloc/memcpy`; Bash `tar/date`; Python `argparse/csv/Decimal`; PostgreSQL `PL/pgSQL/generate_series`, MySQL `SIGNAL SQLSTATE`, SQL Server `MERGE/SYSUTCDATETIME`.
#### Cross-cutting
- Validation fail-fast: `IllegalArgumentException/IllegalStateException/IOException`, `RINGBUF_EINVAL/ENOMEM`, `RAISE/SIGNAL 45000/TRY/CATCH`, `KEEP` regex, `REQUIRED_COLUMNS`.
- Concurrency split: stateless thread-safe `AiConditionEvaluator/AiFactExtractor` vs mutable configs, lazy unsynchronized `LlamaModel`; SPSC volatile `head/tail`, OpenCL `lid`-strided + fence.
- Invariants: power-of-two capacity + `capacity-1` mask, `BINS=256`, surrogate PKs + `UNIQUE isbn/email/barcode/sku`, `CHECK` + partial `idx_loan_open`.
- Secure/config hygiene: API key only over https/loopback, blank provider to mock, null completion to empty, atomic `mv` + `trap` backup cleanup.
