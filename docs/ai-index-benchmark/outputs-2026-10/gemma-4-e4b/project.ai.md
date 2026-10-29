### srcmorph-modelcard
- H: 1.0
- C: 72E2E6EC
- D: 2026-10-28T23:55:11Z
- T: 2026-10-29T00:01:21Z
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
The system integrates AI-driven source code modification and fact extraction, managed by the java/config and java packages, which utilize a pluggable provider framework for generating responses via local GGUF models or remote APIs. Low-level data streaming and computational kernels, including OpenCL for byte frequency distribution, are handled by other and other/kernels. Persistence is managed by specialized SQL layers for library lending, e-commerce shop operations, and warehouse inventory synchronization.

#### Packages
- java/config — This package manages the configuration, evaluation, and fact extraction necessary for AI-driven source code modification and indexing.
- java — This package provides the infrastructure for configuring, evaluating, and executing AI-driven source code modification and fact extraction across pluggable backend providers.
- java/provider — This package provides a pluggable framework for generating AI responses, supporting local GGUF models via JNI bindings and OpenAI-compatible remote APIs.
- other/kernels — This package provides OpenCL kernels for efficiently calculating byte frequency distributions from large input streams.
- other — This package provides low-level utilities for high-performance data streaming, computational kernel execution, and automated system/financial data processing.
- other/ringbuf — This package provides a thread-safe, fixed-capacity byte ring buffer implementation for efficient data streaming between producers and consumers.
- other/scripts — This package provides utility scripts for automated system maintenance, specifically backup rotation, and data processing for generating financial sales reports from CSV sources.
- . — This package suite provides specialized persistence layers for managing library lending, e-commerce order fulfillment, and automated warehouse inventory synchronization.
- sql/library — This package manages the complete lifecycle of a lending library, handling data modeling, transaction processing, reporting, and schema evolution for books, members, and loans.
- sql — This package suite provides distinct, specialized persistence layers for managing library lending operations, e-commerce order fulfillment, and automated warehouse inventory synchronization.
- sql/shop — This package manages the persistence layer for e-commerce operations, handling product inventory, customer order creation, and atomic stock reservation.
- sql/warehouse — This package manages the nightly synchronization of inventory levels from supplier feeds into the master stock records within the warehouse database.
