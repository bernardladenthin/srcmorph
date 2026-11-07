### srcmorph-modelcard
- H: 1.0
- C: B9B0585D
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:39:46Z
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
The project combines AI-assisted source summarization—configuring source selection and generation settings, then routing completions to local, OpenAI-compatible, or mock providers—with byte-processing utilities such as image histograms and a bounded ring buffer. Standalone scripts handle compressed-backup retention and regional sales reports. Its SQL workflows cover library lending and auditing, retail order fulfillment and stock reservation, and warehouse supplier-stock reconciliation.

#### Packages
- java/config — Defines source-selection rules and AI generation settings, including file facts, model/provider options, and oversized-file handling for generated source summaries.
- java — Configures source-summary generation and routes AI completion requests to local, remote, or mock providers.
- java/provider — Generates text from AI prompts through selectable local llama.cpp or OpenAI-compatible backends, with mock fallback, completion cleanup, and optional generation metrics.
- other/kernels — Computes the frequency distribution of byte values in image or stream data, producing a 256-bin histogram.
- other — Provides low-level byte-stream and image-data processing alongside standalone utilities for backup retention and regional sales reporting.
- other/ringbuf — Stores and transfers byte streams through a bounded FIFO buffer, supporting partial reads and writes across wraparound.
- other/scripts — Provides command-line utilities for retaining dated compressed backups and producing monthly regional sales-revenue reports from CSV exports.
- . — Supports AI-assisted source summarization, byte and operational utilities, and database workflows for lending, retail orders, and warehouse inventory.
- sql/library — Supports library lending by managing catalog inventory, member borrowing and returns, loan reporting, auditing, and schema evolution to branch-assigned copies.
- sql — Manages inventory and transactions across library lending, retail order fulfillment, and warehouse supplier-stock reconciliation.
- sql/shop — Maintains shop inventory and customer order totals by recording order lines, reserving product stock, and tracking order status.
- sql/warehouse — Reconciles recent supplier inventory receipts into warehouse stock balances and records each synchronization outcome.
