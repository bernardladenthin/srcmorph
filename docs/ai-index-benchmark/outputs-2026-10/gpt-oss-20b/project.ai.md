### srcmorph-modelcard
- H: 1.0
- C: C97B79A8
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:55:11Z
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
The project centers on a configuration‑driven AI engine that validates source‑code files, extracts deterministic facts, and orchestrates text generation through pluggable local or cloud providers for code‑transformation tasks. A dedicated provider subsystem exposes a unified request/response API, supporting llama.cpp inference, OpenAI‑compatible APIs, and mock providers. Complementary utilities include a lock‑free ring buffer, OpenCL byte‑histogram kernels, and shell/Python scripts for backing up directories and generating sales reports. The data layer is a multi‑tenant PostgreSQL/MySQL/SQL Server system that manages library catalog, member, loan, e‑commerce orders, and warehouse inventory, with specialized modules for a MySQL web‑shop and nightly supplier feed synchronization.

#### Packages
- java/config — A configuration‑driven AI‑powered source‑code transformation engine that validates file‑level filters, extracts deterministic code‑facts, and orchestrates AI model calls with fine‑grained generation settings.
- java — A unified, configuration‑driven AI engine that validates source‑code files, extracts deterministic facts, and orchestrates text generation via pluggable local or cloud providers for code transformation tasks.
- java/provider — A pluggable AI‑generation subsystem that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response API for source‑code transformation tooling.
- other/kernels — Computes a 256‑bin histogram of a byte stream on an OpenCL device using a two‑pass kernel strategy.
- other — Provides a lock‑free ring buffer, OpenCL byte‑histogram kernels, and shell/Python utilities for backup and sales reporting.
- other/ringbuf — Implements a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer single‑consumer data transfer.
- other/scripts — Provides utilities for backing up directories and generating monthly sales reports from CSV order data.
- . — A unified, configuration‑driven AI engine that validates source‑code files, extracts deterministic facts, and orchestrates text generation via pluggable local or cloud providers for code transformation tasks.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, enforces lending rules, tracks late fees, and provides reporting views.
- sql — A multi‑tenant data layer that manages library lending, e‑commerce orders, and warehouse inventory through PostgreSQL, MySQL, and SQL Server stored procedures, tables, and views.
- sql/shop — A MySQL 8 web‑shop module that manages order line items, reserves inventory, and keeps order totals accurate.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.
