### srcmorph-modelcard
- H: 1.0
- C: 7C044CFE
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:22:44Z
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
The project centers on a modular AI‑powered source‑code transformation framework that exposes a Maven‑style JavaBean API for configuration, file selection, fact extraction, and post‑processing. It relies on a pluggable provider layer that supports local llama.cpp, OpenAI‑compatible, or mock back‑ends, handling prompt construction, response parsing, and timing metrics. Low‑level utilities such as a lock‑free ring buffer, two‑pass OpenCL histogram kernels, and shell/Python scripts provide efficient byte‑stream handling and automated backup/reporting. Complementary SQL schemas manage catalog, inventory, and order data across library, e‑commerce, and warehouse domains, enabling analytics and workflow automation.

#### Packages
- java/config — A configuration‑driven framework for AI‑powered source‑code indexing, fact extraction, and generation, exposing a Maven‑style JavaBean API for runtime control.
- java — A framework that configures, selects, and generates AI‑powered source‑code transformations, exposing a Maven‑style JavaBean API and pluggable provider back‑ends for local or remote inference.
- java/provider — A modular AI generation framework that supplies pluggable back‑ends (local llama.cpp, OpenAI‑compatible, or mock) for source‑code transformation tasks, handling prompt construction, response parsing, and timing metrics.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two‑pass OpenCL kernels.
- other — Provides a lightweight, lock‑free ring buffer for byte streams, a two‑pass OpenCL histogram kernel, and shell/Python utilities for backup rotation and sales reporting.
- other/ringbuf — Implements a lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a simple API for single‑producer/single‑consumer use.
- other/scripts — Provides utilities for automated backup rotation and monthly sales revenue reporting.
- . — A modular AI‑powered source‑code transformation framework with pluggable local/remote inference, supporting configuration, file selection, fact extraction, and post‑processing, complemented by low‑level utilities and a multi‑domain SQL schema for catalog, inventory, and order management.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports checkout/return workflows, and provides analytical views for reporting.
- sql — A collection of SQL schemas and procedures that manage catalog, inventory, and order data across a library, e‑commerce shop, and warehouse system.
- sql/shop — A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, with automated stock reservation and order total calculation.
- sql/warehouse — Synchronises nightly supplier stock feeds into the warehouse inventory and records the sync outcome.
