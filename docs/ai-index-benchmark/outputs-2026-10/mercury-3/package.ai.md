### ai
- H: 1.0
- C: CCD7207D
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:33:48Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A mixed operational bundle spanning three independent verticals: pluggable AI text-generation backend configuration for the srcmorph Maven code-indexer, embedded byte-processing primitives (OpenCL GPU histograms, lock-free ring buffer) plus ops shell/Python CLIs, and multi-database SQL (PostgreSQL library circulation, MySQL web-shop ordering, T-SQL warehouse stock sync).

#### Purpose
- Aggregates three self-contained subpackages with no shared code or conventions: AI generation support (java/), byte-processing and ops utilities (other/), and database schemas and routines (sql/).
- Each subpackage serves a distinct business or technical responsibility; the package is a top-level grouping within an indexer toolchain.

#### Responsibilities
- AI generation backend (java/): Maven-plugin configuration beans (`AiGenerationConfig`, `SrcMorphConfiguration`), condition evaluation for file include/exclude, deterministic facts-block extraction, provider abstraction over llama.cpp JNI, OpenAI-compatible endpoints, and a mock provider, plus completion parsing with thinking-block stripping.
- Byte-processing substrate (other/ kernels + ringbuf): two-pass OpenCL GPU histogram kernels over raw byte streams; lock-free single-producer/single-consumer power-of-two ring buffer for UART/DMA/audio/inter-thread transport.
- Ops scripts (other/ scripts): backup rotation (`rotate_backups.sh`) with timestamped tar.gz creation, atomic finalization, and keep-N pruning; CSV sales reporting (`sales_report.py`) with column/currency validation and month/region revenue aggregation.
- Database bundles (sql/): PostgreSQL library circulation schema, views, routines, and audit trigger; MySQL shop ordering with atomic stock reservation in one stored procedure; T-SQL warehouse stock synchronisation via MERGE with audit and error capture.

#### Key units
- `AiGenerationConfig` — mutable JavaBean with 40+ `DEFAULT_*` constants; central AI-generation tuning for model, sampling, caching, batching, and provider endpoints.
- `SrcMorphConfiguration` — root run-config bean (22 accessors) binding mojos/CLI/YAML; defaults for output dir, provider, llama context, AI version.
- `AiOversizeStrategy` — enum `FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC` selected via `<onOversize>` token; default FAIL.
- `AiConditionEvaluator` / `AiConditionGroup` — immutable, thread-safe condition-tree evaluation for file filtering; branch-count validation.
- `AiFactExtractor` — static utility building the deterministic "facts" block prepended to AI summaries.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — provider contract and key-based selection (`mock`, `llamacpp-jni`, `openai-compatible`).
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` (+ `Builder`) — native GGUF inference via llama.cpp JNI (`LlamaModel`, `ChatResponseParser`, `Timings`); immutable config with required `modelPath`.
- `OpenAiCompatibleProviderSupport` — package-private chat-completions request serialization, endpoint validation, leak-safe API-key gating, and timing extraction.
- `AiCompletionParser` — strips Gemma-4 `thinking` blocks (`<|channel>thought` … `<channel|>`); `IOException` on unclosed leading block.
- `histogram_partial` / `histogram_reduce` — first/second-pass OpenCL kernels; per-group local 256-bin counting via `atomic_inc`, then one work-item per bin summing partials.
- `ringbuf_t` with `ringbuf_init`/`ringbuf_free`/`ringbuf_write`/`ringbuf_read`/`ringbuf_used`/`ringbuf_free_space` — fixed-capacity power-of-two FIFO; volatile `head`/`tail`; non-blocking bounded transfers.
- `rotate_backups.sh` — Bash CLI: arg validation, archive creation, stale-archive pruning.
- `Order` / `parse_orders(path, currency)` / `revenue_by_month_and_region(orders)` / `main(argv=None)` — frozen dataclass, CSV generator with currency filter, `Decimal` aggregation keyed by `(month, region)`, CLI entry point.
- library: tables `author`, `book`, `member`, `loan`, `branch`, `book_copy`; views `overdue_loans`, `member_loan_stats`, materialized `popular_books`; routines `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit` + `trg_loan_audit_fn`.
- shop: `product
