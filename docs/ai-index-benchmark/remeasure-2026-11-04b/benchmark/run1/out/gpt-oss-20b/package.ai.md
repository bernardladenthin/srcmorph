### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:55:57Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified AI‑driven source‑code transformation engine with GPU‑accelerated utilities and a robust SQL persistence layer for library, shop, and warehouse domains.

#### Purpose
- Orchestrate AI‑based code analysis, generation, and provider abstraction for Maven projects.
- Offer high‑performance byte‑histogram and ring‑buffer utilities for data processing.
- Persist and enforce business rules for library lending, e‑commerce orders, and warehouse inventory with audit and reporting.

#### Responsibilities
- **AI Engine** – configuration, rule evaluation, provider abstraction, request/response handling, local model orchestration, utilities.
- **GPU & Buffer Utilities** – OpenCL byte‑histogram, lock‑free ring buffer, backup rotation, sales reporting.
- **SQL Domain Logic** – library loan lifecycle, shop order processing, warehouse stock sync, audit trails, reporting views.

#### Key units
- **Java**  
  - `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiOversizeStrategy`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`, `AiGenerationTimings`.
- **Other (C/CLI/Python)**  
  - OpenCL kernels `histogram_partial`, `histogram_reduce`; ring buffer API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`); scripts `rotate_backups.sh`, `sales_report.py`.
- **SQL**  
  - Library tables (`author`, `book`, `member`, `loan`, `loan_audit`, `book_copy`), procedures (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`), trigger `trg_loan_audit`, views (`overdue_loans`, `popular_books`).  
  - Shop tables (`product`, `customer_order`, `order_item`), procedure `add_order_item`.  
  - Warehouse tables (`StockLevel`, `SupplierFeed`, `SyncLog`), procedure `usp_SyncStock`.

#### Data flow
1. **AI Engine**  
   - Load `SrcMorphConfiguration` → evaluate `AiCondition` trees → apply `AiOversizeStrategy` → extract facts → resolve provider via `AiGenerationProviderFactory` → generate text → parse and clean response → return `AiGenerationTimings`.
2. **GPU Utilities**  
   - Input byte stream → `histogram_partial` → `histogram_reduce` → 256‑bin histogram.  
   - Ring buffer: init → write → read → status queries.  
   - Scripts: archive directories → rotate backups; parse CSV → aggregate revenue → report.
3. **SQL Layer**  
   - Library: checkout → insert loan → audit trigger; return → update loan → audit trigger; late fee calculation; reporting views.  
   - Shop: add order item → lock product → update stock & order total.  
   - Warehouse: sync stock → aggregate feed → merge into `StockLevel` → log sync.

#### Dependencies
- **Java** – Lombok, Jackson, OpenCL JNI (`net.ladenthin.llama`), Java 8 utilities, regex, annotations.
- **C/CLI** – OpenCL runtime, standard C libraries, shell utilities (`tar`, `date`, etc.).
- **Python** – argparse, csv, collections, dataclasses, datetime, decimal.
- **SQL** – PostgreSQL/MySQL/SQL Server built‑ins, PL/pgSQL, InnoDB, system functions.

#### Cross-cutting
- Immutable configuration objects, defensive copying, stateless utilities, centralized error handling (`IllegalArgumentException`, `IOException`), unified timing metrics (`AiGenerationTimings`).
- Thread‑safe GPU kernels (atomic increments, barriers), lock‑free ring buffer (single‑producer/single‑consumer).
- Audit trails (`loan_audit`, `SyncLog`), concurrency controls (`SELECT … FOR UPDATE`, transaction blocks), constraints and indexes for data integrity and performance.
