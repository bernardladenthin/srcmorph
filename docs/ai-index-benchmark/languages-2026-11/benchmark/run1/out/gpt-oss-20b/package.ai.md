### ai
- H: 1.0
- C: 245F2262
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:22:28Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A modular AI‑powered source‑code transformation framework with pluggable local/remote inference, supporting configuration, file selection, fact extraction, and post‑processing, complemented by low‑level utilities and a multi‑domain SQL schema for catalog, inventory, and order management.  

#### Purpose
- Centralize AI‑generation settings and file‑selection logic for source‑code morphing.  
- Provide a unified, pluggable interface for generating AI text via local llama.cpp, OpenAI‑compatible, or mock providers.  
- Offer reusable low‑level utilities (ring buffer, GPU histogram, backup/report scripts).  
- Persist and manipulate catalog, loan, order, and inventory data across library, shop, and warehouse domains.

#### Responsibilities
- **AI Configuration & Execution** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiGenerationProviderFactory`.  
- **File Selection & Fact Extraction** – `AiConditionEvaluator`, `AiFactExtractor`, `AiPromptSupport`.  
- **Provider Abstraction** – `AiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`.  
- **Post‑processing & Metrics** – `AiCompletionParser`, `AiGenerationTimings`.  
- **Low‑level Utilities** – ring buffer API, OpenCL histogram kernels, backup rotation script, sales report generator.  
- **Database Layer** – SQL schemas, stored procedures, triggers, and views for library, shop, and warehouse operations.

#### Key units
- **Java**  
  - `SrcMorphConfiguration` – mutable bean for all srcmorph settings.  
  - `AiConditionEvaluator` – stateless validator/evaluator for file inclusion.  
  - `AiFactExtractor` – regex‑based fact counter.  
  - `AiGenerationProviderFactory` – resolves provider key to concrete provider.  
  - `LlamaCppJniAiGenerationProvider` – local inference via GGUF model.  
  - `OpenAiCompatibleProviderSupport` – remote request builder and response parser.  
  - `AiCompletionParser` – removes internal thinking blocks.  
- **C / OpenCL**  
  - `ringbuf_t` API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`, etc.).  
  - `histogram_partial`, `histogram_reduce` kernels for 256‑bin byte histograms.  
- **Shell / Python**  
  - `rotate_backups.sh` – timestamped archive creation and retention.  
  - `sales_report.py` – CSV parsing and revenue aggregation.  
- **SQL**  
  - Library tables (`author`, `book`, `member`, `loan`, `book_copy`, `loan_audit`) and functions (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`).  
  - Shop tables (`product`, `customer_order`, `order_item`) and procedure `add_order_item`.  
  - Warehouse tables (`StockLevel`, `SupplierFeed`, `SyncLog`) and procedure `usp_SyncStock`.  
  - Views (`overdue_loans`, `member_loan_stats`, `popular_books`, etc.) for reporting.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.  
2. **File Selection** – `AiConditionEvaluator` processes `AiCondition` trees against each file’s `AiFileContext`.  
3. **Fact Block** – `AiFactExtractor.factsBlock` generates a facts section for included files.  
4. **Prompt Construction** – `AiPromptSupport` supplies the prompt template.  
5. **Provider Selection** – `AiGenerationProviderFactory` creates the appropriate provider.  
6. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider` runs chat completion, parses JSON, extracts text and timing.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds HTTP request, sends it, parses `ChatResponse`, extracts text and usage.  
7. **Post‑processing** – `AiCompletionParser` strips internal thinking blocks.  
8. **Metrics** – `AiGenerationTimings` returns token counts and rates.  
9. **Low‑level utilities** – ring buffer handles byte streams; OpenCL histogram processes data; backup script archives directories; sales script aggregates CSV orders.  
10. **Database operations** – stored procedures (`fn_checkout_book`, `add_order_item`, `usp_SyncStock`) manipulate data, enforce constraints, and log audit events.

#### Dependencies
- **Internal** – AI domain types (`AiCondition`, `AiFileContext`, `AiPromptDefinition`, etc.), Java utilities (`java.time`, `java.util.regex`), Lombok, Jackson, HTTP client.  
- **External** – `net.ladenthin.llama` for local inference, OpenCL 1.2 runtime, Bash utilities (`tar`, `date`), Python standard library (`csv`, `datetime`).  
- **SQL** – PostgreSQL/MySQL/SQL Server built‑ins, foreign keys across library, shop, and warehouse schemas.

#### Cross‑cutting
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`, `AiCompletionParser`).  
- Immutable configuration objects (`LlamaCppJniConfig`, `AiOversizeStrategy`).  
- Consistent error handling: `IllegalArgumentException` for misconfigurations, `IOException` for I/O or HTTP failures.  
- Uniform timing metrics via `AiGenerationTimings`.  
- Ring buffer enforces power‑of‑two capacity and modulo wrap‑around.  
- OpenCL histogram uses two‑pass reduction with atomic bin updates.  
- Backup script uses UTC timestamps, retention policy, and trap cleanup.  
- Database layer enforces referential integrity, audit logging, and transactional safety across all domains.
