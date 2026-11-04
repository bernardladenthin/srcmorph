### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:35:13Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A framework for orchestrating AI‑driven source‑code transformations, coupled with a lightweight byte‑stream toolkit and a multi‑tenant SQL data platform for library, shop, and warehouse domains.

#### Purpose
- Centralize AI‑powered code generation workflows and provider abstraction.
- Offer high‑performance byte‑level utilities (GPU histograms, lock‑free ring buffer, backup/report scripts).
- Persist and enforce business rules across library, shop, and warehouse domains with reporting and audit.

#### Responsibilities
- **AI Generation Engine** – configuration, rule evaluation, fact extraction, oversize handling, provider selection, request/response parsing, performance metrics.
- **Byte‑Stream Toolkit** – GPU histogram kernels, lock‑free ring buffer API, backup rotation, revenue reporting scripts.
- **SQL Data Platform** – library catalog and loan management, shop order lifecycle, warehouse inventory sync, audit trails, reporting views.

#### Key units
- **Java AI Core**  
  - `SrcMorphConfiguration` (mutable config bean)  
  - `AiGenerationConfig` (immutable provider settings)  
  - `AiOversizeStrategy` (enum)  
  - `AiConditionEvaluator` (stateless validator)  
  - `AiFactExtractor` (regex‑based facts block)  
  - `AiGenerationProviderFactory` (factory)  
  - `LlamaCppJniAiGenerationProvider` (local llama.cpp JNI)  
  - `OpenAiCompatibleProviderSupport` (HTTP client)  
  - `AiCompletionParser` (reasoning block stripper)  
  - `AiGenerationTimings` (latency metrics)
- **Other Toolkit**  
  - OpenCL kernels: `histogram_partial`, `histogram_reduce`  
  - Ring buffer API: `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, etc.  
  - Scripts: `rotate_backups.sh`, `sales_report.py`
- **SQL Platform**  
  - Library: tables (`author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`), functions (`fn_checkout_book`, `fn_late_fee`), procedure (`sp_return_book`), trigger (`trg_loan_audit`), views (`overdue_loans`, `member_loan_stats`, `popular_books`)  
  - Shop: tables (`product`, `customer_order`, `order_item`), procedure (`add_order_item`)  
  - Warehouse: tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`), procedure (`dbo.usp_SyncStock`)

#### Data flow
- **AI Engine**: configuration → rule validation → fact header → oversize decision → provider selection → request construction → local/remote generation → post‑processing → final code output.  
- **Toolkit**: GPU histogram kernels process data → ring buffer streams bytes → backup script rotates archives → sales script aggregates CSV revenue.  
- **SQL Platform**:  
  - Library: checkout → loan insert + audit → return → audit; late fee calculation on demand.  
  - Shop: add order item locks product, updates stock, recalculates order total.  
  - Warehouse: sync procedure merges supplier feed into stock levels, logs sync results.

#### Dependencies
- **Internal**: AI core depends on `AiCondition`, `AiFileContext`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationRequest`.  
- **External**: Java standard libs, Lombok, Jackson, OpenAI/llama.cpp libraries, OpenCL, C stdlib, shell utilities, Python modules.  
- **SQL**: PostgreSQL/MySQL/SQL Server built‑ins, row‑locking, system functions, indexes for performance.

#### Cross‑cutting
- Thread‑safe, stateless components (`AiConditionEvaluator`, `AiFactExtractor`, `AiCompletionParser`, immutable configs).  
- Validation and error handling via `IllegalArgumentException`, `IllegalStateException`, `IOException`.  
- Performance metrics captured in `AiGenerationTimings`.  
- Ring buffer uses power‑of‑two sizing and bitwise masking; GPU kernels use atomic increments.  
- Audit and transaction consistency across library, shop, and warehouse procedures.  
- Consistent error codes and exit conventions in scripts.
