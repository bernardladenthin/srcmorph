### ai
- H: 1.0
- C: 1BB7C853
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:47:48Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
Synthesizing a package summary from the per-file summaries for the ai package, which contains java/, other/, and sql/ subpackages.

The package is a heterogeneous collection. java/ configures srcmorph AI generation. other/ holds miscellaneous tools like OpenCL histograms and ring buffers. sql/ contains three independent databases. I will synthesize these into one dense structured summary.

The java/ subpackage indexes srcmorph sources and generates .ai.md files. The other/ subpackage contains a byte histogram, an SPSC ring buffer, and CLI tools for backup rotation and sales reporting. The sql/ subpackage holds three independent databases: a lending library, a web shop, and a warehouse stock sync.

The summary will use a blockquote lead followed by structured sections: Purpose, Responsibilities, Key units, Data flow, Dependencies, and Cross-cutting. All sections are retained since they apply at some level. The output format requires markdown bullets, short clauses, woven type names, no code fences, and labels starting with ####.

The task-specific indexer instructions take precedence over general formatting rules. The lead sentence will use domain terms such as srcmorph AI indexing, OpenCL histogram, ring buffer, library checkout, shop orders, and warehouse stock.

The lead sentence is finalized. The Purpose section will note that the three subpackages do not call each other. I am now drafting Responsibilities, Key units, and Data flow.

Dependencies will group external libraries without cross-subdirectory links. Cross-cutting notes that the three domains use different error models and share no types. The draft is being kept dense and free of invented content.> Heterogeneous index of srcmorph source selection and `.ai.md` generation, plus independent byte tools and three standalone SQL domains for lending circulation, shop checkout, and warehouse stock sync.

#### Purpose
- Top-level `ai` collects three non-collaborating areas: Maven-bindable srcmorph run settings and generation backends, miscellaneous OpenCL/C/operator utilities, and separate SQL schemas.
- Summaries show no calls among `java/`, `other/`, and `sql/`.

#### Responsibilities
- Srcmorph AI (`java/`): source filters, oversize policy, exact fact counts, and local llama.cpp or OpenAI-compatible generation of cleaned `.ai.md` text.
- Byte and operator tools (`other/`): OpenCL 256-bin histogram, fixed-capacity SPSC byte ring, backup rotation, and monthly regional sales totals. `kernels/`, `ringbuf/`, and `scripts/` do not call each other.
- Databases (`sql/`): PostgreSQL lending library, MySQL web-shop checkout, and SQL Server nightly stock sync, each with its own schema and routines.

#### Key units
- `SrcMorphConfiguration` — mutable run bean for roots, excludes, size bounds, provider, prompts, models, field routing, facts, llama fallbacks, and header versions.
- `AiGenerationConfig` / `LlamaCppJniConfig` — per-model sampling, KV cache, GPU/CPU layers, DRY, stop strings, remote endpoint, and optional `AiCalibration`; JNI config is a 41-field immutable builder result.
- `AiConditionEvaluator` / `AiOversizeStrategy` / `AiFactExtractor` — condition trees against `AiFileContext`, `<onOversize>` as `FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC`, and deterministic `label: count` blocks.
- `AiGenerationProviderFactory` — `effectiveProvider` and `create` for `mock`, `llamacpp-jni`, or `openai-compatible`; `LlamaCppJniAiGenerationProvider` chats a GGUF `LlamaModel`; `OpenAiCompatibleProviderSupport` builds chat-completions JSON; `AiCompletionParser` strips a leading Gemma-4 thinking block.
- `histogram_partial` / `histogram_reduce` — OpenCL C 1.2 partial counts and reduction into a 256-bin histogram (`BINS`).
- `ringbuf_t` — power-of-two SPSC buffer storing at most `capacity - 1` bytes; `ringbuf_write` / `ringbuf_read` copy with wraparound.
- `rotate_backups.sh` — dated `.tar.gz` archives pruned beyond `KEEP`.
- `parse_orders` / `revenue_by_month_and_region` — currency-filtered CSV `Order` rows summed by `("%Y-%m", region)`.
- Library: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit`, views `overdue_loans`, `member_loan_stats`, `popular_books`; v2 `branch` / `book_copy`.
- Shop: `product`, `customer_order`, `order_item`, procedure `add_order_item`.
- Warehouse: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `dbo.usp_SyncStock` (`@Since`).

#### Data flow
- Java: `SrcMorphConfiguration` and `AiModelDefinition` feed filters, `AiConditionEvaluator`, `AiOversizeStrategy`, and optional `AiFactExtractor.factsBlock`; provider strings select mock, JNI, or OpenAI-compatible `generate` of an `AiGenerationRequest` into cleaned text and optional `AiGenerationTimings`. Relative model paths resolve against `modelDirectory`. The socket-sending remote provider is not described.
- Other: no shared pipeline. Kernels write `partial` then `histogram`. The ring copies bytes between one producer and one consumer. Scripts read a backup directory or order CSV and write archives or a TSV.
- SQL: library checkout/return mutates `copies_free` and `loan` and audits via `loan_audit` (seed still uses pre-v2 counters). Shop `add_order_item` locks `product`, updates `order_item` and `stock`, and refreshes `total_cents`. Warehouse `MERGE`s aggregated feed quantity into `OnHand` and logs `$action` or failure.

#### Dependencies
- No stated collaboration among the three top-level directories or among `library/`, `shop/`, and `warehouse/`.
- Java binds `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiGenerationRequest`, `AiPromptSupport`, llama.cpp JNI types, and Jackson; default provider `"mock"`; default remote base `https://openrouter.ai/api/v1`; `apiKeyEnv` is an env-var name.
- Other: OpenCL C 1.2; C `malloc`/`memcpy` and `extern "C"`; bash plus `tar`/`date`; Python `argparse`, `csv`, `Decimal`.
- SQL dialects stay separate: PostgreSQL PL/pgSQL, MySQL 8 InnoDB `utf8mb4`, SQL Server T-SQL `MERGE`/`THROW`. Library and shop use declared FKs; warehouse matches feed to stock only logically. Shop `customer_id` has no customer-table FK.

#### Cross-cutting
- Independent error models: Java fail-fast `IllegalArgumentException` / `IllegalStateException` and generation `IOException`; kernels return void; ring uses `ringbuf_status_t` and treats short copies as success; scripts exit non-zero with `set -euo pipefail`; SQL raises on blocked members, no stock, unknown SKU, or sync failure.
- Java sentinels: `-1` or empty often means do not forward; `0` often disabled; size bounds `<= 0` disable or unlimit filters. Run beans are mutable and not thread-safe; JNI `LlamaModel` lazy init is unsynchronized.
- Stock and money differ by SQL package: library `copies_free` (v2 moves copies to `book_copy.status` without redefining checkout/return), shop locked `product.stock`, warehouse additive `OnHand`; library and shop store integer cents, warehouse stores quantity only.
