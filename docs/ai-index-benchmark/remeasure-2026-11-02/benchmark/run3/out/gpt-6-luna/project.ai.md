### srcmorph-modelcard
- H: 1.0
- C: CB18C310
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:00:55Z
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
The Java source-summary subsystem selects source files, configures AI fact extraction and generation, routes requests to text-generation backends, and produces cleaned completions with optional inference timings. The SQL subsystem models transactional workflows for library circulation, webshop fulfillment, and warehouse stock synchronization, including operational reporting and checkout/return auditing. Supporting utilities handle byte processing—including frequency distributions and bounded FIFO buffering—and operational tasks such as backups and regional sales reporting.

#### Packages
- java/config — Configures source-file selection, AI fact extraction, model generation and routing, and oversized-file handling for source-summary generation.
- java — Selects source files for AI-generated summaries and configures the generation backends that turn selected source text into cleaned completions.
- java/provider — Selects and configures AI text-generation backends, producing cleaned completions and optional inference timings for source-file requests.
- other/kernels — Computes byte-value frequency distributions for image data and arbitrary byte streams.
- other — Provides low-level byte-processing utilities and operational tools for backups and regional sales reporting.
- other/ringbuf — Provides bounded FIFO byte-stream buffering for producer/consumer data exchange using reusable circular storage.
- other/scripts — Provides operational utilities for maintaining dated backups and producing currency-filtered monthly regional sales summaries from CSV exports.
- . — Combines source-summary generation, byte-processing and operational reporting utilities, and transactional data workflows for library lending, webshop fulfillment, and warehouse inventory.
- sql/library — Manages a lending library’s catalog, member accounts, book inventory, loan circulation, operational reporting, and checkout/return auditing.
- sql — Provides SQL data models and transactional workflows for library circulation, webshop order fulfillment, and warehouse stock synchronization.
- sql/shop — Maintains webshop product inventory and customer order totals as items are added to orders.
- sql/warehouse — Synchronizes warehouse stock balances from recent supplier inventory feeds and records each synchronization’s results or errors.
