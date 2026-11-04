### srcmorph-modelcard
- H: 1.0
- C: 80DB301A
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:00:48Z
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
The project combines Java source analysis and documentation generation, with configurable file selection and metadata feeding AI completions from local llama.cpp or OpenAI-compatible backends. Its SQL workflows cover library lending, shop order fulfillment, and warehouse inventory synchronization. Separate byte-stream utilities include frequency analysis and bounded FIFO storage, alongside command-line tools for backup retention and regional sales reporting.

#### Packages
- java/config — Defines source-file selection, AI generation settings, and factual metadata used to produce indexed source documentation.
- java — Configures source analysis and turns selected source files into AI-generated documentation using local or OpenAI-compatible text-generation backends.
- java/provider — Generates and normalizes AI text completions through configurable local llama.cpp or OpenAI-compatible backends, with mock fallback and optional timing metrics.
- other/kernels — Computes byte-frequency distributions for image data or other byte streams.
- other — Provides byte-stream processing and storage alongside command-line utilities for backup retention and regional sales reporting.
- other/ringbuf — Provides bounded FIFO storage for transferring byte streams between a single producer and a single consumer.
- other/scripts — Provides command-line utilities for retaining compressed directory backups and reporting order revenue by month and region.
- . — Supports AI-generated source documentation alongside byte-processing utilities, backup and sales reporting, and library, shop, and warehouse data workflows.
- sql/library — Manages library catalog and lending operations, including member checkout and returns, inventory, overdue reporting, fees, and loan auditing.
- sql — Provides SQL-backed workflows for library lending, shop order fulfillment, and warehouse inventory synchronization.
- sql/shop — Maintains a shop catalog and customer orders, including order-line creation, inventory reservation, and order-total calculation.
- sql/warehouse — Applies recent supplier-feed quantities to warehouse inventory by SKU and records each synchronization outcome.
