### srcmorph-modelcard
- H: 1.0
- C: E181304D
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:43:11Z
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
The project centers on an AI‑driven source‑code transformation engine that selects files, extracts facts, and generates code changes through pluggable local or cloud providers, with a dedicated configuration package for pipeline setup. A provider subsystem offers interchangeable AI backends—including local llama.cpp models, OpenAI‑compatible APIs, and a mock provider—alongside utilities for request handling, response parsing, and performance metrics. Complementing the code‑engineering side, a byte‑level data toolkit supplies GPU‑accelerated histograms, a lock‑free ring buffer, and backup utilities, while a SQL layer manages library, e‑commerce, and warehouse data with dedicated schemas, business logic, and audit trails. Together, these subsystems enable automated code transformation, efficient data processing, and robust database operations within a unified framework.

#### Packages
- java/config — Provides AI‑driven source‑code analysis and generation, including file‑selection, fact extraction, and configurable generation pipelines for the srcmorph tool.
- java — A modular AI‑driven source‑code transformation engine that selects files, extracts facts, and generates code changes via pluggable local or cloud providers.
- java/provider — Provides a pluggable AI generation engine for source‑code transformation, supporting local llama.cpp models, OpenAI‑compatible APIs, and a mock provider, with utilities for request construction, response parsing, and timing metrics.
- other/kernels — Provides OpenCL kernels for computing a 256‑bin histogram of byte data via two‑pass reduction.
- other — Provides a unified toolkit for byte‑level data processing, including GPU‑accelerated histograms, a lock‑free ring buffer, and utility scripts for backup and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — A utility package that provides a shell script for rotating directory backups and a Python tool for aggregating monthly sales data from CSV exports.
- . — A modular AI‑driven source‑code transformation engine that selects files, extracts facts, and generates code changes via pluggable local or cloud providers.
- sql/library — A PostgreSQL library management system that stores catalog, member, and loan data, enforces lending rules, and provides reporting and audit trails.
- sql — A collection of SQL packages that manage library lending, e‑commerce orders, and warehouse inventory, each with dedicated schemas, business logic, and audit trails.
- sql/shop — A MySQL schema for a web shop that stores products, customer orders, and order items, with a stored procedure to add items to an order while reserving stock and updating totals.
- sql/warehouse — Keeps a warehouse inventory in sync with incoming supplier stock feeds, logging each update run.
