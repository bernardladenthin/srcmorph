### srcmorph-modelcard
- H: 1.0
- C: A6E013F6
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T05:25:11Z
- G: 
- A: 0.0.0
- X: project
- F: [java/config](java/config/package.ai.md)
- F: [java](java/package.ai.md)
- F: [java/provider](java/provider/package.ai.md)
- F: [other/kernels](other/kernels/package.ai.md)
- F: [other](other/package.ai.md)
- F: [other/ringbuf](other/ringbuf/package.ai.md)
- F: [other/scripts](other/scripts/package.ai.md)
- F: [.](package.ai.md)
- F: [sql/library](sql/library/package.ai.md)
- F: [sql](sql/package.ai.md)
- F: [sql/shop](sql/shop/package.ai.md)
- F: [sql/warehouse](sql/warehouse/package.ai.md)
---
> Project index of srcmorph-modelcard: one line per package; leads here, clickable links in the header F list.

#### Overview
The project is a multi‑language toolkit centered around AI‑assisted code summarization for Java, delivered through Maven plugins (java/config and java). It also provides OpenCL kernels for fast image histogram calculation (other/kernels), a C ring buffer for efficient single‑producer/single‑consumer data exchange (other/ringbuf), and Python utilities for sales reporting and backup management (other/scripts). Supporting subsystems include a PostgreSQL library management system (sql/library), a MySQL web shop handling products and orders (sql/shop), and a nightly warehouse stock synchronization process (sql/warehouse).

#### Packages
- java/config — A Maven plugin for AI-assisted code summarization and metadata extraction.
- java — A Maven plugin for AI-assisted code summarization and metadata extraction.
- java/provider — **Package**: `package.ai.md`
- other/kernels — Calculates a 256-bin byte histogram of an image or byte stream in two OpenCL passes
- other — A multi-language codebase providing OpenCL kernels for image histogram calculation, a C ring buffer implementation, and Python scripts for sales reporting and backup management.
- other/ringbuf — Implements a fixed-size ring buffer for byte data in C, providing efficient single-producer/single-consumer data exchange.
- other/scripts — A Python package for generating sales reports and managing backups.
- . — A multi‑language package that provides AI‑assisted code summarization for Java, OpenCL kernels for image histogram calculation, a C ring buffer, and Python scripts for sales reporting and backup management, along with a SQL library for library management, a MySQL shop, and a warehouse stock sync.
- sql/library — A PostgreSQL library management system modeling authors, books, members, loans, branches, and audit trails.
- sql — A multi‑module system covering a PostgreSQL library management platform, a MySQL web shop, and a nightly warehouse stock sync.
- sql/shop — Web shop: products, orders and order placement (MySQL 8).
- sql/warehouse — Synchronises nightly supplier stock feeds into the `StockLevel` table, logging the import in `SyncLog`.
