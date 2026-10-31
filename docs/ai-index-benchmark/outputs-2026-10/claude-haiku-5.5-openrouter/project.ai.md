### srcmorph-modelcard
- H: 1.0
- C: 342DAC12
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:38:02Z
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
project.ai.md

The workspace bundles three largely independent areas: an AI-driven per-file documentation generator for srcmorph (java, with java/config and java/provider handling file selection, oversize handling, and routing to mock, llama.cpp, or OpenAI-compatible backends), a GPU and systems toolkit (other, with OpenCL byte-histogram kernels in other/kernels, a lock-free SPSC byte ring buffer in other/ringbuf, and backup and currency-filtered sales-report scripts in other/scripts), and a set of SQL schema domains under sql (a PostgreSQL library-lending database, a MySQL 8 web-shop order system with stock-reserving order procedures, and a SQL Server warehouse stock reconciliation that folds supplier receipts into on-hand levels). The SQL subprojects share no code with the Java and C/OpenCL parts, so they are best navigated as separate domains from the root.

#### Packages
- java/config — Configures and evaluates the AI-driven source-documentation pipeline in srcmorph: which files get summarized, how oversize files are handled, how fact counts and file-selection conditions are evaluated, and how generation settings reach the AI providers.
- java — Generates AI-written per-source-file documentation for srcmorph by configuring which files are summarized and how oversize or fact-bearing content is handled, then routing generation to mock, local llama.cpp, or OpenAI-compatible backends.
- java/provider — Provides pluggable AI text generation for srcmorph, selecting a mock, local llama.cpp JNI, or OpenAI-compatible backend and cleaning model output for AI index files.
- other/kernels — Two-pass OpenCL GPU kernels that count byte frequencies into a 256-bin histogram for image or raw byte data.
- other — A multi-part toolkit that counts byte frequencies on the GPU, queues bytes through a lock-free single-producer/single-consumer ring buffer, and automates directory backups and per-region currency-filtered sales reporting.
- other/ringbuf — A fixed-capacity, power-of-two byte ring buffer in C for queuing bytes between one producer and one consumer with partial read/write semantics.
- other/scripts — Automates dated gzip backups of a directory with per-source retention, and produces currency-filtered monthly revenue reports per region from order CSV exports.
- . — Multi-part workspace bundling an AI-driven per-file source documentation generator for srcmorph, a GPU byte-histogram plus lock-free ring buffer with backup and sales-report tooling, and three independent SQL schema domains (library lending, web-shop orders, warehouse stock sync).
- sql/library — A PostgreSQL lending-library database: schema, reporting views, checkout/return routines with late-fee and audit trail, a branch/copy migration, and seed data.
- sql — A multi-database SQL workspace covering a PostgreSQL lending library, a MySQL web-shop order system, and a SQL Server nightly warehouse stock reconciliation.
- sql/shop — MySQL 8 web-shop order schema: product catalog with stock, customer orders and line items, and a procedure that adds a SKU to an order while reserving stock and refreshing the order total.
- sql/warehouse — Nightly warehouse stock reconciliation that folds supplier feed receipts into on-hand inventory levels and audits each sync run.
