### srcmorph-modelcard
- H: 1.0
- C: 9C87C7E1
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:34Z
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
The project integrates AI-driven source code analysis and transformation with real-time business operations across a lending library, e-commerce shop, and warehouse inventory system. The core AI functionality, centered in the java and java/config packages, enables conditional file processing, deterministic fact extraction, and large-file handling via intelligent oversizing, while the java/provider package offers flexible model integration with local and remote backends. Business logic is managed through domain-specific SQL packages—sql/library, sql/shop, and sql/warehouse—that ensure data consistency, stock validation, and real-time synchronization across operations. Supporting subsystems like other/kernels, other/ringbuf, and other/scripts provide GPU-accelerated data processing, efficient thread-safe buffering, and automated reporting, enabling scalable and reliable workflows.

#### Packages
- java/config — This package provides the core configuration and evaluation logic for AI-driven source code analysis and generation, enabling conditional file matching, deterministic fact extraction, and intelligent handling of large files beyond model context limits.
- java — This package enables AI-driven source code analysis and transformation by evaluating file conditions, extracting deterministic facts, configuring generation parameters, and handling large files through intelligent oversizing strategies—supporting both local and remote AI models with clean, production-ready output.
- java/provider — This package provides pluggable, configurable AI text generation for source code transformation, supporting local models (via llama.cpp JNI), OpenAI-compatible APIs, and mock providers with full timing and reasoning extraction.
- other/kernels — Computes a frequency histogram of byte values (0–255) in image or byte data using parallel OpenCL processing
- other — This package provides GPU-accelerated histogram computation, thread-safe bounded data buffering, and automated time-based backups and revenue reporting from order data.
- other/ringbuf — Manages efficient, thread-safe, bounded data transfer using a circular buffer with power-of-two capacity and precise space tracking
- other/scripts — Generates and manages time-based backups and regional revenue reports from order data
- . — This package provides end-to-end AI-driven source code analysis, transformation, and generation capabilities, alongside real-time business operations for a lending library, e-commerce order processing, and warehouse inventory synchronization—enabling intelligent code processing and data-driven business workflows.
- sql/library — Manages the full lifecycle of a lending library system, from data modeling and availability tracking to loan operations, reporting, and schema evolution with support for branch-specific book copies.
- sql — Manages core business operations across a lending library, e-commerce order processing, and warehouse inventory synchronization, ensuring data consistency, real-time validation, and auditability through domain-specific workflows and schema-driven logic.
- sql/shop — Enables real-time order item addition with stock validation, reservation, and automatic order total updates in a web shop
- sql/warehouse — This package synchronizes supplier-provided stock data with the central warehouse inventory system, ensuring data consistency through transactional updates and detailed audit logging.
