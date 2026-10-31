### srcmorph-modelcard
- H: 1.0
- C: 3C8044BF
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:53:56Z
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
This repository is a multi-domain infrastructure toolkit built around the srcmorph Maven plugin, whose configurable Java pipeline selects source files via condition trees, applies deterministic fact extraction to oversize files, and generates `.ai.md` documentation through pluggable AI backends—local llama.cpp GGUF models over JNI or remote OpenAI-compatible chat APIs with completion parsing and timing metrics. Complementing the documentation engine, the toolkit ships GPU-accelerated byte-stream analytics via a two-pass OpenCL kernel computing 256-bin histograms, a power-of-two-sized lock-free SPSC circular buffer for inter-thread streaming, and standalone operational scripts for timestamped backup rotation and CSV-driven monthly sales reporting. Its SQL layer provides transactional persistence with atomic stored procedures and audit trails across PostgreSQL for multi-branch library circulation (member blocking, loan checkout, due dates, late fees, returns), MySQL for e-commerce catalogs and order lifecycles that reserve inventory and maintain totals, and SQL Server for nightly warehouse merges of supplier deliveries into per-SKU on-hand stock.

#### Packages
- java/config — Configures the srcmorph Maven plugin's AI-driven source documentation pipeline: source-file inclusion rules, per-step inference parameters, oversize-file handling strategies, and deterministic fact extraction for `.ai.md` generation.
- java — Configures and executes the AI-driven source documentation pipeline for the srcmorph Maven plugin, selecting source files via condition trees, handling oversize files with deterministic fact extraction, and generating `.ai.md` summaries through pluggable local llama.cpp or remote OpenAI-compatible LLM backends.
- java/provider — Pluggable AI text-generation backends for the srcmorph source-transformation pipeline, supporting local llama.cpp GGUF models via JNI and remote OpenAI-compatible chat APIs with completion parsing, timing metrics, and secure configuration.
- other/kernels — Computes a 256-bin frequency histogram of uchar byte streams (e.g., image pixels) on GPU using a two-pass OpenCL partial-accumulate-then-reduce kernel pipeline.
- other — Provides GPU-accelerated 256-bin histogram computation for byte streams (e.g., image pixels), a lock-free SPSC circular buffer for inter-thread streaming, and standalone CLI tools for backup rotation and monthly sales reporting.
- other/ringbuf — Provides a fixed-capacity, lock-free single-producer single-consumer (SPSC) circular byte buffer for streaming data between threads using power-of-two sizing and bitmask wraparound.
- other/scripts — Standalone operational utilities for automated timestamped directory backup rotation with retention pruning and CSV-driven monthly sales revenue aggregation by region.
- . — Multi-domain infrastructure toolkit providing AI-driven source documentation generation, GPU-accelerated byte-stream analytics with lock-free streaming, operational automation scripts, and transactional SQL persistence for library, e-commerce, and warehouse inventory systems across PostgreSQL, MySQL, and SQL Server.
- sql/library — PostgreSQL database package for a multi-branch lending library, modeling author catalogs, per-branch physical book copy inventory, member registration with blocking, and the complete loan lifecycle including checkout, due-date tracking, late-fee calculation, returns, and audit logging.
- sql — Multi-domain SQL persistence package implementing transactional inventory and order workflows for library circulation (PostgreSQL), e-commerce sales (MySQL), and warehouse stock synchronization (SQL Server), each with atomic stored procedures and audit trails.
- sql/shop — E-commerce persistence layer for product catalog with stock control, customer order lifecycle management, and transactional order-item insertion that reserves inventory and maintains order totals.
- sql/warehouse — Nightly synchronization pipeline that incrementally merges supplier delivery quantities into per-warehouse on-hand stock levels per SKU while auditing run metrics and errors.
