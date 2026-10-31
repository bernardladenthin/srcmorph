### ai
- H: 1.0
- C: CCD7207D
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:59:40Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package enables AI-configured source inclusion and deterministic code morphing, provides byte-histogram, circular-buffer, and dated backup/sales-report automation, and manages PostgreSQL lending, MySQL e-commerce, and T-SQL warehouse stock-synchronization data systems.

#### Purpose
- Configure and evaluate AI-driven source inclusion rules, extract deterministic whole-file facts, and morph source code through pluggable LLM backends.
- Compute OpenCL byte histograms, manage a lock-free circular byte queue, and run dated backup and monthly regional sales-report scripts.
- Define and operate PostgreSQL lending-library, MySQL web-shop, and T-SQL warehouse stock-synchronization schemas and routines.

#### Responsibilities
- AI/config: Boolean condition tree evaluation and validation against file facts; grouping child conditions for Maven `<and>` / `<or>` combinators; exact whole-file regex match counting and deterministic facts block generation; case-insensitive parsing of oversize response strategies.
- AI/provider: AiGenerationProvider contract, lifecycle, and factory-based selection/instantiation; local GGUF execution via llama.cpp JNI; OpenAI-compatible request construction without network I/O; completion sanitization by stripping leading thinking/reasoning blocks.
- Other: OpenCL 256-bin byte histogram via per-work-group partial histograms and global reduction; fixed-capacity SPSC circular byte buffer with enqueue, dequeue, and occupancy queries; timestamped tar.gz archiving with retention pruning and `.partial` cleanup; CSV order parsing and monthly regional revenue aggregation above a threshold.
- SQL library: core DDL for author, book, member, loan, loan_audit, branch, book_copy; reporting views overdue_loans, member_loan_stats, and materialized view popular_books; lending routines fn_checkout_book, sp_return_book, fn_late_fee; audit trigger trg_loan_audit; schema migration and transactional seed data.
- SQL shop: product catalog with SKU uniqueness and stock; customer orders with status enum and running totals; order items with composite keys; add_order_item procedure for stock reservation and total recalculation.
- SQL warehouse: nightly aggregation of dbo.SupplierFeed receipts by WarehouseId and Sku; MERGE upsert into dbo.StockLevel; transactional audit logging into dbo.SyncLog with rollback on failure.

#### Key units
- AiConditionEvaluator: stateless final class matching and validating AiCondition trees against AiFileContext.
- AiConditionGroup: nullable container for child AiCondition lists bound to Maven `<and>` / `<or>` elements.
- AiFactExtractor: pure utility producing `label: count` facts blocks from AiFactCounter regex matches.
- AiOversizeStrategy: enum with FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC and case-insensitive fromConfig parsing.
- AiGenerationProvider: interface defining generate, generateWithTimings, and close for AI backends.
- AiGenerationProviderFactory: resolves provider keys and instantiates mock, llama.cpp JNI, or OpenAI-compatible providers.
- LlamaCppJniAiGenerationProvider: final provider wrapping net.ladenthin:llama JNI LlamaModel for local GGUF inference.
- LlamaCppJniConfig: configuration for the llama.cpp JNI provider.
- OpenAiCompatibleProviderSupport: package-private helper for OpenAI-compatible payloads, endpoints, API keys, and timings.
- AiCompletionParser: stateless parser stripping leading Gemma-4 thinking blocks from completion text.
- histogram.cl: OpenCL C 1.2 source with histogram_partial and histogram_reduce kernels; BINS fixed at 256.
- ringbuf_t / ringbuf_status_t: ringbuf.h types and status codes for the fixed-capacity SPSC circular queue.
- ringbuf.c: ringbuf_init, ringbuf_free, ringbuf_write, ringbuf_read, ringbuf_used, ringbuf_free_space.
- rotate_backups.sh: Bash utility for SOURCE_DIR archiving, dated final archives, retention pruning, and `.partial` cleanup via EXIT trap.
- sales_report.py: Python utility with frozen Order dataclass, parse_orders generator, revenue_by_month_and_region aggregator, and main CLI driver.
- Library tables: author, book, member, loan, loan_audit, branch, book_copy.
- Library views: overdue_loans, member_loan_stats; materialized view popular_books with unique index idx_popular_books_id.
- Library routines: fn_checkout_book, sp_return_book, fn_late_fee; trigger trg_loan_audit with function trg_loan_audit_fn.
- Shop tables: product, customer_order, order_item; procedure add_order_item.
- Warehouse tables: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog; procedure dbo.usp_SyncStock.

#### Data flow
- AI: AiCondition trees are validated by AiConditionEvaluator.validate, then matched against AiFileContext facts via AiConditionEvaluator.matches to decide file inclusion or exclusion; AiConditionGroup supplies child condition lists to AND/OR nodes during traversal.
- AI: AiFactExtractor.validate checks AiFactCounter regex configuration, then factsBlock emits exact counts for labels over full source text; AiOversizeStrategy.fromConfig converts a config token into the build's oversize response, defaulting to FAIL.
- AI: LlamaCppJniConfig and AiPromptSupport are passed to AiGenerationProviderFactory.create to resolve the effective provider key and instantiate an AiGenerationProvider; callers invoke generate or generateWithTimings with an AiGenerationRequest containing prompt key, source file, source text, and current header.
- AI: LlamaCppJniAiGenerationProvider lazily constructs a native LlamaModel, calls chatComplete, then passes the first content through AiCompletionParser to strip thinking blocks; results are wrapped in AiGenerationTimings with prompt/cached/completion tokens and decode rates.
- Other: host enqueues histogram_partial over an input byte stream to fill per-group partial bins, then enqueues histogram_reduce with one work-item per bin to sum partials into the final 256-bin histogram buffer.
- Other: producer calls ringbuf_write to copy bytes in up to two wrap-around chunks and advance head; consumer calls ringbuf_read to copy bytes out in up to two chunks and advance tail; occupancy is polled via ringbuf_used and ringbuf_free_space.
- Other: rotate_backups.sh tar-compresses the source directory to a `.partial` staging file, renames it to a dated archive, then deletes archives beyond KEEP by `ls -1t` order.
- Other: sales_report.py reads CSV rows into Order objects, aggregates by (YYYY-MM, region) as Decimal totals, filters by `--min-revenue`, and emits tab-separated month/region/revenue rows.
- SQL library: seed data populates author, book, member, and loan; fn_checkout_book validates member.is_blocked, inserts a loan, and the audit trigger writes a CHECKOUT row to loan_audit; sp_return_book sets returned_on, restores the copy, and the trigger writes a RETURN row; fn_late_fee computes capped overdue cents
