### srcmorph-modelcard
- H: 1.0
- C: 8751DD52
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:48:56Z
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
The project centers on an AI‑driven source‑code transformation framework that runs in a Maven environment, with configuration, validation, and execution logic exposed through the java and java/config packages. A pluggable provider layer (java/provider) offers local llama.cpp inference, OpenAI‑compatible APIs, and mock back‑ends, handling prompts, timing, and response parsing. Lightweight data‑stream utilities—such as a lock‑free ring buffer (other/ringbuf) and an OpenCL histogram kernel (other/kernels)—support ancillary command‑line tools for backup rotation and sales analytics (other/scripts). The unified framework also bundles database schemas (sql/library, sql/shop, sql/warehouse) and stored routines for library cataloging, e‑commerce orders, and inventory synchronization, enabling end‑to‑end data management alongside the AI transformation engine.

#### Packages
- java/config — Provides AI‑powered source‑code analysis and transformation configuration, validation, and execution support for a Maven‑based code‑morphing tool.
- java — A framework for configuring, validating, and executing AI‑driven source‑code transformations in a Maven‑based code‑morphing tool.
- java/provider — A pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompt handling, timing metrics, and response parsing.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL passes.
- other — Provides a lightweight, lock‑free ring buffer for byte streams, a two‑pass OpenCL histogram kernel for image data, and command‑line utilities for backup rotation and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer single‑consumer data transfer.
- other/scripts — A collection of command‑line utilities for managing backups and generating sales analytics from CSV exports.
- . — A unified framework for AI‑driven source‑code transformation, lightweight data‑stream utilities, and database schemas that support library, e‑commerce, and warehouse operations.
- sql/library — A PostgreSQL library management system that stores catalog, member, and loan data, exposes reporting views, and provides stored routines for checkout, return, and late‑fee calculation.
- sql — A collection of database schemas and stored routines that manage library cataloging, e‑commerce orders, and warehouse inventory synchronization.
- sql/shop — A MySQL 8 schema for a web shop that stores products, orders, and line items, and provides a stored procedure to add items to an order while reserving stock and updating totals.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, logging each run.
