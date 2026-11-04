### srcmorph-modelcard
- H: 1.0
- C: 20DC7536
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:00Z
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
The project combines source-aware AI documentation generation, configurable to use local llama.cpp, OpenAI-compatible, or mock providers, with SQL workflows for library circulation, shop order fulfillment, and warehouse stock synchronization. Its byte-processing utilities include byte-frequency counting and bounded single-producer/single-consumer buffering, while operational scripts handle backup rotation and currency-specific monthly revenue reports.

#### Packages
- java/config — Defines source-selection rules, code-fact extraction, and AI generation settings used to configure source analysis and oversized-file handling.
- java — Configures source-aware AI documentation generation and routes completion requests through local, remote, or mock model providers.
- java/provider — Generates AI documentation completions from source-aware requests using selectable local llama.cpp, OpenAI-compatible, or mock backends.
- other/kernels — Computes frequency counts for all 256 byte values in image or byte-stream data.
- other — Provides byte-data processing utilities, bounded stream buffering, backup rotation, and currency-based monthly revenue reporting.
- other/ringbuf — Provides bounded byte-stream buffering for single-producer/single-consumer transfer, accepting and returning data as space and queued bytes allow.
- other/scripts — Provides operational tools for rotating dated backups and producing currency-specific monthly revenue reports from order exports.
- . — Supports AI-assisted source documentation alongside byte processing, operational reporting, and database workflows for circulation, order fulfillment, and inventory synchronization.
- sql/library — Supports library cataloguing and circulation by managing books, members, loans, returns, inventory, and borrowing reports.
- sql — Implements database workflows for library circulation, shop order fulfillment, and warehouse stock synchronization across three SQL systems.
- sql/shop — Stores shop products and customer orders, reserving inventory and recalculating order totals when items are added.
- sql/warehouse — Maintains warehouse stock levels by applying recent supplier-feed quantities and recording each synchronization’s outcome.
