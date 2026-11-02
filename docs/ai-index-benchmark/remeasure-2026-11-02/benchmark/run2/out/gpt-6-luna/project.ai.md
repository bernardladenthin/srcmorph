### srcmorph-modelcard
- H: 1.0
- C: 909A6450
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:46:57Z
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
The project includes AI-assisted source indexing, analysis, and generation, configured through model and prompt settings and served by mock, local llama.cpp, or OpenAI-compatible providers. Byte-processing utilities cover histograms and bounded stream buffering, alongside scripts for backup rotation and regional sales reports. Its SQL workflows support library lending, shop orders with stock reservation, and warehouse inventory synchronization.

#### Packages
- java/config — Configures AI-assisted source indexing and generation by selecting files, defining model and prompt behavior, and preparing source facts and oversize policies.
- java — Configures AI-assisted source analysis and routes generation requests to local or OpenAI-compatible models, producing cleaned completions and optional inference timings.
- java/provider — Generates AI text for application requests through mock, local llama.cpp, or OpenAI-compatible backends, with completion cleanup and optional inference metrics.
- other/kernels — Computes byte-value frequency histograms for image data or arbitrary byte streams.
- other — Provides low-level byte-stream processing and buffering alongside command-line utilities for backup rotation and regional sales reporting.
- other/ringbuf — Buffers byte streams between a producer and consumer with bounded FIFO storage and partial transfers.
- other/scripts — Provides standalone utilities for rotating compressed directory backups and generating currency-filtered monthly regional sales reports from CSV orders.
- . — Combines AI-assisted source generation, byte-processing utilities, operational scripts, and transactional data workflows for lending, order fulfillment, and warehouse inventory.
- sql/library — Supports library cataloging, membership, book lending and returns, inventory tracking, and loan reporting.
- sql — Provides SQL data models and transactional workflows for library lending, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages shop product inventory and customer orders, including adding order items while reserving stock and recalculating order totals.
- sql/warehouse — Applies newly received supplier quantities to warehouse stock and records the synchronization result.
