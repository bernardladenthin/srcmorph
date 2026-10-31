### ai
- H: 1.0
- C: C24ADB96
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:19:17Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Heterogeneous reference package combining AI-powered source documentation generation with system utilities and library/shop/warehouse SQL data models.
#### Purpose
- Host AI `.ai.md` summarization pipeline plus unrelated `other/` system utilities and `sql/` reference schemas under one package.
#### Responsibilities
- AI generation pipeline: run-scope configuration, file gating/oversize handling, exact-facts injection, pluggable local/remote LLM backends.
- System utilities: parallel byte-histogram analysis, SPSC circular buffering, timestamped backup rotation, currency-filtered sales reporting.
- SQL reference models: library lending circulation with branch migration, web-shop ordering with stock reservation, warehouse inventory synchronization.
#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig` — Maven-mapped run scope and LLM inference parameters with `DEFAULT_*` sentinels.
- `AiConditionEvaluator`, `AiConditionGroup`, `AiOversizeStrategy`, `AiFileContext` — extension/size/lines/date/glob gating with `FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC`.
- `AiFactExtractor`, `AiFactCounter` — `label: count` `**Facts (exact, whole file):**` block injection.
- `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`, `LlamaCppJniConfig` — `generate`/`generateWithTimings`/`close`, GGUF `chatComplete`, chat-completions JSON, thinking-block cleanup.
- `histogram_partial`, `histogram_reduce`, `BINS`, `partial` — per-group 256-bin counting and global reduction.
- `ringbuf_t`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_status_t` — power-of-two SPSC FIFO with wraparound `memcpy`.
- `rotate_backups.sh`, `sales_report.py`, `Order`, `revenue_by_month_and_region` — `BACKUP_DIR/<basename>_YYYYMMDDTHHMMSSZ.tar.gz` retention, `csv.DictReader` `Decimal` month-region sums.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`, `overdue_loans`, `popular_books`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library circulation with `copies_free`, `due_on`/`returned_on`, `barcode` status.
- `product`, `customer_order`, `order_item`, `add_order_item(p_order_id,p_sku,p_qty)` — shop catalog with `sku`/`price_cents`/`stock`, `FOR UPDATE` reservation and `total_cents` maintenance.
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `dbo.usp_SyncStock @Since` — warehouse `MERGE` on `WarehouseId,Sku` with `$action` audit.
#### Data flow
- AI: Maven/XML/JSON/YAML → `SrcMorphConfiguration` → `AiConditionEvaluator.matches` gating → `AiFactExtractor.factsBlock` prepend → system+user prompt → `LlamaModel.chatComplete`/remote endpoint → `AiCompletionParser.parseCompletion` + `AiGenerationTimings`.
- Histogram: `input` grid-stride loop → `atomic_inc` `local_bins` → `barrier` → `partial` → per-`bin` accumulation → `histogram`.
- Ring buffer: `src` → `ringbuf_write` masked wraparound → `head` advance → `ringbuf_read` → `dst`.
- Ops: `SOURCE_DIR` → `.partial` tar.gz → atomic publish → `ls -1t` KEEP prune; Order CSVs → `parse_orders` currency filter → `Decimal` YYYY-MM sums → stdout report.
- Library: `is_blocked`/`copies_free` check → `loan` INSERT → `loan_audit` CHECKOUT; return → `returned_on` → increment → RETURN; v2 migration `generate_series` → `book_copy` → backfill `copy_id`.
- Shop/warehouse: `p_sku` `FOR UPDATE` → `order_item` upsert → `stock` decrement → `total_cents=SUM`; `SupplierFeed` `ReceivedAt>=@Since` → `SUM(Quantity)` → `MERGE` → `SyncLog`.
#### Dependencies
- Internal AI: `AiCondition`, `AiFileContext`, `AiFactDefinition`, `AiModelDefinition`, `AiPromptDefinition`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`.
- External AI: `net.ladenthin.llama` `LlamaModel`/`ReasoningFormat`/`ChatResponseParser`, Jackson `ObjectMapper`/`ObjectNode`, Lombok `@ToString`, `java.util.regex`, `java.time.Instant`.
- Other: OpenCL C 1.2 `barrier`/`atomic_inc`; C `stdlib.h`/`string.h`; shell `bash/tar/date`; Python `argparse/csv/dataclasses/Decimal`.
- SQL: PostgreSQL `PL/pgSQL`/`BIGSERIAL`/`generate_series`/`COUNT FILTER`; MySQL 8 `InnoDB`/`ENUM`/`ON DUPLICATE KEY UPDATE`/`SIGNAL SQLSTATE '45000'`; T-SQL `MERGE`/`$action`/`SYSUTCDATETIME()`/`TRY/CATCH`.
#### Cross-cutting
- Mixed mutability: null-coerced JavaBeans with `DEFAULT_*` vs immutable `LlamaCppJniConfig` builder; stateless thread-safe evaluators/extractors; unsafe lazy `model` init.
- Fail-fast validation: `IllegalArgumentException` for conditions/facts/provider/`baseUrl`, `IOException` for thinking-block/provider/key, `SIGNAL`/`THROW`/PLpgSQL exceptions for blocked/stock/return guards.
- Integrity/audit: `CHECK`/`UNIQUE`/`FK`/`ON DELETE CASCADE`, `FOR EACH ROW` `trg_loan_audit`, `SyncLog` counts, `total_cents` maintenance, `SELECT ... FOR UPDATE` locking.
- CLI/parallel idioms: grid-stride + `CLK_LOCAL_MEM_FENCE`, `& (capacity-1)` masking, `set -euo pipefail`, UTF-8 CSV `Decimal` arithmetic.
