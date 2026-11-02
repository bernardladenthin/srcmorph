### ai
- H: 1.0
- C: 321EBA94
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:23:17Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework for configuring, evaluating, and executing AI‑driven source‑code generation and summarisation, complemented by low‑level byte‑stream utilities and a comprehensive SQL data model for library, shop, and warehouse operations.

#### Purpose
- Central AI orchestration for srcmorph code‑generation workflows.
- Efficient, thread‑safe byte‑stream primitives for GPU and CPU use.
- Persistent data model and business logic for library lending, e‑commerce, and warehouse stock.

#### Responsibilities
- **AI Orchestration** – configuration, condition evaluation, fact extraction, oversize handling, provider abstraction, prompt building, response parsing, and timing metrics.
- **Byte‑Stream Toolkit** – GPU‑accelerated histograms, lock‑free ring buffers, and auxiliary backup/reporting scripts.
- **Data Persistence** – schemas, views, functions, procedures, and triggers for library, shop, and warehouse domains.

#### Key units
- **AI**: `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`, `AiGenerationTimings`.
- **Byte‑Stream**: `histogram_partial`, `histogram_reduce`, `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free`, `rotate_backups.sh`, `sales_report.py`.
- **SQL**: Tables (`author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`), views (`overdue_loans`, `member_loan_stats`, `popular_books`), functions/procedures (`fn_checkout_book`, `fn_late_fee`, `sp_return_book`, `add_order_item`, `usp_SyncStock`), triggers (`trg_loan_audit`), indexes (`idx_popular_books_id`, `idx_book_copy_book`).

#### Data flow
1. **AI Pipeline**  
   - Load `SrcMorphConfiguration`.  
   - `AiConditionEvaluator` routes files.  
   - `AiFactExtractor` builds deterministic fact blocks.  
   - Oversize strategy decides handling.  
   - `AiGenerationProviderFactory` selects provider.  
   - Provider assembles prompt via `AiPromptSupport`.  
   - Local provider calls `LlamaModel.chatComplete`; remote provider posts to OpenAI‑compatible endpoint.  
   - `ChatResponseParser` + `AiCompletionParser` clean output.  
   - `AiGenerationTimings` records token counts and decode rates.

2. **Byte‑Stream Utilities**  
   - `histogram_partial` → `histogram_reduce` → final 256‑bin histogram.  
   - `ringbuf_init` → `ringbuf_write`/`ringbuf_read` → `ringbuf_used`/`ringbuf_free_space`.  
   - `rotate_backups.sh` archives directories; `sales_report.py` parses CSVs → revenue reports.

3. **SQL Operations**  
   - Library: `fn_checkout_book` → loan creation, status update, audit log; `sp_return_book` → status revert, audit log; `fn_late_fee` → fee calculation.  
   - Shop: `add_order_item` locks product, updates stock, recalculates total.  
   - Warehouse: `usp_SyncStock` aggregates supplier feed, merges into `StockLevel`, logs run.

#### Dependencies
- **Internal**: AI condition trees, fact counters, prompt definitions, model configs, calibration, ring‑buffer status enums, OpenCL kernels.  
- **External**: Java standard libs, Lombok, Jackson, OpenAI‑compatible HTTP, llama.cpp JNI, PostgreSQL/MySQL/SQL Server built‑ins, Bash utilities, Python stdlib.  
- **Remote**: OpenAI‑compatible endpoints, environment‑based API keys.

#### Cross-cutting
- **Thread‑Safety**: AI evaluators, extractors, and provider helpers are stateless/immutable; ring buffer follows single‑producer/single‑consumer lock‑free pattern.  
- **Validation & Error Handling**: Early checks in evaluators and extractors; `IllegalArgumentException`, `IllegalStateException`, `IOException` for config, tree, and communication errors.  
- **Immutability**: `AiOversizeStrategy` enum, `LlamaCppJniConfig`.  
- **Metrics**: Providers expose `generateWithTimings` for token counts and decode rates.  
- **Resource Cleanup**: `LlamaCppJniAiGenerationProvider` implements `AutoCloseable`.  
- **Atomicity**: OpenCL kernels use atomic increments and barriers; ring buffer relies on modulo arithmetic for wrap‑around.  
- **Audit & Logging**: `loan_audit` and `SyncLog` tables capture all state changes.  
- **Transactional Integrity**: All critical SQL operations run in single transactions with row‑level locking.
