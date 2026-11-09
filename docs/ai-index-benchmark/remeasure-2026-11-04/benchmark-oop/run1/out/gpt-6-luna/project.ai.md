### srcmorph-modelcard
- H: 1.0
- C: C890557E
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:17Z
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
The project combines Java-based AI source indexing—covering source selection, factual extraction, settings, and mock, local llama.cpp, or OpenAI-compatible generation—with byte-processing utilities for stream buffering and parallel byte histograms. Operational tools handle compressed backups and regional sales reports, while SQL subsystems support library lending and audits, shop orders and inventory, and timestamp-filtered warehouse stock synchronization.

#### Packages
- java/config — Defines source-file eligibility, factual extraction, generation settings, and oversize handling for producing AI-indexed source summaries.
- java — Selects source files and prepares their facts and settings for AI indexing, then generates summaries through mock, local llama.cpp, or OpenAI-compatible backends.
- java/provider — Provides configurable AI text generation for source-document requests through mock, local llama.cpp, or OpenAI-compatible backends, with completion cleanup and optional timing metrics.
- other/kernels — Computes byte-value frequency distributions for image or stream data using parallel 256-bin histograms.
- other — Provides low-level byte-processing utilities and operational tools for buffering streams, computing byte histograms, rotating backups, and reporting regional sales.
- other/ringbuf — Provides bounded FIFO byte buffering for passing data between a producer and a consumer.
- other/scripts — Provides operational utilities for retaining compressed directory backups and turning order exports into currency-specific monthly regional revenue reports.
- . — Supports AI-assisted source indexing alongside byte-processing utilities, operational reporting, and transactional workflows for library lending, shop orders, and warehouse inventory.
- sql/library — Supports library cataloging, branch inventory, member borrowing, loan reporting, returns, late-fee calculation, and audit tracking.
- sql — Provides database structures and transactional operations for library lending, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Maintains a shop catalog and inventory while recording customer order items and keeping order totals in sync.
- sql/warehouse — Applies timestamp-filtered supplier stock receipts to warehouse/SKU inventory and records synchronization results or failures.
