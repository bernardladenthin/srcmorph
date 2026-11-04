### srcmorph-modelcard
- H: 1.0
- C: 8E98F355
- D: 2026-11-04T12:50:13Z
- T: 2026-11-04T12:53:21Z
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
The project unifies an AI‑driven code‑generation framework with a set of data‑processing utilities and domain‑specific database modules. The core Java packages orchestrate source‑file selection, deterministic fact extraction, and prompt execution through configurable providers that support local llama.cpp, OpenAI‑compatible APIs, and mocks. GPU‑accelerated analytics are enabled by OpenCL kernels that compute byte‑stream histograms, while a lock‑free ring buffer and byte‑level utilities support efficient streaming and backup/reporting scripts. Database logic spans PostgreSQL and MySQL schemas for library cataloging, e‑commerce ordering, and warehouse inventory, each with stored procedures, audit logging, and nightly synchronization, all integrated into Maven/CLI workflows for robust persistence across library, shop, and warehouse systems.

#### Packages
- java/config — A configuration‑driven AI‑generation framework that routes source files, evaluates file‑matching rules, extracts deterministic language facts, and supplies all parameters for model execution.
- java — A framework that orchestrates AI‑driven code generation by selecting source files, extracting deterministic facts, and delegating prompt execution to configurable local or cloud providers.
- java/provider — A lightweight, configurable AI‑generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with robust request parsing, timing, and error handling.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — A collection of utilities for efficient byte‑level data handling, a lock‑free ring buffer for streaming, and small backup/reporting scripts.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized byte ring buffer for single‑producer/single‑consumer scenarios.
- other/scripts — A small utility package that backs up directories and produces monthly sales reports from CSV order exports.
- . — A unified framework for AI‑driven code generation, data‑processing utilities, and domain‑specific database logic, all orchestrated to support Maven/CLI workflows, GPU‑accelerated analytics, and robust persistence across library, shop, and warehouse systems.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, provides reporting views, and enforces business rules through stored routines and audit logging.
- sql — A collection of database‑centric modules that manage library cataloging, e‑commerce ordering, and warehouse inventory synchronization, each with its own schema, business logic, and audit mechanisms.
- sql/shop — Provides a MySQL schema and stored procedure for a web shop’s product catalog, orders, and order line items, handling stock reservation and order total calculation.
- sql/warehouse — Synchronises nightly supplier stock feeds into a warehouse inventory, logging changes and errors.
