### srcmorph-modelcard
- H: 1.0
- C: 6B86738A
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:02:44Z
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
The project combines AI-assisted source analysis—selecting and preparing files, then routing generation requests to local llama.cpp or OpenAI-compatible providers—with GPU byte histograms, streaming buffers, and operational command-line utilities. Its SQL workflows cover library lending, shop order fulfillment and stock reservation, and warehouse inventory synchronization, with supporting catalog, audit, and reporting operations.

#### Packages
- java/config — Configures source-file selection and AI-assisted summarization, including generation settings, oversized-file policies, and whole-source facts.
- java — Selects and prepares source files for AI-assisted analysis, then routes generation requests to configured local or compatible text-generation providers.
- java/provider — Provides configurable AI text generation for source-analysis workflows, including local llama.cpp inference and OpenAI-compatible chat completions.
- other/kernels — Computes a 256-bin frequency distribution of byte values in an image or byte stream using GPU work-groups.
- other — Provides low-level byte-processing utilities and command-line tools for GPU histograms, streaming buffers, backup rotation, and regional sales reporting.
- other/ringbuf — Provides bounded byte storage for transferring streaming data between a producer and consumer.
- other/scripts — Provides command-line utilities for rotating dated directory backups and producing currency-filtered monthly regional sales reports from order CSVs.
- . — Provides AI-assisted source analysis alongside byte-processing utilities, operational scripts, and database workflows for lending, order fulfillment, and inventory synchronization.
- sql/library — Manages a lending library’s catalog, member borrowing, inventory, returns, overdue reporting, and loan auditing.
- sql — Implements database workflows for library lending, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages shop product inventory and customer order totals, including adding order lines while checking and reserving stock.
- sql/warehouse — Applies newly received supplier quantities to warehouse stock by warehouse and SKU, while recording synchronization results.
