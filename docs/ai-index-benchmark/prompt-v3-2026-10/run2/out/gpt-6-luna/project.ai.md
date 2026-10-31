### srcmorph-modelcard
- H: 1.0
- C: 0E5F7B46
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:14:35Z
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
The project combines AI-assisted source documentation, GPU byte-frequency histogramming and producer–consumer buffering, and operational utilities for backup rotation and regional sales reporting. Its database workflows cover library lending and auditing, webshop inventory and orders, and warehouse stock synchronization; the documentation workflow selects source material and uses configurable local or OpenAI-compatible inference backends.

#### Packages
- java/config — Configures source-file selection and AI-generated documentation, including routing, prompt budgets, file facts, and oversized-file handling.
- java — Selects source material and generates AI-assisted documentation through configurable local or OpenAI-compatible inference backends.
- java/provider — Generates text for source-document workflows through selectable mock, local llama.cpp, or OpenAI-compatible AI backends, with configurable inference and optional timing metrics.
- other/kernels — Computes byte-value frequency counts on the GPU by building per-work-group histograms and reducing them into a final 256-bin histogram.
- other — Provides low-level byte-processing utilities for GPU histogramming and producer–consumer buffering, alongside operational backup rotation and regional sales reporting.
- other/ringbuf — Provides bounded byte buffering for producer–consumer data exchange, accepting writes and reads only when capacity or buffered data is available.
- other/scripts — Provides operational utilities for retaining timestamped directory backups and producing monthly regional revenue reports from order exports.
- . — Provides AI-assisted documentation generation, byte-processing and operational utilities, and database workflows for library lending, commerce, and warehouse inventory.
- sql/library — Manages library cataloging, member borrowing, copy-level inventory, loan returns, overdue reporting, and lending-event auditing.
- sql — Implements database-backed workflows for library lending, webshop ordering, and warehouse stock synchronization.
- sql/shop — Maintains webshop product inventory and customer orders, including adding order lines, reserving stock, and recalculating order totals.
- sql/warehouse — Consolidates newly received supplier-feed quantities into warehouse stock levels and records the outcome of each synchronization.
