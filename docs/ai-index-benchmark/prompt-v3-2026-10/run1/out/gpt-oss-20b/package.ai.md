### ai
- H: 1.0
- C: AB388750
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:56:45Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework that orchestrates AI‑driven source‑code generation, high‑performance byte‑stream utilities, and database‑backed catalog, shop, and warehouse logic.

#### Purpose
- Centralize AI generation configuration, execution, and provider abstraction for srcmorph.
- Offer GPU‑accelerated histogram kernels and a lock‑free ring buffer for byte‑stream processing.
- Persist and manipulate library, e‑commerce, and warehouse data with business logic and reporting.

#### Responsibilities
- **AI Generation** – configuration loading, file‑selection evaluation, deterministic fact extraction, provider resolution, response parsing, and runtime orchestration.
- **Byte‑Stream Utilities** – OpenCL histogram kernels, power‑of‑two ring buffer API, backup rotation script, and sales aggregation tool.
- **Database Logic** – catalog, loan, order, and inventory management with stored procedures, triggers, and views across PostgreSQL, MySQL, and SQL Server.

#### Key units
- **Java AI**: `SrcMorphConfiguration`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`, `AiFactExtractor`.
- **Other Utilities**: `histogram_partial`, `histogram_reduce`, `ringbuf_t`, `ringbuf_write`, `ringbuf_read`, `rotate_backups.sh`, `sales_report.py`.
- **SQL Schemas**: `author`, `book`, `loan`, `product`, `customer_order`, `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedures `fn_checkout_book`, `add_order_item`, `dbo.usp_SyncStock`; triggers `trg_loan_audit`.

#### Data flow
1. **AI Pipeline**: `SrcMorphConfiguration` → `AiConditionEvaluator` → `AiFactExtractor` → `AiGenerationProviderFactory` → chosen provider → `AiCompletionParser` → output text + `AiGenerationTimings`.
2. **Histogram**: `histogram_partial` → partial bins → `histogram_reduce` → final 256‑bin histogram.
3. **Ring Buffer**: `ringbuf_init` → `ringbuf_write`/`ringbuf_read` → occupancy via `ringbuf_used`/`ringbuf_free_space`.
4. **Database**: `fn_checkout_book`/`sp_return_book` → loan tables + audit; `add_order_item` → product lock, stock update, order total; `dbo.usp_SyncStock` → feed aggregation → `StockLevel` + `SyncLog`.

#### Dependencies
- **Java**: `net.ladenthin.llama` (JNI), `com.fasterxml.jackson`, `org.jspecify`, `lombok`; HTTP client for OpenAI‑compatible calls.
- **Other**: OpenCL runtime, C standard library, Bash utilities (`tar`, `date`), Python modules (`argparse`, `csv`, `decimal`).
- **SQL**: PostgreSQL, MySQL, SQL Server built‑ins; foreign keys, indexes, triggers, and stored procedures across schemas.

#### Cross‑cutting
- Thread safety: immutable configs (`LlamaCppJniConfig`), stateless parsers, lock‑free ring buffer, atomic OpenCL operations.
- Error handling: centralized validation in `AiConditionEvaluator`, provider lifecycle `close()`, ring buffer status enums, SQL transaction blocks.
- Performance monitoring: `AiGenerationTimings` returned by all providers; OpenCL kernels use atomic increments; ring buffer uses modulo arithmetic.
- Configuration consistency: power‑of‑two sizing, command‑line flags for utilities, JSON/YAML/Maven for AI settings.
