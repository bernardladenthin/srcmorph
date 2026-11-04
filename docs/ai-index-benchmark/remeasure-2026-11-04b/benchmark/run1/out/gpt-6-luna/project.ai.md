### srcmorph-modelcard
- H: 1.0
- C: F118A7E4
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:51:28Z
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
The project combines Java source-code indexing and AI-generated summaries, with configurable file selection, model settings, and selectable mock, local llama.cpp, or OpenAI-compatible providers. Its other utilities handle byte processing, bounded FIFO streams, backup rotation, and regional sales reporting. SQL packages cover library lending, shop orders and stock reservations, and warehouse inventory synchronization.

#### Packages
- java/config — Defines source-file selection, AI generation, and indexing configuration, including condition matching, exact source facts, model settings, and oversized-file policies.
- java — Configures source-code indexing and generates AI summaries through local or OpenAI-compatible models.
- java/provider — Generates source-aware AI completions through selectable mock, local llama.cpp, or OpenAI-compatible backends, with configurable inference and response cleanup.
- other/kernels — Counts byte-value frequencies across an input stream, producing a 256-bin histogram.
- other — Provides low-level byte-processing and transfer utilities alongside command-line tools for backup rotation and regional monthly sales reporting.
- other/ringbuf — Provides bounded FIFO byte-stream storage for single-producer/single-consumer data transfer.
- other/scripts — Provides command-line utilities for maintaining timestamped backup archives and summarizing order revenue by month and region.
- . — Supports source-code indexing and AI-generated summaries alongside byte utilities, backup and sales tools, and database workflows for lending, order fulfillment, and inventory.
- sql/library — Supports library cataloging and member borrowing, tracking loan availability, returns, overdue activity, and branch-level book copies.
- sql — Implements database workflows for library lending, retail order fulfillment, and warehouse stock synchronization.
- sql/shop — Maintains a shop catalog and inventory alongside customer orders, and adds order items while reserving stock and updating order totals.
- sql/warehouse — Supplier inventory updates are aggregated by warehouse and SKU, applied to stock levels, and recorded with synchronization results.
