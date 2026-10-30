### ai
- H: 1.0
- C: 5245D1E9
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:49:23Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package combines AI-assisted source generation, low-level operational utilities, and transactional SQL domains for catalog, order, and stock management.

#### Purpose
- Provides three distinct operational capabilities: source-aware AI generation policy and execution, bounded byte-stream and reporting utilities, and SQL-backed business logic for lending, retail, and warehouse operations.
- Keeps each subsystem self-contained while using strict validation, bounded-state handling, and auditable state transitions.

#### Responsibilities
- AI orchestration and source filtering — Java package selects eligible source files, applies generation policy, and runs local/native or OpenAI-compatible inference.
- Byte processing and operational tooling — C/OpenCL and shell/Python utilities handle histogramming, circular buffering, backup rotation, and sales aggregation.
- SQL domain services — PostgreSQL, MySQL, and SQL Server schema and procedures implement library circulation, online order fulfillment, and stock synchronization.
- Validation and reporting — each subsystem enforces input constraints, emits explicit statuses/errors, and exposes summaries suitable for operational monitoring.

#### Key units
- `SrcMorphConfiguration` — JavaBean for generation scope, file filters, provider selection, model prompts, and llama.cpp defaults.
- `AiGenerationProviderFactory` — resolves the active backend and instantiates the appropriate provider implementation.
- `LlamaCppJniAiGenerationProvider` — loads native model state and executes model inference for source-aware generation.
- `OpenAiCompatibleProviderSupport` — prepares HTTP chat-completions requests and normalizes provider errors/timing metadata.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and match source-file inclusion rules against file context and range conditions.
- `AiFactExtractor` — compiles compact fact blocks used to ground prompts with source metadata.
- `histogram_partial` / `histogram_reduce` — OpenCL kernel steps that aggregate byte counts into a 256-bin histogram.
- `ringbuf_t` / `ringbuf_status_t` — bounded circular byte buffer with validation, occupancy tracking, wraparound writes, and reads.
- `rotate_backups.sh` — shell workflow that timestamps archives and prunes older backups to a bounded retention count.
- `Order` / `parse_orders` / `revenue_by_month_and_region` — CSV parsing and monthly regional sales aggregation.
- `loan` / `member` / `book` / `overdue_loans` — library domain records and views for circulation and overdue management.
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` — PostgreSQL library logic for checkout, return, and fee calculation.
- `product` / `customer_order` / `order_item` / `add_order_item` — e-commerce order lifecycle and stock reservation flow.
- `dbo.StockLevel` / `dbo.SupplierFeed` / `dbo.SyncLog` / `dbo.usp_SyncStock` — warehouse stock reconciliation, merge, and audit logging.

#### Data flow
- AI flow:
  - configuration is assembled in `SrcMorphConfiguration` and `AiGenerationConfig`
  - source files are checked against `AiCondition` trees and context facts via `AiConditionEvaluator` and `AiFactExtractor`
  - oversized inputs are normalized by `AiOversizeStrategy`
  - `AiGenerationProviderFactory` selects the backend
  - the provider runs generation and returns final text after `AiCompletionParser` stripping reasoning markers
- Byte utility flow:
  - OpenCL kernels receive source bytes and produce partial counts, then reduce them to final `histogram[bin]`
  - `ringbuf_write` appends bytes into a wraparound queue and `ringbuf_read` drains them while tracking occupancy
  - backup script archives source directories and prunes older snapshots beyond `KEEP`
  - sales script reads CSV rows, filters by currency, aggregates totals by month and region, and prints results
- SQL flow:
  - library: `member` and `book` state feed `loan` creation and return processing; `overdue_loans` and `member_loan_stats` read the same data for reporting and audit
  - shop: `add_order_item` validates product and stock, reserves quantity, updates `order_item`, decrements `product.stock`, and recalculates `customer_order.total_cents`
  - warehouse: supplier feed deltas are merged into `dbo.StockLevel` with `OUTPUT $action INTO @Changes`, and `dbo.SyncLog` records counts and errors

#### Dependencies
- Internal collaboration:
  - Java AI config and provider layers are tightly coupled through `SrcMorphConfiguration`, `AiGenerationConfig`, `AiGenerationRequest`, and `AiGenerationTimings`
  - `AiConditionEvaluator` depends on file-context metadata and source exclusion rules
  - `other/` components are independent kernel, buffer, shell, and Python modules with no direct package-level coupling
  - SQL domains are internally connected by entity relationships and procedural logic within each database-specific schema
- External modules and runtimes:
  - Java: `java.time`, `java.util.*`, `java.io.File`, `java.net.URL`, `java.util.regex.*`, `org.jspecify.annotations.Nullable`, Jackson, Lombok, and native `net.ladenthin.llama.*`
  - OpenCL/C: `__kernel`, `__global`, `__local`, `atomic_inc`, `barrier`, `malloc`, `memcpy`, `size_t`, `uint8_t`
  - Shell/Python: `bash`, `tar`, `mv`, `rm`, `csv`, `defaultdict`, `Decimal`, `argparse`
  - SQL engine features: PostgreSQL PL/pgSQL, MySQL transactional `FOR UPDATE` and `SIGNAL`, and SQL Server T-SQL `MERGE`, `OUTPUT`, and `TRY...CATCH`

#### Cross-cutting
- Validation-first behavior is consistent across subsystems:
  - Java AI code validates conditions, URLs, keys, and config values with explicit exceptions
  - ring buffer and script logic reject invalid sizes, missing arguments, malformed CSV headers, and empty/no-data states
  - SQL routines guard stock and member status before mutation
- Bounded-state and retention patterns recur:
  - `BINS = 256`, a fixed-capacity ring, and `KEEP` backup retention all enforce explicit limits
  - stock and order totals are recomputed from child rows rather than stored as independent truth
- Auditability and operational traceability:
  - `trg_loan_audit` and `dbo.SyncLog` capture lifecycle and sync events
  - shell output and Python errors are emitted separately to stderr or status messages for operators
- Configuration and defaulting:
  - Java models expose mutable configuration with defaults and blank input normalization
  - SQL defaults use `CURRENT_TIMESTAMP`, `status = 'NEW'`, `total_cents = 0`, and `OnHand = 0`
- Explicit failure handling:
  - provider methods return zeroed timings when unavailable and format errors with context
  - shell flow uses `set -euo pipefail`, Python raises `ValueError`, and SQL procedures use transaction rollback/error paths
