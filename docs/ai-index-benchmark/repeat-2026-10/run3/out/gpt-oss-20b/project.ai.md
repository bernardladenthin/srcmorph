### srcmorph-modelcard
- H: 1.0
- C: 04B085E8
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:59:37Z
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
The project centers on a Java‑centric AI orchestration library that transforms source code by evaluating conditions, extracting facts, and generating text through pluggable local or remote AI back‑ends, with support for oversize‑file handling. It integrates a lightweight cross‑platform toolkit for efficient byte‑stream processing—including a lock‑free circular buffer and OpenCL kernels for histogram computation—alongside Python utilities for data‑management scripts. Database modules provide a PostgreSQL‑based library management system and MySQL‑based e‑commerce order processing, while warehouse inventory is kept in sync via nightly supplier feeds, all coordinated through stored procedures, triggers, and SQL scripts. This architecture enables end‑to‑end code‑morphing workflows that are configurable, provider‑agnostic, and backed by robust data‑management and reporting capabilities.

#### Packages
- java/config — A Maven‑style Java library that orchestrates AI‑powered source‑code transformations, providing configuration, condition evaluation, fact extraction, and oversize‑file handling for a code‑morphing workflow.
- java — A Java library that orchestrates AI‑driven source‑code transformations, from configuration and condition evaluation to provider‑agnostic text generation and oversize‑file handling.
- java/provider — Provides pluggable AI back‑ends for generating source‑code transformation text, supporting both local llama.cpp inference and OpenAI‑compatible APIs, with configurable prompts, timing metrics, and completion parsing.
- other/kernels — Provides a two‑pass OpenCL implementation for computing a 256‑bin histogram of byte data, suitable for image processing or generic stream analysis.
- other — A lightweight, cross‑platform toolkit for efficient byte‑stream processing, circular buffering, and automated data‑management scripts.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams in a single‑producer/single‑consumer context.
- other/scripts — Automates data backup and generates aggregated sales reports from CSV exports.
- . — A Java‑centric AI orchestration library that transforms source code by evaluating conditions, extracting facts, and generating text with pluggable local or remote AI back‑ends, complemented by a lightweight C/CL and Python toolkit for byte‑stream processing and a set of SQL modules that enforce business rules for libraries, shops, and warehouses.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending, returns, late fees, and provides reporting views for overdue loans, member activity, and popular titles.
- sql — A collection of database‑centric modules that manage library cataloging, e‑commerce order processing, and warehouse inventory synchronization, all implemented with stored procedures, triggers, and SQL scripts.
- sql/shop — A MySQL stored procedure that manages order line items, reserves stock, and updates order totals in an e‑commerce webshop.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds and records the sync outcome.
