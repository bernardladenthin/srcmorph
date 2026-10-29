### srcmorph-modelcard
- H: 1.0
- C: 7363E824
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:43:53Z
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
The project orchestrates AI-driven code indexing, parallel system utility kernels, and multi-domain relational inventory management. The Java packages manage pluggable AI text generation backends, including local llama.cpp and OpenAI-compatible APIs, to filter files and generate text for source morphing. Relational operations handle library circulation, web shop order placement, and warehouse synchronization via transactional MERGE operations in PostgreSQL, while utility kernels calculate byte histograms through parallel OpenCL reduction and scripts automate system backup rotation and financial data aggregation.

#### Packages
- java/config — Validates and evaluates AI file filtering conditions and configuration trees for source morphing indexing.
- java — Manages AI-driven source code indexing by filtering files based on conditions and generating text via pluggable inference backends.
- java/provider — Manages pluggable AI text generation backends including local llama.cpp inference and OpenAI-compatible APIs with configuration and timing metrics.
- other/kernels — Calculates 256-bin byte histograms of image or byte stream data via parallel OpenCL reduction.
- other — Implements parallel byte histogram kernels, circular buffer streaming, and automated backup and financial reporting scripts.
- other/ringbuf — Implements a fixed-capacity circular buffer for streaming byte data with automatic wrap-around handling.
- other/scripts — This package provides utility scripts for automated system backup rotation and financial sales data aggregation.
- . — Orchestrates AI-driven code indexing, parallel system utility kernels, and multi-domain relational inventory management.
- sql/library — Manages library inventory, member circulation, and loan lifecycle operations within a PostgreSQL relational database.
- sql — Orchestrates inventory management, member circulation, and order processing across library, retail, and warehouse domains using relational database operations.
- sql/shop — Manages web shop order placement and inventory reservation logic.
- sql/warehouse — Synchronizes warehouse inventory state from supplier feed data using transactional MERGE operations with audit logging.
