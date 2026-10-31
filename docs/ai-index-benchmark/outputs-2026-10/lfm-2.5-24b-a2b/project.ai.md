### srcmorph-modelcard
- H: 1.0
- C: D983C0B8
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:05:17Z
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
The source morphing AI pipeline is implemented across the java/config, java, and java/provider packages, which together provide AI condition evaluation, fact extraction, generation configuration, oversize handling, and pluggable OpenAI-compatible text generation backends. OpenCL-based byte frequency histogram computation is handled by the other/kernels, other, and other/ringbuf packages using parallel two-pass algorithms and fixed-capacity circular buffers for streaming data. SQL data management for lending, e-commerce, and warehouse stock synchronization is provided by the sql/library, sql, sql/shop, and sql/warehouse packages, covering PostgreSQL lending, MySQL e-commerce orders, and nightly stock merge logging. Supporting operations include automated backup rotation with configurable retention and monthly sales revenue reporting from order data via the other/scripts package.

#### Packages
- java/config — This package provides AI condition evaluation, fact extraction, generation configuration, oversize handling, and configuration management for source morphing operations.
- java — Provides AI condition evaluation, fact extraction, generation configuration, oversize handling, and configuration management for source morphing operations.
- java/provider — Provides pluggable AI text generation backends and utilities for parsing LLM completions and supporting offline OpenAI-compatible generation within the srcmorph package.
- other/kernels — This package provides OpenCL kernels that compute a 256-bin byte frequency histogram over input byte streams using a two-pass parallel algorithm.
- other — This package provides OpenCL kernels for parallel byte frequency histogram computation, a fixed-capacity circular byte buffer for streaming data, and scripts for backup rotation and monthly sales revenue reporting.
- other/ringbuf — Provides a fixed-capacity circular byte buffer for efficient single-producer single-consumer data streaming with read/write operations.
- other/scripts — Provides automated backup rotation with configurable retention and monthly sales revenue reporting from order data.
- . — This package provides AI condition evaluation, fact extraction, and generation configuration for source morphing, OpenCL-based byte frequency histogram computation with ring buffers, and SQL data management for lending, e-commerce, and warehouse stock synchronization.
- sql/library — A PostgreSQL lending library package that manages book inventory, member accounts, loan transactions, and reporting views for tracking overdue loans, member statistics, and popular books.
- sql — This package provides a PostgreSQL lending library, a MySQL e-commerce order system, and a SQL Server warehouse stock synchronization service for managing books, products, and inventory.
- sql/shop — Provides e-commerce data modeling and MySQL stored procedures for managing a product catalog, customer orders, and order placement with stock reservation.
- sql/warehouse — Implements nightly warehouse stock synchronization by merging supplier feed quantities into stock levels and recording the merge outcome in a sync log.
