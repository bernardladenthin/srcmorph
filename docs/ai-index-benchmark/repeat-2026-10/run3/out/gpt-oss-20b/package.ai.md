### ai
- H: 1.0
- C: 973AD197
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:59:24Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A Java‑centric AI orchestration library that transforms source code by evaluating conditions, extracting facts, and generating text with pluggable local or remote AI back‑ends, complemented by a lightweight C/CL and Python toolkit for byte‑stream processing and a set of SQL modules that enforce business rules for libraries, shops, and warehouses.

#### Purpose
- Orchestrate AI‑driven source‑code transformations.
- Provide GPU‑accelerated byte histograms, lock‑free ring buffers, and data‑management scripts.
- Persist and enforce domain rules for library, shop, and warehouse data.

#### Responsibilities
- **AI Orchestration** – configuration, condition evaluation, fact extraction, oversize handling, provider selection, and completion parsing.
- **Byte‑Stream Toolkit** – GPU histogram kernels, lock‑free ring buffer API, backup rotation, and sales reporting.
- **Database Layer** – stored procedures, triggers, and views for library cataloging, shop order processing, and warehouse inventory sync.

#### Key units
- **Java**  
  - `SrcMorphConfiguration`, `AiGenerationConfig` – mutable beans for settings.  
  - `AiConditionEvaluator`, `AiConditionGroup` – tree validator.  
  - `AiFactExtractor`, `AiFactCounter` – deterministic fact block builder.  
  - `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).  
  - `AiGenerationProvider`, `AiGenerationProviderFactory` – provider abstraction.  
  - `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig` – local llama.cpp implementation.  
  - `OpenAiCompatibleProviderSupport` – OpenAI‑compatible request helper.  
  - `AiCompletionParser` – strips internal thinking blocks.  
- **C / OpenCL**  
  - `histogram_partial`, `histogram_reduce` – OpenCL kernels for 256‑bin histograms.  
  - `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – lock‑free ring buffer API.  
- **Python**  
  - `sales_report.py` – `Order` dataclass, CSV parsing, monthly revenue aggregation.  
- **SQL**  
  - **Library** – tables (`author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`), functions (`fn_checkout_book`, `fn_late_fee`), procedures (`sp_return_book`), triggers (`trg_loan_audit`), views (`overdue_loans`, `member_loan_stats`, `popular_books`).  
  - **Shop** – tables (`product`, `customer_order`, `order_item`), procedure (`add_order_item`).  
  - **Warehouse** – tables (`StockLevel`, `SupplierFeed`, `SyncLog`), procedure (`usp_SyncStock`).

#### Data flow
1. **AI Pipeline** – `SrcMorphConfiguration` → `AiConditionEvaluator` → `AiFactExtractor` → `AiGenerationProviderFactory` → chosen `AiGenerationProvider` → `AiCompletionParser` → final transformed code.  
2. **Histogram** – byte stream → `histogram_partial` → partial buffer → `histogram_reduce` → 256‑bin result.  
3. **Ring Buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → state queries.  
4. **Sales Report** – CSV → `parse_orders` → `revenue_by_month_and_region` → CLI output.  
5. **Library** – `fn_checkout_book` → loan insert, copy count update, audit trigger.  
6. **Shop** – `add_order_item` → stock validation, item insert/update, order total recompute.  
7. **Warehouse** – `usp_SyncStock` → merge supplier feed into `StockLevel`, log changes.

#### Dependencies
- **Internal** – Java packages `net.ladenthin.srcmorph.*` (config, prompt, support), C headers (`stdlib.h`, `string.h`, `stddef.h`, `stdint.h`), OpenCL 1.2, Bash utilities, Python stdlib.  
- **External** – Jackson, Lombok, `org.jspecify.annotations`, `net.ladenthin.llama` JNI bindings, PostgreSQL/SQL Server system functions, transaction control.  
- **Provider‑specific** – `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`.

#### Cross‑cutting
- Stateless, thread‑safe evaluators and factories; mutable configuration beans.  
- Validation of regex patterns and non‑null labels; misconfigurations throw `IllegalArgumentException`.  
- Immutable enums and config holders (`AiOversizeStrategy`, `LlamaCppJniConfig`).  
- Timing metrics (`AiGenerationTimings`) for prompt/cache token counts and throughput.  
- Ring buffer uses power‑of‑two sizing, modulo arithmetic, and atomic head/tail updates.  
- GPU kernels employ local memory and atomic increments to reduce global traffic.  
- SQL procedures enforce referential integrity, use transactions (`TRY…CATCH`, `ROLLBACK`), and maintain audit tables with UTC timestamps.  
- Backup and reporting scripts use strict error handling (`set -euo pipefail`, Python `ValueError` propagation).
