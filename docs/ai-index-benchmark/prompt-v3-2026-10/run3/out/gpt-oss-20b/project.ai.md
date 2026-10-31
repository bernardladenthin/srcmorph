### srcmorph-modelcard
- H: 1.0
- C: 4FC2FB9A
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:47:45Z
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
The core of srcmorph-modelcard is a configuration‑driven AI‑powered source‑code transformation engine that evaluates file‑selection rules, extracts language‑agnostic facts, and orchestrates model‑based generation for documentation or refactoring. This engine is built on a modular AI‑generation framework that supplies pluggable back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) and exposes a clean request/response API, handling configuration, timing, and response parsing. Complementary GPU‑accelerated utilities in the other package provide a lock‑free ring buffer and OpenCL‑based byte‑histogram computation, while shell/Python scripts automate backup rotation and generate sales reports. The project also includes a PostgreSQL‑based library management system and a set of SQL modules for library lending, e‑commerce orders, and warehouse inventory, each with its own data model, business logic, and audit trail.

#### Packages
- java/config — A configuration‑driven AI‑powered source‑code transformation engine that evaluates file‑selection rules, extracts language‑agnostic facts, and orchestrates model‑based generation for documentation or refactoring.
- java — A configurable AI‑powered source‑code transformation engine that evaluates file‑selection rules, extracts language‑agnostic facts, and orchestrates model‑based generation for documentation or refactoring.
- java/provider — A modular AI‑generation framework that supplies pluggable back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) for source‑code transformation tasks, exposing a clean request/response API and handling configuration, timing, and response parsing.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL passes.
- other — Provides GPU‑accelerated byte‑histogram computation, a lock‑free ring buffer for byte streams, and shell/Python utilities for backup rotation and sales reporting.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized byte ring buffer for single‑producer, single‑consumer use.
- other/scripts — Automates backup rotation and generates monthly revenue reports from order CSVs.
- . — A configurable AI‑powered source‑code transformation engine that evaluates file‑selection rules, extracts language‑agnostic facts, and orchestrates model‑based generation for documentation or refactoring.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending, returns, late‑fee calculation, and provides reporting views and audit trails.
- sql — A set of SQL modules that manage library lending, e‑commerce orders, and warehouse inventory, each with its own data model, business logic, and audit trail.
- sql/shop — Provides the core data model and stored procedure for a web shop’s product catalog, orders, and order items, including stock reservation and total calculation.
- sql/warehouse — Synchronises nightly supplier stock feeds into a warehouse inventory, logging changes and errors.
