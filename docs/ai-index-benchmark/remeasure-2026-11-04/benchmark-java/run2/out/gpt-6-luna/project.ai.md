### srcmorph-modelcard
- H: 1.0
- C: 1345C900
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:32:36Z
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
The project combines AI-assisted source indexing—configured file selection, fact extraction, and model generation through selectable local, OpenAI-compatible, or mock providers—with byte-processing utilities, including a bounded ring buffer and a 256-bin byte histogram. Its SQL workflows manage library lending and auditing, web-shop orders and stock reservations, and warehouse inventory synchronization. Operational scripts support compressed-backup rotation and regional sales reporting.

#### Packages
- java/config — Configures source-file selection, AI fact extraction, model generation, and oversized-file handling for producing project indexes.
- java — Converts configured source-file context into AI-generated project index content using selectable local, OpenAI-compatible, or mock generation.
- java/provider — Generates source-aware AI completions through selectable mock, local llama.cpp, or OpenAI-compatible backends, with completion cleanup and optional performance metrics.
- other/kernels — Computes a 256-bin frequency distribution for byte data, combining per-work-group counts into a final histogram.
- other — Provides low-level byte processing and buffering utilities alongside operational backup rotation and regional sales reporting.
- other/ringbuf — Provides bounded byte-stream buffering for producer-consumer transfer, allowing partial reads and writes across a fixed-capacity circular storage area.
- other/scripts — Provides lightweight operational scripts for rotating compressed backups and reporting monthly regional sales revenue from CSV order data.
- . — Provides AI-assisted project indexing alongside byte-processing utilities, operational reporting, and database workflows for lending, order fulfillment, and stock synchronization.
- sql/library — Manages a lending library’s catalogue, memberships, physical copy inventory, book loans, overdue reporting, and loan auditing.
- sql — Implements database workflows for library lending, web-shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Maintains web-shop product inventory and customer orders, including adding order items, reserving stock, and recalculating order totals.
- sql/warehouse — Synchronizes warehouse on-hand inventory from recent supplier-feed quantities and records each synchronization’s outcome.
