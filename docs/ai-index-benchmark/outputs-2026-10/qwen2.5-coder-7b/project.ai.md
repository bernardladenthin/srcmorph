### srcmorph-modelcard
- H: 1.0
- C: 914FA6DA
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:18:56Z
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
The project is structured around core AI operations and a lending library system. The `java` package provides a pluggable interface for generating text using AI, while the `java/config` and `java/provider` packages manage AI-related configurations and generation providers, respectively. The `other` package includes modules for image histograms, thread-safe data storage, and backup rotation and sales report generation, with specific implementations in `other/kernels`, `other/ringbuf`, and `other/scripts`. The `sql` package manages the core operations and data for a lending library system, including schema modeling, reporting, routine book management, and data migration, with additional functionality for web shop orders in `sql/shop` and nightly stock synchronization in `sql/warehouse`.

#### Packages
- java/config — Manages AI-related configurations, including condition evaluation, fact extraction, and generation settings.
- java — Manages AI-related configurations and provides a pluggable interface for generating text using AI.
- java/provider — Manages AI generation providers, including local implementations and support for OpenAI-compatible providers.
- other/kernels — Calculates a 256-bin byte histogram of an image or byte stream using OpenCL C 1.2.
- other — Manages image histograms, thread-safe data storage, and backup rotation and sales report generation.
- other/ringbuf — Implements a thread-safe ring buffer for efficient data storage and retrieval.
- other/scripts — Manages backup rotation and sales report generation for specified directories and regions.
- . — Manages core AI operations, including configuration, fact extraction, and text generation using various providers.
- sql/library — Manages the core operations and data for a lending library system, including schema modeling, reporting, routine book management, and data migration.
- sql — Manages the core operations and data for a lending library system, including schema modeling, reporting, routine book management, and data migration.
- sql/shop — Manages web shop orders, including adding items and updating stock and totals.
- sql/warehouse — Nightly stock synchronization from a supplier feed.
