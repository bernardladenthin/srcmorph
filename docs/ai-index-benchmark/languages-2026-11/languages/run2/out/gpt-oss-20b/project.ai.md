### srcmorph-modelcard
- H: 1.0
- C: 0E3166C5
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:35:49Z
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
The core of srcmorph‑modelcard is a log‑aggregation and deployment toolkit that collects web‑server logs, runs background workers, and orchestrates the deployment of releases. It exposes a CLI for reporting host statistics and slow requests, while shell utilities automate queue management and nightly payment exports. Configuration, schema, and transformation files in the XML package provide the build setup, reminder scheduling, shipment validation, and statement rendering needed for the invoice‑and‑shipment service. Together these subsystems enable a fully automated, configurable workflow from log collection to invoicing and shipment.

#### Packages
- other — Aggregates web‑server logs, manages deployment and background workers, and supports an invoice‑and‑shipment service through configuration, validation, and presentation artifacts.
- other/perl — Aggregates web‑server access logs into per‑host and per‑status statistics, then exposes a CLI for reporting the busiest hosts and slow request counts.
- other/shell — A collection of shell utilities for deploying releases, managing a queue worker, and nightly payment exports in a production environment.
- other/xml — A collection of configuration, schema, and transformation files that support an invoice‑and‑shipment service, including build setup, reminder scheduling, shipment validation, and statement rendering.
- . — A toolkit that aggregates web‑server logs, manages deployment and background workers, and builds and schedules an invoice‑and‑shipment service.
