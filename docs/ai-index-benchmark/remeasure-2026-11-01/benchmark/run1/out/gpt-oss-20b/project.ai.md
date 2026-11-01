### srcmorph-modelcard
- H: 1.0
- C: AC2566C5
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:30:08Z
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
The project centers on a Maven‑based AI code‑generation framework that evaluates source files against configurable rules, extracts deterministic facts, and delegates text generation to pluggable local or remote AI back‑ends. Configuration and rule evaluation are handled by the java/config package, while the java/provider package supplies a unified request/response contract for local llama.cpp inference, OpenAI‑compatible APIs, and mock back‑ends. Complementing the AI subsystem is a lightweight, high‑performance byte‑stream toolkit in the other package, which includes GPU‑accelerated histograms, a lock‑free ring buffer, and command‑line utilities for data backup and sales reporting. The sql subpackages provide relational schemas and stored procedures for a PostgreSQL library system, a MySQL web shop, and a warehouse inventory, each enforcing its own business rules and audit mechanisms.

#### Packages
- java/config — Provides AI‑powered source‑code analysis, fact extraction, and generation configuration for a Maven‑based code‑generation plugin.
- java — A Maven‑based AI code‑generation framework that evaluates source files against configurable rules, extracts deterministic facts, and delegates text generation to pluggable local or remote AI back‑ends.
- java/provider — Provides a pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock back‑ends, exposing a unified request/response contract and configurable prompt handling.
- other/kernels — Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.
- other — Provides a lightweight, high‑performance toolkit for byte‑stream processing, including GPU‑accelerated histograms, a lock‑free ring buffer, and command‑line utilities for data backup and sales reporting.
- other/ringbuf — Implements a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — Provides utilities for backing up directories and generating monthly sales reports from CSV order exports.
- . — A Maven‑based AI code‑generation framework that evaluates source files against configurable rules, extracts deterministic facts, and delegates text generation to pluggable local or remote AI back‑ends.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, enforces lending rules, tracks overdue items, and supports branch‑level inventory.
- sql — A set of relational schemas and stored procedures that manage catalog, inventory, and order data across a library, an online shop, and a warehouse, each with its own business rules and audit mechanisms.
- sql/shop — Provides a MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and total calculation.
- sql/warehouse — Synchronises nightly supplier stock feeds into a warehouse inventory table and records the sync outcome.
