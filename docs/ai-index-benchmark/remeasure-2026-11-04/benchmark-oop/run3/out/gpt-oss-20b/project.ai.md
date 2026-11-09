### srcmorph-modelcard
- H: 1.0
- C: EF44F5E8
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:57:25Z
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
The srcmorph‑modelcard project is a Maven‑based framework that orchestrates AI‑driven source‑code generation, validation, and file‑selection through a configuration‑driven plugin architecture. It exposes a pluggable inference provider layer that supports local llama.cpp, mock, and OpenAI‑compatible HTTP backends, while low‑level utilities in the other package provide byte‑level data processing, lock‑free ring buffers, and command‑line tools for backups and revenue reporting. The SQL sub‑packages supply a PostgreSQL library management system and a MySQL‑based shop and warehouse modules, each offering stored procedures, functions, and audit mechanisms to enforce business rules and maintain transactional integrity across lending, e‑commerce, and inventory synchronization. Together, these subsystems enable end‑to‑end AI‑assisted code generation, validation, and data‑centric business logic within a single Maven project.

#### Packages
- java/config — A configuration‑driven framework for AI‑powered source‑code generation, validation, and file‑selection logic used by the Maven srcmorph plugin.
- java — A Maven plugin framework that orchestrates AI‑driven source‑code generation, validation, and file‑selection through configurable prompts, models, and pluggable inference backends.
- java/provider — Provides a pluggable AI generation framework that supports local llama.cpp inference, mock generation, and OpenAI‑compatible HTTP backends, exposing a unified request/response API and timing metrics.
- other/kernels — Computes a 256‑bin byte histogram of an input byte stream using two OpenCL kernel passes.
- other — Provides low‑level utilities for byte‑level data processing, circular buffering, and simple command‑line tooling for backups and revenue reporting.
- other/ringbuf — A lock‑free, power‑of‑two ring buffer for single‑producer/single‑consumer byte streams.
- other/scripts — Provides command‑line utilities for backing up directories and generating monthly revenue reports from order CSV exports.
- . — A Maven‑based framework that orchestrates AI‑driven source‑code generation, validation, and file‑selection, backed by pluggable local or remote inference engines and a set of low‑level utilities for data processing and database‑centric business logic.
- sql/library — A PostgreSQL library management system that stores catalog, member, and loan data, enforces business rules, and provides reporting and audit capabilities.
- sql — A collection of database‑centric modules that manage library lending, e‑commerce order lines, and warehouse stock synchronization, each exposing stored procedures, functions, and audit mechanisms for transactional integrity and reporting.
- sql/shop — A MySQL 8 stored procedure that adds or updates an order line, reserves stock, and updates the order total in a web‑shop.
- sql/warehouse — Keeps the warehouse inventory in sync with incoming supplier feeds and records each sync run.
