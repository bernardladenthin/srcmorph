### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:58:33Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code generation, from file selection and fact extraction to prompt creation and model execution, while also providing low‑level byte‑stream utilities and a set of SQL packages for inventory, lending, and e‑commerce data management.  

#### Purpose  
- Orchestrate AI code‑generation jobs across source trees.  
- Offer reusable byte‑stream tools (histograms, ring buffers, scripts).  
- Persist and enforce business rules for library, shop, and warehouse domains.  

#### Responsibilities  
- **AI Generation Pipeline** – configuration, file filtering, fact extraction, oversize handling, provider abstraction, and response cleaning.  
- **Byte‑Stream Utilities** – OpenCL histogram kernels, lock‑free ring buffer, backup and reporting scripts.  
- **Data Persistence** – schema, stored procedures, audit triggers, and reporting views for library, shop, and warehouse.  

#### Key units  
- **Java AI Core**  
  - `SrcMorphConfiguration`, `AiGenerationConfig` – mutable Maven‑style settings.  
  - `AiConditionEvaluator`, `AiConditionGroup` – file‑selection logic.  
  - `AiFactExtractor` – deterministic fact block builder.  
  - `AiOversizeStrategy` – handling for context‑limit files.  
  - `AiGenerationProvider` / `AiGenerationProviderFactory` – pluggable back‑ends (llama.cpp, OpenAI, mock).  
  - `AiCompletionParser` – removes internal reasoning blocks.  
- **Other Utilities**  
  - `histogram_partial`, `histogram_reduce` – OpenCL 256‑bin histogram kernels.  
  - `ringbuf_t`, `ringbuf_*` – power‑of‑two lock‑free circular buffer.  
  - `rotate_backups.sh`, `sales_report.py` – backup rotation and CSV sales aggregation.  
- **SQL Packages**  
  - **Library (PostgreSQL)** – tables (`author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`), views (`overdue_loans`, `member_loan_stats`, `popular_books`), functions (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`), trigger (`trg_loan_audit_fn`).  
  - **Shop (MySQL)** – tables (`product`, `customer_order`, `order_item`), procedure (`add_order_item`).  
  - **Warehouse (SQL Server)** – tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`), procedure (`dbo.usp_SyncStock`).  

#### Data flow  
1. **AI Generation**  
   - Maven plugin → `SrcMorphConfiguration` → `AiGenerationConfig`.  
   - `AiConditionEvaluator` filters files → `AiFactExtractor` builds fact blocks.  
   - Oversize strategy chosen → provider resolved by `AiGenerationProviderFactory`.  
   - Provider generates text → `AiCompletionParser` cleans → result returned.  
2. **Byte‑Stream**  
   - Input bytes → `histogram_partial` → `histogram_reduce` → 256‑bin histogram.  
   - `ringbuf_init` → `ringbuf_write`/`ringbuf_read` → status queries.  
   - Scripts consume or produce files (tarballs, CSV reports).  
3. **SQL Domain**  
   - **Library**: `fn_checkout_book` → loan insert, stock decrement, audit trigger.  
   - **Shop**: `add_order_item` locks product, updates stock, recalculates order total.  
   - **Warehouse**: `usp_SyncStock` merges supplier feed into stock levels, logs run.  

#### Dependencies  
- **Internal** – AI core shares `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, etc.; SQL routines reference cross‑package tables and views.  
- **External** – Lombok, Jackson, OpenCL 1.2, HTTP client for OpenAI, native llama.cpp library, PostgreSQL/MySQL/SQL Server system functions.  
- **Runtime** – environment variables for API keys, native libraries for llama.cpp, shell utilities (`tar`, `date`), Python stdlib.  

#### Cross‑cutting  
- **Immutability & Thread‑Safety** – stateless evaluators, extractors, and provider support; mutable configs are not thread‑safe.  
- **Validation & Error Handling** – `validate` throws `IllegalArgumentException`; parsing errors propagate `IOException`.  
- **Timing Metrics** – providers expose `AiGenerationTimings`; defaults are zeroed.  
- **Response Parsing** – `AiCompletionParser` standardizes output across providers.  
- **Ring Buffer** – power‑of‑two sizing, modulo arithmetic, single‑producer/single‑consumer semantics.  
- **Histogram Kernels** – atomic increments, local memory, barriers for race‑free aggregation.  
- **Audit & Concurrency** – triggers, row‑level locks, transactional MERGE, and audit logs across SQL packages.
