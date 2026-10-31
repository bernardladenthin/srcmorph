### ai
- H: 1.0
- C: 3BF5828F
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:13:14Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code transformations, backed by GPU‑accelerated utilities and a set of SQL packages for library, shop, and warehouse data management.

#### Purpose
- Central orchestration of AI‑based source‑code morphing.
- High‑performance GPU histogram and ring‑buffer utilities.
- Persistent business logic for library loans, shop orders, and warehouse inventory.

#### Responsibilities
- **AI Orchestration** – configuration, routing, fact extraction, oversize handling, and provider abstraction for local, cloud, and mock AI backends.
- **GPU & Buffer Utilities** – OpenCL histogram kernels, power‑of‑two ring buffer API, and command‑line backup/report scripts.
- **SQL Data Management** – stored procedures, triggers, and views for library lending, e‑commerce orders, and warehouse stock synchronization.

#### Key units
- **Java AI Core**  
  - `SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`.  
  - `AiGenerationProvider` interface with `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `MockAiGenerationProvider`.  
  - `AiCompletionParser`, `AiGenerationTimings`, `AiPromptSupport`.
- **C/OpenCL Utilities**  
  - `histogram_partial`, `histogram_reduce` kernels; `ringbuf_t` API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`).  
  - Scripts: `rotate_backups.sh`, `sales_report.py`.
- **SQL Packages**  
  - **Library**: tables `author`, `book`, `member`, `loan`, `book_copy`; functions `fn_checkout_book`, `fn_late_fee`; procedure `sp_return_book`; trigger `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`.  
  - **Shop**: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.  
  - **Warehouse**: tables `StockLevel`, `SupplierFeed`, `SyncLog`; procedure `usp_SyncStock`.

#### Data flow
1. **AI Pipeline** – `SrcMorphConfiguration` → `AiConditionEvaluator` → `AiFactExtractor` → `AiOversizeStrategy` → `AiGenerationProviderFactory` → chosen provider → `AiGenerationTimings` → cleaned output.  
2. **GPU Histogram** – `histogram_partial` builds per‑group partials → `histogram_reduce` aggregates into 256‑bin result.  
3. **Ring Buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → usage queries.  
4. **SQL Workflows** –  
   - Library: `fn_checkout_book` → `loan` insert → trigger audit → `sp_return_book`.  
   - Shop: `add_order_item` locks product → updates stock → recalculates order total.  
   - Warehouse: `usp_SyncStock` merges `SupplierFeed` into `StockLevel` → logs in `SyncLog`.

#### Dependencies
- **Internal** – AI core utilities (`AiPromptDefinition`, `AiModelDefinition`, etc.), OpenCL kernels, ring‑buffer API, SQL tables and procedures.  
- **External** – Lombok, Jackson, OpenCL 1.2, PostgreSQL/MySQL/SQL Server built‑ins, Bash, Python standard library.

#### Cross‑cutting
- **Immutability & Thread‑Safety** – stateless evaluators, immutable configs, lazy‑loaded native models.  
- **Error & Validation** – `IllegalArgumentException`, `IllegalStateException`, wrapped IO/JSON errors, explicit SQL error codes.  
- **Logging & Diagnostics** – Lombok `@ToString`, `AiGenerationTimings` token metrics, UTC timestamps in scripts and logs.  
- **Configuration Normalization** – null‑coercion, unmodifiable lists, default values.  
- **Concurrency Control** – `SELECT … FOR UPDATE`, single‑transaction merges, ring‑buffer head/tail bitwise wrapping.
