### srcmorph-modelcard
- H: 1.0
- C: 920AA6FD
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:56:04Z
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
The project centers on an AI‑driven source‑code transformation framework that is configuration‑driven and orchestrated through a Maven‑based build pipeline. It integrates a modular provider layer that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock backends, while a lightweight OpenCL toolkit accelerates data processing by computing byte‑frequency histograms and supplying a lock‑free circular buffer for streaming. SQL packages supply persistent storage for library cataloging, e‑commerce orders, and warehouse inventory, each with dedicated models, business logic, and audit/reporting capabilities, all tied together by the unified framework’s validation and modular provider architecture.

#### Packages
- java/config — A configuration and evaluation framework that drives AI‑powered source‑code transformation jobs, handling file selection, fact extraction, and generation settings for a Maven‑based build pipeline.
- java — A framework that orchestrates AI‑driven source‑code transformations, from configuration‑driven file selection and fact extraction to pluggable text‑generation backends.
- java/provider — A modular AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable request building, response parsing, and timing metrics.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — A lightweight, OpenCL‑accelerated image‑processing toolkit that builds byte‑frequency histograms, supplies a lock‑free circular buffer for streaming data, and offers shell/Python helpers for backup and sales reporting.
- other/ringbuf — A lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — Provides utilities for backing up directories and generating monthly sales reports from CSV order data.
- . — A unified framework for AI‑driven source‑code transformation, GPU‑accelerated data processing, and domain‑specific SQL persistence, all orchestrated through configuration, validation, and modular providers.
- sql/library — A PostgreSQL library management system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails.
- sql — A collection of SQL packages that manage library cataloging, e‑commerce orders, and warehouse inventory, each with dedicated data models, business logic, and audit/reporting capabilities.
- sql/shop — A MySQL schema for a web shop that stores products, orders, and line items, and provides a stored procedure to add items, reserve stock, and update order totals.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.
