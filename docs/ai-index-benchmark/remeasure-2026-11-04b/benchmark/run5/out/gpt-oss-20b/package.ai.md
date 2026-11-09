### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:55:58Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified framework for AI‑driven source‑code transformation, GPU‑accelerated data processing, and domain‑specific SQL persistence, all orchestrated through configuration, validation, and modular providers.

#### Purpose
- Centralize AI‑based code morphing in Maven builds.
- Offer GPU‑accelerated histogram and ring‑buffer utilities for high‑throughput data streams.
- Persist and manage library, shop, and warehouse data with transactional logic and audit/reporting.

#### Responsibilities
- **AI Morphing** – configuration, file selection, fact extraction, oversize handling, provider resolution, and generation execution.
- **GPU & Buffering** – two‑pass OpenCL histogram kernels, lock‑free ring buffer API, and auxiliary backup/report scripts.
- **SQL Domain Logic** – cataloging, checkout, order processing, stock sync, audit trails, and reporting views.

#### Key units
- **Java**: `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `AiGenerationProvider` (and concrete `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `MockAiGenerationProvider`), `AiCompletionParser`, `LlamaCppJniConfig`.
- **C/OpenCL**: `histogram_partial`, `histogram_reduce`, `ringbuf_t` API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`).
- **Shell/Python**: `rotate_backups.sh`, `sales_report.py` (`parse_orders`, `revenue_by_month_and_region`).
- **SQL**: Library tables (`author`, `book`, `member`, `loan`, `book_copy`, `loan_audit`), functions (`fn_checkout_book`, `fn_late_fee`), procedures (`sp_return_book`), views (`overdue_loans`, `member_loan_stats`, `popular_books`); Shop tables (`product`, `customer_order`, `order_item`), procedure `add_order_item`; Warehouse tables (`StockLevel`, `SupplierFeed`, `SyncLog`), procedure `usp_SyncStock`.

#### Data flow
1. **AI Pipeline** – load `SrcMorphConfiguration` → evaluate `AiCondition` against `AiFileContext` → extract facts with `AiFactExtractor` → apply `AiOversizeStrategy` → create provider via `AiGenerationProviderFactory` → generate text, return `String` or `AiGenerationTimings`.
2. **Histogram** – input byte array → `histogram_partial` → per‑group bins → `histogram_reduce` → 256‑bin result.
3. **Ring Buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → state via `ringbuf_used`/`ringbuf_free_space`.
4. **SQL Workflows** – Library checkout/return procedures update copies, loans, and audit; Shop `add_order_item` locks product, updates stock, recalculates totals; Warehouse `usp_SyncStock` merges supplier feed into stock levels and logs run.

#### Dependencies
- **Internal** – AI: `AiCondition`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`; C/OpenCL: ring‑buffer structs; SQL: cross‑package tables and functions.
- **External** – Java: Jackson, Lombok, `java.time`, `java.util.regex`; C/OpenCL: OpenCL runtime; Python: standard library; SQL: PostgreSQL/MySQL/SQL Server built‑ins and features.

#### Cross-cutting
- **Immutability & Thread‑Safety** – `LlamaCppJniConfig`, provider implementations, and `AiOversizeStrategy` are immutable; evaluator and extractor are stateless.
- **Validation & Error Handling** – `AiConditionEvaluator.validate`, `AiFactExtractor.validate` enforce config correctness; providers throw `IllegalArgumentException` on misconfig, `IOException` on malformed responses.
- **Performance Metrics** – Providers expose `generateWithTimings` returning `AiGenerationTimings` (throughput, token rates).
- **Configuration Knobs** – 37 llama.cpp parameters in `LlamaCppJniConfig`; ring buffer uses power‑of‑two sizing; histogram kernels use two‑pass aggregation.
- **Audit & Logging** – Library `loan_audit`, Warehouse `SyncLog`; consistent error codes across C, Python, and shell utilities.
