### ai
- H: 1.0
- C: 031A9421
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T04:46:55Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package provides a multi-faceted infrastructure for AI-driven source code analysis, high-performance data processing, and relational database management for inventory and library systems.

#### Purpose
* Provides a unified framework for managing AI generation parameters, model routing, and source code processing strategies.
* Implements high-performance parallel histogram generation and thread-safe circular buffering.
* Manages structured data models and transactional logic for library, retail, and warehouse inventory systems.

#### Responsibilities
* **AI Inference & Analysis**: Handles configuration for local GGUF models (via JNI) and remote OpenAI-compatible APIs, including condition-based file filtering and response sanitization.
* **Data Processing & Streaming**: Executes parallel byte histogram calculations using OpenCL and manages SPSC circular buffers for efficient FIFO byte storage.
* **System & Financial Utilities**: Automates directory backups, retention policies, and parses CSV data for regional/monthly revenue reporting.
* **Database & Inventory Management**: Manages multi-branch library loan lifecycles, retail order fulfillment with atomic stock deduction, and warehouse supplier feed synchronization.

#### Key units
* `SrcMorphConfiguration`: Aggregates project paths, file filters, and model definitions.
* `AiConditionEvaluator`: Recursively evaluates `AiCondition` trees against `AiFileContext`.
* `AiGenerationProvider`: Core interface for unified text generation and performance metrics.
* `LlamaCppJniAiGenerationProvider`: Implementation for local GGUF inference via native JNI.
* `ringbuf_t`: Core structure for circular buffer data and volatile head/tail indices.
* `StockLevel`, `SupplierFeed`, `SyncLog`: Tables for warehouse inventory, staging, and sync metrics.
* `fn_checkout_book`, `sp_return_book`: Procedures for library loan logic.
* `usp_SyncStock`: Procedure for aggregating supplier feeds and merging into master stock.

#### Data flow
* **AI Pipeline**: `SrcMorphConfiguration` inputs $\rightarrow$ `AiConditionEvaluator` filtering $\rightarrow$ `AiFactExtractor` metadata generation $\rightarrow$ `AiGenerationProvider` inference $\rightarrow$ `AiCompletionParser` cleaning.
* **Histogram Pipeline**: Input byte streams $\rightarrow$ `histogram_partial` (local/global buffers) $\rightarrow$ `histogram_reduce` $\rightarrow$ Final Histogram.
* **Buffer Pipeline**: `ringbuf_write` (source array $\rightarrow$ bitwise masked buffer) $\rightarrow$ `ringbuf_read` (buffer $\rightarrow$ destination array).
* **Warehouse Pipeline**: `SupplierFeed` $\rightarrow$ Aggregation by SKU/Warehouse $\rightarrow$ `MERGE` into `StockLevel` $\rightarrow$ `SyncLog` population.

#### Dependencies
* **Internal**: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`.
* **External**: `llama.cpp` JNI bindings, Jackson (JSON), `net.ladenthin.llama`, OpenCL C 1.2, PostgreSQL, SQL Server.
* **System**: `tar`, `date`, `du`, `ls`, `mkdir`.

#### Cross-cutting
* **Concurrency & Performance**: SPSC thread safety via `volatile` indices; OpenCL kernels for parallel reduction; Power-of-Two constraints for bitwise masking.
* **Validation & Integrity**: Extensive `validate()` methods for AI conditions; strict shell exit codes (`set -euo pipefail`); atomic stock updates via stored procedures.
* **Resource Management**: Providers implement `AutoCloseable` for native/network handles; automated `loan_audit` and `SyncLog` for database audit trails.
* **Security**: HTTPS/loopback checks for remote API keys; standardized `IOException` for native/network failures.
