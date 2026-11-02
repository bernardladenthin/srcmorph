### srcmorph-modelcard
- H: 1.0
- C: 623846A6
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:04:40Z
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
The project centers on a configuration‑driven AI source‑morphing engine that indexes code, applies file‑selection rules, extracts deterministic facts, and orchestrates AI generation for code summaries and transformations. It exposes a pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, all wrapped in a unified request/response API with timing metrics. Complementary GPU‑accelerated utilities compute byte‑histograms via OpenCL kernels, provide a lock‑free ring buffer for streaming data, and offer command‑line tools for backup rotation and sales reporting. The database layer spans PostgreSQL, MySQL, and SQL Server, managing library lending, e‑commerce order lines, and warehouse inventory synchronization with audit and reporting capabilities.

#### Packages
- java/config — A configuration‑driven AI‑powered source‑morphing engine that indexes code files, evaluates selection rules, extracts deterministic facts, and orchestrates AI generation for code summaries and transformations.
- java — A configuration‑driven AI source‑morphing engine that indexes code, evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI generation for code summaries and transformations.
- java/provider — A pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response API with timing metrics and robust request construction.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — Provides GPU‑accelerated byte‑histogram computation, a lock‑free ring buffer for streaming data, and command‑line utilities for backup rotation and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer, single‑consumer data transfer.
- other/scripts — A utility package that provides a shell script for rotating directory backups and a Python tool for summarizing monthly sales revenue from CSV exports.
- . — A configuration‑driven AI source‑morphing engine that indexes code, evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI generation for code summaries and transformations.
- sql/library — A PostgreSQL‑based library lending system that stores catalog, member, and loan data, enforces borrowing rules, tracks audit events, and provides reporting views.
- sql — A collection of database‑centric modules that manage library lending, e‑commerce order lines, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.
- sql/shop — A MySQL 8 stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, logging each update.
