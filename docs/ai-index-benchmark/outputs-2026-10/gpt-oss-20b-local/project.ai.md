### srcmorph-modelcard
- H: 1.0
- C: 330D97C8
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:26:20Z
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
The project centers on a runtime‑driven AI source‑code transformation engine that orchestrates file selection, deterministic fact extraction, and AI‑powered code generation or analysis across a multi‑language codebase. A configuration‑driven variant and a pluggable provider layer support local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompts, timing metrics, and robust completion parsing. Low‑level utilities such as a lock‑free ring buffer, OpenCL histogram kernels, and maintenance scripts for backup rotation and sales reporting underpin data‑processing pipelines, while a PostgreSQL‑based library system and MySQL‑based shop and warehouse modules provide persistent business logic and reporting for library, e‑commerce, and inventory use cases.

#### Packages
- java/config — A configuration‑driven AI‑powered source‑code transformation engine that validates file selection, extracts deterministic facts, and orchestrates AI generation steps for code generation or analysis.
- java — A runtime‑driven AI source‑code transformation engine that selects files, extracts deterministic facts, and orchestrates AI generation for code generation or analysis.
- java/provider — Provides a pluggable AI‑generation layer for source‑code transformation, supporting local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompts, timing metrics, and robust completion parsing.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — Provides low‑level byte‑stream utilities (histogram kernels, ring buffer) and higher‑level maintenance scripts (backup rotation, sales reporting) for data processing pipelines.
- other/ringbuf — Provides a lock‑free, power‑of‑two circular buffer for single‑producer, single‑consumer byte streams.
- other/scripts — Automates backup rotation and generates monthly sales revenue reports from CSV order exports.
- . — A runtime‑driven AI source‑code transformation engine that orchestrates file selection, deterministic fact extraction, and AI‑powered code generation or analysis across a multi‑language codebase.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending logic, and provides reporting views for overdue items and popular titles.
- sql — A collection of database‑centric packages that provide core data persistence, business logic, and reporting for a library, an e‑commerce shop, and a warehouse inventory system.
- sql/shop — A MySQL 8 stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.
- sql/warehouse — Keeps warehouse inventory in sync with nightly supplier feeds, recording success and error metrics.
