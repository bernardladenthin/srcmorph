### srcmorph-modelcard
- H: 1.0
- C: 17B92CCE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:48:00Z
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
The srcmorph-modelcard project centers on a pluggable AI text-generation layer that drives LLM inference (local GGUF via llama.cpp JNI or remote OpenAI-compatible endpoints) to produce code and documentation completions, governed by file-exclusion filters, per-model inference parameters, oversize policies, and deterministic fact extraction. It is bundled with independent performance-critical primitives—an OpenCL two-pass byte histogram kernel and a lock-free SPSC ring buffer for inter-thread FIFO exchange—along with standalone operational scripts for backup rotation and sales revenue reporting. A separate set of PostgreSQL schemas and stored procedures covers three operational business domains: multi-branch library lending with late-fee calculation, web-shop order placement with atomic stock reservation, and nightly warehouse stock synchronisation from supplier feeds.

#### Packages
- java/config — Configuration and filtering layer for the srcmorph AI source-documentation engine: defines how files are selected/excluded, how AI models are parameterized, and how oversize or fact-augmented generation is governed.
- java — Pluggable AI text-generation and configuration layer for the srcmorph source-documentation engine: selects, configures, and executes LLM inference (local GGUF via llama.cpp JNI or remote OpenAI-compatible) to produce code or documentation completions, governed by file-exclusion filters, per-model inference parameters, oversize policies, and deterministic fact extraction.
- java/provider — Pluggable AI text-generation backend layer that selects, configures, and executes LLM inference (local GGUF via llama.cpp JNI or remote OpenAI-compatible) to produce code or documentation completions for the srcmorph pipeline.
- other/kernels — GPU-accelerated 256-bin byte histogram computation via a two-pass OpenCL kernel (local-memory accumulation then cross-group reduction).
- other — Independent collection of performance-critical data-processing primitives (GPU histogram, lock-free ring buffer) and standalone operational CLI scripts (backup rotation, sales revenue reporting).
- other/ringbuf — Lock-free single-producer single-consumer (SPSC) fixed-capacity byte ring buffer for inter-thread FIFO data exchange.
- other/scripts — Standalone operational and analytical utility scripts for backup rotation and sales revenue reporting.
- . — Pluggable AI text-generation layer for the srcmorph source-documentation engine (LLM inference via local GGUF or remote OpenAI-compatible endpoints), bundled with independent performance primitives (OpenCL histogram, C SPSC ring buffer) and operational SQL schemas (library lending, web-shop ordering, warehouse stock sync).
- sql/library — PostgreSQL lending-library database: catalog, membership, circulation (checkout/return), late-fee calculation, audit trail, and reporting views for a multi-branch book loan system.
- sql — Relational database schemas and stored procedures for three operational business domains: library lending, web-shop order placement, and warehouse stock synchronisation.
- sql/shop — Web shop order placement: product catalog, customer order headers, order line items, and a stored procedure that atomically adds a line to an order while reserving stock and recalculating the order total.
- sql/warehouse — Nightly warehouse stock synchronisation: merges supplier feed quantities into per-warehouse SKU stock levels and logs sync outcomes.
