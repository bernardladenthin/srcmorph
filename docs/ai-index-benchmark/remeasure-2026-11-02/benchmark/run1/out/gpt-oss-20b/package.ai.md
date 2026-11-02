### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:33:35Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code morphing, from rule‑based file selection and deterministic fact extraction to pluggable LLM generation and post‑processing, with supporting utilities for byte‑stream handling, GPU histograms, and database‑level cataloging, e‑commerce, and inventory logic.  

#### Purpose
- Central AI‑powered source‑code transformation engine.  
- Provide pluggable LLM providers (local, cloud).  
- Offer ancillary GPU and database utilities for performance and persistence.

#### Responsibilities
- **Configuration & Orchestration** – load runtime settings, evaluate file‑selection rules, route oversized files.  
- **Fact Extraction** – build deterministic fact blocks from source text.  
- **Provider Abstraction** – select and invoke LLM providers, handle HTTP/ JNI interactions.  
- **Post‑processing** – clean LLM outputs, extract timings, persist results.  
- **Utility Services** – GPU‑accelerated byte histograms, lock‑free ring buffers, backup rotation, sales reporting, and database modules for library, shop, and warehouse domains.

#### Key units
- `SrcMorphConfiguration` – runtime settings bean.  
- `AiConditionEvaluator` – stateless rule validator.  
- `AiOversizeStrategy` – enum for oversized‑file handling.  
- `AiFactExtractor` – regex‑based fact builder.  
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.  
- `AiGenerationProviderFactory` – resolves concrete provider.  
- `LlamaCppJniAiGenerationProvider` – native llama.cpp wrapper.  
- `OpenAiCompatibleAiGenerationProvider` – HTTP chat completion client.  
- `AiCompletionParser` – strips internal thinking blocks.  
- `OpenAiCompatibleProviderSupport` – JSON builder, endpoint validation, timing extraction.  
- GPU kernels: `histogram_partial`, `histogram_reduce`.  
- Ring buffer API: `ringbuf_init`, `ringbuf_write`, `ringbuf_read`.  
- Scripts: `rotate_backups.sh`, `sales_report.py`.  
- SQL modules: library tables (`author`, `book`, `loan`, …), shop tables (`product`, `order_item`), warehouse tables (`StockLevel`, `SupplierFeed`).  
- Stored procedures: `fn_checkout_book`, `add_order_item`, `usp_SyncStock`.  
- Views: `overdue_loans`, `popular_books`.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` populated from Maven/CLI/JSON.  
2. **File Selection** – `AiConditionEvaluator` filters files; oversized files routed per `AiOversizeStrategy`.  
3. **Fact Generation** – `AiFactExtractor` produces fact blocks.  
4. **Provider Selection** – `AiGenerationProviderFactory.create` picks provider.  
5. **Generation** – Provider’s `generate` consumes `AiGenerationRequest`; returns raw completion.  
6. **Post‑processing** – `AiCompletionParser.parseCompletion` cleans output; timings extracted via provider interface.  
7. **Persistence** – cleaned text written back or used for transformation.  
8. **Utility Pipelines** – GPU kernels process byte streams; ring buffer handles producer/consumer data flow; scripts rotate backups and aggregate sales.  
9. **Database Flow** – Library functions (`fn_checkout_book`, `sp_return_book`) modify loan state and audit; shop procedure (`add_order_item`) locks product, updates inventory, recalculates totals; warehouse sync aggregates supplier feed into stock levels and logs.

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `Java8CompatibilityHelper`.  
- **External** – Java core (`java.time`, `java.util.regex`), Lombok, Jackson, HTTP client, OpenCL 1.2, native llama.cpp bindings, Python stdlib, shell utilities.  
- **Database** – PostgreSQL, MySQL, SQL Server built‑ins and locking primitives.

#### Cross‑cutting
- **Thread Safety** – stateless utilities; immutable config objects.  
- **Validation & Error Handling** – explicit checks in evaluators and extractors; provider errors surfaced as `IOException`; malformed responses wrapped with context.  
- **Performance** – lazy model loading in `LlamaCppJniAiGenerationProvider`; GPU reduction kernels; ring buffer power‑of‑two wrap‑around.  
- **Extensibility** – new LLM providers via `AiGenerationProvider`; new fact counters or conditions via configuration beans.  
- **Transactional Integrity** – each stored procedure runs in a single transaction; rollback on error.  
- **Audit & Reporting** – library `loan_audit`, warehouse `SyncLog`; materialized views for popular books.  
- **Concurrency Control** – shop uses row‑level locking; library relies on FK constraints and triggers.
