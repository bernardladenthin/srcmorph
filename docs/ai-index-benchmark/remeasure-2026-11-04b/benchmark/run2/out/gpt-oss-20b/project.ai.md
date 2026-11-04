### srcmorph-modelcard
- H: 1.0
- C: 1D7D7144
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:04:34Z
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
The srcmorph‑modelcard framework orchestrates AI‑driven source‑code transformation through a Maven‑based pipeline that routes requests to modular back‑ends such as OpenAI‑compatible services, llama.cpp JNI, or mock providers, while a configuration‑driven engine selects files and manages context. It couples this generation engine with a low‑latency GPU analytics subsystem that computes 256‑bin histograms of byte streams via OpenCL kernels and a lock‑free C ring buffer, enabling efficient data‑flow and backup/reporting utilities. The same project also bundles a cross‑platform SQL data‑management suite: a PostgreSQL library system, a MySQL shop module for order line items and inventory reservation, and a warehouse sync component that processes nightly supplier feeds, all sharing audit trails and business logic. Together, these subsystems provide a unified platform for AI‑powered code generation, real‑time analytics, and robust data‑management.

#### Packages
- java/config — A configuration‑driven framework for AI‑powered source‑code generation, routing, and fact extraction within a Maven‑based build pipeline.
- java — A Maven‑based framework that configures, evaluates, and drives AI‑powered source‑code generation and transformation, supporting multiple back‑ends and runtime decision‑making for file selection and context handling.
- java/provider — A modular AI‑generation framework that abstracts multiple back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) and provides configuration, request parsing, and timing support for source‑code transformation tasks.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — Provides a lightweight, lock‑free byte‑stream histogram pipeline and utilities for ring‑buffer handling and backup/reporting tasks.
- other/ringbuf — Provides a lock‑free, fixed‑size ring buffer for byte stream handling in C.
- other/scripts — A small utility package that backs up directories and generates monthly sales reports from CSV exports.
- . — A unified framework for AI‑driven source‑code transformation, coupled with low‑latency GPU analytics and a multi‑domain SQL data‑management suite.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, enforces business rules, and provides reporting and audit capabilities.
- sql — A cross‑platform data‑management suite that handles library cataloging, e‑commerce order processing, and warehouse inventory synchronization, all built around SQL‑centric business logic and audit trails.
- sql/shop — A MySQL package that manages order line items, reserves inventory, and keeps order totals accurate.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.
