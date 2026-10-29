### srcmorph-modelcard
- H: 1.0
- C: 2E340DD5
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:55:35Z
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
srcmorph provides an AI-driven file processing and generation framework that integrates pluggable backends for local GGUF models and OpenAI-compatible APIs to perform deterministic source code transformations. The system relies on a configuration layer that validates condition trees and manages inference parameters, while provider packages handle the parsing and execution of generation tasks across different model backends. Complementary infrastructure packages manage byte-stream buffering via lock-free ring buffers, image analysis through OpenCL histogram kernels, and data pipeline reporting, ensuring reliable operation across model inference and file processing workflows.

#### Packages
- java/config — srcmorph AI condition evaluation and configuration: validates condition trees, computes facts blocks, and manages generation parameters for model inference.
- java — srcmorph integrates AI-driven file processing and generation with pluggable backends for local GGUF models and OpenAI-compatible APIs.
- java/provider — This package provides pluggable AI generation backends for source code transformation, supporting local GGUF models via llama.cpp JNI bindings and OpenAI-compatible API integration, with parsing and configuration infrastructure.
- other/kernels — Calculates a 256-bin byte histogram for an image or byte stream using OpenCL parallel reduction across two passes.
- other — Calculates histograms, buffers byte-streams, and automates backups/reporting for data pipelines.
- other/ringbuf — A lock-free single-producer single-consumer circular buffer for high-throughput byte-stream data buffering.
- other/scripts — A backup rotation utility and a sales aggregation script that together handle data preservation and financial reporting from CSV exports.
- . — This package integrates AI-driven file processing and generation with pluggable backends for local GGUF models and OpenAI-compatible APIs, enabling deterministic source code transformations and model inference.
- sql/library — A lending library system managing authors, books, members, and loans with branch-aware copy tracking and audit trails.
- sql — A data synchronization layer that merges supplier nightly feeds into warehouse stock records, enforces inventory integrity, and logs every change for audit and rollback.
- sql/shop — A web shop model for products, orders, and order placement with stock reservation and total calculation.
- sql/warehouse — Synchronises nightly stock levels from a supplier feed into the warehouse inventory, merging new and updated quantities and logging the result.
