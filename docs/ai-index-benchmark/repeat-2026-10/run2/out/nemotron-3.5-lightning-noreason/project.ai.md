### srcmorph-modelcard
- H: 1.0
- C: 79624AC6
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:13Z
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
srcmorph provides AI-assisted source code transformation pipelines that configure condition-based file selection, generation parameters, and oversize handling across pluggable AI backends. The system integrates pluggable AI generation backends supporting local GGUF models via llama.cpp JNI, OpenAI-compatible APIs, and mock implementations. Complementary tooling includes a 256-bin byte histogram calculation using OpenCL kernels, fixed-capacity byte ring buffers, and data processing utilities for streaming, financial reporting, and backup management. Supporting subsystems model lending library domains with integrity constraints and manage web shop stock synchronization.

#### Packages
- java/config — srcmorph configures AI-assisted source processing: condition evaluation, generation parameters, and oversize handling for file transformation pipelines.
- java — srcmorph provides AI-assisted source code transformation pipelines, configuring condition-based file selection, generation parameters, and oversize handling across pluggable AI backends.
- java/provider — This package provides pluggable AI generation backends for source code transformation, supporting local GGUF models via llama.cpp JNI, OpenAI-compatible APIs, and mock implementations, with completion parsing and configuration management.
- other/kernels — Calculates a 256-bin byte histogram from image data or byte streams using a two-pass OpenCL kernel approach for parallel efficiency.
- other — A collection of tools for data processing, streaming buffers, and financial reporting across image kernels, circular buffers, and backup/revenue scripts.
- other/ringbuf — A fixed-capacity, single-producer/single-consumer byte ring buffer for streaming data with power-of-two capacity constraints.
- other/scripts — This package provides tools for data management and financial reporting: rotating directory backups and generating monthly sales revenue reports from CSV order exports.
- . — ai-assisted source code transformation pipelines, configuring condition-based file selection, generation parameters, and oversize handling across pluggable AI backends.
- sql/library — A lending library system that models authors, books, members, and loans with integrity constraints, checkout logic, and branch management.
- sql — A lending library system that models authors, books, members, and loans with integrity constraints, checkout logic, and branch management.
- sql/shop — A MySQL data model and procedure for a web shop, handling product catalog, customer orders, and stock-aware order item insertion with total recalculation.
- sql/warehouse — Warehouse stock synchronisation from a supplier feed, merging new and updated rows into the StockLevel table and logging the operation.
