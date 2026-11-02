### srcmorph-modelcard
- H: 1.0
- C: 88F3C70E
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:33:42Z
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
The project is an AI‑driven source‑morphing framework that evaluates rule‑based file selection, extracts deterministic facts, and delegates code generation to pluggable LLM providers such as llama.cpp, OpenAI‑compatible APIs, or mocks. It integrates a byte‑stream utility layer that includes lock‑free circular buffers, OpenCL‑based 256‑bin histograms, and backup/reporting scripts, supporting efficient data handling for the morphing pipeline. Database modules span PostgreSQL, MySQL, and SQL Server, providing cataloging, e‑commerce order processing, warehouse inventory sync, and lending‑library logic, all orchestrated by the core framework. The overall architecture ties AI generation, stream processing, and multi‑database persistence into a cohesive source‑code transformation and documentation system.

#### Packages
- java/config — A configuration‑driven AI‑powered source‑morphing framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI generation for source‑code documentation and transformation.
- java — A framework that orchestrates AI‑driven source‑code morphing by evaluating file‑selection rules, extracting deterministic facts, and delegating generation to pluggable LLM providers.
- java/provider — Provides a pluggable AI generation subsystem that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, along with utilities for parsing completions and building request payloads.
- other/kernels — Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.
- other — A collection of utilities for processing byte streams, managing lock‑free circular buffers, and automating backup and sales reporting tasks.
- other/ringbuf — Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.
- other/scripts — A utility package that backs up directories and generates monthly sales reports from CSV order exports.
- . — A framework that orchestrates AI‑driven source‑code morphing, from rule‑based file selection and deterministic fact extraction to pluggable LLM generation and post‑processing, with supporting utilities for byte‑stream handling, GPU histograms, and database‑level cataloging, e‑commerce, and inventory logic.
- sql/library — A PostgreSQL schema and supporting objects for a multi‑branch lending library, including catalog, member, loan logic, reporting views, and migration to branch‑specific book copies.
- sql — A collection of database‑level modules that manage library cataloging, e‑commerce order lines, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.
- sql/shop — A MySQL stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.
- sql/warehouse — Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.
