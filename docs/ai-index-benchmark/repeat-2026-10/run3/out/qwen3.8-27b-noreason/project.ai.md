### srcmorph-modelcard
- H: 1.0
- C: E03722E2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:51:33Z
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
The srcmorph-modelcard project centers on an AI-assisted source code summarization and indexing pipeline, utilizing a pluggable provider abstraction to support both local llama.cpp JNI inference and remote OpenAI-compatible APIs for fact extraction and condition evaluation. This core Java subsystem is complemented by high-performance data processing primitives, including OpenCL kernels for parallel byte histogram computation and a lock-free ring buffer for efficient single-producer/single-consumer byte streaming. Additionally, the repository includes transactional SQL schemas and business logic for managing library lending, e-commerce order processing, and warehouse stock synchronization, alongside operational scripts for backup rotation and financial reporting.

#### Packages
- java/config — Defines the configuration model, condition evaluation, and fact extraction logic for AI-assisted source code summarization and indexing in the srcmorph build pipeline.
- java — Defines the configuration model, condition evaluation, fact extraction, and pluggable AI generation provider abstraction (local llama.cpp JNI and remote OpenAI-compatible) for the srcmorph source code summarization and indexing pipeline.
- java/provider — Provides a pluggable abstraction for AI text generation backends, supporting local llama.cpp JNI inference and remote OpenAI-compatible APIs, with configuration management, response parsing, and timing metrics for source-morphing tasks.
- other/kernels — Provides OpenCL kernels for computing a 256-bin byte histogram of image or stream data via parallel partial counting and global reduction.
- other — Provides a heterogeneous toolkit combining GPU-accelerated histogram computation, a lock-free ring buffer for byte streaming, and operational scripts for backup rotation and financial sales reporting.
- other/ringbuf — Provides a fixed-capacity, power-of-two byte ring buffer for lock-free, single-producer/single-consumer circular data storage.
- other/scripts — Provides operational utilities for automated backup rotation and financial sales reporting from order data.
- . — Provides a multi-language toolkit for AI-driven source code summarization, high-performance data processing primitives, and transactional database schemas for library, e-commerce, and warehouse domains.
- sql/library — Manages the data model, business logic, and reporting for a multi-branch lending library system, handling book inventory, member accounts, loan transactions, and late-fee calculations.
- sql — Provides SQL-based data models and transactional logic for library lending, e-commerce order processing, and warehouse stock synchronization.
- sql/shop — Defines the relational schema and transactional logic for a web shop's product catalog, customer orders, and order line items, including stock enforcement and total recalculation.
- sql/warehouse — Nightly warehouse stock synchronisation that merges supplier feed quantities into on-hand stock levels and logs the sync outcome.
