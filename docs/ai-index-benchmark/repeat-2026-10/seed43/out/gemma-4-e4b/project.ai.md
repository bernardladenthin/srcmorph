### srcmorph-modelcard
- H: 1.0
- C: 532AD92F
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T14:01:44Z
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
This project integrates AI-driven source code analysis and generation, managed by the core Java infrastructure, which utilizes a pluggable provider framework to interface with various LLM backends. Data processing relies on specialized components for high-performance operations, including OpenCL kernels for byte frequency distribution and thread-safe ring buffers for streaming. The system also incorporates distinct persistence layers handling library lending, e-commerce order fulfillment, and warehouse inventory synchronization, alongside operational scripts for maintenance and BI reporting.

#### Packages
- java/config — This package manages the configuration, evaluation, and extraction of structured facts necessary for AI-driven source code analysis and generation.
- java — This package provides the infrastructure for configuring, evaluating, and executing AI-driven analysis and generation tasks against source code contexts.
- java/provider — This package provides a unified, pluggable framework for interfacing with various Large Language Model backends, supporting both local (llama.cpp) and external (OpenAI-compatible) inference.
- other/kernels — This package provides OpenCL kernels for efficiently calculating byte frequency distributions from input streams.
- other — This package provides specialized components for high-performance data processing, including kernel-level byte frequency analysis, thread-safe data streaming buffers, and operational automation scripts for backups and business intelligence reporting.
- other/ringbuf — This package provides a thread-safe, fixed-capacity byte ring buffer implementation for efficient sequential data streaming between producers and consumers.
- other/scripts — This package provides operational scripts for automated system maintenance, specifically backup rotation, and business intelligence reporting on sales data.
- . — This package suite provides distinct persistence layers for managing library lending, e-commerce order fulfillment, and automated warehouse inventory synchronization.
- sql/library — This package manages the complete lifecycle of a lending library, from defining core entities and tracking inventory to executing lending transactions and generating analytical reports.
- sql — This package suite provides distinct persistence layers for managing library lending, e-commerce order fulfillment, and automated warehouse inventory synchronization.
- sql/shop — This package manages the persistence layer for a web shop, defining schemas for products, customer orders, and the transactional logic for placing orders.
- sql/warehouse — This package manages the automated synchronization of inventory levels from external supplier feeds into the master stock records.
