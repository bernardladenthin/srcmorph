### ai
- H: 1.0
- C: B8012CEF
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:28:41Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package offers a wide range of AI-driven capabilities, efficient data processing techniques, and comprehensive management solutions for library operations, e-commerce transactions, and warehouse management.

#### Purpose
- AI-powered condition evaluation and fact extraction
- Source code transformation using AI
- Byte frequency distribution computation
- Circular buffer implementation for data streaming
- Backup rotation and sales report generation
- Lending library data and operations management
- E-commerce transaction processing
- Warehouse stock management

#### Responsibilities
- AI condition evaluation and validation
- Fact extraction from code
- Source code transformation
- AI provider management and text generation
- Parallel processing of byte data
- Histogram computation and reduction
- Circular buffer management
- Backup rotation
- Sales report generation
- Library data management and lending operations
- Product inventory and sales transaction handling
- Stock management and supplier data integration

#### Key units
- `AiConditionEvaluator`: Evaluates and validates AI condition trees
- `AiFactExtractor`: Computes deterministic facts from AI fact counters
- `AiGenerationConfig`: Configures AI generation parameters
- `SrcMorphConfiguration`: Configures AI-driven source code transformations
- `AiCompletionParser`: Parses and cleans AI model answers
- `AiGenerationProvider`: Interface for AI text generation
- `AiGenerationProviderFactory`: Selects and instantiates AI providers
- `LlamaCppJniAiGenerationProvider`: Local GGUF model-based AI generation
- `LlamaCppJniConfig`: Configuration for llama.cpp JNI provider
- `OpenAiCompatibleProviderSupport`: Manages OpenAI-compatible AI requests
- `histogram_partial`: Computes local histograms for work-groups
- `histogram_reduce`: Combines partial histograms into final result
- `ringbuf_t`: Ring buffer structure
- `ringbuf_init()`: Initializes ring buffer
- `ringbuf_free()`: Frees allocated memory
- `ringbuf_write()`: Writes data to buffer
- `ringbuf_read()`: Reads data from buffer
- `rotate_backups.sh`: Manages backup rotation for specified directories
- `sales_report.py`: Processes CSV files to generate monthly sales reports
- PostgreSQL schema (tables, indexes)
- Views (overdue_loans, member_loan_stats, popular_books)
- Functions (fn_checkout_book, fn_late_fee)
- Procedures (sp_return_book)
- Triggers (trg_loan_audit)
- Audit table (loan_audit)
- Branch management (branch, book_copy)
- `product` table
- `customer_order` table
- `order_item` table
- `add_order_item` procedure
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`

#### Data flow
AI conditions are evaluated against file contexts, facts are extracted from source text, and source code is transformed based on AI configurations. AI generation requests flow through the provider factory, configuration, and execution pipeline, with results returned as generated text or timing information. Byte data is processed through local histograms, then combined into the final 256-bin histogram. Data moves through the ring buffer from producers to consumers using lock-free operations. Backup rotation: Source directory → Compressed archive → Backup directory. Sales report generation: CSV files → Parsed data → Formatted output. Library operations flow through the PostgreSQL schema, with views aggregating data for reporting and audit tables logging activities. E-commerce transactions are processed through MySQL tables and procedures, updating both order and product information. Warehouse stock levels are updated based on supplier feed data, synchronized nightly.

#### Dependencies
- Core Java utilities
- AI-specific libraries (e.g., jSpecify for nullability annotations)
- Custom AI condition and fact counter classes
- Jackson library for JSON processing
- Custom classes for request/response handling and configuration
- OpenCL runtime for parallel execution
- Work-group and memory management for efficient processing
- `<stdlib.h>`: For memory allocation
- `<string.h>`: For data copying
- `<stddef.h>`: For `size_t` type
- `<stdint.h>`: For `uint8_t` type
- Bash shell for `rotate_backups.sh`
- Python standard library for `sales_report.py`
- CSV files for input data
- Core tables: author, book, member, loan
- Supporting tables: branch, book_copy
- Views depend on loan, member, book, author tables
- Functions and procedures interact with loan, book, member tables
- Audit trigger depends on loan table
- Collaborates with `dbo.StockLevel` and `dbo.SupplierFeed` for data management
- Uses `dbo.SyncLog` for activity tracking

#### Cross-cutting
- Thread-safe immutable classes for condition evaluation and fact extraction
- Configurable AI generation parameters
- Extensible source code transformation strategies
- Immutable configuration objects
- Thread-safe implementations where applicable
- Error handling and timing management across providers
- Unified API for different AI provider implementations
- Two-pass algorithm for efficient histogram computation
- Parallel processing patterns for large dataset handling
- Lock-free operations for thread safety
- Power-of-two capacity optimization
- Two-chunk read/write for efficient data handling
- Error handling for invalid inputs and allocation failures
- Both scripts handle exceptions and provide meaningful error messages
- Both scripts validate input parameters and handle potential errors
- SQL schema for product, order, and item management
- Stock management logic
- Order status tracking
- Implements error handling and logging
- Uses T-SQL for database operations
- Follows a consistent schema for warehouse and supplier data
