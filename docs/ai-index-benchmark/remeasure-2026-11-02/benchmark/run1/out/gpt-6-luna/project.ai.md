### srcmorph-modelcard
- H: 1.0
- C: 09B771E5
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:30:26Z
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
The Java subsystem configures source-analysis runs and generates AI-assisted documentation or indexing text, routing requests to local llama.cpp, OpenAI-compatible, or mock providers. The `other` packages handle byte-stream processing—including an OpenCL byte-frequency histogram and a single-producer/single-consumer ring buffer—alongside backup rotation and regional sales reporting. The SQL workflows cover library cataloging and circulation, webshop inventory and orders, and warehouse stock replenishment.

#### Packages
- java/config — Configures AI-assisted source indexing through file-selection conditions, model and prompt routing, generation settings, exact source facts, and oversized-file policies.
- java — Configures source-analysis runs and generates AI-assisted documentation text using local or OpenAI-compatible models.
- java/provider — Generates AI text for source-aware documentation or indexing workflows through configurable local llama.cpp, OpenAI-compatible, or mock backends.
- other/kernels — Computes 256-bin byte-frequency histograms for image data or other byte streams using OpenCL.
- other — Provides byte-stream processing and buffering alongside operational utilities for backup rotation and regional sales reporting.
- other/ringbuf — Provides fixed-capacity byte-stream buffering for a single producer and single consumer, with partial reads and writes that preserve unread data across wraparound.
- other/scripts — Provides operational utilities for rotating compressed directory backups and reporting monthly sales revenue by region and currency.
- . — Provides source-documentation generation tools, byte-processing and operational utilities, and database workflows for circulation, commerce, and inventory.
- sql/library — Manages library cataloging and circulation, including member borrowing, copy inventory, overdue reporting, and loan auditing.
- sql — Implements database workflows for library circulation, webshop order fulfillment, and warehouse stock replenishment.
- sql/shop — Maintains webshop product inventory and customer orders, including stock reservation and order-total calculation when items are added.
- sql/warehouse — Applies newly received supplier quantities to warehouse SKU stock and records synchronization results.
