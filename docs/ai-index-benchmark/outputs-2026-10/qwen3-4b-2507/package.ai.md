### ai
- H: 1.0
- C: 783A4955
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:18Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package provides end-to-end AI-driven source code analysis, transformation, and generation capabilities, alongside real-time business operations for a lending library, e-commerce order processing, and warehouse inventory synchronization—enabling intelligent code processing and data-driven business workflows.

#### Purpose  
- Enables AI-powered source code transformation through conditional filtering, deterministic fact extraction, and intelligent handling of large files  
- Supports pluggable AI backends (local models, remote APIs) with consistent parsing and performance tracking  
- Manages core business operations including book loans, order processing, and warehouse stock synchronization with real-time validation and audit logging  

#### Responsibilities  
- AI source code analysis: evaluate file eligibility, extract deterministic code facts, configure generation, and handle oversized files  
- AI generation orchestration: route requests to local or remote models, manage parameters, and parse responses  
- Business operations: manage library loans and availability, process product orders with stock validation, synchronize supplier stock data  

#### Key units  
- **AiConditionEvaluator**, **AiConditionGroup**: Evaluate file conditions using metadata and content rules  
- **AiFactExtractor**: Extracts exact counts of code elements via regex matching across full files  
- **AiGenerationConfig**, **SrcMorphConfiguration**: Central configuration for AI model parameters and transformation rules  
- **AiOversizeStrategy**: Defines strategies for handling files larger than model context (trim, sample, map-reduce, deterministic)  
- **AiGenerationProvider**, **AiGenerationProviderFactory**: Factory and interface for AI model instantiation and routing  
- **LlamaCppJniAiGenerationProvider**, **LlamaCppJniConfig**: Execute local GGUF models via llama.cpp with full timing and GPU support  
- **AiCompletionParser**: Strips internal reasoning from model responses to extract clean outputs  
- `author`, `book`, `member`, `loan`, `branch`, `book_copy`: Core library entities for book and member lifecycle  
- `product`, `order_item`, `customer_order`: Shop order data with real-time stock and total updates  
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`: Warehouse inventory tables for stock sync and audit  

#### Data flow  
- AI: File metadata is evaluated by **AiConditionEvaluator** → eligible files are processed by **AiFactExtractor** → generation config is applied → oversized files are handled via **AiOversizeStrategy** → request routed to provider → raw response parsed by **AiCompletionParser**  
- Library: Seed data → validate availability → checkout reduces `copies_free` → return increases it → audit logs all actions → reports derived from views  
- Shop: Order item added → stock validated → row locked → availability checked → item inserted → stock deducted → order total updated  
- Warehouse: Supplier feed → filtered by `@Since` → grouped by (WarehouseId, Sku) → MERGE into `StockLevel` → sync metrics logged in `SyncLog`  

#### Dependencies  
- AI: `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `java.util.List`, `java.io.File`, `java.time.Instant`, `java.util.regex.Pattern`, `lombok.ToString`, `org.jspecify.annotations.Nullable`, `com.fasterxml.jackson.databind.ObjectMapper`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.llama.value.ChatResponse`, `Usage`  
- Business: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`, `product`, `order_item`, `customer_order`, `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `generate_series`, `current_date`, `now()`, `SYSUTCDATETIME()`  

#### Cross-cutting  
- Immutable design: all configuration and response objects are immutable; no shared mutable state  
- Null safety: all inputs validated or treated as empty; outputs never null  
- Defensive copying: collections in condition groups are copied to prevent mutation  
- Configuration validation at build time (regex, dates, condition structure)  
- Error handling focused on invalid inputs (e.g., malformed regex, invalid stock)  
- Concurrency: thread-safe via immutability and lazy initialization  
- Real-time validation: stock checks, availability enforcement, and data locking (e.g., `FOR UPDATE`)  
- Audit logging: all key operations (loans, orders, syncs) are logged for compliance  
- Monetary precision: all financial values stored in cents to avoid floating-point errors  
- Type safety: Python dataclasses used in revenue reporting; OpenCL kernels assume valid input length  
- OpenCL thread safety: power-of-two buffer sizes, volatile head/tail pointers, space tracking  
- Input validation: enforced in histogram, ring buffer, and order processing operations
