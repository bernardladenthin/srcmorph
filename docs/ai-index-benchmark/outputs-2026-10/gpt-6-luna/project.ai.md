### srcmorph-modelcard
- H: 1.0
- C: 9C491948
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:23:53Z
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
The project combines AI-assisted source documentation with interchangeable mock, local llama.cpp, and OpenAI-compatible generation backends, configured through source-selection, model, and metadata settings. Low-level byte utilities include fixed-capacity ring buffers and parallel OpenCL histogram kernels, alongside operational backup rotation and monthly regional revenue reporting. Database workflows cover library circulation and loan auditing, shop order placement with inventory reservation and synchronized totals, and warehouse inventory synchronization from supplier feeds.

#### Packages
- java/config — Defines source-selection, AI generation, and metadata settings for producing AI-assisted source documentation.
- java — Configures AI-assisted source documentation and routes generation requests to local, mock, or OpenAI-compatible models.
- java/provider — Generates AI text through interchangeable mock, local llama.cpp, and OpenAI-compatible backends, with configurable model settings and completion cleanup.
- other/kernels — Computes byte-frequency distributions for image or stream data using parallel OpenCL histogram kernels.
- other — Provides low-level byte processing and buffering alongside operational backup rotation and monthly regional order-revenue reporting.
- other/ringbuf — Provides fixed-capacity FIFO byte buffering for producer/consumer data streams.
- other/scripts — Provides operational utilities for rotating dated backups and summarizing order revenue by month and region.
- . — Provides AI-assisted source documentation alongside low-level byte utilities, operational reporting and backup tools, and database workflows for library circulation, shop orders, and warehouse inventory.
- sql/library — Manages lending-library catalog and circulation data, including inventory, member borrowing, returns, overdue reporting, and loan auditing.
- sql — Provides database-backed workflows for library circulation, shop order fulfillment, and warehouse inventory synchronization.
- sql/shop — Shop order placement records product lines, reserves available inventory, and keeps order totals synchronized.
- sql/warehouse — Synchronizes warehouse inventory from recent supplier-feed quantities and records the outcome of each synchronization.
