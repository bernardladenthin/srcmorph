### srcmorph-modelcard
- H: 1.0
- C: CD384990
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:37:51Z
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
The Java subsystem selects eligible source files and generates AI-assisted summaries through local or OpenAI-compatible providers, using configurable generation settings and run metadata. The other packages provide byte-processing tools, including OpenCL histograms and bounded FIFO storage, alongside backup rotation and regional sales reporting. SQL workflows manage lending-library circulation, retail orders and inventory, and warehouse stock synchronization.

#### Packages
- java/config — Defines source-file eligibility, AI generation settings, and run metadata for producing AI-assisted source summaries.
- java — Selects eligible source files, configures AI-assisted summary generation, and produces text through local or OpenAI-compatible providers.
- java/provider — Generates AI text for application requests through selectable local or OpenAI-compatible backends, with configurable inference and completion cleanup.
- other/kernels — Computes byte-value frequency histograms from image or byte-stream data using parallel OpenCL kernels.
- other — Provides byte-processing primitives and operational utilities for backup rotation and regional sales reporting.
- other/ringbuf — Provides bounded FIFO byte storage for transferring data between a producer and a consumer.
- other/scripts — This package provides operational utilities for rotating directory backups and generating monthly regional sales-revenue reports from order CSVs.
- . — Provides AI-assisted source summarization alongside byte-processing utilities, backup and sales-reporting tools, and SQL workflows for lending, retail orders, and inventory synchronization.
- sql/library — Manages a lending library’s catalog, members, branch inventory, book loans, returns, and borrowing reports.
- sql — Supports lending-library circulation, retail order fulfillment, and warehouse stock synchronization across catalog, inventory, and transaction records.
- sql/shop — Manages product inventory and customer orders, reserving stock and updating order totals when products are added to orders.
- sql/warehouse — Applies recent supplier inventory updates to warehouse stock and records synchronization results or failures.
