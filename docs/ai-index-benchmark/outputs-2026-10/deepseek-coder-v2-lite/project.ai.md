### srcmorph-modelcard
- H: 1.0
- C: B21C221E
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T03:14:57Z
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
The project, `srcmorph-modelcard`, is a comprehensive system designed to evaluate and validate AI condition trees against an AI file context. It is composed of several interconnected packages that work together to achieve its objectives. The `java` package, in particular, is responsible for evaluating and validating AI condition trees, which is crucial for the core functionality of the project. The `java/provider` package extends this functionality by calculating VAT for invoices using a Java implementation that leverages a local llama.cpp JNI binding for AI-driven content generation, supporting various model configurations and parameters. Additionally, the `other` package manages a byte ring buffer for efficient data handling, while the `other/kernels` package calculates a 256-bin byte histogram using OpenCL C for image or byte stream analysis. The `other/scripts` package is responsible for creating compressed, dated backups of directories and generating sales reports with VAT calculations. Finally, the `sql` packages handle the lending library and web shop functionalities, including core schema management, reporting views, and database interactions for efficient data management and reporting.

#### Packages
- java/config — Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.
- java — Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.
- java/provider — Calculates VAT for invoices by providing a Java implementation for generating AI-driven content using a local llama.cpp JNI binding, supporting various model configurations and parameters for efficient AI generation.
- other/kernels — Calculates a 256-bin byte histogram of an image or any byte stream using OpenCL C.
- other — Calculates a 256-bin byte histogram of an image or any byte stream using OpenCL C and manages a fixed-capacity byte ring buffer for data transfer between a single producer and a single consumer, essential for applications requiring minimal memory allocation and efficient data handling.
- other/ringbuf — Calculates VAT for invoices and manages a fixed-capacity byte ring buffer for data transfer between a single producer and a single consumer.
- other/scripts — Creates compressed, dated backups of directories and generates sales reports with VAT calculations.
- . — Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.
- sql/library — Lending library: core schema (PostgreSQL) and reporting views for tracking overdue loans and member loan statistics, including a materialized view for popular books. Additionally, it includes routines for book checkout, return, late fee calculation, and audit trail.
- sql — Lending library: core schema (PostgreSQL) and reporting views for tracking overdue loans and member loan statistics, including a materialized view for popular books. Additionally, it includes routines for book checkout, return, late fee calculation, and audit trail.
- sql/shop — Web shop: products, orders and order placement (MySQL 8).
- sql/warehouse — Warehouse: nightly stock synchronisation from a supplier feed (SQL Server, T-SQL).
