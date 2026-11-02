### srcmorph-modelcard
- H: 1.0
- C: 614417F9
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:03:42Z
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
The project combines AI-assisted source documentation, byte-stream utilities, operational tools, and SQL database workflows. The Java packages configure source-aware documentation generation and route requests to local llama.cpp or OpenAI-compatible models; the byte-processing packages provide stream primitives, bounded FIFO buffering, and byte-frequency analysis, while scripts handle backups and regional sales reports. SQL subsystems manage library circulation, web-shop inventory and orders, and warehouse stock synchronization.

#### Packages
- java/config — Defines the configuration and rule utilities for selecting source files, routing AI generation, counting source facts, and controlling oversized-file handling for generated documentation.
- java — Configures source-aware AI documentation generation and routes completion requests to local or OpenAI-compatible models.
- java/provider — Generates AI completions for source-generation requests through selectable local llama.cpp or OpenAI-compatible backends, with prompt cleanup and optional timing metrics.
- other/kernels — Computes a complete 256-bin frequency distribution for byte-valued image data or other byte streams.
- other — Provides byte-stream processing primitives and operational tools for backups and regional sales reporting.
- other/ringbuf — Provides bounded FIFO byte buffering for transferring data between a single producer and a single consumer.
- other/scripts — Provides operational utilities for retaining dated directory backups and producing currency-filtered monthly regional revenue reports from order exports.
- . — Provides AI-assisted source documentation tooling alongside byte-processing utilities, operational reporting, and database workflows for lending, commerce, and inventory.
- sql/library — Manages a lending library’s catalog, member borrowing, copy inventory, loan lifecycle, overdue reporting, and audit history.
- sql — Implements database workflows for library circulation, web-shop order and inventory management, and warehouse stock synchronization.
- sql/shop — Maintains web-shop product inventory and customer orders, including stock reservation and order-total calculation when items are added.
- sql/warehouse — Applies recent supplier inventory quantities to warehouse stock levels and records the synchronization results.
