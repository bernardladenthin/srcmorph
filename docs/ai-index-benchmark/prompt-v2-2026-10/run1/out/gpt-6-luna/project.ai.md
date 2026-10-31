### srcmorph-modelcard
- H: 1.0
- C: 370F0F1B
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:35:01Z
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
The project combines AI-assisted source documentation, database workflows, and low-level data utilities with operational tools. Java configuration and provider packages select sources, filter files, and route source-aware text generation to local or OpenAI-compatible backends; the database packages cover library lending, shop order fulfillment, and warehouse inventory synchronization. Other utilities handle byte and image data, including OpenCL frequency analysis and ring-buffered streams, alongside backup retention and monthly sales reporting.

#### Packages
- java/config — Defines source selection, AI model routing and generation settings, file-condition filtering, factual count formatting, and oversized-file handling for source documentation runs.
- java — Configures source-documentation runs and routes source-aware text generation through local or OpenAI-compatible AI backends.
- java/provider — Selects and runs AI text-generation backends for source-aware requests, with configurable local or OpenAI-compatible inference and cleaned completion output.
- other/kernels — Computes byte-value frequency distributions for image or byte-stream data on OpenCL devices.
- other — Provides low-level byte and image-data utilities alongside operational tools for backup retention and monthly sales reporting.
- other/ringbuf — Provides bounded FIFO byte-stream buffering for producer/consumer data transfer, preserving order as reads and writes wrap around the buffer.
- other/scripts — Provides operational utilities for retaining timestamped directory backups and producing currency-filtered monthly sales revenue reports.
- . — Provides configurable AI-assisted source documentation, low-level data utilities and operational reports, and database workflows for lending, order fulfillment, and inventory synchronization.
- sql/library — Manages a lending library’s catalog, members, physical inventory, checkout and return workflows, reporting, and loan history.
- sql — Provides database-backed workflows for library lending, retail order fulfillment, and warehouse inventory synchronization.
- sql/shop — Manages a shop’s product inventory and customer orders, including adding order items while reserving stock and recalculating order totals.
- sql/warehouse — Synchronizes warehouse SKU inventory by applying supplier quantities received since a cutoff and recording each run’s outcome.
