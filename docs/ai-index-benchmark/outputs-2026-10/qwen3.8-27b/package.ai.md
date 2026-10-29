### ai
- H: 1.0
- C: F0404DDE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:47:30Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Pluggable AI text-generation layer for the srcmorph source-documentation engine (LLM inference via local GGUF or remote OpenAI-compatible endpoints), bundled with independent performance primitives (OpenCL histogram, C SPSC ring buffer) and operational SQL schemas (library lending, web-shop ordering, warehouse stock sync).

#### Purpose
- Primary: configuration surface and provider SPI for LLM-based code/documentation completion within the srcmorph Maven plugin.
- Secondary: self-contained performance primitives and database-layer business-rule implementations with no cross-dependency on the AI layer.

#### Responsibilities
- **AI run configuration** – `SrcMorphConfiguration` (top-level JavaBean) and `AiGenerationConfig` (45 per-model inference knobs) aggregate all parameters from Maven XML or JSON/YAML.
- **File gating** – `AiConditionEvaluator` walks and/or/not condition trees (extensions, size, lines, mtime, path glob) to decide per-file inclusion; `AiOversizeStrategy` resolves the oversize policy token.
- **Deterministic fact extraction** – `AiFactExtractor` computes regex match counts and formats a facts block prepended to AI prompts.
- **Provider SPI & factory** – `AiGenerationProvider` interface + `AiGenerationProviderFactory` for key-based instantiation with model-level override.
- **Local inference** – `LlamaCppJniAiGenerationProvider` drives GGUF model loading, chat completion, KV-cache reuse, GPU offload via `net.ladenthin:llama` JNI.
- **Remote inference** – `OpenAiCompatibleProviderSupport` builds JSON request bodies, resolves endpoints, guards cleartext API-key transmission (loopback-only), derives timings.
- **Output parsing** – `AiCompletionParser` strips Gemma-4 chain-of-thought reasoning blocks.
- **GPU histogram** (`other/kernels/`) – two-pass OpenCL kernel computing a 256-bin byte histogram via local-memory accumulation + cross-group reduction.
- **Lock-free ring buffer** (`other/ringbuf/`) – fixed-capacity power-of-two SPSC circular byte buffer for inter-thread FIFO exchange.
- **Operational scripts** (`other/scripts/`) – bash backup rotation with retention; Python sales-revenue aggregation by month/region.
- **Library lending** (`sql/library/`) – PostgreSQL loan lifecycle, late fees, multi-branch migration, audit trigger, reporting views.
- **Web-shop ordering** (`sql/shop/`) – MySQL product catalog, order headers, line-item upserts, atomic stock-reserving procedure.
- **Warehouse stock sync** (`sql/warehouse/`) – SQL Server nightly MERGE of supplier feed into per-warehouse SKU stock levels with sync logging.

#### Key units
- `SrcMorphConfiguration` – top-level mutable JavaBean; 23 getter/setter pairs; defaults for output dir, provider, llama fallbacks.
- `AiGenerationConfig` – 45-field mutable JavaBean; per-model inference knobs; null-to-default coercion.
- `AiConditionEvaluator` – stateless final class; `matches`, `validate`, `usesLines` public API.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`); `fromConfig` parser.
- `AiFactExtractor` – static utility; `factsBlock` and `validate`; non-overlapping regex match counting.
- `AiGenerationProvider` – SPI interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves effective provider key (model override > run default); instantiates concrete provider.
- `LlamaCppJniAiGenerationProvider` – final class; lazy-loads `LlamaModel`; executes `chatComplete`; parses via `ChatResponseParser`.
- `LlamaCppJniConfig` – immutable builder-pattern config; 39 optional fields defaulting to `AiGenerationConfig.DEFAULT_*`.
- `OpenAiCompatibleProviderSupport` – package-private final class; Jackson serialization, URL construction, loopback-only cleartext-key guard.
- `AiCompletionParser` – stateless utility; strips leading thinking-block span.
- `AiGenerationTimings` – value object: generated text, prompt/predicted token counts, cache-hit count, per-second throughput.
- `histogram_partial` / `histogram_reduce` – OpenCL `__kernel` functions for per-group accumulation and final reduction.
- `ringbuf_t` / `ringbuf_init` / `ringbuf_write` / `ringbuf_read` – C SPSC ring buffer struct and full lifecycle API.
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` – PostgreSQL loan-lifecycle routines.
- `add_order_item` – MySQL stored procedure; `FOR UPDATE` lock, idempotent upsert, stock decrement.
- `dbo.usp_SyncStock` – SQL Server parameterised MERGE procedure with `SyncLog` capture.

#### Data flow
- **AI pipeline**: Maven plugin → `SrcMorphConfiguration` → `AiFieldGenerationConfig` → `AiGenerationConfig` per model → `AiConditionEvaluator.matches` gates file → `AiOversizeStrategy.fromConfig` resolves policy → `AiFactExtractor.factsBlock` produces deterministic prefix → `AiGenerationProviderFactory.create` resolves provider → provider receives `AiGenerationRequest` → local: `LlamaModel.chatComplete` → `ChatResponseParser` → `AiCompletionParser.parseCompletion`; remote: Jackson body → HTTP POST → parse response → `AiGenerationTimings` returned.
- **Histogram**: `uchar* input` → pass 1 `__local uint[256]` accumulation → `partial[group*256+bin]` → pass 2 serial sum → `histogram[256]`.
- **Ring buffer**: producer `ringbuf_write` (≤2 `memcpy`) → `head++`; consumer `ringbuf_read` (≤2 `memcpy`) → `tail++`.
- **Library**: caller → `fn_checkout_book` → validate blocked/availability → INSERT `loan` → trigger → `loan_audit`.
- **Shop**: caller → `add_order_item` → `FOR UPDATE` lock → upsert `order_item` → decrement `product.stock` → recalculate `customer_order.total_cents`.
- **Warehouse**: `SupplierFeed` (watermark-filtered) → aggregate `SUM(Quantity)` per `(WarehouseId, Sku)` → MERGE into `StockLevel` → INSERT `SyncLog`.

#### Dependencies
- **AI layer internal**: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniProviderSupport`.
- **AI layer external**: `net.ladenthin:llama` JNI (`LlamaModel`, `ModelParameters`, `ChatResponseParser`, `ChatResponse`, `Timings`, `FlashAttn`, `ReasoningFormat`), Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`), Lombok, `org.jspecify.annotations.Nullable`.
- **Other sub-package**: OpenCL C 1.2 runtime; C stdlib (`stdlib.h`, `string.h`, `stddef.h`, `stdint.h`); Python stdlib (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`); system utilities (`tar`, `gzip`, `ls`, `mv`, `rm`).
- **SQL sub-package**: PostgreSQL (PL/pgSQL, materialized views, partial indexes), MySQL (InnoDB, `ON DUPLICATE KEY UPDATE`, `SIGNAL`), SQL Server (T-SQL, `MERGE`, `DATETIME2`).
- **No cross-dependency** between the three top-level sub-packages.

#### Cross-cutting
- **Nullability** – `@Nullable` (jspecify) marks "not configured" fields; setters coerce null to defaults or empty collections.
- **Validation** – fail-fast at config time (`IllegalArgumentException`); runtime `IllegalStateException` only if validation was skipped.
- **Statelessness vs mutability** – evaluators, parsers, and support classes are stateless/immutable (thread-safe by construction); `SrcMorphConfiguration` and `AiGenerationConfig` are mutable and not thread-safe.
- **Lazy initialization** – `LlamaCppJniAiGenerationProvider.model()` uses check-then-act (single-threaded assumption per instance).
- **Security guard** – `OpenAiCompatibleProviderSupport.apiKeyFor` enforces loopback-only for cleartext HTTP key transmission.
- **Timing degradation** – both local and remote paths degrade to zero-rate `AiGenerationTimings` when timing data is absent.
- **Maven XML binding** – field names and collection wrapper types (`AiConditionGroup`) shaped for Maven Plexus/JAXB singularization.
- **Concurrency** – ring buffer uses `volatile` for SPSC visibility (no atomics, no locks); histogram uses `barrier(CLK_LOCAL_MEM_FENCE)`; SQL uses pessimistic row locks (`FOR UPDATE`), implicit serialisation, or single-transaction MERGE.
- **Monetary representation** – integer cents throughout SQL layer (`price_cents`, `total_cents`, `unit_price_cents`).
- **Idempotency** – shop uses `ON DUPLICATE KEY UPDATE`; warehouse idempotent only if caller advances `@Since` watermark.
- **Error handling** – engine-native mechanisms: `RAISE EXCEPTION` (PostgreSQL), `SIGNAL SQLSTATE '45000'` (MySQL), `TRY/CATCH` + re-throw (SQL Server); ring buffer clamps silently; scripts use explicit exit codes.
