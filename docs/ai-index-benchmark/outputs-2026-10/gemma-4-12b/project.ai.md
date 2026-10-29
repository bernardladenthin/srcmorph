### srcmorph-modelcard
- H: 1.0
- C: B7350580
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T04:50:10Z
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
This project provides a multi-faceted infrastructure for AI-driven source code analysis, featuring a pluggable inference system that supports local GGUF models via JNI and remote OpenAI-compatible APIs. It integrates high-performance data processing capabilities, including OpenCL kernels for parallel histogram generation and thread-safe circular buffering for stream communication. Additionally, the system includes a comprehensive relational database suite for managing multi-branch library systems, retail e-commerce inventory, and automated warehouse stock synchronization.

#### Packages
- java/config — This package provides a comprehensive configuration framework for an AI-driven source code analysis tool, managing file filtering, model parameters, and generation strategies.
- java — This package provides a pluggable infrastructure for configuring and executing AI-driven source code analysis, supporting both local GGUF models via JNI and remote OpenAI-compatible APIs.
- java/provider — This package provides a pluggable infrastructure for executing AI model inference, supporting both local GGUF models via llama.cpp JNI and remote OpenAI-compatible APIs.
- other/kernels — Provides high-performance parallel image and byte stream histogram generation using OpenCL kernels.
- other — This package provides a high-performance data processing infrastructure featuring parallelized histogram generation, thread-safe circular buffering for stream communication, and administrative utilities for backup and financial reporting.
- other/ringbuf — Provides a fixed-capacity circular buffer for thread-safe, single-producer/single-consumer byte stream communication.
- other/scripts — This package provides administrative and reporting utilities for automated data maintenance and financial analysis.
- . — This package provides a multi-faceted infrastructure for AI-driven source code analysis, high-performance data processing, and relational database management for inventory and library systems.
- sql/library — This package provides a relational database schema and business logic for a multi-branch lending library system, managing inventory, member accounts, and loan lifecycles.
- sql — This package provides a comprehensive suite of relational database schemas and business logic for managing multi-branch library systems, retail e-commerce inventory, and warehouse stock synchronization.
- sql/shop — This package manages a web shop's inventory and order processing system, handling product cataloging, stock management, and transactional order fulfillment.
- sql/warehouse — This package manages the automated synchronization of warehouse inventory levels by merging supplier feed data into a master stock table.
