### ai
- H: 1.0
- C: 2D842599
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T03:00:21Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.

#### Purpose
- To determine if a file matches a set of conditions defined in an {@link AiCondition} tree.
- To validate the conditions to ensure they are correctly configured.
- To identify if any condition requires line count information.

#### Responsibilities
- **AiConditionEvaluator**:
  - Evaluates condition trees against file contexts.
  - Validates condition trees for correctness.
  - Checks if any condition requires line count information.

#### Key Units
- **AiConditionEvaluator**:
  - **matches**: Recursively evaluates condition trees.
  - **validate**: Ensures conditions are correctly configured.
  - **usesLines**: Checks for line count requirements.

#### Data flow
- **Input**: {@link AiCondition} tree and {@link AiFileContext}.
- **Output**: Boolean result and boolean indicating line count requirement.
- **Process**:
  - `matches` method processes the condition tree.
  - `validate` method ensures conditions are valid.
  - `usesLines` method checks for line count conditions.

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`

#### Cross-cutting
- **Shared base types**: Utilizes `java.time` for date handling.
- **Exception handling**: Throws `IllegalArgumentException` for invalid conditions.
- **Threading**: Methods do not rely on shared mutable state.

#### Purpose
- Provide a Java implementation for generating AI-driven content using a local llama.cpp JNI binding.
- Support various model configurations and parameters for efficient AI generation.

#### Responsibilities
- Parses raw LLM completion text to extract the model answer.
- Provides a method for generating text based on an AI generation request.
- Selects and instantiates an AI generation provider based on the given provider name.
- Calculates VAT for invoices by processing request bodies, endpoints, API key handling, and error messages for OpenAI-compatible AI generation providers.

#### Key units
- `AiCompletionParser`: Parses raw LLM completion text to extract the model's actual answer from raw completion text by stripping internal reasoning blocks.
- `AiGenerationProvider`: Interface for generating text based on an AI generation request.
- `AiGenerationProviderFactory`: Selects and instantiates an AI generation provider based on the given provider name.
- `OpenAiCompatibleProviderSupport`: Provides support for configuring and interacting with OpenAI-compatible AI generation providers.

#### Data flow
- `AiCompletionParser` processes raw completion text to extract the model's actual answer.
- `AiGenerationProvider` generates text based on an AI generation request.
- `AiGenerationProviderFactory` selects and instantiates an AI generation provider based on the given provider name.
- `OpenAiCompatibleProviderSupport` processes request bodies, endpoints, API key handling, and error messages for OpenAI-compatible AI generation providers.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `java.util.function.Function`
- `java.util.regex.Pattern`
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Cross-cutting
- Shared base types/interfaces: `AiGenerationProvider`, `AutoCloseable`
- Common exception/error handling: `IOException`
- Threading/concurrency notes: The class is not thread-safe as it does not modify any shared state.
- Configuration: `LlamaCppJniConfig`, `AiGenerationConfig`

#### Purpose
- Calculates a 256-bin byte histogram of an image or any byte stream using OpenCL C and manages a fixed-capacity byte ring buffer for data transfer between a single producer and a single consumer, essential for applications requiring minimal memory allocation and efficient data handling.

#### Responsibilities
- **kernels/**: Calculates a 256-bin byte histogram using OpenCL C.
  - **Pass 1**: Counts byte occurrences in chunks assigned to each work-group, storing partial histograms in `partial`.
  - **Pass 2**: Sums the partial histograms from all work-groups into the final `histogram`.

#### Responsibilities
- **kernels/histogram.cl**: Calculates a 256-bin byte histogram using OpenCL C.
- **ringbuf/**: Initialize and manage a ring buffer for data storage and provides methods to write and read data from the ring buffer.

#### Key units
- **histogram_partial**: Computes partial histograms for each work-group.
- **histogram_reduce**: Sums partial histograms to produce the final histogram.
- **ringbuf_t**: Structure representing the ring buffer.
- **ringbuf_init**: Function to initialize the ring buffer.
- **ringbuf_free**: Function to free the allocated memory for the ring buffer.
- **ringbuf_write**: Function to write data into the ring buffer.
- **ringbuf_read**: Function to read data from the ring buffer.

#### Data flow
- **kernels/**:
  - **Pass 1**:
    - `input` (global memory, byte array) -> `partial` (global memory, array to store partial histograms)
    - `length` (global, length of the input array) -> `partial` (global memory, array to store partial histograms)
    - `local_bins` (local memory, array for each work-group's histogram bins) -> `partial` (global memory, array to store partial histograms)
  - **Pass 2**:
    - `partial` (global memory, array containing partial histograms from each work-group) -> `histogram` (global memory, array to store the final histogram)
    - `group_count` (global, number of work-groups) -> `histogram` (global memory, array to store the final histogram)
- **ringbuf/**:
  - `ringbuf_init` initializes the ring buffer.
  - `ringbuf_write` writes data into the ring buffer.
  - `ringbuf_read` reads data from the ring buffer.
  - `ringbuf_free` frees the allocated memory for the ring buffer.

#### Dependencies
- **kernels/**: None
- **ringbuf/**:
  - `ringbuf.h`: Header file defining the ring buffer structure and status codes.
  - `stdlib.h`: For memory allocation and deallocation.
  - `string.h`: For memory copying.
- **scripts/**:
  - `bash` utilities like `basename`, `date`, `dirname`, `tar`, `mkdir`, `ls`, `cut`, `du`, `mapfile`.
  - Python libraries like `argparse`, `csv`, `sys`, `collections.defaultdict`, `datetime.date`, `decimal.Decimal`.

#### Cross-cutting
- Both kernels and scripts use optional parameters and handle exceptions/errors gracefully.
- `scripts/sales_report.py` uses a minimum revenue threshold for filtering results.

#### Purpose
- Models authors, books, members, and loans in a lending library.
- Tracks book inventory, member details, and loan transactions.
- Provides reporting views for overdue loans and member loan statistics, including a materialized view for popular books.
- Includes routines for book checkout, return, late fee calculation, and audit trail.

#### Responsibilities
- **01_schema.sql**: Models authors, books, members, and loans in a lending library.
- **02_views.sql**: Provides reporting views for tracking overdue loans and member loan statistics, including a materialized view for popular books.
- **03_routines.sql**: Includes routines for book checkout, return, late fee calculation, and audit trail.
- **04_migration_v2.sql**: Adds branches, moves copies per branch, renames a column and retires the per-book copy counters.
- **05_seed_data.sql**: Seeds initial data into the database for a lending library system.

#### Key units
- **author**: Models authors with `author_id`, `full_name`, `birth_year`, and `country_code`.
- **book**: Models books with `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- **member**: Models members with `member_id`, `email`, `full_name`, `joined_on`, and `is_blocked`.
- **loan**: Tracks loan transactions with `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, and `returned_on`.
- **overdue_loans**: A view tracking loans that are overdue.
- **member_loan_stats**: A view providing statistics on member loans.
- **popular_books**: A materialized view listing books loaned multiple times within the last year.
- **fn_checkout_book**: Function for checking out a book.
- **sp_return_book**: Stored procedure for returning a book.
- **fn_late_fee**: Function for calculating late fees.
- **trg_loan_audit_fn**: Trigger for recording audit trail.

#### Data flow
- **Inputs**: Initial data from `05_seed_data.sql` into `author`, `book`, `member`, and `loan` tables.
- **Outputs**: Views `overdue_loans`, `member_loan_stats`, and materialized view `popular_books`.
- **Intermediates**: Routines `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, and `trg_loan_audit_fn` handle data manipulation.

#### Dependencies
- **Internal**: `01_schema.sql`, `02_views.sql`, `03_routines.sql`, `04_migration_v2.sql`, `05_seed_data.sql`.
- **External**: PostgreSQL database.

#### Cross-cutting
- **Shared interfaces**: SQL objects (`author`, `book`, `member`, `loan`, `overdue_loans`, `member_loan_stats`, `popular_books`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`).
- **Exception handling**: Routines handle exceptions and errors internally.
- **Concurrency**: Routines handle concurrent access to shared resources.
- **Configuration**: No specific configuration required beyond database setup.

#### Purpose
- Manage products with SKU, name, price, and stock.
- Track orders with status, creation time, and total amount.
- Link orders to products and track quantities and prices.
- Manage order items, including adding items to an order, reserving stock, and updating order totals.

#### Responsibilities
- **Products**: Manage products with SKU, name, price, and stock.
- **Customer Orders**: Track orders with status, creation time, and total amount.
- **Order Items**: Link orders to products and track quantities and prices.
- **Procedures**: Manage order items, including adding items to an order, reserving stock, and updating order totals.

#### Key units
- **product**: Manages products with SKU, name, price, and stock.
- **customer_order**: Tracks orders with status, creation time, and total amount.
- **order_item**: Links orders to products and tracks quantities and prices.
- **add_order_item**: Manages order items, including adding items to an order, reserving stock, and updating order totals.

#### Data flow
- Inputs: SKU, quantity, order ID, customer ID, product ID.
- Outputs: Order status, total amount, updated stock.
- Flow: Retrieves product details, checks stock, updates order items, updates product stock, and updates order totals.

#### Dependencies
- References `product` and `customer_order` tables.
- Uses `order_item` for item management.

#### Cross-cutting
- Uses procedures for complex logic.
- References external tables (`product`, `customer_order`, `order_item`).
- Manages stock and order totals.

#### Purpose
- Synchronize stock levels from a supplier feed into the warehouse database.
- Log the outcome of the synchronization process.

#### Responsibilities
- SQL procedure for synchronizing stock levels.
- Logging synchronization results.

#### Key units
- **dbo.StockLevel**
  - Stores stock levels for each warehouse and SKU.
- **dbo.SupplierFeed**
  - Contains supplier feed data for stock synchronization.
- **dbo.SyncLog**
  - Logs the synchronization process outcomes.

#### Data flow
- Reads data from `dbo.SupplierFeed` to update `dbo.StockLevel`.
- Writes synchronization results to `dbo.SyncLog`.

#### Dependencies
- References `dbo.SupplierFeed` for data.
- Uses `dbo.SyncLog` for logging.

#### Cross-cutting
- Uses transactions for data consistency.
- Logs errors and exceptions during synchronization.

This structured summary provides a clear understanding of the package's responsibilities and how files within the package collaborate to achieve the overall business goal of managing a lending library, including its web shop and warehouse operations.
