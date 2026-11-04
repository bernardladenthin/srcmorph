### srcmorph-modelcard
- H: 1.0
- C: 842C8971
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:27:50Z
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
The srcmorph‑modelcard project centers on an AI‑driven source‑code processing engine that validates, extracts facts, and generates code or documentation, orchestrated through a unified Java framework that supports Maven projects. The engine’s pluggable provider layer offers local llama.cpp, OpenAI‑compatible, and mock back‑ends, while the Java config package handles validation and orchestration of indexing and generation workflows. Complementary utilities in the “other” namespace provide efficient byte‑stream handling with OpenCL histograms, lock‑free ring buffers, and automation scripts for backup rotation and sales reporting. A separate SQL component supplies PostgreSQL and MySQL schemas for library, shop, and warehouse domains, exposing stored procedures for checkout, ordering, and inventory synchronization.

#### Packages
- java/config — A Java library that configures, validates, and orchestrates AI‑driven source‑code indexing and generation for Maven projects.
- java — A unified framework for configuring, validating, and executing AI‑driven source‑code indexing and generation across Maven projects.
- java/provider — Provides a pluggable AI generation layer that supports local llama.cpp models, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response interface for source‑code transformation tasks.
- other/kernels — Provides an OpenCL implementation for computing a 256‑bin histogram of arbitrary byte data using a two‑pass reduction strategy.
- other — A lightweight toolkit for processing byte streams: it builds OpenCL‑based histograms, offers a lock‑free ring buffer for I/O, and supplies shell/Python helpers for backup rotation and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — Automates backup rotation and generates monthly sales summaries from CSV order exports.
- . — A unified AI‑driven source‑code processing engine that validates, extracts facts, and generates code or documentation using pluggable back‑ends (llama.cpp, OpenAI‑compatible, or mock).
- sql/library — A PostgreSQL library system that stores catalog, members, and loan data, and provides checkout, return, and reporting functionality.
- sql — A collection of three relational database schemas that model a library, an online shop, and a warehouse, each exposing stored procedures, functions, and audit mechanisms for common business operations such as lending, ordering, and inventory syncing.
- sql/shop — A MySQL schema for a web shop that defines products, orders, and order items, and provides a stored procedure to add items to an order while reserving stock and updating totals.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, logging each run and handling errors.
