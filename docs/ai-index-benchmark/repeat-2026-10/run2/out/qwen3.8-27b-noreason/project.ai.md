### srcmorph-modelcard
- H: 1.0
- C: CC70DECF
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:11:09Z
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
The srcmorph-modelcard project integrates an AI-driven source code summarization pipeline with high-performance parallel data processing and multi-domain inventory management. The Java subsystem configures and executes AI inference using pluggable backends, including local llama.cpp JNI and remote OpenAI-compatible APIs, to generate code summaries and indices. Concurrently, the native components provide a heterogeneous toolkit featuring OpenCL kernels for byte histogram computation and lock-free ring buffers for circular data storage. These capabilities are complemented by operational scripts for backup rotation and sales analytics, as well as a multi-database layer that manages library circulation, e-commerce order processing, and warehouse stock reconciliation.

#### Packages
- java/config — Configures and validates the parameters, exclusion rules, and generation settings for AI-driven source code summarization and indexing within the srcmorph build pipeline.
- java — Provides the configuration schema and pluggable inference backends for AI-driven source code summarization and indexing within the srcmorph build pipeline.
- java/provider — Provides a pluggable AI text-generation layer for source-morphing pipelines, supporting local llama.cpp JNI inference, OpenAI-compatible remote APIs, and mock backends with configurable sampling, timing metrics, and response parsing.
- other/kernels — Provides a two-pass OpenCL kernel implementation for computing a 256-bin byte histogram from image or stream data.
- other — Provides a heterogeneous toolkit for parallel byte histogram computation, lock-free circular buffering, and operational data processing for backups and sales analytics.
- other/ringbuf — Provides a fixed-capacity, power-of-two byte ring buffer for lock-free single-producer/single-consumer circular data storage.
- other/scripts — Provides operational utilities for automated backup rotation with retention policies and regional sales revenue aggregation from order data.
- . — Provides a unified platform for AI-driven source code summarization, high-performance parallel data processing, and multi-database inventory management across library, e-commerce, and warehouse domains.
- sql/library — PostgreSQL database schema and logic for a multi-branch lending library system, managing book inventory, member accounts, loan circulation, and reporting.
- sql — Multi-database inventory and transaction management system handling library circulation, e-commerce order processing, and warehouse stock reconciliation.
- sql/shop — MySQL schema and stored procedure for a web shop, managing product inventory, customer orders, and order line items with atomic stock reservation and total recalculation.
- sql/warehouse — Nightly warehouse stock reconciliation that merges supplier feed quantities into per-warehouse SKU stock levels and logs the outcome.
