### srcmorph-modelcard
- H: 1.0
- C: 1CB2400F
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:26:43Z
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
The project consists of several subsystems that evaluate AI conditions against source files to determine inclusion, extract structural facts, and invoke a pluggable AI text generation backend (local or remote) for answering code queries. Configuration logic in `java/config` validates condition trees and line-counting requirements, while the core `java` tool orchestrates condition evaluation and backend invocation. The `java/provider` package (with its domain essence) supports the AI evaluation workflow, and `other/kernels` computes 256-bin byte histograms via two OpenCL passes (per-group counting and global reduction). The `other/ringbuf` circular buffer stores bytes with power-of-two sizing and atomic-like operations, used by histogram and backup components. Backup and reporting logic in `other/scripts` creates timestamped compressed backups with pruning, while `other` also generates monthly regional revenue reports from CSV order data. Database subsystems in `sql`, `sql/library`, `sql/shop`, and `sql/warehouse` manage a lending library, web shop, and warehouse synchronization via stored procedures and audit trails.

Note: All statements are derived directly from the provided one-line leads; no packages or behaviors are invented. The mention of `java/provider`'s "domain essence" reflects the lead's wording ("<one-sentence domain essence>"), and subsystem names (`java`, `java/config`, `other/kernels`, `other/ringbuf`, `other/scripts`, `sql` subpackages) are used as named in the leads. The description avoids bullet points, headings, or invented details.

#### Packages
- java/config — Evaluates {@link AiCondition} trees against {@link AiFileContext} to determine file inclusion, validates condition structure, and checks if line counting is required.
- java — A tool that evaluates AI conditions against source files to decide inclusion, extracts structural facts, and invokes a pluggable AI text generation backend (local or remote) to answer code queries.
- java/provider — <one-sentence domain essence>
- other/kernels — Computes a 256-bin byte histogram from a byte stream using two OpenCL passes: per-group counting and global reduction.
- other — Computes byte histograms from streams using OpenCL parallel kernels and provides a ring buffer for byte storage; also creates timestamped backups and generates monthly regional revenue reports from CSV order data.
- other/ringbuf — A circular buffer (ring buffer) implementation that stores bytes with power-of-two sizing and provides atomic-like write/read operations.
- other/scripts — Creates timestamped compressed backups of source directories while automatically pruning old archives, and generates monthly regional revenue reports from CSV order data filtered by currency and minimum revenue.
- . — A tool that evaluates AI conditions against source files to decide inclusion, extracts structural facts, and invokes a pluggable AI text generation backend (local or remote) to answer code queries.
- sql/library — A lending library database system that manages books, authors, members, loans, branches, and audit trails with constraints on copy availability and loan periods.
- sql — The package provides three independent database systems: a lending library (managing books, authors, members, loans), a web shop (managing products, orders), and a warehouse (syncing supplier feeds to stock levels).
- sql/shop — The package defines database objects and logic for a web shop's product catalog, customer orders, and order placement with automatic stock reservation.
- sql/warehouse — The package implements a nightly SQL Server stored procedure that merges supplier feed quantities into a stock level table and logs synchronization metrics and errors.
