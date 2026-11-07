### srcmorph-modelcard
- H: 1.0
- C: DF7E2B61
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:54:27Z
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
Source summarization is configured in `java/config` and routed through `java` and `java/provider` to mock, local llama.cpp, or OpenAI-compatible backends, with exact source facts and oversized-file handling. Byte-processing utilities include image and stream frequency analysis, buffering, and ring buffers, while command-line tools handle backup retention and sales reporting. The SQL workflows cover library lending, shop orders and inventory, and warehouse stock synchronization.

#### Packages
- java/config — Configures source-file selection, AI generation and model routing, exact source facts, and oversized-file handling for generated `.ai.md` summaries.
- java — Configures source-summary generation and routes requests to mock, local llama.cpp, or OpenAI-compatible AI providers.
- java/provider — Converts source-generation requests into cleaned AI completions through selectable mock, local llama.cpp, or OpenAI-compatible backends.
- other/kernels — Computes byte-value frequency distributions for image data or arbitrary byte streams.
- other — Provides low-level byte processing and buffering utilities alongside command-line tools for backup retention and sales reporting.
- other/ringbuf — Provides bounded circular byte-stream buffering for passing data between a producer and a consumer.
- other/scripts — Provides command-line utilities for rotating compressed directory backups and reporting monthly sales revenue by region and currency.
- . — Supports AI-assisted source summarization, byte and operations utilities, and database workflows for lending, order fulfillment, and warehouse inventory.
- sql/library — Manages a lending library’s catalog, members, branch-specific copies, checkout and return workflows, overdue reporting, and loan history.
- sql — Defines database schemas and transactional workflows for library lending, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages a web shop’s product inventory, customer orders, and order-line pricing, including stock reservation and order-total updates.
- sql/warehouse — Applies recent supplier-feed quantities to warehouse stock by SKU and records synchronization results or errors.
