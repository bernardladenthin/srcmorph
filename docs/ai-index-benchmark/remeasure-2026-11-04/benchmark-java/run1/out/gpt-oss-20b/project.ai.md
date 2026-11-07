### srcmorph-modelcard
- H: 1.0
- C: DF0455B2
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:19:43Z
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
The srcmorph‑modelcard framework orchestrates AI‑powered source‑code transformations through a Java engine that validates, configures, and routes generation requests, while pluggable provider modules supply local llama.cpp or OpenAI‑compatible back‑ends with prompt tuning and timing metrics. Low‑level GPU utilities, such as two‑pass OpenCL kernels for byte‑level histograms and a lock‑free ring buffer, support efficient data handling for backup and sales reporting scripts. A unified multi‑domain SQL persistence layer spans library cataloging, e‑commerce orders, and warehouse inventory, with dedicated schemas and stored procedures for shop, warehouse, and lending rules, enabling end‑to‑end cataloging, order processing, and inventory synchronization.

#### Packages
- java/config — A Java library that configures, validates, and orchestrates AI‑powered source‑code generation and routing for the srcmorph engine.
- java — Enables AI‑powered source‑code generation and routing for the srcmorph engine.
- java/provider — Provides pluggable AI back‑ends for source‑code generation, including local llama.cpp inference and OpenAI‑compatible chat completions, with configurable prompts, timing metrics, and response parsing.
- other/kernels — Computes a 256‑bin byte histogram of a byte stream using two OpenCL kernel passes.
- other — Provides a reusable byte‑level histogram generator, a lock‑free ring buffer, and small shell/Python utilities for backups and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a minimal API for initialization, read/write, and space queries.
- other/scripts — A small utility bundle that creates dated backups and produces aggregated sales reports from CSV exports.
- . — A unified framework that orchestrates AI‑driven source‑code transformations, provides low‑level GPU utilities, and supplies a multi‑domain SQL persistence layer for cataloging, e‑commerce, and inventory management.
- sql/library — A PostgreSQL library system that stores catalog, member, and loan data, enforces lending rules, tracks overdue status, and supports branch‑level copy management.
- sql — A PostgreSQL‑, MySQL‑, and SQL Server‑based data layer that manages library cataloging, e‑commerce orders, and warehouse inventory, each with its own set of tables, stored procedures, and reporting views.
- sql/shop — Provides the database schema and stored procedure for a web shop’s product catalog, orders, and order line items, handling stock reservation and order total calculation.
- sql/warehouse — Keeps warehouse inventory in sync with nightly supplier feeds and records each sync run.
