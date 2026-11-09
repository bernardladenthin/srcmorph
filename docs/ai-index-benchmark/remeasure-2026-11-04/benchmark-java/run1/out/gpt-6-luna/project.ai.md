### srcmorph-modelcard
- H: 1.0
- C: F5F3823A
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:14:20Z
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
The project combines AI-assisted documentation generation—using source-indexing configuration and selectable mock, local llama.cpp, or OpenAI-compatible text-generation backends—with byte-processing utilities, including parallel OpenCL histograms and bounded FIFO buffering. Its database workflows cover library circulation and inventory, shop orders and stock reservation, and warehouse synchronization from supplier feeds; command-line tools also support backup rotation and regional sales reporting.

#### Packages
- java/config — Defines source-indexing and AI-generation configuration, including file-selection rules, model routing, exact source facts, and oversized-file handling for generated project documentation.
- java — Configures source-aware AI documentation generation and routes text-generation requests to mock, local llama.cpp, or OpenAI-compatible backends.
- java/provider — Generates AI text through selectable mock, local llama.cpp, or OpenAI-compatible backends, with shared configuration, completion cleanup, and optional generation metrics.
- other/kernels — Computes byte-value frequency distributions for image or byte-stream data using parallel OpenCL histogram kernels.
- other — This package provides low-level byte-processing utilities and command-line workflows for histogram analysis, FIFO buffering, backup rotation, and regional sales reporting.
- other/ringbuf — Provides bounded FIFO byte storage for passing data between a single producer and a single consumer.
- other/scripts — Provides command-line utilities for rotating compressed directory backups and summarizing order revenue by month and region.
- . — Supports AI-assisted documentation generation alongside byte-processing, operational reporting, and database workflows for circulation, order fulfillment, and stock synchronization.
- sql/library — Manages book lending from catalog and member records through checkout, returns, overdue reporting, and branch-based physical inventory.
- sql — Implements database workflows for library circulation, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages shop inventory and customer order lines, reserving product stock and maintaining order totals when items are added.
- sql/warehouse — Synchronizes recent supplier-feed quantities into warehouse stock balances and records the outcome of each synchronization.
