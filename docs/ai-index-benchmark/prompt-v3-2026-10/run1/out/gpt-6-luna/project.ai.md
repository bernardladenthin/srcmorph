### srcmorph-modelcard
- H: 1.0
- C: AFBEE115
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:52:19Z
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
The project combines Java-based AI source documentation, byte-processing utilities, operational reporting, and SQL data workflows. The Java packages configure source selection and documentation metadata, then route generation through configurable local or OpenAI-compatible providers; byte tools cover parallel OpenCL histograms and bounded producer-consumer transfer, with scripts for backup rotation and sales reporting. SQL packages handle library circulation, shop orders and stock reservation, and supplier-driven warehouse inventory synchronization.

#### Packages
- java/config — Defines source-file selection, AI generation, and documentation metadata settings, while providing condition evaluation and source-fact formatting for code documentation workflows.
- java — Configures source selection and AI documentation generation, then routes generation requests to local or OpenAI-compatible providers.
- java/provider — Selects and runs configurable AI text-generation backends for document completions, cleaning generated answers and optionally reporting inference timings.
- other/kernels — Computes byte-frequency histograms for image or byte-stream data using parallel OpenCL counting and reduction.
- other — Supports byte-data analysis and transfer alongside operational backup rotation and currency-filtered regional sales reporting.
- other/ringbuf — Provides bounded FIFO byte-stream storage for transferring data between a single producer and consumer.
- other/scripts — Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly regional sales revenue reports.
- . — Provides AI-assisted source documentation alongside byte-processing utilities, operational reporting, and database workflows for library circulation, shop orders, and warehouse inventory.
- sql/library — Manages a lending library’s catalog, branch-level copy inventory, memberships, borrowing and returns, operational reporting, late fees, and loan auditing.
- sql — Implements database workflows for library circulation, shop order and inventory management, and supplier-driven warehouse stock synchronization.
- sql/shop — Maintains a web-shop catalog, inventory, customer orders, and order totals while reserving stock when items are added.
- sql/warehouse — Synchronizes recent supplier inventory feeds into warehouse stock levels and records each run’s results or failure.
