### srcmorph-modelcard
- H: 1.0
- C: ABCBFFFB
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:16:23Z
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
The project organizes around an AI-aware source transformation layer (java packages) that handles source file selection, generation parameter tuning, oversized-file strategy management, and pluggable text generation and completion parsing via local or OpenAI-compatible models. A parallel data processing subsystem (other packages) computes 256-bin byte-level histograms using OpenCL reduction, manages fixed-capacity circular buffers, and performs directory/CSV-based revenue aggregation and backup rotation. A multi-domain relational data infrastructure (sql packages) provides schema, operational routines, and audit trails for lending libraries, e-commerce, and warehouse management, including MySQL web shop data models, order control via transactional stored procedures, and supplier inventory feed synchronization.

#### Packages
- java/config — This package enables AI-aware source file selection, generation parameter tuning, and oversized-file strategy management for build-time code transformation.
- java — This package provides AI‑aware source file selection, generation parameter tuning, oversized‑file strategy management, and pluggable AI text generation and completion parsing for srcmorph document workflows.
- java/provider — Pluggable AI text generation and completion parsing infrastructure for srcmorph document workflows, integrating local model inference and OpenAI-compatible model interactions.
- other/kernels — Computes 256-bin byte-level histograms for image or byte-stream data using OpenCL two-pass reduction.
- other — This package provides parallel histogram computation, fixed-capacity byte buffering, and directory/CSV-based revenue aggregation for data processing workflows.
- other/ringbuf — Provides a fixed-capacity circular buffer for single-producer single-consumer byte data transfer.
- other/scripts — This package provides backup rotation and monthly revenue aggregation tools for directory and CSV order data.
- . — A multi-domain platform that enables AI‑aware source file manipulation, parallel data processing, and relational database operations for lending, e‑commerce, and warehouse management.
- sql/library — A lending library system managing book inventory, member checkouts, branch distribution, and audit trails.
- sql — A multi-domain data infrastructure system providing schema, operational routines, and audit trails for lending library, e-commerce, and warehouse inventory management.
- sql/shop — Provides a MySQL 8 web shop data model with product catalog, order management, and stock control via a transactional stored procedure.
- sql/warehouse — Synchronises supplier inventory feeds into warehouse stock-level records with change logging.
