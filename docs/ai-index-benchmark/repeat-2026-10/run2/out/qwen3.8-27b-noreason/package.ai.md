### ai
- H: 1.0
- C: C91EB333
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:10:51Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides a unified platform for AI-driven source code summarization, high-performance parallel data processing, and multi-database inventory management across library, e-commerce, and warehouse domains.

#### Purpose
- Integrates an AI inference pipeline for code analysis with low-level performance utilities (OpenCL histograms, lock-free buffers) and operational automation scripts.
- Implements distinct, transactionally safe database schemas for library circulation, e-commerce order processing, and warehouse stock reconciliation.

#### Responsibilities
- **AI Code Analysis**: Configuration management, pluggable inference backends (local llama.cpp, remote OpenAI-compatible), source filtering, and fact extraction for prompt enrichment.
- **High-Performance Data Structures**: Parallel byte histogram computation via OpenCL kernels and lock-free circular buffering for single-producer/single-consumer scenarios.
- **Operational Automation**: Bash-based backup rotation with retention policies and Python-based regional sales revenue aggregation.
- **Database Management**:
    - **Library (PostgreSQL)**: Book inventory, member accounts, loan circulation, late fee calculation, and audit logging.
    - **E-Commerce (MySQL)**: Product catalog, customer orders, atomic stock reservation, and order total recalculation.
    - **Warehouse (SQL Server)**: Stock level synchronization from supplier feeds via merge operations and sync auditing.

#### Key units
- **AI Layer**:
    - `SrcMorphConfiguration`: Root configuration bean for AI model routing, sampling, and hardware acceleration.
    - `AiGenerationProvider`: SPI interface for text generation; implementations include `LlamaCppJniAiGenerationProvider` (local) and `OpenAiCompatibleAiGenerationProvider` (remote).
    - `AiConditionEvaluator`: Recursive tree structure for filtering source files based on size, lines, and path globs.
    - `AiFactExtractor`: Computes deterministic regex-based metrics for prompt enrichment.
    - `AiCompletionParser`: Strips chain-of-thought blocks from raw LLM outputs.
- **Performance & Ops**:
    - `histogram_partial` / `histogram_reduce`: OpenCL kernels for 256-bin byte frequency distribution.
    - `ringbuf_t`: Lock-free circular byte buffer with bitwise index wrapping.
    - `rotate_backups.sh`: Bash script for timestamped `tar.gz` archives and retention enforcement.
    - `sales_report.py`: Python module for regional sales revenue aggregation using `Decimal` precision.
- **Database Objects**:
    - **Library**: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit`, `overdue_loans` view.
    - **Shop**: `add_order_item` procedure for atomic stock and total updates.
    - **Warehouse**: `dbo.usp_SyncStock` procedure for merging supplier feeds into `dbo.StockLevel`.

#### Data flow
- **AI Pipeline**: Configuration binds to `SrcMorphConfiguration` → `AiConditionEvaluator` filters files → `AiFactExtractor` enriches prompts → `AiGenerationProviderFactory` resolves backend → Local/Remote inference executes → `AiCompletionParser` cleans output → Returns text or `AiGenerationTimings`.
- **Histogram**: Input `uchar` data → `histogram_partial` (local shared memory) → Global partial buffer → `histogram_reduce` → Final 256-bin histogram.
- **Ring Buffer**: Bytes written to `head` index → Read from `tail` index → Indices wrapped via bitwise AND masking.
- **Library Checkout**: Member/Book IDs → `fn_checkout_book` validates and decrements inventory → Trigger logs to `loan_audit`.
- **Shop Order**: `add_order_item` locks product row → Validates stock → Updates `order_item` and `product.stock` → Recalculates `customer_order.total_cents`.
- **Warehouse Sync**: `dbo.usp_SyncStock` aggregates `dbo.SupplierFeed` → Merges into `dbo.StockLevel` → Logs metrics to `dbo.SyncLog`.

#### Dependencies
- **AI Layer**: `net.ladenthin.llama` (JNI bindings), `com.fasterxml.jackson.databind` (JSON), `lombok`, `org.jspecify.annotations`.
- **Performance**: OpenCL built-ins (`atomic_inc`, `barrier`), C Standard Library (`malloc`, `memcpy`), Unix utilities (`tar`, `mv`), Python Standard Library (`csv`, `decimal`, `dataclasses`).
- **Database**: PostgreSQL (PL/pgSQL, partial indexes), MySQL 8 (InnoDB, utf8mb4), SQL Server (`SYSUTCDATETIME`, `TRY...CATCH`).

#### Cross-cutting
- **Concurrency**: OpenCL kernels use `barrier`/`atomic_inc`; ring buffer uses `volatile` indices for lock-free safety; databases use row locking (`FOR UPDATE`) and transactional isolation.
- **Error Handling**: AI providers throw `IOException` for failures and `IllegalArgumentException` for config errors; ring buffer returns specific status codes; databases use SQLSTATE/`TRY...CATCH` for error signaling.
- **Security & Integrity**: AI remote providers enforce HTTPS/loopback for API keys; sales script uses `Decimal` for financial precision; databases enforce referential integrity and check constraints (e.g., `stock >= 0`).
- **Audit Trails**: Persistent logs maintained via `loan_audit` (library) and `dbo.SyncLog` (warehouse); AI providers track `AiGenerationTimings` for performance metrics.
- **Resource Management**: AI providers implement `AutoCloseable` for native resource cleanup; backup scripts use atomic renames and `trap` for cleanup.
