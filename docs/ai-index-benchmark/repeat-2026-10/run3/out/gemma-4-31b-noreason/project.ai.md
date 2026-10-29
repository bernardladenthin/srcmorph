### srcmorph-modelcard
- H: 1.0
- C: 66F413A3
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:33:52Z
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
This multi-domain toolkit integrates AI-driven source code analysis with high-performance data processing and relational database management. The AI subsystem orchestrates source code transformation by combining configuration schemas and fact extraction with a pluggable LLM provider framework supporting local GGUF and remote APIs. High-performance processing is handled via GPU-accelerated byte-frequency analysis and SPSC circular buffers, while the SQL layer manages inventory, e-commerce, and warehouse logistics across multiple domains.

#### Packages
- java/config — Provides configuration schemas and evaluation logic for AI-driven source code analysis, including file filtering, regex-based fact extraction, and LLM inference parameter management.
- java — Orchestrates AI-driven source code analysis and transformation by combining configurable file filtering and deterministic fact extraction with a pluggable LLM provider framework.
- java/provider — Provides a pluggable framework for integrating Large Language Model (LLM) backends, supporting local GGUF models via JNI and remote OpenAI-compatible APIs to generate source code transformations.
- other/kernels — Performs high-performance byte-frequency analysis of image or byte-stream data using parallelized GPU kernels.
- other — Provides high-performance byte-stream processing via GPU-accelerated frequency analysis, lock-free circular buffering, and supporting system maintenance and reporting utilities.
- other/ringbuf — Provides a fixed-capacity, single-producer single-consumer (SPSC) circular buffer for efficient byte-stream data storage and retrieval.
- other/scripts — A collection of utility scripts for system maintenance and business data reporting.
- . — A multi-domain toolkit providing AI-driven source code analysis, high-performance GPU-accelerated data processing, and relational database schemas for inventory and logistics management.
- sql/library — A PostgreSQL-based lending library system for managing book inventory, member registrations, loan lifecycles, and multi-branch distribution.
- sql — A collection of database schemas and logic for managing inventory, circulation, and order fulfillment across library, e-commerce, and warehouse domains.
- sql/shop — Manages e-commerce product inventory, customer order tracking, and atomic stock reservation.
- sql/warehouse — Synchronizes warehouse inventory levels by aggregating supplier feed data into a consolidated stock ledger.
