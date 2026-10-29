### srcmorph-modelcard
- H: 1.0
- C: 7C53A52B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:12Z
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
This multi-domain toolkit integrates AI-driven source code analysis with high-performance data utilities and relational database systems. The AI subsystem orchestrates LLM provider integrations and configuration logic for metadata extraction and source code transformations, supporting both local GGUF models and remote APIs. GPU-accelerated utilities provide byte frequency analysis and lock-free streaming, while the SQL components manage transactional lifecycles for library, e-commerce, and warehouse inventory systems.

#### Packages
- java/config — Provides configuration models and evaluation logic for AI-driven source code analysis, including file filtering, regex-based fact extraction, and LLM inference parameters.
- java — Orchestrates AI-driven source code analysis by managing LLM provider integrations and the configuration logic for file filtering, metadata extraction, and inference strategies.
- java/provider — Provides a pluggable framework for integrating Large Language Model (LLM) backends to perform source code transformations, supporting local GGUF models via JNI and remote OpenAI-compatible APIs.
- other/kernels — Performs high-performance byte frequency distribution analysis of images or data streams using GPU-accelerated parallel reduction.
- other — Provides a heterogeneous set of utilities for GPU-accelerated data analysis, lock-free byte streaming, and administrative system/financial reporting.
- other/ringbuf — Provides a fixed-capacity, power-of-two circular byte buffer for efficient single-producer single-consumer (SPSC) data exchange.
- other/scripts — Provides a collection of administrative and reporting utility scripts for backup management and financial data aggregation.
- . — A multi-domain toolkit providing AI-driven source code analysis, high-performance GPU/streaming utilities, and relational database systems for inventory and transactional management.
- sql/library — A PostgreSQL-based database system for managing a lending library's inventory, member registrations, and book loan lifecycles.
- sql — A collection of database systems for managing inventory and transactional lifecycles across library lending, e-commerce retail, and warehouse synchronization.
- sql/shop — Manages e-commerce product inventory, customer order tracking, and the transactional processing of order line items and stock updates.
- sql/warehouse — Synchronizes warehouse stock levels by aggregating supplier feed data and logging operation results.
