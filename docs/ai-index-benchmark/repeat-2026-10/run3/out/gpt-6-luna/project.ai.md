### srcmorph-modelcard
- H: 1.0
- C: 6AACA1DB
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:24:51Z
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
The project combines AI-assisted source documentation, with configurable file selection and local or OpenAI-compatible model backends, alongside byte-processing utilities such as parallel 256-bin histograms and a bounded single-producer/single-consumer ring buffer. Operational scripts rotate backups and report regional sales from CSV exports. Its SQL subsystems manage library lending and audits, shop orders with inventory reservation, and warehouse stock synchronization, using transactional routines to coordinate database updates.

#### Packages
- java/config — Configures which source files qualify for AI documentation, how generation is routed and tuned, and what factual or oversized-input handling applies.
- java — Selects eligible source files for AI-assisted documentation, configures generation behavior, and routes requests to local or remote model backends.
- java/provider — Generates and cleans AI-produced source-editing text through selectable local or OpenAI-compatible model backends, with configurable inference and request safety controls.
- other/kernels — Computes byte-frequency histograms by counting values in parallel work-groups and reducing their partial counts into a 256-bin result.
- other — Provides low-level byte-processing and streaming utilities alongside operational backup rotation and CSV-based regional revenue reporting.
- other/ringbuf — Provides a bounded FIFO byte queue for passing streamed data between a single producer and a single consumer.
- other/scripts — Provides operational utilities for rotating directory backups and reporting monthly regional order revenue from CSV exports.
- . — Supports AI-assisted source documentation, byte and backup utilities, regional sales reporting, and transactional library, shop, and warehouse operations.
- sql/library — Manages library catalog and membership records, book lending and returns, inventory tracking, operational auditing, and borrowing reports.
- sql — Supports library lending, online order inventory reservation, and warehouse stock synchronization through database schemas and transactional SQL routines.
- sql/shop — Maintains a web-shop product catalog and customer orders, reserving inventory and updating order totals as items are added.
- sql/warehouse — Synchronizes supplier inventory feeds into warehouse stock balances and records each synchronization’s results or errors.
