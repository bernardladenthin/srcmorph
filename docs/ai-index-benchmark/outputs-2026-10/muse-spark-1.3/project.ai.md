### srcmorph-modelcard
- H: 1.0
- C: 1322B8E0
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:19:29Z
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
The project combines AI-powered source-code summarization with system utilities and multi-domain SQL data models. The java subsystem selects files, builds prompts with exact facts, and generates documentation headers via local llama.cpp or OpenAI-compatible backends, with run configuration, file filtering, and oversize handling. Parallel 256-bin byte-histogram analysis, SPSC FIFO byte buffering, filesystem backup rotation, and monthly regional sales aggregation provide low-level and operational utilities. Library lending circulation with overdue fees, web-shop ordering with stock reservation and order totals, and nightly warehouse stock synchronization from supplier feeds cover relational inventory and ordering modeling.

#### Packages
- java/config — AI documentation run configuration, file filtering, and oversize handling for source-code summarization.
- java — AI-powered source-code summarization pipeline that selects files, builds prompts with exact facts, and generates documentation headers via local or remote LLM backends.
- java/provider — Pluggable AI text-generation backends that produce source-file headers via local llama.cpp or OpenAI-compatible servers with thinking-block cleanup.
- other/kernels — Counts 256-bin byte-value frequencies for images and byte streams via parallel two-pass reduction.
- other — Heterogeneous utilities for parallel byte-histogram analysis, SPSC byte buffering, backup rotation, and sales reporting.
- other/ringbuf — Single-producer single-consumer FIFO byte queue buffering variable-length streams in power-of-two wraparound storage.
- other/scripts — Standalone operational scripts for filesystem backup rotation and monthly regional sales aggregation.
- . — Heterogeneous reference package combining AI-powered source documentation generation with system utilities and library/shop/warehouse SQL data models.
- sql/library — Lending library circulation tracking book lending, returns, overdue fees, and branch inventory.
- sql — Multi-domain SQL data model covering library lending circulation, web-shop ordering with stock reservation, and warehouse inventory synchronization.
- sql/shop — Web shop order placement with product catalog, customer orders, line items, stock reservation and order total maintenance.
- sql/warehouse — Nightly warehouse stock synchronization from supplier feeds into on-hand inventory with audit logging.
