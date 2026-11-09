### srcmorph-modelcard
- H: 1.0
- C: D3C1C9EF
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:47:28Z
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
The Java subsystem selects and prepares source material for AI documentation, with configurable settings and mock, local llama.cpp, or OpenAI-compatible completion backends. Byte-stream utilities include parallel byte-value histograms and a bounded FIFO ring buffer, alongside backup-rotation and sales-reporting scripts. SQL workflows cover library lending, shop orders with stock reservation, and warehouse inventory synchronization.

#### Packages
- java/config — Defines source-selection rules and AI documentation-generation settings, including model routing, oversized-file handling, and exact source facts.
- java — Selects and prepares source material for AI documentation, then generates text through configurable local or OpenAI-compatible model backends.
- java/provider — Generates source-aware text completions through configurable mock, local llama.cpp, or OpenAI-compatible backends, with response cleanup and optional inference metrics.
- other/kernels — Computes byte-value frequency distributions for image or byte-stream data using parallel histogramming.
- other — Provides low-level byte-stream processing alongside operational backup rotation and regional sales reporting utilities.
- other/ringbuf — Buffers producer-to-consumer byte streams in FIFO order using bounded circular storage.
- other/scripts — Provides operational utilities for rotating compressed directory backups and reporting monthly regional sales revenue from order CSVs.
- . — Provides AI-assisted documentation generation, byte-stream and operational utilities, and database workflows for lending, order fulfillment, and warehouse inventory.
- sql/library — Defines a lending-library database for cataloging books and authors, managing members and loans, reporting borrowing activity, and evolving inventory to branch-level copies.
- sql — Defines database workflows for library lending, retail order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages shop inventory and customer orders, including adding order items while reserving stock and updating order totals.
- sql/warehouse — Synchronizes warehouse on-hand inventory from recent supplier quantities and records the outcome of each run.
