### srcmorph-modelcard
- H: 1.0
- C: 660A5694
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:45:08Z
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
The project is a Maven‑based AI‑powered source‑code transformation engine that validates files, extracts deterministic facts, and orchestrates local or remote LLM generation with fine‑grained configuration. It relies on a modular provider framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, while exposing configurable prompts and timing metrics. Low‑latency byte‑stream utilities—including an OpenCL histogram kernel, a lock‑free ring buffer, and small shell/Python helpers—support data processing and backup tasks. A unified data‑management toolkit models a multi‑branch library, an e‑commerce shop, and a warehouse inventory system, providing PostgreSQL, MySQL, and SQL Server schemas, business logic, and synchronization routines.

#### Packages
- java/config — Provides a configurable AI‑powered source‑code transformation engine that evaluates file‑level conditions, extracts deterministic facts, and orchestrates model‑generation parameters for Maven‑based projects.
- java — A Maven‑based AI source‑code transformation engine that selects files, extracts deterministic facts, and orchestrates local or remote LLM generation with fine‑grained configuration.
- java/provider — A modular AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompts, timing metrics, and robust request/response handling.
- other/kernels — Computes a 256‑bin histogram of byte streams via two‑pass OpenCL kernels.
- other — Provides low‑latency byte‑stream processing utilities: an OpenCL histogram kernel, a lock‑free ring buffer, and small shell/Python helpers for backup and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling non‑blocking FIFO read/write in single‑producer single‑consumer contexts.
- other/scripts — A small utility package that provides a shell script for rotating directory backups and a Python script for summarizing sales data from CSV exports.
- . — A Maven‑based AI‑powered source‑code transformation engine that validates, extracts facts, and orchestrates local or remote LLM generation with fine‑grained configuration.
- sql/library — A PostgreSQL data model and procedural toolkit for managing a multi‑branch lending library, including schema, reporting views, business logic for check‑outs and returns, and migration to a copy‑based inventory system.
- sql — A unified data‑management toolkit that models a multi‑branch library, an e‑commerce shop, and a warehouse inventory system, providing schema, business logic, and synchronization routines across PostgreSQL, MySQL, and SQL Server.
- sql/shop — Provides a MySQL‑based e‑commerce data model with tables for products, orders, and order items, plus a stored procedure that adds items to an order while reserving stock and updating the order total.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.
