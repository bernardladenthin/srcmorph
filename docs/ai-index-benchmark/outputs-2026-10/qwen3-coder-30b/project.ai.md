### srcmorph-modelcard
- H: 1.0
- C: 2E6B6417
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:24:04Z
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
The project consists of several interconnected subsystems focused on AI-driven code analysis and generation, alongside data processing and business logic modules. The core AI workflow is managed through configuration and evaluation components that handle conditions, facts, and generation parameters across source files, with pluggable providers supporting both local and remote text generation including GGUF models and OpenAI-compatible APIs. Parallel data processing capabilities are provided by OpenCL kernels for byte frequency analysis, supported by lock-free ring buffers for efficient streaming and automated backup/reporting scripts. Additional subsystems manage a lending library system with full lifecycle operations, web shop order processing, and warehouse inventory synchronization, all built on SQL-based data models.

#### Packages
- java/config — Configures and evaluates AI-driven code generation and analysis workflows, managing conditions, facts, and generation parameters across source files.
- java — Configures and evaluates AI-driven code generation and analysis workflows, managing conditions, facts, and generation parameters across source files.
- java/provider — Provides pluggable local and remote AI text generation for source code transformation, with support for GGUF models via JNI and OpenAI-compatible APIs.
- other/kernels — Computes byte frequency distributions using parallel OpenCL kernels in a two-phase histogram algorithm.
- other — Provides parallel byte frequency analysis using OpenCL, lock-free data streaming with ring buffers, and automated backup/reporting scripts for data processing workflows.
- other/ringbuf — Provides a lock-free, single-producer single-consumer circular buffer for efficient data streaming with atomic read/write operations.
- other/scripts — Automates backup management and generates revenue reports from CSV data.
- . — Configures and evaluates AI-driven code generation and analysis workflows, managing conditions, facts, and generation parameters across source files.
- sql/library — Manages the full lifecycle of a lending library system, from data modeling and reporting to book checkout, return, and overdue fee calculation.
- sql — Manages the full lifecycle of a lending library system, from data modeling and reporting to book checkout, return, and overdue fee calculation.
- sql/shop — Manages web shop order processing, including product inventory reservation, order itemization, and real-time total calculation.
- sql/warehouse — Automates nightly synchronization of warehouse inventory from supplier feeds, maintaining accurate stock levels and audit logs.
