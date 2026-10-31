### srcmorph-modelcard
- H: 1.0
- C: 8B9D5709
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:48:26Z
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
The project combines AI-assisted Java source analysis with configurable source selection, exact source facts, and normalized summaries generated through local llama.cpp or OpenAI-compatible providers. Its low-level data utilities include parallel OpenCL byte-frequency histograms and bounded single-producer/single-consumer ring buffers, alongside backup rotation and regional sales-reporting scripts. Relational SQL workflows support lending-library operations, shop order fulfillment with stock reservation, and warehouse inventory synchronization with recorded outcomes.

#### Packages
- java/config — Configures source-file selection and AI summary generation, including condition-based filtering, exact source facts, model/provider settings, and oversize-file policies.
- java — Selects source files for AI-assisted processing and generates normalized summaries through local llama.cpp or OpenAI-compatible models.
- java/provider — Generates and routes source-processing prompts to local llama.cpp or OpenAI-compatible models, with completion cleanup and optional generation metrics.
- other/kernels — Counts byte-value frequencies in image or byte-stream data through parallel OpenCL histogram computation.
- other — Provides low-level byte-data processing and buffering alongside operational backup rotation and regional sales reporting utilities.
- other/ringbuf — Provides bounded FIFO byte buffering between a single producer and a single consumer, preserving order across circular wraparound.
- other/scripts — Provides operational utilities for rotating directory backups and producing monthly regional revenue reports from order CSV exports.
- . — This package combines AI-assisted source summarization, low-level data utilities and operational scripts, and relational workflows for lending, commerce, and warehouse inventory.
- sql/library — Manages a lending library’s catalog, member borrowing, inventory, loan reporting, and checkout-to-return lifecycle.
- sql — Manages lending-library operations, shop order fulfillment, and warehouse stock synchronization through relational data models and transactional SQL workflows.
- sql/shop — Stores shop products and customer orders, while adding order items with stock reservation and order-total recalculation.
- sql/warehouse — Recent supplier feed quantities are applied to warehouse stock levels, with each synchronization’s outcome recorded for operational tracking.
