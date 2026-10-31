### srcmorph-modelcard
- H: 1.0
- C: 89C2FEFA
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:20:19Z
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
srcmorph-modelcard centers on AI-driven source documentation generation that selects files, budgets LLM generation, handles oversized sources, and routes to local llama.cpp or OpenAI-compatible backends for source-file header replacement. It also provides byte-stream and ops utilities including 256-bin histogram counting, single-producer single-consumer circular byte FIFO streaming, dated backup rotation, and monthly regional sales revenue reporting. Complementing these are multi-domain SQL reference schemas for library circulation with overdue tracking and late fees, web-shop ordering with stock reservation, and nightly warehouse stock synchronization with audit logging.

#### Packages
- java/config — Configures AI-driven source documentation runs by selecting files, budgeting LLM generation, and handling oversized sources.
- java — Configures and executes AI-driven source-file documentation runs by selecting files, budgeting generation, and routing to local or remote LLMs.
- java/provider — Pluggable LLM backends that generate replacement text for source-file headers via local llama.cpp or OpenAI-compatible servers.
- other/kernels — Byte-value frequency counting for image/byte streams via 256-bin histogram.
- other — Heterogeneous byte-stream and ops utilities for histogram counting, SPSC FIFO transfer, and backup/sales reporting.
- other/ringbuf — Single-producer single-consumer circular byte FIFO for streaming producer-consumer byte transfer with wraparound reads/writes.
- other/scripts — Standalone operational utilities for dated backup rotation and monthly regional sales revenue reporting.
- . — Heterogeneous reference package combining AI-driven source documentation generation with byte-stream primitives, ops scripts, and library/shop/warehouse SQL models.
- sql/library — Lending library circulation management for book inventory, member borrowing, branch holdings, overdue tracking, and late fees.
- sql — Multi-domain SQL reference schemas for library circulation, web-shop ordering, and warehouse inventory synchronization.
- sql/shop — Web shop order placement with product catalog, customer orders, and stock reservation.
- sql/warehouse — Nightly warehouse stock synchronization from supplier deliveries into on-hand inventory with audit logging.
