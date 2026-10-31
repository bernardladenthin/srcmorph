### srcmorph-modelcard
- H: 1.0
- C: 5D1FD589
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:45:08Z
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
srcmorph-modelcard indexes source files and generates AI-assisted metadata for each file within a project, with pluggable LLM text generation provided by java/provider supporting local llama.cpp models and OpenAI-compatible providers. The other package provides byte-level histogram computation using two-pass OpenCL kernels in other/kernels, lock-free byte streaming via the single-producer single-consumer ring buffer in other/ringbuf, and sales reporting and backup automation in other/scripts. The sql package combines a lending library managed by sql/library for book copies, member loans, and checkout operations across branches, a web shop in sql/shop for product catalogs, customer orders, stock reservation, and total calculation, and a warehouse in sql/warehouse for nightly stock synchronisation merging supplier feed quantities into warehouse stock levels. The multi-domain package in . ties these subsystems together as a lending library, web shop, and warehouse stock synchronisation system.

#### Packages
- java/config — Indexes source files and generates AI-assisted metadata for each file within a project.
- java — Indexes source files and generates AI-assisted metadata for each file within a project.
- java/provider — Provides pluggable LLM text generation for source-document AI requests, supporting local llama.cpp models and OpenAI-compatible providers.
- other/kernels — Computes a 256-bin byte histogram of an image or byte stream using two-pass OpenCL kernels.
- other — A package providing byte-level histogram computation, lock-free byte streaming, and sales reporting/backup automation.
- other/ringbuf — A fixed-capacity, single-producer single-consumer byte ring buffer for lock-free byte streaming between producer and consumer
- other/scripts — Generates monthly sales reports from order CSV exports and rotates directory backups.
- . — A multi-domain package combining a lending library, a web shop, and a warehouse stock synchronisation system.
- sql/library — A lending library system managing book copies, member loans, and checkout operations across branches.
- sql — A multi-domain package combining a lending library, a web shop, and a warehouse stock synchronisation system.
- sql/shop — Models a web shop's product catalog, customer orders, and order placement with stock reservation and total calculation.
- sql/warehouse — Nightly stock synchronisation merges supplier feed quantities into warehouse stock levels and logs the outcome.
