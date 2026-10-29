### srcmorph-modelcard
- H: 1.0
- C: 2412768E
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:08:52Z
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
This project integrates AI-driven source code analysis and generation, managed by the core java package, with specialized persistence layers for library lending, e-commerce inventory, and warehouse stock synchronization. The system utilizes pluggable provider infrastructure to interface with various LLM backends, while low-level data streaming and kernel computation are handled by other components. SQL packages manage the relational data structures for the lending, shop, and warehouse domains, complementing the overall operational logic found in the root package.

#### Packages
- java/config — This package manages the comprehensive configuration, evaluation, and data extraction logic required for AI-driven source code analysis and generation.
- java — This package provides the infrastructure for configuring, evaluating, and executing AI-driven source code analysis and generation across various LLM backends.
- java/provider — This package provides pluggable infrastructure for interfacing with various Large Language Model backends, supporting both local GGUF inference and OpenAI-compatible APIs.
- other/kernels — This package provides OpenCL kernels for efficiently calculating byte histograms from input data streams.
- other — This package provides low-level, high-performance components for data streaming, kernel computation, and automated system/business reporting utilities.
- other/ringbuf — This package provides a thread-safe, fixed-capacity circular buffer mechanism for efficient sequential byte data streaming between producers and consumers.
- other/scripts — This package provides utility scripts for automated system maintenance, specifically backup rotation, and business intelligence reporting on sales data.
- . — This package suite provides specialized persistence and operational logic for managing library lending, e-commerce inventory, and warehouse stock synchronization, alongside AI-driven code analysis and low-level data streaming utilities.
- sql/library — This package provides the complete relational database structure and operational logic for managing a lending library system, covering entity definition, reporting, and transaction processing.
- sql — This package suite provides distinct, specialized persistence and operational logic for managing library lending, e-commerce inventory, and warehouse stock synchronization.
- sql/shop — This package manages the persistence layer for product catalog, customer order tracking, and inventory stock management using MySQL schema definitions.
- sql/warehouse — This package manages the automated synchronization of inventory stock levels from external supplier feeds into the master warehouse stock records.
