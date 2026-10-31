### srcmorph-modelcard
- H: 1.0
- C: C7DB0C89
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:33:57Z
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
The project is a mixed operational bundle spanning three independent verticals. First, there is a pluggable AI text-generation backend for the srcmorph Maven code-indexer, with configuration for source-file inclusion/exclusion and generation parameters, supporting native local GGUF inference via llama.cpp JNI, OpenAI-compatible endpoints, and a mock provider with thinking-block stripping. Second, an embedded/edge byte-processing substrate provides OpenCL GPU histogram kernels for byte-stream analysis, a lock-free power-of-two ring buffer, and standalone shell/Python CLI utilities. Third, a multi-database SQL bundle covers PostgreSQL library circulation, MySQL web-shop ordering with stock reservation, and T-SQL warehouse stock synchronisation.

#### Packages
- java/config — Configured source-file inclusion/exclusion and AI-generation parameter plumbing for the srcmorph Maven plugin's code-indexer pipeline.
- java — Configuration and pluggable AI-generation backend for the srcmorph Maven plugin's code-indexer pipeline, deciding which source files are summarized and how AI text summaries are generated via native, remote, or mock providers.
- java/provider — Pluggable AI text-generation backend for srcmorph's code-morphing and calibration workflow, supporting local GGUF inference via llama.cpp JNI, OpenAI-compatible endpoints, and a mock provider, with thinking-block stripping and timing-aware generation.
- other/kernels — Provides OpenCL GPU kernels that compute 256-bin histograms of raw byte streams via a two-pass parallel reduction strategy.
- other — Embedded/edge byte-processing substrate: OpenCL GPU histogram kernels for byte-stream analysis, a lock-free power-of-two ring buffer for producer/consumer byte transport, and standalone shell/Python operational CLI utilities.
- other/ringbuf — Provides a fixed-capacity, power-of-two circular byte FIFO for lock-free single-producer/single-consumer streaming between tasks, interrupts, or hardware byte streams.
- other/scripts — Operational command-line utilities for scheduled directory backup retention (Bash) and monthly revenue reporting from CSV exports (Python).
- . — A mixed operational bundle spanning three independent verticals: pluggable AI text-generation backend configuration for the srcmorph Maven code-indexer, embedded byte-processing primitives (OpenCL GPU histograms, lock-free ring buffer) plus ops shell/Python CLIs, and multi-database SQL (PostgreSQL library circulation, MySQL web-shop ordering, T-SQL warehouse stock sync).
- sql/library — A PostgreSQL lending-library database that models the catalog, members, and circulation lifecycle — checkout, return, late-fee assessment, audit logging, and reporting — with a v2 migration introducing per-copy inventory across branches.
- sql — A multi-database SQL bundle covering three operational domains: PostgreSQL library circulation, MySQL web-shop ordering with atomic stock reservation, and T-SQL warehouse stock synchronisation.
- sql/shop — MySQL data model for a web shop that manages product catalog, customer orders, and order-line transactions with stock reservation.
- sql/warehouse — Nightly warehouse stock synchronisation: merges supplier feed quantities into StockLevel and logs sync outcomes in T-SQL.
