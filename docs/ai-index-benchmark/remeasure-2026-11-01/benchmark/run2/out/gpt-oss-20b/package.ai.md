### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:48:50Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework for AI‑driven source‑code transformation, lightweight data‑stream utilities, and database schemas that support library, e‑commerce, and warehouse operations.

#### Purpose
- Central AI configuration, validation, and execution engine for code morphing.
- Provide lock‑free ring buffer and OpenCL histogram utilities for high‑performance data streams.
- Persist and enforce business rules for library loans, shop orders, and warehouse inventory.

#### Responsibilities
- **AI Engine** – configuration aggregation, rule validation, fact extraction, oversize handling, and provider orchestration.
- **Data‑Stream Utilities** – power‑of‑two ring buffer API, two‑pass OpenCL histogram kernel, backup rotation script, sales reporting script.
- **Database Layer** – schemas, views, functions, and procedures for library, shop, and warehouse domains.

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – runtime AI settings bean.  
  - `AiConditionEvaluator` – stateless validator/evaluator for rule trees.  
  - `AiFactExtractor` – deterministic facts header generator.  
  - `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).  
  - `AiGenerationProvider` interface & `AiGenerationProviderFactory`.  
  - `LlamaCppJniAiGenerationProvider` – local llama.cpp inference.  
  - `OpenAiCompatibleProviderSupport` – remote API request builder.  
  - `AiCompletionParser` – strips reasoning blocks from LLM output.
- **C / OpenCL**  
  - `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.  
  - OpenCL kernels `histogram_partial`, `histogram_reduce` (256‑bin).  
- **Python / Shell**  
  - `rotate_backups.sh` – backup rotation utility.  
  - `sales_report.py` – CSV parsing, monthly revenue aggregation.  
- **SQL**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`, `product`, `customer_order`, `order_item`, `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`, `add_order_item`, `dbo.usp_SyncStock`.

#### Data flow
1. **AI Pipeline**  
   - `SrcMorphConfiguration` → `AiConditionEvaluator` (filter files) → `AiFactExtractor` (oversized facts) → `AiGenerationProviderFactory` → chosen `AiGenerationProvider` (local or remote) → `AiCompletionParser` → `AiGenerationTimings` returned for transformation.  
2. **Ring Buffer** – `ringbuf_init` → producer `ringbuf_write` → consumer `ringbuf_read` → status queries.  
3. **Histogram** – `histogram_partial` (per‑group) → `histogram_reduce` (global 256‑bin).  
4. **Scripts** – `rotate_backups.sh` archives and cleans old backups; `sales_report.py` parses CSV → aggregates → TSV output.  
5. **Database** –  
   - Library: `fn_checkout_book` inserts loan, updates copy, logs audit; `sp_return_book` updates loan, restores copy, logs audit.  
   - Shop: `add_order_item` validates stock, inserts/updates line item, decrements stock, recalculates total.  
   - Warehouse: `dbo.usp_SyncStock` merges `SupplierFeed` into `StockLevel`, logs in `SyncLog`.

#### Dependencies
- **Internal** – Java AI classes (`AiPromptSupport`, `AiGenerationRequest`, `ChatResponseParser`, etc.), C ring buffer headers, OpenCL built‑ins, SQL cross‑references.  
- **External** – Lombok, JSPECIFY, Jackson, llama.cpp JNI, OpenAI‑compatible APIs, standard C/OpenCL libraries, shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`), Python `argparse`, `csv`, `datetime`, `decimal`.

#### Cross-cutting
- **Thread safety** – stateless evaluators, immutable configs, lock‑free ring buffer.  
- **Validation & Error handling** – configuration checks, regex validation, `IllegalArgumentException`, `IOException`.  
- **Metrics** – `AiGenerationTimings` captures text, token counts, throughput; providers expose timing APIs.  
- **Prompt templating** – `AiPromptSupport` supplies system/user messages.  
- **Logging** – local provider logs truncation and cache reuse; remote provider validates loopback key usage.  
- **Integrity & Concurrency** – foreign keys, check constraints, `SELECT … FOR UPDATE`, transactional MERGE, trigger‑based audit logging.  
- **Performance** – ring buffer power‑of‑two sizing, OpenCL two‑pass reduction, database indexes on key columns.
