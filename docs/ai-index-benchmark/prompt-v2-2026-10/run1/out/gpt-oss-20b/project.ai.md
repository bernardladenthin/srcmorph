### srcmorph-modelcard
- H: 1.0
- C: CFFCB00C
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:44:14Z
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
The project is a Maven‑plugin‑friendly AI‑generation framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI model invocation for source‑code summarisation and transformation. It supports multiple AI back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) through a modular provider package, while also offering GPU‑accelerated histogram utilities backed by a lock‑free ring buffer for high‑performance data analysis. The framework is complemented by a set of PostgreSQL and MySQL schemas that manage library, shop, and warehouse data, enforce business rules, and provide reporting and audit trails. Utility scripts and a two‑pass OpenCL implementation further enable routine data processing, backup, and image‑processing workloads.

#### Packages
- java/config — A configuration‑driven AI‑generation framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI model invocation for source‑code summarisation and transformation.
- java — A Maven‑plugin‑friendly framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI model invocation for source‑code summarisation and transformation.
- java/provider — A modular AI generation framework that supports multiple back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) and provides utilities for request construction, response parsing, and timing metrics.
- other/kernels — Provides a two‑pass OpenCL implementation for computing a 256‑bin histogram of arbitrary byte data, suitable for image processing or generic stream analysis.
- other — A GPU‑accelerated histogram engine backed by a lock‑free ring buffer and a set of shell/Python utilities for data backup and sales reporting.
- other/ringbuf — Implements a lock‑free, power‑of‑two circular buffer for byte streams, exposing a simple API for initialization, read/write, and space queries.
- other/scripts — A collection of utility scripts for routine data processing and backup management.
- . — A Maven‑plugin framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI model invocation for source‑code summarisation and transformation, while also providing GPU‑accelerated histogram utilities and a set of SQL schemas for library, shop, and warehouse domains.
- sql/library — A PostgreSQL‑based library management system that defines core data structures, reporting views, business logic for check‑outs and returns, and a migration to a branch‑based copy model.
- sql — A collection of database schemas and stored procedures that manage library, shop, and warehouse data, enforce business rules, and provide reporting and audit trails.
- sql/shop — A MySQL schema and stored procedure for managing a web shop’s catalog, orders, and inventory.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, logging each update.
