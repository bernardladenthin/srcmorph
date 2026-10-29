### srcmorph-modelcard
- H: 1.0
- C: 6F7FC165
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:54:35Z
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
This project integrates AI-driven source code modification and analysis, managed by the core Java infrastructure which utilizes a pluggable provider framework for interfacing with local GGUF or OpenAI-compatible model backends. Data processing relies on low-level utilities for high-throughput streaming, including a thread-safe ring buffer and OpenCL kernels for byte frequency distribution calculation. Persistence is handled by specialized SQL layers managing library lifecycle, e-commerce transactions, and warehouse inventory synchronization.

#### Packages
- java/config — This package manages the configuration, evaluation, and fact extraction necessary for AI-driven source code modification and analysis.
- java — This package provides the infrastructure for configuring, evaluating, and executing AI-driven source code analysis and modification tasks using pluggable model backends.
- java/provider — This package provides a pluggable framework for interfacing with various AI model backends, supporting both local GGUF inference and OpenAI-compatible API calls.
- other/kernels — This package provides OpenCL kernels for efficiently calculating byte frequency distributions from data streams.
- other — This package provides low-level utilities for high-throughput data streaming, kernel-accelerated frequency analysis, and automated system reporting/backup management.
- other/ringbuf — This package provides a thread-safe, fixed-capacity byte ring buffer implementation for efficient data streaming between producers and consumers.
- other/scripts — This package provides utility scripts for automated system maintenance, specifically managing data backups and generating structured financial sales reports from raw order data.
- . — This package suite provides specialized persistence layers for library management, e-commerce order processing, and automated inventory synchronization.
- sql/library — This package manages the complete lifecycle of a lending library, handling data modeling, transactional logic, reporting, and schema evolution for book and member management.
- sql — This package suite provides distinct, specialized persistence layers for library management, e-commerce order processing, and automated inventory synchronization.
- sql/shop — This package manages the persistence layer for e-commerce transactions, handling product inventory, customer order creation, and atomic stock reservation.
- sql/warehouse — This package manages the automated synchronization of current inventory levels from external supplier data into the master warehouse stock records.
