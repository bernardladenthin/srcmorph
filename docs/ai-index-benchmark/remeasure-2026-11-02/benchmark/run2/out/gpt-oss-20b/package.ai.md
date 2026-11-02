### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:49:55Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code transformations, coupled with lightweight image‑processing utilities and a set of database modules for library, shop, and warehouse operations.

#### Purpose
- Centralize AI‑based code generation and transformation for the srcmorph tool.
- Provide efficient byte‑histogram and ring‑buffer primitives for high‑performance data streams.
- Persist and enforce business rules across library lending, e‑commerce orders, and warehouse inventory.

#### Responsibilities
- **AI Generation Engine** – configuration, condition evaluation, fact extraction, oversize handling, provider abstraction, local/remote inference, mock support, and completion parsing.
- **Image‑Processing Toolkit** – OpenCL byte‑histogram kernels, lock‑free ring buffer, and ancillary backup/reporting scripts.
- **Database Modules** – transactional logic for library loans, shop order items, and warehouse stock synchronization.

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – runtime settings holder.  
  - `AiConditionEvaluator` – validates file‑selection trees.  
  - `AiFactExtractor` – builds fact blocks for prompts.  
  - `AiOversizeStrategy` – enum (`FAIL`, `HEAD`, `CHUNK`, `METADATA`).  
  - `AiGenerationProvider` / `AiGenerationProviderFactory` – provider abstraction.  
  - `LlamaCppJniAiGenerationProvider` – local llama.cpp inference.  
  - `OpenAiCompatibleProviderSupport` – HTTP chat‑completion helper.  
  - `AiCompletionParser` – cleans LLM output.  
- **Other (C/OpenCL)**  
  - `histogram_partial`, `histogram_reduce` – two‑pass reduction kernels.  
  - `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – lock‑free ring buffer API.  
  - `rotate_backups.sh` – backup rotation script.  
  - `sales_report.py` – CSV parsing and revenue aggregation.  
- **SQL**  
  - Library tables (`author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`) and views (`overdue_loans`, `member_loan_stats`, `popular_books`).  
  - Shop tables (`product`, `customer_order`, `order_item`) and procedure `add_order_item`.  
  - Warehouse tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`) and procedure `dbo.usp_SyncStock`.

#### Data flow
1. **AI Engine** – configuration → condition tree → file filtering → oversize routing → fact‑augmented prompt → provider selection → text generation → post‑processing → code persistence.  
2. **Histogram** – byte stream → `histogram_partial` → `histogram_reduce` → 256‑bin histogram.  
3. **Ring Buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → status queries.  
4. **Database** –  
   - Library: `fn_checkout_book` → loan insert → audit trigger.  
   - Shop: `add_order_item` → stock lock → item insert/update → order total recalc.  
   - Warehouse: `dbo.usp_SyncStock` → feed aggregation → stock merge → sync log.

#### Dependencies
- **Internal** – AI components depend on `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, etc.; database modules rely on their own tables and views.  
- **External** – Java: `net.ladenthin.llama`, Jackson, Lombok; C: OpenCL built‑ins; SQL: PostgreSQL PL/pgSQL, MySQL 8 features, SQL Server T‑SQL.

#### Cross‑cutting
- **Thread safety** – stateless evaluators, immutable configs, lock‑free ring buffer.  
- **Validation & error handling** – explicit `IllegalArgumentException`, `IOException`, and provider‑specific checks.  
- **Metrics** – `generateWithTimings` returns token counts and decode rates.  
- **Security** – HTTPS enforcement for OpenAI provider, loopback restriction for API keys.  
- **Transactional integrity** – all critical DB updates wrapped in transactions; audit trails (`loan_audit`, `SyncLog`).  
- **Concurrency control** – MySQL row‑level locks, PostgreSQL constraints, SQL Server `MERGE`.  
- **Performance** – two‑pass reduction kernels, atomic operations, power‑of‑two ring buffer indices.
