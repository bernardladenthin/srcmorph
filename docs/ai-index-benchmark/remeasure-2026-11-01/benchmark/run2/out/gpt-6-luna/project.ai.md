### srcmorph-modelcard
- H: 1.0
- C: 0009E829
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:45:47Z
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
The Java packages configure AI-assisted source indexing and documentation, with completion requests routed through providers for local llama.cpp or OpenAI-compatible models and optional mock fallback. The `other` packages cover byte processing, GPU-based byte-frequency histograms, circular-buffer queues, and operational backup and sales-reporting utilities. SQL workflows manage library lending, webshop orders and stock reservation, and warehouse inventory synchronization.

#### Packages
- java/config — Configures AI-assisted source indexing by defining file-selection rules, generation/model settings, reusable source facts, and oversized-file handling.
- java — Configures AI-assisted source documentation generation and routes completion requests to local or OpenAI-compatible models.
- java/provider — Provides configurable AI text generation for application requests through local llama.cpp models or OpenAI-compatible services, with mock fallback, completion cleanup, and optional timing metrics.
- other/kernels — Computes byte-value frequency distributions for image or byte-stream data using parallel GPU histogramming.
- other — Provides low-level byte-processing utilities alongside operational backup rotation and currency-filtered regional sales reporting.
- other/ringbuf — Queues byte streams in FIFO order through a fixed-capacity circular buffer for single-producer, single-consumer use.
- other/scripts — Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly regional revenue reports from order CSVs.
- . — Combines AI-assisted source documentation settings and providers with byte-processing utilities, operational reports, and SQL workflows for lending, purchasing, and inventory synchronization.
- sql/library — Manages a lending library’s catalog, members, book loans, inventory availability, borrowing reports, and loan audit history.
- sql — Defines database workflows for library lending, webshop ordering, and warehouse stock synchronization.
- sql/shop — Maintains webshop product inventory and customer orders, including adding order items while reserving stock and updating order totals.
- sql/warehouse — Synchronizes supplier-received quantities into warehouse on-hand stock by warehouse and SKU, while recording each run’s outcome.
