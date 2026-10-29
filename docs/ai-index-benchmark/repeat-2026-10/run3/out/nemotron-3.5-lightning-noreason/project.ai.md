### srcmorph-modelcard
- H: 1.0
- C: F4261C7B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:18Z
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
The project implements an AI-assisted source modification framework that governs model selection and application to files during builds, supported by pluggable text generation backends for code transformation. It also provides a lending library system for managing authors, books, and loans, alongside a separate module for calculating image byte histograms and managing deterministic byte-stream throughput via a lock-free ring buffer.

#### Packages
- java/config — This package provides a configurable framework for AI-assisted source modification, governing how AI models are selected, conditioned, and applied to source files during build-time processing.
- java — This package provides a configurable framework for AI-assisted source modification, governing how AI models are selected, conditioned, and applied to source files during build-time processing.
- java/provider — This package provides pluggable AI text generation capabilities for code transformation tasks, supporting multiple backends including local GGUF models via llama.cpp JNI, OpenAI-compatible APIs, and mock implementations, with integrated completion parsing and configuration management.
- other/kernels — Calculates a 256-bin byte histogram of an image or byte stream using two-pass OpenCL parallel reduction.
- other — Calculates a 256-bin byte histogram of an image or byte stream using two-pass OpenCL parallel reduction, and provides a lock-free ring buffer for deterministic byte-stream throughput, while scripts manage backup rotation and sales revenue aggregation.
- other/ringbuf — A lock-free single-producer/single-consumer ring buffer with power-of-two capacity for deterministic byte-stream throughput.
- other/scripts — Rotates and prunes dated backups, and generates monthly sales revenue reports from CSV order data.
- . — This package provides a configurable framework for AI-assisted source modification, governing how AI models are selected, conditioned, and applied to source files during build-time processing.
- sql/library — A lending library system managing authors, books, members, and loans with full checkout, return, and reporting capabilities.
- sql — A lending library system managing authors, books, members, and loans with checkout, return, and reporting capabilities.
- sql/shop — This package models a web shop's product catalog and order lifecycle, providing the database schema and a stored procedure to manage item addition, stock validation, and order total calculation.
- sql/warehouse — Synchronises nightly stock levels from a supplier feed into the warehouse stock table and logs the operation.
