### srcmorph-modelcard
- H: 1.0
- C: 17A9A482
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:17:46Z
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
The project combines AI-assisted source documentation in `java/config`, `java`, and `java/provider`, which configure source selection and code-fact extraction and route generation to local llama.cpp or OpenAI-compatible backends. The `other` packages provide byte-processing utilities, including image or stream byte-frequency analysis and bounded ring-buffering, alongside backup and sales-reporting scripts. The `sql` packages model transactional workflows for library lending, shop order fulfillment with inventory reservation, and warehouse stock synchronization with recorded outcomes.

#### Packages
- java/config — Configures source-file selection, AI generation and routing, code-fact extraction, and oversized-file handling for `.ai.md` documentation.
- java — Configures source-to-documentation generation and routes requests through local or OpenAI-compatible AI text-generation backends.
- java/provider — Selects and runs AI text-generation backends for source-transformation requests, supporting local llama.cpp inference and OpenAI-compatible chat APIs.
- other/kernels — Computes a 256-bin frequency distribution of byte values across an image or byte stream.
- other — Provides low-level byte-processing and buffering utilities alongside operational backup and sales-reporting tools.
- other/ringbuf — Provides bounded byte-stream buffering between a producer and consumer, with partial reads and writes governed by available space and queued data.
- other/scripts — Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly regional revenue reports from order CSV files.
- . — Supports AI-assisted source documentation, low-level byte and operational utilities, and transactional workflows for lending, retail orders, and warehouse inventory.
- sql/library — Defines a lending-library database for cataloging books and members, managing loans and returns, reporting borrowing activity, and evolving inventory from book-level counts to branch-assigned copies.
- sql — Provides SQL data models and transactional workflows for library lending, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages shop products and customer orders, including adding order items while reserving inventory and updating order totals.
- sql/warehouse — Recent supplier-feed quantities are applied to warehouse stock by SKU, with each synchronization outcome recorded for operational tracking.
