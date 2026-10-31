### srcmorph-modelcard
- H: 1.0
- C: 1D5FB5D6
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:05:39Z
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
The project centers on srcmorph source morphing, combining Java-based source-file condition evaluation, deterministic fact extraction, and mutable AI generation configuration with pluggable text-generation providers for local llama.cpp JNI models and OpenAI-compatible backends, including completion parsing that strips internal thinking blocks. A separate set of utilities handles monthly regional revenue aggregation from order CSVs, timestamped directory backups with retention limits, 256-bin byte histogram computation via OpenCL, and fixed-capacity circular byte buffering for streaming. The SQL layer synchronizes warehouse stock across PostgreSQL, MySQL, and SQL Server schemas, maintains a MySQL web shop catalog with orders, stock reservation, and order total recalculation, and manages a PostgreSQL lending library schema with authors, books, members, loans, availability tracking, and reporting views for overdue loans and popular books.

#### Packages
- java/config — Evaluates source-file conditions, extracts deterministic facts, and carries mutable AI generation and run configuration for srcmorph processing.
- java — Evaluates source-file conditions, extracts deterministic facts, and carries mutable AI generation and run configuration for srcmorph processing, while resolving and instantiating pluggable AI text-generation providers for source morphing, supporting local llama.cpp JNI models and OpenAI-compatible backends, and parsing model completions to strip internal thinking blocks.
- java/provider — Resolves and instantiates pluggable AI text-generation providers for source morphing, supporting local llama.cpp JNI models and OpenAI-compatible backends, while parsing model completions to strip internal thinking blocks.
- other/kernels — Computes a 256-bin byte histogram of an image or byte stream using a two-pass OpenCL reduction.
- other — Aggregates monthly regional revenue from order CSVs, manages timestamped directory backups with retention limits, computes 256-bin byte histograms via OpenCL, and provides a fixed-capacity circular byte buffer for streaming.
- other/ringbuf — Provides a fixed-capacity circular byte buffer for producer-consumer data streaming with power-of-two capacity.
- other/scripts — Generates monthly regional revenue reports from order CSV exports and creates dated, retention-limited backups of directories.
- . — Evaluates source-file conditions and generates AI summaries for source morphing, aggregates monthly regional revenue from order CSVs, manages timestamped directory backups, computes 256-bin byte histograms via OpenCL, and synchronizes warehouse stock across PostgreSQL, MySQL, and SQL Server schemas.
- sql/library — Models a lending library's core domain: authors, books, members, book loans with availability tracking, physical copies per branch, and reporting views for overdue loans and popular books.
- sql — Synchronizes warehouse stock from supplier feeds, manages a MySQL web shop catalog and orders, and maintains a PostgreSQL lending library schema with loans, copies, and reporting.
- sql/shop — Models a MySQL web shop catalog, customer orders, and order line items with stock reservation and order total recalculation when items are added.
- sql/warehouse — Nightly warehouse stock synchronization aggregates supplier feed quantities received since a cutoff, merges them into stock levels, and logs insert/update counts or errors.
