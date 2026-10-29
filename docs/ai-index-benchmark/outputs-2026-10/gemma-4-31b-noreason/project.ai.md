### srcmorph-modelcard
- H: 1.0
- C: BE85DFBE
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:56:38Z
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
This multi-domain toolkit integrates AI-driven source code analysis, high-performance system utilities, and relational database schemas. The AI subsystem orchestrates LLM providers—supporting both local GGUF models and remote APIs—with configurable file filtering and metric extraction. High-performance tools include GPU-accelerated byte-frequency analysis and SPSC circular buffers, while the SQL domain manages transactional lifecycles for library lending, e-commerce retail, and warehouse synchronization.

#### Packages
- java/config — Provides configuration and evaluation logic for AI-driven source code analysis, including file filtering, LLM parameter management, and deterministic metric extraction.
- java — Orchestrates AI-driven source code analysis by integrating LLM providers with configurable file filtering and deterministic metric extraction.
- java/provider — Provides a pluggable framework for integrating AI text generation backends, supporting local GGUF models via JNI and remote OpenAI-compatible APIs.
- other/kernels — Performs high-performance byte-frequency distribution analysis of image or stream data using parallel GPU acceleration.
- other — Provides a collection of high-performance data processing tools, including GPU-accelerated byte analysis, lock-free stream buffering, and system utility scripts.
- other/ringbuf — Provides a fixed-capacity, single-producer single-consumer (SPSC) circular buffer for efficient byte-stream data storage and retrieval.
- other/scripts — Provides standalone utility scripts for system backup rotation and financial sales data aggregation.
- . — A multi-domain toolkit providing AI-driven source code analysis, high-performance GPU/system utilities, and relational database schemas for inventory and transaction management.
- sql/library — A PostgreSQL-based lending library system for managing book inventories, member registrations, loan lifecycles, and multi-branch distribution.
- sql — A collection of database schemas for managing inventory and transactional lifecycles across library lending, e-commerce retail, and warehouse synchronization domains.
- sql/shop — Manages e-commerce product inventory, customer order tracking, and the transactional processing of order line items.
- sql/warehouse — Synchronizes warehouse inventory levels by aggregating supplier feed data into a consolidated stock state.
