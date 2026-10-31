### srcmorph-modelcard
- H: 1.0
- C: 4E8E1715
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:01:52Z
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
The project combines AI-assisted index generation with byte-processing and operational utilities: Java configuration selects sources, routes prompts, and generates content through mock, local llama.cpp, or OpenAI-compatible providers, while parallel OpenCL kernels and a bounded ring buffer support byte-frequency analysis and data transfer. Its SQL workflows cover library cataloging and circulation, shop inventory and order totals, and warehouse stock synchronization; scripts handle backup rotation and regional sales reporting.

#### Packages
- java/config — Defines source-selection, AI-generation, routing, and metadata settings for producing AI-assisted project indexes.
- java — Configures source selection and generates AI-assisted project-index content through mock, local llama.cpp, or OpenAI-compatible providers.
- java/provider — Provides configurable AI text generation through local llama.cpp JNI or OpenAI-compatible backends, with provider selection, prompt handling, and optional completion timing data.
- other/kernels — Computes byte-frequency histograms for image or byte-stream data using parallel OpenCL work-groups and a final reduction.
- other — Provides parallel byte-frequency analysis, bounded byte-stream buffering, and operational utilities for backup rotation and regional sales reporting.
- other/ringbuf — Provides bounded circular byte storage for producer-consumer data transfer, with partial reads and writes when capacity is limited.
- other/scripts — Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly sales revenue reports by region.
- . — Supports AI-assisted project-index generation alongside byte processing, operational reporting, and database workflows for library circulation, shop ordering, and warehouse inventory.
- sql/library — Defines a lending-library database for cataloging books, managing members and circulation, reporting borrowing activity, and evolving inventory from book-level counts to branch-level copies.
- sql — Provides SQL data models and operational workflows for library circulation, shop ordering, and warehouse inventory synchronization.
- sql/shop — Maintains a web shop’s product inventory and customer order totals as order items are added.
- sql/warehouse — Applies recent supplier-received quantities to warehouse stock levels and records synchronization counts or errors.
