### srcmorph-modelcard
- H: 1.0
- C: C737738C
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T10:02:06Z
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
The srcmorph-modelcard monorepo integrates a Java-based AI code-indexing pipeline with heterogeneous system-level utilities and multi-dialect SQL schemas. The Java subsystem provides a pluggable infrastructure for deterministic fact extraction and source-code indexing, supporting both local llama.cpp JNI and remote OpenAI-compatible LLM inference. Complementing this, the C/C++/Python/OpenCL components offer GPU-accelerated byte histogram computation and lock-free circular byte buffers for efficient inter-thread data exchange. Finally, the SQL package defines domain-specific data models and transactional logic for library lending, e-commerce order processing, and warehouse inventory synchronization.

#### Packages
- java/config — Configuration and evaluation logic for AI-driven source code indexing, including file exclusion rules, inference parameter management, and deterministic fact extraction.
- java — Pluggable AI text-generation infrastructure for source-code indexing, providing configuration management, file-exclusion logic, deterministic fact extraction, and multi-backend LLM inference (local llama.cpp JNI and remote OpenAI-compatible APIs).
- java/provider — Provides a pluggable abstraction for AI text generation, supporting local llama.cpp JNI inference and remote OpenAI-compatible APIs, with configuration management, response parsing, and timing metrics.
- other/kernels — Provides OpenCL GPU kernels for computing a 256-bin byte histogram of image or stream data via a two-pass parallel reduction strategy.
- other — Provides a heterogeneous toolkit for GPU-accelerated byte histogram computation, lock-free inter-thread data buffering, and operational automation for backup rotation and sales revenue aggregation.
- other/ringbuf — Provides a lock-free, single-producer/single-consumer circular byte buffer for efficient, thread-safe data exchange between concurrent entities.
- other/scripts — Provides operational utilities for automated backup rotation with retention policies and monthly sales revenue aggregation from CSV order exports.
- . — A heterogeneous monorepo combining a Java-based pluggable AI code-indexing pipeline, C/C++/Python/OpenCL system-level utilities for GPU histograms and lock-free buffers, and multi-dialect SQL schemas for library, e-commerce, and warehouse inventory management.
- sql/library — A PostgreSQL-based lending library system managing book inventory, member accounts, loan transactions, and multi-branch operations with automated fee calculation and audit logging.
- sql — A multi-dialect SQL package providing domain-specific data models and transactional logic for library lending, e-commerce order processing, and warehouse inventory synchronization.
- sql/shop — MySQL schema and stored procedure for a web shop that manages product inventory, customer orders, and order line items with atomic stock reservation and order total recalculation.
- sql/warehouse — Nightly warehouse stock synchronisation that merges supplier feed quantities into per-warehouse SKU stock levels and logs the outcome.
