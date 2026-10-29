### srcmorph-modelcard
- H: 1.0
- C: F74E3317
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:52:42Z
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
The project combines source-summary generation, AI completion, and model-routing subsystems: configuration defines generation policies, while selectable local llama.cpp and OpenAI-compatible providers produce cleaned completions. It also includes byte-stream utilities, parallel byte-frequency histograms, and operational scripts for backup rotation and regional sales reporting. Its SQL workflows cover library catalog and circulation, shop ordering and stock reservation, and warehouse inventory synchronization.

#### Packages
- java/config — Defines source-selection, AI generation, model-routing, and oversized-file policies for producing project source summaries and metadata.
- java — Configures source-summary generation and provides selectable AI backends that turn generation requests into cleaned completion text.
- java/provider — Generates and cleans AI completions through selectable local llama.cpp or OpenAI-compatible backends, with configurable inference and timing support.
- other/kernels — Computes byte-frequency histograms for image data or arbitrary byte streams in parallel.
- other — Provides byte-stream processing utilities alongside directory backup rotation and monthly regional sales reporting.
- other/ringbuf — Provides fixed-capacity FIFO byte buffering for producer-consumer data streams.
- other/scripts — Provides command-line utilities for rotating compressed directory backups and reporting monthly order revenue by region.
- . — Provides source-summary generation capabilities alongside byte-processing utilities, operational reporting tools, and database workflows for circulation, ordering, and inventory.
- sql/library — Manages library catalog, membership, book circulation, availability, loan reporting, and the migration from aggregate inventory counts to branch-assigned copies.
- sql — Defines relational database operations for library circulation, shop ordering and stock reservation, and warehouse inventory synchronization.
- sql/shop — Manages shop product inventory and customer order totals as order lines are added.
- sql/warehouse — Applies recent supplier inventory feeds to warehouse SKU stock levels and records synchronization results or failures.
