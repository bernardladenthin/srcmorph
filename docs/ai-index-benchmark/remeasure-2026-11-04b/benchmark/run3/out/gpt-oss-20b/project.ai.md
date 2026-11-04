### srcmorph-modelcard
- H: 1.0
- C: 418A4294
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:12:29Z
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
The srcmorph‑modelcard project unifies an AI‑driven source‑generation framework with low‑level performance utilities and cross‑domain database schemas. The core Java packages configure file‑selection rules, orchestrate model runs, and provide pluggable inference back‑ends (llama.cpp, OpenAI‑compatible APIs, or mocks) for generating deterministic, language‑agnostic code artifacts. Supporting this, the “other” utilities expose OpenCL kernels for byte‑histogramming, a lock‑free ring buffer for non‑blocking byte streams, and command‑line tools for data processing and reporting. The SQL subpackages expose PostgreSQL, MySQL, and SQL Server schemas and stored procedures that manage library lending, e‑commerce orders, and warehouse inventory, enabling synchronized data flows and audit trails across the system.

#### Packages
- java/config — A configuration‑driven AI source‑generation framework that evaluates file‑selection rules, extracts deterministic language‑agnostic facts, and orchestrates model runs with fine‑grained tuning.
- java — A framework that configures, selects, and generates source‑code artifacts using AI models, with pluggable local or remote inference back‑ends.
- java/provider — Provides a pluggable AI text‑generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and a mock provider, along with utilities for request construction, response parsing, and configuration.
- other/kernels — Computes a 256‑bin byte histogram of an image or byte stream using two OpenCL kernel passes.
- other — A collection of low‑level utilities for image histogramming, byte‑stream buffering, and command‑line data processing.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling non‑blocking FIFO read/write between a single producer and consumer.
- other/scripts — A utility package that provides command‑line tools for backing up directories and generating monthly sales reports from CSV order exports.
- . — A unified framework for AI‑driven source‑code generation, low‑level performance utilities, and cross‑domain database schemas, all orchestrated to support automated code synthesis, data processing, and business rule enforcement.
- sql/library — A PostgreSQL‑based library management system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails for lending operations.
- sql — A collection of database schemas and stored procedures that manage library lending, e‑commerce orders, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.
- sql/shop — A MySQL schema and stored procedure for managing a web‑shop’s product catalog, customer orders, and order line items, including stock reservation and total calculation.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, logging each run and handling inserts, updates, and errors.
