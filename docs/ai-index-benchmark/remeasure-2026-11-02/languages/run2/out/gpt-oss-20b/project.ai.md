### srcmorph-modelcard
- H: 1.0
- C: 78269EC9
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T23:42:37Z
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
The srcmorph‑modelcard project centers on a unified toolkit that ingests web‑server logs, automates deployment, and processes XML‑based invoices and shipment notices. The core logic lives in the root package, while the `other/perl` subpackage aggregates and summarizes access logs into per‑host, per‑status, and slow‑request statistics, optionally emitting JSON. Shell scripts in `other/shell` handle release deployment, queue‑worker health checks, and nightly payment extraction from Oracle. The `other/xml` package supplies configuration, schema, and XSLT artifacts that support an invoice‑generation service, a reminder scheduler, and XML‑based shipment and statement processing. Together, these subsystems provide a cohesive pipeline from log ingestion to automated deployment and XML‑based business processing.

#### Packages
- other — A unified toolkit for log analysis, deployment automation, and XML‑based invoice and shipment processing.
- other/perl — Aggregates and summarizes web‑server access logs, producing per‑host, per‑status, and slow‑request statistics, optionally in JSON.
- other/shell — A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment extraction from Oracle.
- other/xml — A set of configuration, schema, and transformation artifacts that support an invoice‑generation service, a reminder scheduler, and XML‑based shipment and statement processing.
- . — A unified toolkit that digests web‑server logs, automates deployment and worker health, and processes XML‑based invoices and shipment notices.
