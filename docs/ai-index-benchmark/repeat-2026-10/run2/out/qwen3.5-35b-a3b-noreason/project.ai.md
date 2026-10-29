### srcmorph-modelcard
- H: 1.0
- C: F1100E8C
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:06:21Z
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
The project unifies AI-driven source code summarization with high-performance parallel data processing and multi-domain transactional logic. The core orchestration layer coordinates configuration validation and pluggable text generation backends, while the parallel subsystem leverages OpenCL kernels and lock-free ring buffers for efficient histogram computation and binary streaming. These capabilities support distinct persistence domains, including a multi-branch library system with schema evolution, retail order processing with atomic stock reservation, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server environments.

#### Packages
- java/config — Central configuration and evaluation engine for AI-driven source code summarization, managing file filtering, condition matching, generation parameters, and deterministic fact extraction.
- java — Orchestrates AI-driven source code summarization by managing configuration validation, file filtering, and pluggable text generation across local and remote backends.
- java/provider — Provides a pluggable architecture for local and remote AI text generation, managing model lifecycle, configuration, and response parsing for llama.cpp and OpenAI-compatible backends.
- other/kernels — Computes 256-bin byte frequency distributions from image or stream data using a two-pass parallel OpenCL reduction algorithm.
- other — Provides high-performance parallel histogram computation, lock-free streaming buffers, and operational utilities for backup rotation and financial reporting.
- other/ringbuf — Provides a lock-free, single-producer/single-consumer circular buffer for efficient binary data streaming with power-of-two sizing and O(1) operations.
- other/scripts — Provides operational utilities for automated backup rotation and financial revenue aggregation from CSV exports.
- . — Orchestrates AI-driven source code summarization, high-performance parallel data processing, and multi-database transactional logic for library, retail, and supply chain domains.
- sql/library — Manages the complete data lifecycle for a multi-branch lending library, from core inventory and patron modeling to transactional processing, audit logging, and historical schema evolution.
- sql — Provides domain-specific data persistence and transactional logic for library lending, retail order processing, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server environments.
- sql/shop — MySQL stored procedure that atomically adds line items to an order by validating stock, reserving inventory, and recalculating order totals.
- sql/warehouse — Nightly T-SQL stored procedure that merges supplier feed quantities into warehouse stock levels and logs the sync outcome.
