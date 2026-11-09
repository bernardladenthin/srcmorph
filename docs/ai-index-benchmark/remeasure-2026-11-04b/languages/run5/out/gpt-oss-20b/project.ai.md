### srcmorph-modelcard
- H: 1.0
- C: 709BDDCF
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:23Z
- G: 
- A: 0.0.0
- X: project
- F: [other](other/package.ai.md)
- F: [other/perl](other/perl/package.ai.md)
- F: [other/shell](other/shell/package.ai.md)
- F: [other/xml](other/xml/package.ai.md)
- F: [.](package.ai.md)
---
> Project index of srcmorph-modelcard: one line per package; leads here, clickable links in the header F list.

#### Overview
The srcmorph‑modelcard project centers on a billing and logistics workflow that is driven by web‑server log aggregation and automated deployment. Core utilities in the root package collect logs, launch workers, and orchestrate nightly payment extraction, while the `other/perl` package provides a CLI for summarizing access logs by host and status, flagging slow requests. Shell scripts in `other/shell` handle release deployment and queue worker management, and the `other/xml` package supplies build configuration, database migrations, PDF generation, reminder scheduling, shipment XML schemas, and statement‑to‑HTML transformations to support the billing system. Together, these subsystems form a cohesive pipeline from log ingestion to billing and shipment reporting.

#### Packages
- other — A collection of utilities and configuration artifacts that aggregate web‑server logs, automate deployment and worker management, and support billing, logistics, and reporting workflows.
- other/perl — Condenses web‑server access logs into per‑host and per‑status summaries, highlighting slow requests and providing a CLI for reporting.
- other/shell — A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment extraction.
- other/xml — Provides build configuration, database migration, PDF generation, reminder scheduling, shipment XML schema, and statement-to‑HTML transformation for a billing and logistics system.
- . — A collection of utilities that aggregate web‑server logs, automate deployment and worker management, and support billing, logistics, and reporting workflows.
