### srcmorph-modelcard
- H: 1.0
- C: 5F15BEC4
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:07:28Z
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
The `srcmorph-modelcard` project integrates AI-driven source code generation with a PostgreSQL-based lending and e-commerce domain. The `java` and `java/provider` packages orchestrate inference via local Llama.cpp or remote backends, while the `other` and `other/ringbuf` subsystems provide high-performance OpenCL histogram computation and zero-copy circular buffers for data processing. These components feed into the `.` package, which unifies the AI engine with the `sql` and `sql/library` subsystems to manage book inventory, member loans, and shop order workflows.

#### Packages
- java/config — This package manages the configuration, validation, and execution logic for AI-driven source code generation and summarization, including model routing, file filtering, and context handling.
- java — This package orchestrates AI-driven source code generation and summarization by managing configuration schemas, validating file inclusion rules, and executing inference via local Llama.cpp or remote OpenAI-compatible backends.
- java/provider — Provides a pluggable local and remote AI generation engine supporting Llama.cpp JNI inference, OpenAI-compatible APIs, and mock backends with reasoning block parsing and timing metrics.
- other/kernels — Provides a two-pass OpenCL kernel implementation for computing a 256-bin byte frequency histogram from image data or byte streams.
- other — Provides a high-performance OpenCL histogram computation engine, a fixed-capacity circular buffer for zero-copy data transfer, and operational utilities for data archival and financial reporting.
- other/ringbuf — Provides a fixed-capacity circular buffer implementation for efficient zero-copy data transfer between producers and consumers using power-of-two capacity validation and bitwise indexing.
- other/scripts — Provides operational utilities for data archival via shell scripting and financial analytics via Python aggregation.
- . — A multi-domain package integrating AI-driven code generation and summarization, high-performance OpenCL histogram computation, and a PostgreSQL-based lending library system with shop order management.
- sql/library — A PostgreSQL-based lending library system managing book inventory, member loans, branch locations, and automated audit trails for checkout/return operations.
- sql — A PostgreSQL-based lending library system managing book inventory, member loans, branch locations, and automated audit trails for checkout/return operations, while integrating shop order management and warehouse stock synchronization via SQL schemas and stored procedures.
- sql/shop — Provides SQL schema and stored procedures for managing product inventory, customer orders, and order line items in a web shop, including stock reservation and total calculation.
- sql/warehouse — Synchronizes warehouse inventory levels by merging supplier feed quantities into the StockLevel table and logging operation metrics.
