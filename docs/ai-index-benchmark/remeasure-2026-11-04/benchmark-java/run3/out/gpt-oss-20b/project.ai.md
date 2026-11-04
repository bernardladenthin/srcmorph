### srcmorph-modelcard
- H: 1.0
- C: 9338636F
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:50:23Z
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
The srcmorph‑modelcard project is a unified framework that orchestrates AI‑driven source‑code transformation, GPU‑accelerated data processing, and relational data management across library, shop, and warehouse domains. Its core engine evaluates file‑selection rules, extracts deterministic code‑facts, and delegates generation to pluggable AI back‑ends such as llama.cpp or OpenAI‑compatible APIs, while a configuration‑centric layer fine‑tunes AI‑generation settings. GPU kernels compute 256‑bin byte histograms and a lock‑free ring buffer streams data to the AI pipeline, with utilities for backup and sales reporting. Relational schemas in PostgreSQL and MySQL enforce business rules, audit logs, and reporting for cataloging, e‑commerce orders, and inventory synchronization.

#### Packages
- java/config — A configuration‑centric framework that evaluates file‑selection rules, extracts deterministic code‑facts, and orchestrates AI‑generation settings for source‑morph indexing.
- java — A framework that orchestrates AI‑driven source‑code transformation by evaluating file‑selection rules, extracting deterministic facts, and delegating generation to pluggable AI back‑ends.
- java/provider — Provides a pluggable AI text‑generation layer for source‑code transformation, supporting local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompts, timing metrics, and robust response parsing.
- other/kernels — Computes a 256‑bin byte histogram of an input byte stream using two OpenCL kernel passes.
- other — Provides GPU‑accelerated byte‑histogram computation, a lock‑free ring buffer for streaming data, and small shell/Python utilities for backup and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — A small utility package that backs up directories and generates monthly sales reports from CSV exports.
- . — A framework that orchestrates AI‑driven source‑code transformation, GPU‑accelerated data processing, and relational data management for library, shop, and warehouse domains.
- sql/library — A PostgreSQL library management system that stores catalog, member, and loan data, enforces borrowing rules, tracks copy availability, and provides reporting views and audit trails.
- sql — A collection of relational schemas and stored procedures that manage library cataloging, e‑commerce orders, and warehouse inventory, each with business‑rule enforcement, audit logging, and reporting capabilities.
- sql/shop — A MySQL schema for a web shop that stores products, customer orders, and order items, with a stored procedure to add items to an order while reserving stock and updating totals.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.
