### srcmorph-modelcard
- H: 1.0
- C: 78A4665D
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:39:19Z
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
The project combines AI-assisted source documentation, byte-processing utilities, operational scripts, and SQL-backed business workflows. Java configuration routes documentation generation to mock, local llama.cpp, or OpenAI-compatible providers, while the other utilities handle byte-frequency analysis, FIFO buffering, backup rotation, and regional sales reporting. The SQL workflows cover library lending and inventory, shop orders and stock reservations, and warehouse stock synchronization from supplier feeds.

#### Packages
- java/config — Defines source-selection, fact-counting, model-routing, and generation settings for producing AI-assisted source documentation.
- java — Configures AI-assisted source documentation and routes text-generation requests to mock, local llama.cpp, or OpenAI-compatible models.
- java/provider — Provides configurable AI text generation through mock, local llama.cpp, and OpenAI-compatible backends, with completion cleanup and optional performance metrics.
- other/kernels — Computes a complete 256-bin frequency distribution of byte values from image data or arbitrary byte streams.
- other — Provides byte-stream and image-data processing utilities alongside backup rotation and monthly regional sales reporting.
- other/ringbuf — Buffers byte streams between a producer and consumer while preserving FIFO order within bounded storage.
- other/scripts — Provides operational utilities for retaining directory backups and reporting monthly regional sales revenue by currency and region.
- . — Provides AI-assisted source documentation, byte and sales utilities, and database workflows for library lending, shop inventory, and warehouse stock synchronization.
- sql/library — Manages a lending library’s catalog, member borrowing, loan lifecycle and fees, audit history, reporting, and branch-level copy inventory.
- sql — Implements SQL-backed workflows for library lending, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages shop product inventory and customer orders, including adding order items while reserving stock and recalculating order totals.
- sql/warehouse — Applies recent supplier-feed quantity changes to warehouse stock by SKU and records synchronization results.
