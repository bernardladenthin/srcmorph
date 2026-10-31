### ai
- H: 1.0
- C: F65CE4D9
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:44:03Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A Maven‑plugin framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI model invocation for source‑code summarisation and transformation, while also providing GPU‑accelerated histogram utilities and a set of SQL schemas for library, shop, and warehouse domains.

#### Purpose
- Enable AI‑driven source‑code processing via a single, configurable Maven plugin.  
- Offer deterministic fact blocks and robust file‑selection logic for AI prompts.  
- Provide GPU‑accelerated histogram computation and ring‑buffer utilities.  
- Persist and manipulate domain data for libraries, e‑commerce shops, and warehouses with audit and reporting support.

#### Responsibilities
- **AI orchestration** – configuration aggregation, file‑selection, fact extraction, provider selection, request/response handling, and metrics.  
- **GPU histogram engine** – two‑pass OpenCL kernels, lock‑free ring buffer, and backup/reporting utilities.  
- **Database domain** – schema definition, business logic (checkout, return, order item, stock sync), reporting views, migration helpers, and audit trails.

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – runtime options bean.  
  - `AiConditionEvaluator` / `AiConditionGroup` – stateless rule engine.  
  - `AiFactExtractor` – deterministic fact header builder.  
  - `AiOversizeStrategy` – enum for oversized file handling.  
  - `AiGenerationProviderFactory` – provider resolver.  
  - `LlamaCppJniAiGenerationProvider` – native llama.cpp inference.  
  - `OpenAiCompatibleProviderSupport` – OpenAI‑compatible request/response helper.  
  - `AiCompletionParser` – strips reasoning blocks.  
- **Other**  
  - `histogram_partial` / `histogram_reduce` OpenCL kernels – two‑pass histogram.  
  - `ringbuf_t` API – power‑of‑two lock‑free buffer.  
  - `rotate_backups.sh` – backup rotation script.  
  - `sales_report.py` – CSV parsing and revenue aggregation.  
- **SQL**  
  - Library tables (`author`, `book`, `member`, `loan`, `book_copy`, `branch`) and functions (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`).  
  - Shop tables (`product`, `customer_order`, `order_item`) and procedure `add_order_item`.  
  - Warehouse tables (`StockLevel`, `SupplierFeed`, `SyncLog`) and procedure `dbo.usp_SyncStock`.  
  - Views (`overdue_loans`, `member_loan_stats`, `popular_books`) and audit triggers.

#### Data flow
1. **AI Pipeline** – `SrcMorphConfiguration` → `AiConditionEvaluator` → `AiOversizeStrategy` → `AiFactExtractor` → `AiGenerationProviderFactory` → `AiGenerationProvider` → `OpenAiCompatibleProviderSupport.requestBody` → AI service → `AiCompletionParser` → output written to source.  
2. **Histogram Engine** – host enqueues `histogram_partial` → local histograms → `histogram_reduce` → final 256‑bin result.  
3. **Ring Buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer).  
4. **Backup/Reporting** – `rotate_backups.sh` archives directories; `sales_report.py` parses CSV → aggregates revenue → prints report.  
5. **Database Operations** – `fn_checkout_book`/`sp_return_book` update book copies, insert/update loans, trigger audit. `add_order_item` locks product, updates stock, upserts order item, recalculates order total. `dbo.usp_SyncStock` merges supplier feed into stock levels and logs sync.

#### Dependencies
- **Internal** – AI components (`AiCondition`, `AiFactCounter`, `AiPromptDefinition`, etc.), histogram kernels, ring buffer API, backup/reporting scripts.  
- **External** – Java standard libraries, Lombok, Jackson, llama.cpp native bindings, OpenCL 1.2, PostgreSQL/MySQL/SQL Server built‑ins, Bash utilities, Python standard library.

#### Cross‑cutting
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).  
- Defensive copying and validation in rule and fact builders.  
- Immutable configuration objects (`LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`).  
- Error handling via `IOException`/`IllegalArgumentException` and status codes in C API.  
- Logging of truncation, cache reuse, and timing metrics.  
- Atomic operations for histogram bin updates and ring buffer indices.  
- Power‑of‑two buffer size enforcement and UTC timestamp formatting for backups.  
- Integrity constraints, audit triggers, and row‑level locking across SQL schemas.
