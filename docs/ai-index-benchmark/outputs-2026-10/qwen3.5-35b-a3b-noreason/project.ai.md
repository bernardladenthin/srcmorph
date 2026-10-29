### srcmorph-modelcard
- H: 1.0
- C: 46E450A2
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:58:31Z
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
The srcmorph-modelcard project integrates AI-driven source code analysis and generation through a pluggable provider architecture that supports local and remote language models with strict configuration validation. This core intelligence layer is complemented by high-performance parallel data processing utilities, including OpenCL-based byte frequency histograms and lock-free circular buffers for efficient stream management. Underpinning these computational and analytical capabilities is a robust SQL data layer that enforces transactional integrity across multi-branch library lending, atomic shop order processing, and warehouse inventory synchronization. Operational scripts and utilities further support the ecosystem by automating backup rotation and aggregating financial reporting data from diverse domain sources.

#### Packages
- java/config — Provides configuration, validation, and execution logic for AI-driven source code analysis, file filtering, and generation parameter management within the srcmorph build tool.
- java — Orchestrates AI-driven source code analysis and generation by managing configuration validation, file filtering logic, and pluggable backend execution for local or remote language models.
- java/provider — Provides a pluggable architecture for local and remote AI text generation, managing model lifecycle, configuration, and response sanitization.
- other/kernels — Computes 256-bin byte frequency distributions from image or stream data using a two-pass OpenCL parallel reduction strategy.
- other — Provides parallel histogram computation, lock-free circular buffering, and operational utilities for backup rotation and financial reporting.
- other/ringbuf — Provides a lock-free, single-producer single-consumer circular buffer for efficient byte stream management with power-of-two capacity constraints.
- other/scripts — Provides operational utilities for automated backup rotation and financial reporting aggregation from CSV order exports.
- . — Orchestrates AI-driven code analysis and generation while providing parallel data processing, operational utilities, and transactional data layers for library, e-commerce, and warehouse domains.
- sql/library — Manages the complete data lifecycle of a multi-branch lending library system, from core inventory modeling and transactional logic to reporting analytics and schema evolution.
- sql — Provides a unified data layer for multi-branch library lending, atomic shop order processing, and batch warehouse inventory synchronization with strict transactional integrity and audit capabilities.
- sql/shop — Atomic MySQL stored procedure to add items to a shop order while validating stock, reserving inventory, and recalculating totals.
- sql/warehouse — Orchestrates nightly batch merging of supplier feed data into warehouse inventory levels with transactional integrity and audit logging.
