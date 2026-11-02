### srcmorph-modelcard
- H: 1.0
- C: 94DF7983
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:56:49Z
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
The core of srcmorph-modelcard is a Maven‑style build framework that orchestrates AI‑driven source‑code generation, with java/config supplying rule‑based file selection and runtime settings, java/provider handling local (llama.cpp) and OpenAI‑compatible backends, and java executing the generation pipeline. High‑performance byte‑stream utilities in other, including a lock‑free ring buffer and OpenCL kernels for histogramming, support efficient data handling for the AI and catalog subsystems. The unified framework ties these components to a database‑backed catalog, shop, and warehouse logic, with sql providing PostgreSQL, MySQL, and SQL Server schemas and stored procedures for library, e‑commerce, and inventory management, while sql/warehouse synchronises nightly supplier feeds. This architecture enables seamless AI code generation, fast stream processing, and robust multi‑database business logic.

#### Packages
- java/config — A Maven‑style configuration and evaluation framework for AI‑driven source‑code generation, providing rule‑based file selection, fact extraction, and runtime AI generation settings.
- java — A framework for configuring, evaluating, and executing AI‑driven source‑code generation within a Maven‑style build process.
- java/provider — A package that supplies AI‑generation backends, parses model responses, and manages configuration for local (llama.cpp) and OpenAI‑compatible providers.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — A lightweight, cross‑platform toolkit for efficient byte‑stream processing, circular buffering, and automated data‑management utilities.
- other/ringbuf — Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — Provides utilities for backing up directories and generating monthly sales reports from CSV order exports.
- . — A unified framework that orchestrates AI‑driven source‑code generation, high‑performance byte‑stream utilities, and database‑backed catalog, shop, and warehouse logic.
- sql/library — A PostgreSQL data model and procedural toolkit for a multi‑branch lending library, covering catalog, members, loans, reporting, and audit.
- sql — A collection of database schemas and stored procedures that manage catalog, e‑commerce, and warehouse inventory across PostgreSQL, MySQL, and SQL Server.
- sql/shop — Provides a MySQL schema and stored procedure for managing e‑commerce orders, products, and inventory.
- sql/warehouse — Synchronises nightly supplier stock feeds into a warehouse inventory system and records the sync outcome.
