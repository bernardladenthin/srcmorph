### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T12:50:13Z
- T: 2026-11-04T12:53:15Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework for AI‑driven code generation, data‑processing utilities, and domain‑specific database logic, all orchestrated to support Maven/CLI workflows, GPU‑accelerated analytics, and robust persistence across library, shop, and warehouse systems.  

#### Purpose
- Centralize AI‑generation configuration and execution for Maven/CLI workflows.  
- Provide efficient byte‑level utilities (histograms, ring buffers) and helper scripts.  
- Persist and enforce business rules for library loans, shop orders, and warehouse inventory.  

#### Responsibilities
- **AI Generation** – configuration, fact extraction, provider abstraction, local and cloud inference, response parsing.  
- **Data Utilities** – GPU histogram kernels, lock‑free ring buffer, backup/reporting scripts.  
- **Database Modules** – library cataloging & auditing, shop order processing, warehouse stock synchronization.  

#### Key units
- **Java AI** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`.  
- **Other Utilities** – `histogram_partial`, `histogram_reduce`, `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `rotate_backups.sh`, `sales_report.py`.  
- **SQL Modules** – Library tables (`author`, `book`, `member`, `loan`, `loan_audit`) and functions (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`); Shop tables (`product`, `customer_order`, `order_item`) and procedure (`add_order_item`); Warehouse tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`) and procedure (`dbo.usp_SyncStock`).  

#### Data flow
1. **AI Pipeline** – `SrcMorphConfiguration` → `AiConditionEvaluator` selects files → `AiFactExtractor` builds facts → oversize strategy applied → prompt assembled → `AiGenerationProviderFactory` selects provider → provider generates → `AiCompletionParser` cleans response → metrics recorded.  
2. **Utility Flow** – Host data → GPU kernels (`histogram_partial` → `histogram_reduce`) → final histogram; ring buffer: init → write → read → metrics.  
3. **Database Flow** – Library: checkout → update book copies → insert loan → audit; return → update loan → increment copies → audit.  
   Shop: add order item → lock product → adjust stock → recalc order total.  
   Warehouse: ingest supplier feed → `MERGE` into `StockLevel` → log in `SyncLog`.  

#### Dependencies
- **Internal** – AI: `AiCondition`, `AiFileContext`, `AiPromptSupport`, `MockAiGenerationProvider`; Utilities: ring buffer headers, OpenCL built‑ins; SQL: schema‑specific tables and functions.  
- **External** – Java: `net.ladenthin.llama`, Jackson, Lombok, JSPECIFY; C: OpenCL 1.2, standard headers; Python: argparse, csv, collections; SQL: PostgreSQL, MySQL 8, SQL Server.  

#### Cross‑cutting
- **Immutability & Thread Safety** – `LlamaCppJniConfig`, provider factories, and ring buffer designed for lock‑free or stateless use.  
- **Error Handling** – AI parsers throw IOExceptions; provider support validates URLs and keys; ring buffer returns status codes; SQL functions raise explicit errors for business rule violations.  
- **Metrics & Logging** – AI timings (`AiGenerationTimings`), cache reuse logs, and database audit tables capture state changes.  
- **Configuration Knobs** – `LlamaCppJniConfig` exposes 37 tuning parameters; AI fact extraction uses regex counters; SQL modules enforce constraints and indexes for performance.  
- **Time Handling** – UTC timestamps used in scripts and SQL logs for consistency.
