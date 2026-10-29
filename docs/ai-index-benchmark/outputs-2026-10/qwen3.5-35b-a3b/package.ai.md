### ai
- H: 1.0
- C: 3EE55F54
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:43:31Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Orchestrates AI-driven code indexing, parallel system utility kernels, and multi-domain relational inventory management.

#### Purpose
*   Validate `AiCondition` trees and resolve pluggable AI text generation backends.
*   Compute byte frequency distributions via OpenCL and automate system/financial tasks.
*   Define relational schemas and business logic for library, retail, and warehouse transactions.

#### Responsibilities
*   **AI Indexing:** Evaluate file filtering conditions and instantiate `AiGenerationProvider` implementations.
*   **System Utilities:** Manage memory buffers, execute parallel histogram kernels, and rotate archives.
*   **Data Management:** Enforce referential integrity, execute stored procedures for loans and orders, and sync stock levels.

#### Key units
*   `AiConditionEvaluator`: Evaluates file filtering conditions against `AiFileContext`.
*   `AiGenerationProvider`: Interface for text generation with `generate` and `close` methods.
*   `LlamaCppJniAiGenerationProvider`: Implements inference using `LlamaModel` native handle.
*   `histogram_partial`: Generates group-specific 256-bin counts using atomic increments.
*   `histogram_reduce`: Sums partial histograms across all groups into final 256-bin output.
*   `ringbuf_t`: Struct with `head` (producer) and `tail` (consumer) indices.
*   `rotate_backups.sh`: Bash script managing archive lifecycle and cleanup.
*   `sales_report.py`: Python CLI entry point for sales data processing.
*   `library` schema: Tables `author`, `book`, `member`, `loan`, `book_copy`, `branch`.
*   `shop` schema: Tables `product`, `customer_order`, `order_item`, `add_order_item`.
*   `warehouse` schema: Tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.

#### Data flow
*   Java: `AiCondition` tree + `AiFileContext` -> `matches()` -> Provider resolves -> `generate()` -> `String` + `Timings`.
*   Other: Byte stream -> `histogram_partial` -> `histogram_reduce` -> Final counts; `source_dir` -> `tar` -> `backup_dir`.
*   SQL: `add_order_item` locks `product` -> inserts `order_item` -> updates totals; `usp_SyncStock` filters `SupplierFeed` -> `MERGE`s `StockLevel`.

#### Dependencies
*   Java: `net.ladenthin.llama`, `com.fasterxml.jackson.databind`, `java.time`, `lombok`.
*   Other: OpenCL C 1.2, `stdlib.h`, `bash`, `argparse`, `csv`.
*   SQL: PostgreSQL DDL/PL/pgSQL, MySQL 8 dialect, SQL Server features.

#### Cross-cutting
*   Java: `AiConditionEvaluator` stateless/thread-safe, `LlamaCppJniConfig` immutable, `AutoCloseable` cleanup.
*   Other: Two-pass kernel model, power-of-two capacity validation, `ringbuf_status_t` error codes.
*   SQL: Row-level locking, foreign key constraints, audit logging (`loan_audit`, `SyncLog`).
