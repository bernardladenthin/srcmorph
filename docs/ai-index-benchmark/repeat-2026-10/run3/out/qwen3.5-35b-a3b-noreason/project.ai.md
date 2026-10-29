### srcmorph-modelcard
- H: 1.0
- C: 3C3756CC
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:45:50Z
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
The srcmorph-modelcard project unifies deterministic AI-driven code summarization and generation with high-performance byte-level data analysis and multi-domain inventory management. The Java subsystem orchestrates inference through pluggable backends like llama.cpp and OpenAI, while the other package group handles parallel byte-frequency computation via OpenCL and efficient streaming buffers. Complementing these, the SQL layer manages the complete lifecycle of a lending library system and synchronizes supply chain data across retail and warehouse domains through atomic stored procedures.

#### Packages
- java/config — Provides configuration management, condition evaluation, and deterministic fact extraction for AI-driven source code summarization and generation within the srcmorph engine.
- java — Orchestrates deterministic AI-driven source code summarization and generation by managing configuration, file filtering, and pluggable inference backends.
- java/provider — Provides a pluggable architecture for AI text generation that supports local llama.cpp inference, OpenAI-compatible APIs, and mock backends while handling prompt construction, response parsing, and security validation.
- other/kernels — Computes 256-bin byte frequency histograms from image or byte streams using a two-pass OpenCL reduction strategy.
- other — Provides parallel byte-frequency analysis, lock-free streaming buffers, and operational scripts for backup rotation and financial reporting.
- other/ringbuf — Provides a fixed-capacity, single-producer single-consumer circular buffer for efficient byte streaming with power-of-two optimized indexing.
- other/scripts — Provides utility scripts for automated backup rotation and financial revenue aggregation from CSV exports.
- . — Orchestrates deterministic AI-driven code generation, high-performance byte-level data analysis, and multi-domain inventory transaction management across Java, C, and SQL layers.
- sql/library — Manages the complete lifecycle of a multi-branch lending library system, from core inventory and member data modeling to transactional lending logic, audit trails, and analytical reporting.
- sql — Provides a unified data layer for inventory management, lending operations, and supply chain synchronization across library, retail, and warehouse domains.
- sql/shop — MySQL stored procedure to atomically add line items to customer orders, validate inventory availability, and recalculate order totals.
- sql/warehouse — Nightly T-SQL stored procedure that merges supplier feed quantities into warehouse stock levels and logs the synchronization outcome.
