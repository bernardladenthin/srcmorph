### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:09:15Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified AI‑driven source‑generation framework that evaluates file‑level filters, extracts deterministic facts, and orchestrates model‑specific generation settings across local and OpenAI‑compatible providers, complemented by GPU‑accelerated image histogram tooling and a SQL library for catalog, inventory, and order management.

#### Purpose
- AI‑powered source‑generation engine with mutable configuration, filter evaluation, fact extraction, and oversize handling.
- GPU‑accelerated byte histogram construction and lock‑free ring buffer for data streaming.
- SQL libraries for catalog, inventory, and order data with business logic, reporting, and synchronization.

#### Responsibilities
- **AI Generation** – configuration, condition evaluation, fact extraction, oversize strategy, provider abstraction, local inference, OpenAI‑compatible support, completion parsing.
- **GPU & Buffering** – two‑pass OpenCL histogram kernels, power‑of‑two lock‑free ring buffer API.
- **SQL Domain** – table definitions, stored procedures, triggers, views, audit logs, data migration scripts.

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – mutable runtime config holder.  
  - `AiGenerationConfig` – model and prompt parameters.  
  - `AiConditionEvaluator` – recursive filter tree evaluator.  
  - `AiFactExtractor` – deterministic fact block builder.  
  - `AiOversizeStrategy` – enum for context‑limit handling.  
  - `AiGenerationProvider` – interface for generate/close.  
  - `AiGenerationProviderFactory` – provider resolution and instantiation.  
  - `LlamaCppJniConfig` – immutable llama.cpp knobs.  
  - `LlamaCppJniAiGenerationProvider` – local inference via JNI.  
  - `OpenAiCompatibleProviderSupport` – OpenAI‑compatible request/response handling.  
  - `AiCompletionParser` – strips internal reasoning blocks.
- **Other (C/OpenCL)**  
  - `histogram_partial`, `histogram_reduce` – OpenCL kernels for 256‑bin histograms.  
  - `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – lock‑free ring buffer API.  
  - `rotate_backups.sh` – backup rotation script.  
  - `sales_report.py` – CSV order parsing and revenue aggregation.
- **SQL**  
  - Library tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Library procedures/functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, trigger `trg_loan_audit`.  
  - Library views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Shop tables: `product`, `customer_order`, `order_item`.  
  - Shop procedure: `add_order_item`.  
  - Warehouse tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Warehouse procedure: `dbo.usp_SyncStock`.

#### Data flow
1. **AI Generation**  
   - `SrcMorphConfiguration` → `AiGenerationConfig` → `AiConditionEvaluator` (file context) → `AiFactExtractor` (facts block) → `AiGenerationProviderFactory` (provider) → provider (`LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleProviderSupport`) → `AiCompletionParser` → final source output.
2. **GPU Histogram**  
   - Byte stream → `histogram_partial` (per‑group) → `histogram_reduce` → 256‑bin histogram.
3. **Ring Buffer**  
   - `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → status queries.
4. **SQL Operations**  
   - Library: `fn_checkout_book` → updates `book_copy`, inserts `loan`, logs `loan_audit`.  
   - Shop: `add_order_item` → locks `product`, updates `order_item`, decrements stock, recalculates order total.  
   - Warehouse: `dbo.usp_SyncStock` → aggregates `SupplierFeed`, merges into `StockLevel`, logs `SyncLog`.

#### Dependencies
- **Internal** – AI condition trees (`AiCondition`, `AiConditionGroup`), fact counters (`AiFactCounter`), prompt definitions (`AiPromptDefinition`), model definitions (`AiModelDefinition`), request/response types (`AiGenerationRequest`, `AiGenerationTimings`), Java8 compatibility helpers.  
- **External** – `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (model, parameters, JSON parsing), Jackson (`com.fasterxml.jackson`), OpenCL runtime, C standard library, Bash utilities, Python standard library, PostgreSQL/MySQL/SQL Server built‑ins.  

#### Cross‑cutting
- **Thread safety** – Evaluator and extractor utilities are stateless; ring buffer is lock‑free single‑producer/single‑consumer.  
- **Validation & Error handling** – `AiConditionEvaluator.validate`, `AiFactExtractor.validate` throw `IllegalArgumentException`; I/O or parsing errors propagate as `IOException`.  
- **Immutability** – `LlamaCppJniConfig`, provider support classes are immutable and thread‑safe.  
- **Timing & Metrics** – `AiGenerationTimings` records token counts and throughput for both local and OpenAI providers.  
- **Logging & Security** – Providers log cache reuse, truncation warnings, and block non‑loopback API key transmission.  
- **Referential Integrity & Concurrency** – SQL packages enforce foreign keys, unique constraints, row‑level locks (`FOR UPDATE`), and transaction atomicity.  
- **Error propagation** – SQL procedures raise errors for blocked members, insufficient stock, or sync failures; Java components surface exceptions for invalid configurations or runtime failures.
