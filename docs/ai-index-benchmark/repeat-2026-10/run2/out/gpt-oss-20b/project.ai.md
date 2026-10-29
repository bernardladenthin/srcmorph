### srcmorph-modelcard
- H: 1.0
- C: 0E6C71E3
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:19:14Z
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
The project is a configuration‑driven AI‑powered source‑code morphing engine that orchestrates file selection, condition evaluation, fact extraction, and AI generation for code transformations, with a pluggable provider layer capable of invoking local llama.cpp models or OpenAI‑compatible APIs and exposing timing metrics. GPU‑accelerated utilities in the “other” packages provide a lock‑free byte ring buffer, a 256‑bin histogram builder via OpenCL kernels, and command‑line tools for backup rotation and sales reporting. The SQL sub‑packages expose a PostgreSQL library management system, a MySQL webshop schema, and a warehouse inventory sync, each with integrated business logic, reporting views, and audit trails. Together, these subsystems enable automated code transformation, performance monitoring, and data‑centric reporting across the project.

#### Packages
- java/config — A configuration‑driven source‑code morphing engine that evaluates file‑level AI conditions, extracts deterministic facts, and orchestrates AI generation for code transformation.
- java — A configuration‑driven AI‑powered source‑code morphing engine that orchestrates file selection, condition evaluation, fact extraction, and AI generation for code transformations.
- java/provider — Provides a pluggable AI generation layer that can invoke local llama.cpp models or OpenAI‑compatible APIs, parse their responses, and expose timing metrics for source‑morph transformations.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — Provides a GPU‑accelerated histogram builder, a lock‑free byte ring buffer, and command‑line utilities for backup rotation and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized byte queue for single‑producer, single‑consumer scenarios.
- other/scripts — Automates backup rotation and generates monthly sales revenue reports from CSV order exports.
- . — A configuration‑driven AI‑powered source‑code morphing engine that orchestrates file selection, condition evaluation, fact extraction, and AI generation for code transformations.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, exposes reporting views, and provides stored routines for checkout, return, and audit.
- sql — A set of relational schemas and stored procedures that manage library cataloging, webshop orders, and warehouse inventory, each with integrated business logic, reporting, and audit trails.
- sql/shop — A MySQL 8 schema and stored procedure for managing a webshop’s product catalog, customer orders, and order line items, including stock reservation and order total calculation.
- sql/warehouse — Synchronises nightly supplier stock feeds into a warehouse inventory, logging each run and handling errors.
