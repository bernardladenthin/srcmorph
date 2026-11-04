### srcmorph-modelcard
- H: 1.0
- C: 1328F3D4
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:56:01Z
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
The srcmorph‑modelcard project is an AI‑driven source‑code transformation engine that orchestrates analysis, generation, and provider abstraction for Maven‑based Java projects, leveraging a configuration‑driven framework to evaluate file‑selection rules and extract deterministic language facts. It integrates a modular AI generation provider layer that supports local llama.cpp, OpenAI‑compatible APIs, and a mock backend, while also offering GPU‑accelerated utilities such as a 256‑bin byte‑histogram kernel and a lock‑free power‑of‑two ring buffer for efficient byte stream handling. The system persists its transformations and domain data in a robust SQL layer, comprising PostgreSQL schemas for library management, MySQL for e‑commerce shop operations, and warehouse inventory, all backed by stored procedures that enforce audit, concurrency, and reporting requirements. Utility scripts provide backup rotation and monthly sales reporting, tying together the AI, GPU, and database components into a cohesive code‑generation and analytics platform.

#### Packages
- java/config — A configuration‑driven AI‑powered source‑code analysis and generation framework that evaluates file‑selection rules, extracts deterministic language facts, and orchestrates model‑based code generation.
- java — A framework that orchestrates AI‑driven source‑code analysis, generation, and provider abstraction for Maven‑based projects.
- java/provider — A modular AI generation framework that abstracts multiple back‑ends (local llama.cpp, OpenAI‑compatible APIs, and a mock provider) and supplies utilities for request construction, response parsing, and timing metrics.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — Provides GPU‑accelerated byte‑histogram computation, a lock‑free power‑of‑two ring buffer for byte streams, and small utilities for backup rotation and sales reporting.
- other/ringbuf — A lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a minimal API for allocation, read/write, and space queries.
- other/scripts — A small utility package that backs up directories and generates monthly sales reports from CSV order exports.
- . — A unified AI‑driven source‑code transformation engine with GPU‑accelerated utilities and a robust SQL persistence layer for library, shop, and warehouse domains.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports checkout/return operations, and provides reporting views for overdue loans and popular books.
- sql — A set of relational schemas and stored procedures that manage library lending, e‑commerce orders, and warehouse inventory, each with audit, concurrency, and reporting support.
- sql/shop — A MySQL schema and stored procedure that manage a web shop’s product catalog, customer orders, and order line items, ensuring stock reservation and accurate order totals.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, logging each update.
