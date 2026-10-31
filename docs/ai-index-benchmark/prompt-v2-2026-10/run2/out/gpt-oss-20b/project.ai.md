### srcmorph-modelcard
- H: 1.0
- C: D3D6CBC0
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:57:25Z
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
The core of srcmorph‑modelcard is a configuration‑driven routing engine that scans source files, applies selection rules, extracts deterministic facts, and then dispatches the work to AI models via a modular provider layer. The provider layer supports local llama.cpp inference, OpenAI‑compatible APIs, and mock backends, while utilities handle request construction, response parsing, and timing metrics. GPU‑accelerated histogram kernels and a lock‑free ring buffer supply efficient data processing for auxiliary tasks such as backup rotation and sales reporting, and the SQL subpackages expose a multi‑tenant data layer for library, shop, and warehouse domains with dedicated schemas and stored procedures.

#### Packages
- java/config — A configuration‑driven framework for routing source files to AI models, evaluating file‑selection rules, extracting deterministic facts, and managing AI generation parameters.
- java — A framework that routes source files to AI models, extracts deterministic facts, and orchestrates local or remote AI generation with configurable strategies.
- java/provider — A modular AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with utilities for request construction, response parsing, and timing metrics.
- other/kernels — Computes a 256‑bin histogram of arbitrary byte data using two OpenCL kernel passes.
- other — A lightweight, GPU‑accelerated histogram engine, a lock‑free ring buffer, and a set of automation scripts for backup rotation and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — Automates backup rotation and generates monthly sales summaries from CSV order exports.
- . — A framework that routes source files to AI models, extracts deterministic facts, and orchestrates local or remote AI generation with configurable strategies.
- sql/library — A PostgreSQL data model and set of routines for a multi‑branch lending library, covering catalog, members, loans, reporting, and inventory management.
- sql — A multi‑tenant data layer that manages library catalogs, shop orders, and warehouse inventory, each with its own SQL schema, stored procedures, and reporting mechanisms.
- sql/shop — Provides a MySQL 8 schema and stored procedure for managing products, orders, and inventory in an online shop.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds and records the results.
