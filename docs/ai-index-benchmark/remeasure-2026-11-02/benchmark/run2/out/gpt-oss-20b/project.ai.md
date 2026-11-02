### srcmorph-modelcard
- H: 1.0
- C: CEBEA562
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:50:00Z
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
The srcmorph‑modelcard project is a configuration‑driven AI framework that orchestrates source‑code transformations across pluggable provider back‑ends, exposing rich runtime parameters and validation logic. It integrates an OpenCL‑based image‑processing toolkit that builds byte histograms, supplies a lock‑free ring buffer for streaming data, and offers shell/Python helpers for backup and sales reporting. The system also includes a PostgreSQL‑centric library management module, a MySQL‑based shop order processor, and a warehouse inventory synchronizer, all coordinated through a set of database modules that manage lending, e‑commerce orders, and nightly supplier feeds. AI‑powered text generation is handled by a provider package that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock testing with configurable prompts and timing metrics.

#### Packages
- java/config — A configuration‑driven framework for selecting, filtering, and summarizing source files with AI, exposing a rich set of runtime parameters and validation logic.
- java — A framework that selects, filters, and AI‑generates source‑code transformations, driven by a rich configuration and pluggable provider back‑ends.
- java/provider — Provides AI‑powered text generation for source‑code transformation, supporting local llama.cpp inference, OpenAI‑compatible APIs, and mock testing, with configurable prompts, timing metrics, and robust error handling.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — A lightweight, OpenCL‑based image‑processing toolkit that builds byte histograms, supplies a lock‑free ring buffer for streaming data, and offers shell/Python helpers for backup and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling non‑blocking single‑producer/single‑consumer data transfer.
- other/scripts — A collection of utilities for managing backups and generating sales revenue reports from CSV exports.
- . — A framework that orchestrates AI‑driven source‑code transformations, coupled with lightweight image‑processing utilities and a set of database modules for library, shop, and warehouse operations.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending, returns, late fee calculation, and provides reporting views for overdue loans and popular books.
- sql — A collection of database‑centric modules that manage library lending, e‑commerce order lines, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.
- sql/shop — A MySQL stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.
