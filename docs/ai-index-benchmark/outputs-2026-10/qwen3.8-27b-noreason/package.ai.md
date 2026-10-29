### ai
- H: 1.0
- C: B3E04815
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T10:01:34Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A heterogeneous monorepo combining a Java-based pluggable AI code-indexing pipeline, C/C++/Python/OpenCL system-level utilities for GPU histograms and lock-free buffers, and multi-dialect SQL schemas for library, e-commerce, and warehouse inventory management.

#### Purpose
- Integrates three distinct functional domains: AI-driven source code analysis, high-performance system-level data processing/automation, and transactional database logic for inventory and lending.
- Provides end-to-end capabilities from LLM inference and file filtering to GPU-accelerated computation and atomic database state changes.

#### Responsibilities
- **AI Code Indexing (Java)**: Manages configuration, file exclusion logic, deterministic fact extraction, and multi-backend LLM inference (local JNI/llama.cpp and remote OpenAI-compatible APIs).
- **System Utilities (C/Python/OpenCL)**: Implements GPU-accelerated byte histogram computation, lock-free single-producer/single-consumer ring buffers, and operational scripts for backup rotation and sales revenue aggregation.
- **Database Logic (SQL)**: Defines domain-specific models and transactional procedures for library lending (PostgreSQL), e-commerce order processing (MySQL), and warehouse inventory synchronization (T-SQL).

#### Key units
- **AI Infrastructure**: `SrcMorphConfiguration` (mutable run-state), `AiGenerationProvider` (inference contract), `LlamaCppJniAiGenerationProvider` (local JNI inference), `OpenAiCompatibleProviderSupport` (remote HTTP handling), `AiConditionEvaluator` (file filtering), `AiFactExtractor` (regex-based facts).
- **System Tools**: `histogram_partial`/`histogram_reduce` (OpenCL kernels), `ringbuf_t` (lock-free buffer structure), `rotate_backups.sh` (backup automation), `sales_report.py` (CSV aggregation with `Order` dataclass).
- **Database Objects**: `fn_checkout_book`/`sp_return_book` (PostgreSQL library logic), `add_order_item` (MySQL atomic stock/order update), `dbo.usp_SyncStock` (T-SQL MERGE-based inventory sync), `loan_audit`/`SyncLog` (audit tables).

#### Data flow
- **AI Pipeline**: Configuration binding → File filtering via `AiConditionEvaluator` → Fact extraction via `AiFactExtractor` → Provider resolution via `AiGenerationProviderFactory` → Inference (JNI or HTTP) → Post-processing via `AiCompletionParser` → Clean text output.
- **GPU Histogram**: Raw byte stream → `histogram_partial` (shared memory atomic increments) → Partial global buffer → `histogram_reduce` (global summation) → 256-bin histogram.
- **Ring Buffer**: Producer writes to `head` (wrap-around `memcpy`) → Consumer reads from `tail` (wrap-around `memcpy`) → Occupancy tracked via bitwise arithmetic.
- **Database Transactions**:
  - **Library**: Checkout validates status → Decrements inventory → Inserts `loan` → Triggers `loan_audit`.
  - **Shop**: `add_order_item` locks `product` row → Validates stock → Upserts `order_item` → Decrements stock → Recalculates order total.
  - **Warehouse**: `usp_SyncStock` aggregates `SupplierFeed` → MERGEs into `StockLevel` → Logs to `SyncLog`.

#### Dependencies
- **Java**: `net.ladenthin.llama` (JNI), `com.fasterxml.jackson.databind` (JSON), `lombok`, `org.jspecify.annotations`.
- **C/OpenCL**: OpenCL 1.2 runtime, C standard library (`stdlib.h`, `string.h`).
- **Python**: Standard library (`argparse`, `csv`, `dataclasses`, `decimal`, `collections`).
- **SQL**: PostgreSQL (PL/pgSQL, triggers), MySQL (InnoDB, `FOR UPDATE`), T-SQL (SQL Server built-ins, explicit transactions).

#### Cross-cutting
- **Concurrency & Safety**: Lock-free `volatile` indices in C ring buffers; `FOR UPDATE` row locks in MySQL; explicit transactions in T-SQL; non-thread-safe lazy init in Java JNI provider.
- **Error Handling**: `IllegalArgumentException`/`IOException` in Java; specific status codes (`RINGBUF_EINVAL`) in C; `ValueError` in Python; SQLSTATE/`THROW` exceptions in SQL.
- **Audit & Logging**: Immutable append-only logs across all domains (`loan_audit`, `SyncLog`, AI timing metrics).
- **Configuration**: Immutable builder patterns for AI inference params; CLI arguments for operational scripts; mutable JavaBeans for run-state.
- **Null Safety & Immutability**: Extensive `@Nullable` annotations in Java; frozen dataclasses in Python; immutable configuration holders.
