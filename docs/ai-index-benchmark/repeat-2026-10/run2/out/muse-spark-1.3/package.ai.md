### ai
- H: 1.0
- C: D868045A
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:20:07Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Heterogeneous reference package combining AI-driven source documentation generation with byte-stream primitives, ops scripts, and library/shop/warehouse SQL models.
#### Purpose
- Centralize pluggable AI summarization runs, low-level byte/ops utilities, and self-contained SQL domain schemas for indexing.
#### Responsibilities
- AI documentation runs: run configuration via `SrcMorphConfiguration`, file filtering, generation tuning, oversize handling, LLM provider routing.
- Byte-stream primitives: 256-bin histogram counting, SPSC circular FIFO transfer.
- Ops/reporting scripts: timestamped `tar.gz` backup rotation, CSV monthly regional revenue aggregation.
- SQL reference domains: lending-library circulation, web-shop ordering, warehouse inventory synchronization.
#### Key units
- `SrcMorphConfiguration` — entire run bean; directories, filters, providers, prompts, models, routing, fallback.
- `AiGenerationConfig` — single-step LLM knobs; sampling, budgeting, caching, GPU, remote, DRY.
- `AiConditionEvaluator`, `AiConditionGroup`, `AiFileContext` — extension/size/lines/date/glob filtering with `<and>`/`<or>`.
- `AiFactExtractor`, `AiOversizeStrategy` — regex-count `FACTS` block; `FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC` routing.
- `AiGenerationProvider`, `AiGenerationProviderFactory` — `mock`/`llamacpp-jni`/`openai-compatible` backend contract and router.
- `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, `AiCompletionParser`, `OpenAiCompatibleProviderSupport` — local GGUF inference, thinking-block stripping, remote chat-completions plumbing.
- `histogram_partial`, `histogram_reduce`, `BINS 256` — grid-stride counting and per-bin reduction.
- `ringbuf_t`, `ringbuf_init/free/write/read/used/free_space`, `ringbuf_status_t` — power-of-two SPSC FIFO with wraparound.
- `rotate_backups.sh`, `Order`, `parse_orders`, `revenue_by_month_and_region` — backup retention with `KEEP`, CSV to TSV revenue grouping.
- `author/book/member/loan`, `branch/book_copy`, `loan_audit/overdue_loans/member_loan_stats/popular_books`, `fn_checkout_book/sp_return_book/fn_late_fee/trg_loan_audit` — library holdings, checkout/return/fee, audit/reports.
- `product/customer_order/order_item`, `add_order_item` — catalog, stock-reserving line placement, total maintenance.
- `dbo.usp_SyncStock`, `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` — nightly feed merge with run auditing.
#### Data flow
- AI path: `SrcMorphConfiguration` scope/routing → `AiConditionEvaluator.matches` filtering → `AiOversizeStrategy.fromConfig` branch with `factsBlock` prepend → `AiGenerationRequest` → factory-selected provider → local `LlamaModel.chatComplete` or remote `/chat/completions` → `parseCompletion` → `String`/`AiGenerationTimings`.
- Histogram: `input` → strided `local_bins[input[i]]` → `partial[group_count*BINS]` → `histogram[256]`.
- Ringbuf: `ringbuf_init` heap buffer → `ringbuf_write` clamped `memcpy` advancing `head` → `ringbuf_read` clamped `memcpy` advancing `tail`.
- Scripts: `SOURCE_DIR` → timestamped archive → newest-N pruning; CSV orders → `parse_orders` → `revenue_by_month_and_region` → TSV stdout.
- Library: blocked/copies check → decrement + `INSERT loan` → audit `CHECKOUT`/`RETURN`; `loan JOIN member/book/author` → overdue/popularity reports.
- Shop: `sku/stock FOR UPDATE` → upsert `order_item` → decrement `stock` → `SUM` to `total_cents`.
- Warehouse: `SupplierFeed WHERE ReceivedAt>=@Since` → `SUM GROUP BY` → `MERGE INTO StockLevel` → counts to `SyncLog`.
#### Dependencies
- Internal: `AiCondition/AiRangeCondition/AiFileContext`, `AiPromptSupport/AiPromptDefinition/AiModelDefinition/AiFieldGenerationConfig`, `AiGenerationRequest/AiGenerationTimings/AiCalibration`; `book→author`, `loan→book/member/book_copy`, `order_item→product/customer_order`, `usp_SyncStock` ↔ `StockLevel/SupplierFeed/SyncLog`.
- External: Jackson `ObjectMapper/ArrayNode/ObjectNode`, `net.ladenthin.llama` `LlamaModel/ChatResponseParser/Timings/Usage`, Lombok, JSpecify; OpenCL 1.2 `atomic_inc/barrier`, C stdlib `malloc/memcpy`, Bash/GNU coreutils, Python stdlib `csv/argparse/Decimal`; PostgreSQL `PL/pgSQL/generate_series`, MySQL 8 InnoDB, T-SQL `MERGE/TRY/CATCH`.
#### Cross-cutting
- Mutable populate-then-read-only JavaBeans with `DEFAULT_*`, null-coalescing setters vs immutable `AiOversizeStrategy`/`LlamaCppJniConfig`; stateless thread-safe evaluators vs lazy unsynchronized `LlamaModel` init.
- Fail-fast validation: `IllegalArgumentException/IOException/IllegalStateException`, `SIGNAL SQLSTATE '45000'`, `TRY/CATCH ROLLBACK+THROW`, `RINGBUF_EINVAL/ENOMEM`, CLI exit 2/1.
- Constraint/integrity conventions: `UNIQUE/CHECK/FK`, composite keys, `SELECT FOR UPDATE`, `SET NOCOUNT ON`, UTC timestamps, cleartext API-key loopback-only refusal, thinking-block stripping.
