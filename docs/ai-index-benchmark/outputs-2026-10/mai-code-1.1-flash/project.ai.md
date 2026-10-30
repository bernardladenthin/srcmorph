### srcmorph-modelcard
- H: 1.0
- C: 3BBB1C7D
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:49:34Z
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
The project combines AI-assisted source generation with low-level byte-stream and OpenCL processing, tying together source-aware indexing, provider orchestration, and operational data utilities. The java/config and java packages define eligible source-file selection, oversized-input handling, model/prompt policy, and local or OpenAI-compatible inference orchestration, while java/provider executes local llama.cpp/JNI and HTTP-based generation and strips reasoning before storing completions. The other/* packages handle byte histograms, fixed-capacity buffering, data hygiene/reporting, and shell/Python integration, and the sql/* domains cover library circulation, e-commerce order fulfillment, and warehouse stock synchronization in PostgreSQL.

#### Packages
- java/config — Maintains the AI-driven file-selection and generation configuration that decides which source files are eligible, how oversized inputs are handled, and what model and prompt settings are used for srcmorph indexing.
- java — AI configuration and provider orchestration for source-aware generation, selecting eligible files, setting model and prompt policy, and executing local or OpenAI-compatible inference.
- java/provider — Local AI text generation for source-aware coding tasks, with provider selection, native llama.cpp/JNI execution, OpenAI-compatible HTTP handling, and reasoning stripping before completion storage.
- other/kernels — Computes byte-frequency histograms for byte streams or image regions in OpenCL, aggregating per-group counts into a final 256-bin distribution.
- other — Provides low-level byte-stream processing, bounded in-memory transfer, and operational data hygiene/reporting utilities for OpenCL, C, and shell/Python workflows.
- other/ringbuf — Provides a fixed-capacity byte queue for producer/consumer stream buffering and in-memory data transfer.
- other/scripts — This package manages operational data hygiene and reporting by rotating backup archives and aggregating sales totals by month and region for review.
- . — This package combines AI-assisted source generation, low-level operational utilities, and transactional SQL domains for catalog, order, and stock management.
- sql/library — This package manages a PostgreSQL lending-library system, covering catalog data, borrowing flows, overdue reporting, and migration from book-level inventory to branch-aware copy tracking.
- sql — This package covers three SQL-backed operational domains: library circulation and overdue management, e-commerce order fulfillment, and warehouse stock synchronization.
- sql/shop — This package manages the web shop’s catalog, order lifecycle, and inventory reservation for placing and reconciling order items.
- sql/warehouse — Nightly supplier feed changes are merged into warehouse stock balances and recorded in a transaction audit trail for stock synchronization.
