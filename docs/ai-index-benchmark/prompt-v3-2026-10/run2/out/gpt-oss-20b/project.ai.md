### srcmorph-modelcard
- H: 1.0
- C: 8B76CF81
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:23:55Z
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
The srcmorph‑modelcard project unifies an AI‑driven source‑code generation framework with a robust data‑management layer. The core Java packages configure, evaluate, and execute AI generation and summarisation, while the provider subpackage offers pluggable back‑ends for llama.cpp, OpenAI‑compatible APIs, and mocks, complete with prompt handling and token‑budget control. Low‑level utilities in the other package supply GPU‑accelerated byte‑histogram kernels, lock‑free ring buffers, and ancillary shell/Python scripts for backup and sales reporting. Complementing these, the sql packages expose PostgreSQL and MySQL schemas and stored procedures that manage library catalogues, shop inventories, and warehouse stock feeds, enabling end‑to‑end data flow from AI‑generated code to operational reporting.

#### Packages
- java/config — A Java library that configures, evaluates, and drives AI‑powered source‑code generation and summarisation within the srcmorph toolchain.
- java — A framework for configuring, evaluating, and executing AI‑driven source‑code generation and summarisation within the srcmorph toolchain.
- java/provider — A lightweight, pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompt handling, token‑budget control, and timing metrics.
- other/kernels — Computes a 256‑bin byte histogram of a byte stream using two OpenCL kernel passes.
- other — A lightweight, multi‑layered toolkit for handling byte streams, from low‑level GPU‑accelerated histograms to lock‑free ring buffers, plus auxiliary shell/Python utilities for backup and sales reporting.
- other/ringbuf — Implements a lock‑free, power‑of‑two circular byte buffer for single‑producer/single‑consumer scenarios, exposing a minimal API for initialization, non‑blocking read/write, and space queries.
- other/scripts — Automates backup rotation and generates monthly sales revenue reports from CSV order exports.
- . — A unified framework for configuring, evaluating, and executing AI‑driven source‑code generation and summarisation, complemented by low‑level byte‑stream utilities and a comprehensive SQL data model for library, shop, and warehouse operations.
- sql/library — A PostgreSQL data model and procedural toolkit for a multi‑branch lending library, covering catalog, members, loans, reporting, and audit trails.
- sql — A collection of database schemas and stored procedures that manage inventory, sales, and supplier feeds across a library, online shop, and warehouse system.
- sql/shop — A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and total calculation.
- sql/warehouse — Synchronises nightly supplier stock feeds into the warehouse inventory and logs the results.
