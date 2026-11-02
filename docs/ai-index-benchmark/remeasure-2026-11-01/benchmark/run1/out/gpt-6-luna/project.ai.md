### srcmorph-modelcard
- H: 1.0
- C: 6E4D73FE
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:24:21Z
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
The Java packages configure source selection and AI summarization, then generate indexed-document text through local or OpenAI-compatible backends; `java/provider` also supports completion cleanup and optional inference metrics. The `other` packages provide parallel byte-frequency processing, bounded FIFO buffering, backup rotation, and monthly revenue reporting. The SQL packages cover lending-library catalog and loan workflows, shop order fulfillment and totals, and warehouse stock synchronization.

#### Packages
- java/config — Configures source-file selection and AI summarization policies, evaluates file conditions, and formats deterministic source facts for generated `.ai.md` indexes.
- java — Configures source-file selection and AI generation, then routes requests to local or OpenAI-compatible backends to produce indexed-document text.
- java/provider — Generates text for document-processing workflows through configurable local or OpenAI-compatible AI backends, with completion cleanup and optional inference metrics.
- other/kernels — Computes byte-value frequency distributions for image or byte-stream data using parallel OpenCL histogram kernels.
- other — Supports byte-stream processing and operational data tasks: parallel byte-frequency counting, bounded FIFO buffering, backup rotation, and monthly order-revenue reporting.
- other/ringbuf — Provides bounded FIFO byte-stream buffering between a single producer and a single consumer.
- other/scripts — Provides operational utilities for rotating compressed directory backups and producing currency-filtered monthly revenue reports from order exports.
- . — Covers AI-assisted document generation alongside byte-processing utilities, operational reporting, and database workflows for lending, order fulfillment, and stock synchronization.
- sql/library — Manages a lending library’s catalog, branch-based copy inventory, memberships, borrowing and returns, reporting, and loan-event auditing.
- sql — Implements database workflows for library lending, shop order fulfillment, and warehouse stock synchronization.
- sql/shop — Manages a shop’s product inventory and customer order totals as order items are added or increased.
- sql/warehouse — Synchronizes warehouse stock balances from newly received supplier quantities and records each synchronization’s outcome.
