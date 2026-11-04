### srcmorph-modelcard
- H: 1.0
- C: F63FE425
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:58:41Z
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
The srcmorph‑modelcard framework orchestrates AI‑driven source‑code generation by selecting files, extracting deterministic facts, and delegating prompt creation to pluggable AI back‑ends such as local llama.cpp, OpenAI‑compatible APIs, or mocks. Its core Java packages provide a Maven‑style configuration engine, a unified provider interface for AI generation, and a runtime orchestrator that ties file selection, fact extraction, and prompt execution together. Low‑level utilities in the other package supply byte‑stream processing tools—including an OpenCL histogram kernel, a lock‑free ring buffer, and backup/reporting scripts—while the sql packages expose PostgreSQL, MySQL, and SQL Server schemas and stored procedures for inventory, lending, e‑commerce, and warehouse management, enabling the generated code to interact with real data stores.

#### Packages
- java/config — A Maven‑style configuration and runtime engine for AI‑driven source‑code generation, handling file selection, fact extraction, and model‑specific generation settings.
- java — A Maven‑style framework that orchestrates AI‑driven source‑code generation, selecting files, extracting deterministic facts, and delegating prompt generation to pluggable AI back‑ends (local llama.cpp, OpenAI‑compatible APIs, or mocks).
- java/provider — Provides a pluggable AI generation layer that supports local llama.cpp models, OpenAI‑compatible APIs, and mock back‑ends, exposing a unified request/response contract and optional timing metrics.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — Provides low‑level utilities for byte‑level data processing, including an OpenCL histogram kernel, a lock‑free ring buffer, and small shell/Python tools for backup and sales reporting.
- other/ringbuf — A lock‑free, power‑of‑two sized byte ring buffer for single‑producer/single‑consumer scenarios.
- other/scripts — A small utility bundle that backs up directories and aggregates sales data from CSV exports.
- . — A framework that orchestrates AI‑driven source‑code generation, from file selection and fact extraction to prompt creation and model execution, while also providing low‑level byte‑stream utilities and a set of SQL packages for inventory, lending, and e‑commerce data management.
- sql/library — A PostgreSQL‑based library management system that defines catalog, lending, and reporting objects, and provides stored routines for checkout, return, and fee calculation.
- sql — A collection of SQL packages that manage inventory, lending, and e‑commerce data across PostgreSQL, MySQL, and SQL Server, each providing schema, business logic, and audit capabilities for their domain.
- sql/shop — A MySQL schema and stored procedure that model a web shop’s product catalog, customer orders, and order items, ensuring stock reservation and order total consistency.
- sql/warehouse — Keeps warehouse inventory in sync with nightly supplier feeds and records each sync run.
