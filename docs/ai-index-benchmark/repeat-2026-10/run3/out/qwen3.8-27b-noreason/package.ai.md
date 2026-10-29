### ai
- H: 1.0
- C: 973AD197
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:51:21Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides a multi-language toolkit for AI-driven source code summarization, high-performance data processing primitives, and transactional database schemas for library, e-commerce, and warehouse domains.

#### Purpose
- Integrates a Java-based AI generation pipeline for source code indexing with heterogeneous low-level data processing utilities (OpenCL, C) and operational scripts.
- Implements domain-specific SQL schemas and stored procedures for library lending, e-commerce order processing, and warehouse stock synchronization.

#### Responsibilities
- **AI Source Summarization (Java)**: Manages configuration, condition-based file filtering, fact extraction, and pluggable AI inference backends (local llama.cpp JNI, remote OpenAI-compatible) for code analysis.
- **High-Performance Data Primitives (OpenCL/C)**: Implements GPU-accelerated 256-bin byte histogramming and a lock-free single-producer/single-consumer circular byte buffer.
- **Operational Automation (Bash/Python)**: Executes timestamped backup rotation with retention policies and generates financial sales reports from CSV data.
- **Database Domain Logic (SQL)**: Enforces data integrity, concurrency control, and audit logging for library loan lifecycles, atomic e-commerce stock validation, and warehouse supplier feed synchronization.

#### Key units
- `SrcMorphConfiguration`, `AiGenerationConfig`: Central mutable JavaBeans for global run settings and per-model inference parameters.
- `AiGenerationProvider`, `AiGenerationProviderFactory`: SPI interface and factory for resolving and instantiating AI backends.
- `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`: Concrete implementations for local native inference and remote API calls.
- `AiConditionEvaluator`, `AiFactExtractor`: Stateless utilities for filtering source files and generating deterministic regex-based facts.
- `histogram_partial`, `histogram_reduce`: OpenCL kernels for parallel byte frequency aggregation.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`: C structures and functions for lock-free circular byte storage.
- `rotate_backups.sh`, `sales_report.py`: Bash and Python scripts for backup management and revenue analysis.
- `fn_checkout_book`, `sp_return_book`, `loan_audit`: PostgreSQL functions, procedures, and audit tables for library management.
- `add_order_item`: MySQL stored procedure for atomic stock validation and order total recalculation.
- `dbo.usp_SyncStock`, `dbo.StockLevel`, `dbo.SupplierFeed`: SQL Server procedure and tables for MERGE-based warehouse stock synchronization.

#### Data flow
- **AI Pipeline**: Maven/JSON config populates `SrcMorphConfiguration` → `AiConditionEvaluator` filters files → `AiFactExtractor` generates facts → `AiGenerationProviderFactory` instantiates backend → Provider executes inference (JNI or HTTP) → `AiCompletionParser` strips chain-of-thought blocks → Returns cleaned text and timings.
- **GPU Histogram**: Input `uchar` stream → `histogram_partial` (local atomic counting) → Global partial array → `histogram_reduce` (cross-group summation) → Final histogram array.
- **Ring Buffer**: Producer bytes → `ringbuf_write` (chunked copy, advance head) → Consumer reads via `ringbuf_read` (chunked copy, advance tail).
- **Library Loans**: `fn_checkout_book` validates and inserts loan → Trigger logs to `loan_audit` → `sp_return_book` updates status and inventory.
- **E-commerce Orders**: `add_order_item` locks product row → Validates stock → Updates `order_item` and `product.stock` → Recalculates `customer_order.total_cents`.
- **Warehouse Sync**: `dbo.usp_SyncStock` aggregates `dbo.SupplierFeed` → MERGEs into `dbo.StockLevel` → Logs outcomes to `dbo.SyncLog`.

#### Dependencies
- **Java**: `net.ladenthin.llama` (JNI bindings), `com.fasterxml.jackson.databind` (JSON), `lombok`, `org.jspecify.annotations`.
- **OpenCL/C**: OpenCL built-ins (`atomic_inc`, `barrier`), C Standard Library (`stdlib.h`, `string.h`, `stdint.h`).
- **Scripts**: Unix utilities (`tar`, `date`, `du`), Python Standard Library (`argparse`, `csv`, `collections`, `dataclasses`).
- **SQL**: PostgreSQL (PL/pgSQL, materialized views), MySQL 8 (InnoDB, row-level locking), SQL Server (T-SQL, MERGE, `SYSUTCDATETIME`).

#### Cross-cutting
- **Concurrency & Thread Safety**: Lock-free SPSC ring buffer using `volatile` indices; OpenCL work-group synchronization via `barrier`; SQL row-level locking (`FOR UPDATE`) and transactional MERGE.
- **Resource Management**: Java AI providers extend `AutoCloseable` for native/network resource cleanup; C ring buffer manages memory via `malloc`/`free`.
- **Error Handling**: Fail-fast validation via `IllegalArgumentException` in Java; explicit status codes in C; SQLSTATE exceptions and `THROW` in SQL; strict validation (`set -euo pipefail`, `ValueError`) in scripts.
- **Audit & Logging**: Immutable `loan_audit` table in PostgreSQL; `dbo.SyncLog` in SQL Server; timestamped backup archives in Bash.
- **Configuration**: JavaBeans for Maven binding; CLI arguments for scripts; function parameters for C/OpenCL primitives.
