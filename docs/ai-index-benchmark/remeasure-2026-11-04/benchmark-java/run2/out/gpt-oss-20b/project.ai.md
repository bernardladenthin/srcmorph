### srcmorph-modelcard
- H: 1.0
- C: E566E225
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:35:18Z
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
The project orchestrates AI‑driven source‑code transformations through a Java framework that handles configuration, rule evaluation, and provider‑agnostic model execution, while a dedicated provider package selects and executes back‑ends such as llama.cpp or OpenAI services and parses responses. A lightweight byte‑stream toolkit, featuring GPU‑accelerated histogram kernels, a lock‑free ring buffer, and command‑line utilities, supports high‑performance data processing and backup tasks. The multi‑tenant SQL platform manages library, shop, and warehouse domains, with PostgreSQL for catalog and loan logic, MySQL for e‑commerce orders and inventory, and nightly syncs for warehouse stock. Together, these subsystems enable automated code generation, efficient data handling, and robust domain‑specific data management.

#### Packages
- java/config — A Java library that powers AI‑driven source‑code generation, providing configuration, condition evaluation, fact extraction, and oversized‑file handling for a Maven‑style plugin.
- java — A Java framework that orchestrates AI‑driven source‑code generation, from configuration and rule evaluation to provider‑agnostic model execution and response handling.
- java/provider — A framework for configuring, selecting, and executing AI text‑generation back‑ends—whether local llama.cpp or OpenAI‑compatible services—while parsing responses, handling errors, and measuring performance.
- other/kernels — Computes a 256‑bin byte histogram of an input byte stream using two OpenCL kernel passes.
- other — Provides a lightweight, high‑performance byte‑stream processing toolkit: a GPU‑accelerated histogram builder, a lock‑free ring buffer, and command‑line utilities for data backup and revenue reporting.
- other/ringbuf — A lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a minimal API for initialization, bulk read/write, and usage metrics.
- other/scripts — Provides utilities for backing up directories and generating monthly revenue reports from order CSVs.
- . — A framework for orchestrating AI‑driven source‑code transformations, coupled with a lightweight byte‑stream toolkit and a multi‑tenant SQL data platform for library, shop, and warehouse domains.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports checkout/return logic, tracks late fees, and provides reporting views for overdue loans, member activity, and popular titles.
- sql — A multi‑tenant data platform that manages library catalogs, e‑commerce orders, and warehouse inventory, each with its own SQL schema, business logic, and reporting layers.
- sql/shop — Provides a MySQL 8 schema and stored procedure for managing products, orders, and order items in a web shop, including stock reservation and order total calculation.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.
