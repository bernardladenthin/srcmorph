### srcmorph-modelcard
- H: 1.0
- C: 867E3E01
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:09:25Z
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
The project is a unified AI‑driven source‑generation framework that evaluates file‑level filters, extracts deterministic facts, and orchestrates model‑specific generation settings across local and OpenAI‑compatible providers. It is built on a Maven‑based workflow and includes a modular provider layer that abstracts inference, request construction, and response parsing for code‑generation tasks. Complementary GPU‑accelerated image‑processing utilities compute byte histograms via OpenCL kernels and expose a lock‑free ring buffer for efficient streaming. The framework also bundles a PostgreSQL‑centric SQL library for catalog, inventory, and order management, with additional MySQL schemas for web‑shop and warehouse synchronization.

#### Packages
- java/config — A configuration‑driven AI source‑generation engine that evaluates file‑level filters, extracts deterministic facts, and orchestrates model‑specific generation settings for a Maven‑based code‑generation workflow.
- java — A Maven‑based AI source‑generation engine that evaluates file‑level filters, extracts deterministic facts, and orchestrates model‑specific generation settings for code‑generation tasks.
- java/provider — A modular AI generation framework that abstracts local and OpenAI‑compatible back‑ends, providing configurable inference, request construction, and response parsing for code‑generation tasks.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — A lightweight image‑processing and data‑transfer toolkit that builds byte histograms on GPU, offers a lock‑free ring buffer for streaming, and supplies shell/Python helpers for backup and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — Provides utilities for backing up directories and generating monthly sales revenue reports from CSV order exports.
- . — A unified AI‑driven source‑generation framework that evaluates file‑level filters, extracts deterministic facts, and orchestrates model‑specific generation settings across local and OpenAI‑compatible providers, complemented by GPU‑accelerated image histogram tooling and a SQL library for catalog, inventory, and order management.
- sql/library — A PostgreSQL library system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails for check‑outs and returns.
- sql — A collection of SQL libraries that manage catalog, inventory, and order data across PostgreSQL, MySQL, and SQL Server, providing core business logic, reporting, and synchronization.
- sql/shop — A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and order total calculation.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.
