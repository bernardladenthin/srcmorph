### srcmorph-modelcard
- H: 1.0
- C: A28C3C7B
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:59:49Z
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
The project provides AI-configured source inclusion and deterministic code morphing through pluggable LLM backends, including local GGUF models via llama.cpp JNI, OpenAI-compatible endpoints, and mock implementations, while stripping model reasoning blocks from completions. It also includes byte-histogram and streaming utilities that compute 256-bin byte frequency histograms over images or streams using a two-pass OpenCL reduction, manage a lock-free circular byte buffer for single-producer/single-consumer transfer, and automate dated directory backups and monthly regional sales reports from CSV exports. A separate SQL layer operates three independent domain systems: a PostgreSQL lending library for books and members, a MySQL web shop catalog that reserves product stock and maintains order totals, and a T-SQL warehouse process that synchronizes nightly stock levels from supplier feeds.

#### Packages
- java/config — Configures and evaluates AI-driven source file inclusion rules, extracts deterministic whole-file facts, and defines build behavior for oversized source files.
- java — Configures AI-based source file inclusion and deterministic fact extraction, then morphs source code through pluggable LLM backends (local GGUF, OpenAI-compatible, mock) while sanitizing model reasoning blocks from completions.
- java/provider — Provides pluggable AI text-generation backends for source-code morphing, including local GGUF models via llama.cpp JNI, OpenAI-compatible endpoints, and mock implementations, while stripping leading model reasoning blocks from completions.
- other/kernels — Computes a 256-bin byte frequency histogram of an image or byte stream using a two-pass OpenCL reduction.
- other — Computes byte-frequency histograms over image or stream data, manages a lock-free circular byte queue for producer-consumer transfer, and automates dated directory backups and monthly regional sales reports from CSV exports.
- other/ringbuf — A fixed-capacity circular byte buffer for single-producer/single-consumer streaming with power-of-two wrap-around indexing.
- other/scripts — The package provides standalone automation utilities for creating and pruning dated directory backups and for generating monthly regional sales revenue reports from CSV order exports.
- . — This package enables AI-configured source inclusion and deterministic code morphing, provides byte-histogram, circular-buffer, and dated backup/sales-report automation, and manages PostgreSQL lending, MySQL e-commerce, and T-SQL warehouse stock-synchronization data systems.
- sql/library — A PostgreSQL lending library system that manages authors, books, members, and loans, tracks physical copies per branch, enforces checkout and return rules, calculates late fees, and reports overdue and popular-book analytics.
- sql — The package implements three independent domain systems: a PostgreSQL lending library for books and members, a MySQL web shop catalog with order stock reservation, and a T-SQL warehouse stock synchronization from supplier feeds.
- sql/shop — Models a web shop catalog and order lifecycle by reserving product stock and maintaining order totals when order lines are added.
- sql/warehouse — Nightly warehouse stock levels are synchronized from a supplier feed by aggregating received quantities per warehouse/SKU, upserting them into StockLevel, and logging insert/update counts or errors in SyncLog.
