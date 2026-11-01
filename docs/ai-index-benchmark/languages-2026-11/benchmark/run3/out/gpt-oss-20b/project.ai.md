### srcmorph-modelcard
- H: 1.0
- C: 9FBE3A74
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:07:35Z
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
The project centers on an AI‑driven source‑code transformation framework that runs as a Maven plugin, orchestrating configuration, file selection, and post‑processing while delegating text generation to a modular provider layer that supports mock, llama.cpp, and OpenAI‑compatible back‑ends. Low‑level utilities for byte‑stream handling—including a lock‑free ring buffer and OpenCL kernels for efficient histogram computation—support the plugin’s data pipelines and backup/reporting scripts. A unified set of PostgreSQL, MySQL, and SQL Server schemas manages library cataloging, e‑commerce orders, and warehouse inventory, with stored procedures that synchronize inventory, reserve stock, and generate audit trails across the system.

#### Packages
- java/config — Provides AI‑powered source‑code generation and analysis, orchestrating configuration, condition evaluation, fact extraction, and oversized‑file handling for a Maven‑based code‑generation plugin.
- java — A Maven‑based code‑generation plugin that orchestrates AI‑powered source‑code transformation, from configuration and file selection to provider‑agnostic text generation and post‑processing.
- java/provider — A modular AI‑generation framework that abstracts multiple back‑ends (mock, llama.cpp, OpenAI‑compatible) and provides a clean API for generating text, parsing responses, and measuring performance.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes, avoiding global atomics through per‑group partial reductions.
- other — Provides low‑level utilities for byte‑stream processing, a lock‑free ring buffer, and simple backup/reporting scripts.
- other/ringbuf — A lock‑free, power‑of‑two circular buffer for byte streams, exposing a minimal C API for single‑producer/single‑consumer use.
- other/scripts — A small utility package that provides a shell script for rotating directory backups and a Python script for summarizing sales revenue from CSV exports.
- . — A unified framework for AI‑driven source‑code transformation, low‑level byte‑stream utilities, and cross‑platform database schemas that support library, e‑commerce, and warehouse operations.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports branch‑level inventory, and provides reporting and audit trails for book lending operations.
- sql — A collection of database schemas and procedures that manage library cataloging, e‑commerce orders, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server environments.
- sql/shop — A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, with automated stock reservation and order total calculation.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, logging changes and errors.
