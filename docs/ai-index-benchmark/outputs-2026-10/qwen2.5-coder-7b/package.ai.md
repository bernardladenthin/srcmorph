### ai
- H: 1.0
- C: 783A4955
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:18:43Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Manages core AI operations, including configuration, fact extraction, and text generation using various providers.

#### Purpose
- Manages AI-related configurations.
- Handles condition evaluation and validation.
- Extracts facts from source code.
- Configures AI generation parameters.
- Provides a pluggable interface for generating text using AI.
- Supports both local and mock implementations.
- Manages configuration and request handling for AI generation providers.
- Parses raw LLM completion text to extract the model's answer.
- Implements an AI generation service using the Llama.cpp JNI binding.
- Caches the GGUF model for subsequent generation calls.
- Parses chat responses and extracts completion text.
- Supports logging and warning on truncated answers.
- Measures and returns generation timings.

#### Responsibilities
- **Configuration Management**: Manages AI-related configurations, including condition evaluation, fact extraction, and generation settings.
- **Fact Extraction**: Extracts facts from source code.
- **AI Generation Interface**: Defines a contract for AI backend providers that can generate text based on an `AiGenerationRequest`.
- **Provider Factory**: Selects and instantiates an `AiGenerationProvider` implementation by name.
- **Completion Parsing**: Parses raw LLM completion text to isolate the model's answer and strip internal reasoning blocks.
- **Thread Safety**: Ensures the provider is designed to be thread-safe with lazy initialization of the native model handle.

#### Key units
- `AiConditionEvaluator` - Evaluates and validates `AiCondition` trees.
- `AiFactExtractor` - Computes a deterministic "facts" block from `AiFactCounter`s.
- `AiGenerationConfig` - Manages configuration parameters for AI generation.
- `AiOversizeStrategy` - Configures handling strategies for oversized source files.
- `SrcMorphConfiguration` - Holds configuration parameters for `srcmorph` runs.
- `AiGenerationProvider`: Interface for generating text using AI.
- `LlamaCppJniAiGenerationProvider`: Class implementing AI generation using the Llama.cpp JNI binding.
- `LlamaCppJniConfig`: Immutable configuration class for the llama.cpp JNI provider.
- `AiCompletionParser`: Class for parsing raw LLM completion text to extract the model's answer.
- `AiGenerationProviderFactory`: Factory class for creating `AiGenerationProvider` instances based on a provider name.

#### Data flow
- **Inputs**: `AiCondition` trees, `AiFileContext`, `AiFactCounter`s, and configuration settings.
- **Outputs**: `boolean` for condition matches, `String` for facts block, and configuration parameters.
- **Process**:
  - The `AiGenerationProviderFactory` determines the effective provider name and creates an instance.
  - The `LlamaCppJniAiGenerationProvider` generates AI completion text using the Llama.cpp JNI binding, caches the model, and handles logging and warnings.
  - The `AiCompletionParser` parses the raw completion text to extract the model's answer and strip internal reasoning blocks.

#### Dependencies
- **Internal Collaborations**:
  - `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `File`, `List`, `String`, `int`, `float`, `long`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
  - `AiPromptSupport`: For prompt lookup support.
  - `LlamaCppJniConfig`: For configuration settings.
  - `AiGenerationRequest`: For generation requests.
- **External Modules/Packages/Tables**:
  - `net.ladenthin.llama.LlamaModel`: For the native LlamaModel handle.
  - `com.fasterxml.jackson.databind.ObjectMapper`: For JSON processing.
  - `java.io.IOException`: For handling I/O exceptions.
  - `java.util.List`: For handling lists of stop strings and sequence breakers.
  - `java.util.function.Function`: For environment variable lookup.
  - `java.util.regex.Pattern`: For pattern matching in error messages.

#### Cross-cutting
- `@Nullable` annotations for handling potential null values.
- Defensive copying of input collections to prevent null pointer exceptions.
- No explicit concurrency handling.
- **Shared Base Types/Interfaces**: `AiGenerationProvider`, `AutoCloseable`.
- **Common Exception/Handling**: Throws `IOException` and `IllegalArgumentException`.
- **Concurrency**: The provider is designed to be thread-safe with lazy initialization of the native model handle.
- **Configuration**: Managed through the `LlamaCppJniConfig` class with a fluent builder pattern.

### other/
> Manages image histograms, thread-safe data storage, and backup rotation and sales report generation.

#### Purpose
- Computes histograms of byte values in image or byte streams.
- Provides a circular buffer for efficient data storage and retrieval.
- Rotates backups of specified directories and generates monthly sales reports.

#### Responsibilities
- Parallel byte counting and reduction for histograms.
- Data storage and retrieval with thread safety.
- Backup management and sales report generation.

#### Key units
- `histogram_partial`: Counts byte occurrences in the input stream and writes partial histograms.
- `histogram_reduce`: Sums partial histograms to produce the final histogram.
- `ringbuf_t`: Represents the ring buffer structure.
- `ringbuf_status_t`: Enumerates the status of operations.
- `rotate_backups.sh`: Bash script for rotating backups.
- `sales_report.py`: Python script for generating sales reports.

#### Data flow
- Histograms: `input` (global constant array of `uchar`), `length` (uint), `partial` (global array), `histogram` (global array).
- Ring buffer: Data written to `ringbuf_write`, read from `ringbuf_read`, tracked by `ringbuf_used` and `ringbuf_free_space`.
- Backup rotation: `rotate_backups.sh` reads `<source_dir>`, creates archives, deletes older ones.
- Sales report generation: `sales_report.py` reads `csv_files`, calculates revenue, and prints reports.

#### Dependencies
- Histograms: `uchar`, `uint`, `__global`, `__local`.
- Ring buffer: `ringbuf.h`, `stdlib.h`, `string.h`, `stddef.h`, `stdint.h`.
- Backup rotation: Bash, tar, ls, rm, du.
- Sales report generation: argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal.

#### Cross-cutting
- Bash scripts handle file operations.
- Python scripts use standard libraries for data processing.
- Both scripts validate input parameters and handle exceptions.

### sql/
> Manages the core operations and data for a lending library system, including schema modeling, reporting, routine book management, and data migration.

#### Purpose
- Models the core schema for a lending library, including authors, books, members, and loans.
- Provides reporting views for overdue loans, member loan statistics, and popular books.
- Manages book loans, including checkout, return, late fee calculations, and audit trails.
- Migrates the lending library schema to version 2, adding branches, managing book copies per branch, renaming a column, and retiring per-book copy counters.
- Seeds the lending library database with initial data for authors, books, and members.

#### Responsibilities
- **Schema Management**: Defines the database schema for the lending library.
- **Reporting**: Creates views and materialized views to provide insights into library operations.
- **Book Management**: Handles the checkout, return, and late fee calculations for books.
- **Data Migration**: Manages schema migrations to update the database structure.
- **Data Seeding**: Initializes the database with necessary data.

#### Key units
- **author**: Manages library authors.
- **book**: Manages library books, including their copies.
- **member**: Manages library members.
- **loan**: Manages book loans, including audit trails.
- **branch**: Manages library branches.
- **book_copy**: Manages individual copies of books across branches.
- **loan_audit**: Logs audit actions for book loans.
- **fn_checkout_book**: Function to checkout a book.
- **fn_late_fee**: Function to calculate late fees.
- **sp_return_book**: Stored procedure to return a book.
- **trg_loan_audit_fn**: Trigger to log loan audit actions.
- **overdue_loans**: View to show overdue loans.
- **member_loan_stats**: View to provide statistics on member loans.
- **popular_books**: Materialized view to show popular books.

#### Data flow
- **Inputs**: Member ID, Book ID, Loaned On, Due On, Returned On.
- **Process**: Checks if member is blocked, updates book copies, inserts loan records, calculates late fees, updates loan status.
- **Outputs**: New Loan ID, Updated Loan Record, Late Fee Amount.

#### Dependencies
- **author**: Manages authors.
- **book**: Manages books and their copies.
- **member**: Manages members.
- **loan**: Manages book loans.
- **branch**: Manages library branches.
- **book_copy**: Manages individual book copies across branches.
- **generate_series**: Used for generating series in migrations.

#### Cross-cutting
- **Shared Base Types**: `BIGSERIAL`, `VARCHAR`, `TIMESTAMPTZ`.
- **Common Exception/Handling**: Not explicitly mentioned in the summaries.
- **Threading/Concurrency**: Not explicitly mentioned in the summaries.
- **Configuration**: Not explicitly mentioned in the summaries.

### shop/
> Manages web shop orders, including adding items and updating stock and totals.

#### Purpose
- Manages order operations within the web shop.

#### Responsibilities
- Order item management.
- Stock updates.
- Order total calculations.

#### Key units
- `orders_mysql.sql`: Manages web shop orders, including adding items and updating stock and totals.

#### Data flow
- **Inputs**: `order_id`, `sku`, `quantity`.
- **Outputs**: Updated stock, updated order total.

#### Dependencies
- `product`: For product details and stock management.
- `customer_order`: For order status and total updates.
- `order_item`: For linking orders to items and managing item quantities.

#### Cross-cutting
- Common exception/error handling for stock issues and invalid products.

### warehouse/
> Nightly stock synchronization from a supplier feed.

#### Purpose
- Synchronizes stock levels in the warehouse with data from a supplier feed.
- Logs the synchronization process, including rows inserted and updated.

#### Key units
- **stock_sync_tsql.sql**: Procedure for nightly stock synchronization.

#### Data flow
- **SupplierFeed** data is read.
- Data is merged into **StockLevel**.
- The synchronization process is logged in **SyncLog**.

#### Dependencies
- **StockLevel**: Central table for stock levels.
- **SupplierFeed**: Source of stock data from the supplier.
- **SyncLog**: Table for logging synchronization results.
