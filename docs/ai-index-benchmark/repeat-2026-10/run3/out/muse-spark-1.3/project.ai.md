### srcmorph-modelcard
- H: 1.0
- C: 9B9A5A75
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:26:39Z
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
AI-driven source-file documentation generation provides configurable file-scope selection with pluggable local/remote LLM inference, tuned by indexing configuration for summarization, oversized-file and fact handling plus backend timing and parsing. Supporting utilities cover GPU byte-value frequency 256-bin histograms, single-producer single-consumer lock-free streaming FIFO circular buffers, and operational scripts for dated backup rotation and monthly regional revenue reporting. Multi-dialect SQL examples model library lending circulation with overdues, popularity and late fees, web-shop order placement with product catalog and stock reservation, and nightly warehouse stock synchronization applying supplier feeds with audit logging.

#### Packages
- java/config — AI source indexing configuration — selects files, tunes LLM summarization, and controls oversized-file and fact handling.
- java — AI-driven source-file documentation generation with configurable file selection and pluggable local/remote LLM inference.
- java/provider — Local and remote LLM backends that generate source-file header text with timing, parsing, and configuration.
- other/kernels — Counts byte-value frequencies into 256-bin histograms for images or byte streams.
- other — Heterogeneous utilities for GPU byte-frequency histograms, lock-free streaming FIFO, and backup/revenue ops reporting.
- other/ringbuf — Single-producer single-consumer byte FIFO circular buffer for lock-free streaming data storage.
- other/scripts — Operational utility scripts for dated backup rotation and monthly regional revenue reporting.
- . — AI documentation generation with file-scope selection and pluggable LLM inference, plus GPU/ring-buffer/ops utilities and multi-dialect library/shop/warehouse SQL examples.
- sql/library — Lending library circulation for cataloging authors/books, managing members/loans, and tracking overdues, popularity, and late fees across branches.
- sql — Multi-dialect SQL examples for library lending circulation, web-shop ordering, and warehouse stock synchronization.
- sql/shop — Web shop order placement with product catalog, customer orders, and stock reservation.
- sql/warehouse — Nightly warehouse stock synchronisation applying supplier feed quantities to on-hand levels with audit logging.
