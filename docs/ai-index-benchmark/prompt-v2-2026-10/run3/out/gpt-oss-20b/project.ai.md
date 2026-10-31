### srcmorph-modelcard
- H: 1.0
- C: 8AC4E062
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:13:23Z
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
The srcmorph‑modelcard project centers on a Java framework that orchestrates AI‑driven source‑code transformations, with rule‑based routing, fact extraction, and a pluggable generation backend exposed by java/provider (supporting llama.cpp JNI, OpenAI‑compatible APIs, and mock providers). GPU‑accelerated utilities in the other package provide a two‑pass OpenCL kernel for 256‑bin histograms, a lock‑free ring buffer for byte streams, and command‑line tools for directory backup and sales data aggregation, all of which support the transformation orchestration. Data persistence is handled by a suite of SQL packages that manage library lending, e‑commerce orders, and warehouse inventory, each with dedicated stored procedures, audit logging, and data‑flow pipelines, forming a cohesive backend for the tool.

#### Packages
- java/config — A Java library that configures, validates, and orchestrates AI‑driven source‑code transformations, providing rule‑based routing, fact extraction, and generation parameter management for the srcmorph tool.
- java — A Java framework that orchestrates AI‑driven source‑code transformations, providing configurable routing, fact extraction, and a pluggable generation backend for the srcmorph tool.
- java/provider — A pluggable AI generation framework that supports local llama.cpp JNI, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response API for source‑code transformation tooling.
- other/kernels — Provides a two‑pass OpenCL implementation for computing a 256‑bin histogram of arbitrary byte data, avoiding global atomics by first generating per‑group partial histograms and then reducing them into the final result.
- other — Provides GPU‑accelerated histogram computation, a lock‑free ring buffer for byte streams, and command‑line utilities for backing up directories and summarizing sales data.
- other/ringbuf — Implements a lock‑free, power‑of‑two sized circular buffer for byte streams, providing efficient single‑producer/single‑consumer read and write operations.
- other/scripts — A lightweight utility package that backs up directories and aggregates sales data from CSV exports.
- . — A framework that orchestrates AI‑driven source‑code transformations, backed by GPU‑accelerated utilities and a set of SQL packages for library, shop, and warehouse data management.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails for lending operations.
- sql — A collection of SQL packages that manage library lending, e‑commerce orders, and warehouse inventory, each with dedicated stored procedures, audit logging, and data‑flow pipelines.
- sql/shop — A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and order total calculation.
- sql/warehouse — Synchronises nightly stock levels from a supplier feed into the warehouse inventory table and records the operation outcome.
