### srcmorph-modelcard
- H: 1.0
- C: 0D267A2C
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:48:17Z
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
This multi-domain toolkit integrates an AI-driven source code summarization engine that employs pluggable LLM providers and configuration management for content extraction. It provides high-performance data processing primitives, including GPU kernels for byte frequency analysis and circular byte buffers for FIFO streaming. The project also includes a collection of SQL-based inventory and resource management systems for library loans, retail order processing, and warehouse stock synchronization.

#### Packages
- java/config — Provides configuration management and evaluation logic for an AI-driven source code summarization engine, handling file filtering, inference parameters, and content extraction strategies.
- java — An AI-driven source code summarization engine that manages file filtering, configuration, and pluggable LLM provider integrations.
- java/provider — Provides a pluggable infrastructure for integrating various AI text generation backends, including local JNI-based models and remote OpenAI-compatible APIs, to process source code requests.
- other/kernels — Performs high-performance byte frequency distribution analysis using parallel GPU kernels.
- other — A collection of high-performance data processing primitives and administrative utility scripts for system maintenance and financial reporting.
- other/ringbuf — A circular byte buffer for efficient FIFO data streaming between a single producer and a single consumer.
- other/scripts — A collection of administrative utility scripts for system maintenance and business data reporting.
- . — A multi-domain toolkit providing AI-powered source code summarization, high-performance data processing primitives, and diverse inventory management database systems.
- sql/library — A lending library system for managing book inventory, member registration, and the borrowing lifecycle.
- sql — A collection of inventory and resource management systems covering library loans, retail order processing, and warehouse stock synchronization.
- sql/shop — Manages product inventory, customer order tracking, and transactional stock reservation for a web shop.
- sql/warehouse — Synchronizes warehouse inventory levels by aggregating supplier feed data into a master stock table with execution logging.
