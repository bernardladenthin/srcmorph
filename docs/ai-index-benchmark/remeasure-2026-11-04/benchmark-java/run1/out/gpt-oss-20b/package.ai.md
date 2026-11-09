### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:19:33Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework that orchestrates AI‑driven source‑code transformations, provides low‑level GPU utilities, and supplies a multi‑domain SQL persistence layer for cataloging, e‑commerce, and inventory management.  

#### Purpose
- Central AI orchestration for source‑code generation and routing.  
- GPU‑accelerated byte‑level histogram and lock‑free ring buffer utilities.  
- SQL data layer for library, shop, and warehouse domains.  

#### Responsibilities
- **AI Engine** – configuration, validation, provider selection, prompt construction, response parsing, and timing.  
- **GPU & Buffer** – compute 256‑bin histograms, manage a single‑producer/single‑consumer ring buffer.  
- **SQL Layer** – enforce business rules, audit trails, and reporting for library lending, online orders, and warehouse stock.  

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – runtime settings bean.  
  - `AiGenerationConfig` – AI provider options.  
  - `AiConditionEvaluator` – stateless tree validator.  
  - `AiFactExtractor` – regex‑based fact counter.  
  - `AiOversizeStrategy` – enum for context‑limit handling.  
  - `AiGenerationProviderFactory` – resolves provider keys.  
  - `AiGenerationProvider` interface (`generate`, `generateWithTimings`, `close`).  
  - `MockAiGenerationProvider` – test stub.  
  - `LlamaCppJniAiGenerationProvider` – local llama.cpp inference.  
  - `OpenAiCompatibleProviderSupport` – request builder & parser.  
  - `AiCompletionParser` – strips reasoning blocks.  
  - `LlamaCppJniConfig` – immutable llama.cpp parameters.  
- **Other (C/OpenCL)**  
  - `histogram_partial`, `histogram_reduce` – OpenCL kernels for 256‑bin histograms.  
  - `ringbuf_t`, `ringbuf_status_t` – core buffer struct & status.  
  - `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – API.  
  - `rotate_backups.sh` – backup rotation script.  
  - `sales_report.py` – CSV sales aggregation.  
- **SQL**  
  - **Library** – tables `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; functions `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit_fn`; views `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - **Shop** – tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.  
  - **Warehouse** – tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.  

#### Data flow
1. **AI Pipeline** – configuration → file selection via `AiConditionEvaluator` → oversize handling → fact block prep → provider selection → prompt construction → generation → post‑processing.  
2. **Histogram** – byte stream → `histogram_partial` → `histogram_reduce` → 256‑bin array.  
3. **Ring Buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → status checks.  
4. **SQL Operations** –  
   - Library: checkout → `fn_checkout_book` → audit; return → `sp_return_book`.  
   - Shop: `add_order_item` locks product, updates stock, recalculates order total.  
   - Warehouse: `usp_SyncStock` merges feed into `StockLevel`, logs in `SyncLog`.  

#### Dependencies
- **Internal** – AI domain types (`AiCondition`, `AiPromptDefinition`, etc.), ring buffer structs, OpenCL kernels.  
- **External** – Java standard libraries, Lombok, Jackson, llama.cpp JNI, OpenAI API, PostgreSQL/MySQL/SQL Server built‑ins, OpenCL runtime, shell utilities, Python stdlib.  

#### Cross‑cutting
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).  
- Immutable configuration objects (`LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`).  
- Unified error handling: `IllegalArgumentException` for config issues, `IOException` for I/O/response errors.  
- Timing metrics via `AiGenerationTimings`.  
- Resource cleanup: `AiGenerationProvider` implements `AutoCloseable`.  
- Ring buffer lock‑free design with power‑of‑two sizing.  
- Consistent status codes in C utilities and non‑zero exit codes in scripts.  
- SQL integrity: primary keys, foreign keys, unique constraints, audit triggers, and transactional procedures.
