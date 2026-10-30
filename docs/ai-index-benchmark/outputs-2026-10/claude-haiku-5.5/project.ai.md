### srcmorph-modelcard
- H: 1.0
- C: 68FBE1BD
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:33:54Z
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
The project combines an AI-driven source-file summarisation engine in Java, with configuration rules for file selection and oversize handling and pluggable text generation providers (mock, llama.cpp JNI, and OpenAI-compatible). Alongside it sit C/OpenCL and Bash/Python utilities, including a GPU byte-histogram kernel, a lock-free single-producer/single-consumer ring buffer, and backup-rotation and monthly sales-report scripts. Three independent SQL artifacts cover a PostgreSQL lending library, a MySQL 8 web shop with stock-reserving order procedures, and a T-SQL warehouse stock sync.

#### Packages
- java/config — Configuration model and condition/fact/oversize rules that control how srcmorph selects, sizes, and summarises Java source files for AI-generated indexes.
- java — Java package that configures and runs AI-driven source-file summarisation, selecting files, sizing oversize inputs, and generating per-file text via pluggable local or remote model providers.
- java/provider — Provides pluggable AI text generation for source-file documentation via mock, local llama.cpp JNI, and OpenAI-compatible backends, with provider selection, config, and completion parsing.
- other/kernels — Two-pass OpenCL GPU kernel package that computes a 256-bin byte histogram of input data such as image pixels.
- other — A multi-part toolkit covering a GPU byte-histogram computation, a lock-free single-producer/single-consumer byte ring buffer, and backup-rotation and monthly sales-reporting utilities.
- other/ringbuf — A fixed-size power-of-two circular byte buffer for single-producer, single-consumer queuing of raw bytes with wraparound read and write.
- other/scripts — Bash and Python utilities that create timestamped backups with retention pruning and produce monthly per-region revenue reports from order CSV exports.
- . — A multi-language workspace combining an AI-driven source-file summarisation engine (Java), a GPU and ring-buffer toolkit with backup and sales-report utilities (C/OpenCL/Bash/Python), and three independent SQL database artifacts for library lending, web shop orders, and warehouse stock sync.
- sql/library — Lending library PostgreSQL database: schema for authors, books and copies across branches, members, loans, reporting views, checkout/return routines, audit logging, migration, and seed data.
- sql — Multi-database SQL workspace covering a lending library system (PostgreSQL), a web shop order schema (MySQL 8), and a warehouse stock sync procedure (T-SQL).
- sql/shop — Web shop order management schema in MySQL 8: products with stock, customer orders with totals, and an `add_order_item` procedure that reserves stock and recalculates order totals.
- sql/warehouse — Nightly T-SQL sync that merges aggregated supplier feed quantities into warehouse on-hand stock levels and logs each run.
