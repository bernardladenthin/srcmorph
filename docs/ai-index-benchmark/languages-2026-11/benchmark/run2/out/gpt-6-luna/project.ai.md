### srcmorph-modelcard
- H: 1.0
- C: 5B471C18
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:13Z
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
The project combines AI-assisted source documentation in Java, where configuration selects files and generation settings, and provider routing sends completion requests to local llama.cpp, OpenAI-compatible, or mock backends. Byte-processing utilities include parallel GPU histograms and bounded ring buffers, alongside backup rotation and order-revenue reporting tools. Its SQL workflows cover library lending and reporting, shop catalog and order inventory, and warehouse stock synchronization.

#### Packages
- java/config — Configures source-file selection, fact extraction, AI model routing, prompt generation, and oversized-file handling for AI-generated source documentation.
- java — Configures and runs AI-assisted source documentation by selecting project files, preparing generation settings, and routing completion requests to supported inference backends.
- java/provider — Generates and normalizes AI completions for source-document workflows, selecting local llama.cpp, OpenAI-compatible, or mock backends and optionally reporting generation timings.
- other/kernels — Computes byte-value frequency distributions for image or byte-stream data using parallel GPU histogramming.
- other — Provides byte-data processing and stream utilities alongside operational tools for rotating backups and reporting filtered order revenue.
- other/ringbuf — Provides bounded FIFO byte-stream buffering for producer/consumer code, with partial reads and writes across circular-buffer wraparound.
- other/scripts — Provides command-line utilities for rotating compressed directory backups and summarizing order revenue by month and region.
- . — Provides AI-assisted source documentation, byte-processing and operational utilities, and relational workflows for library circulation, online orders, and warehouse inventory.
- sql/library — Tracks library catalog inventory, member borrowing, loan returns, overdue charges, and operational reporting across branches.
- sql — Implements relational data workflows for library lending, online orders and inventory, and warehouse stock synchronization.
- sql/shop — Maintains a web-shop catalog and inventory while building customer orders, reserving stock, and keeping order totals current.
- sql/warehouse — Applies recent supplier inventory feeds to warehouse stock balances and records each synchronization result.
