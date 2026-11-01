### srcmorph-modelcard
- H: 1.0
- C: 166E5BD7
- D: 2026-11-01T08:28:04Z
- T: 2026-11-01T14:58:30Z
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
The srcmorph‑modelcard project is a unified Perl‑based toolkit that aggregates web‑server logs, automates deployment and data export, and supplies XML artifacts for an invoicing service. It provides reusable modules and command‑line tools for log analysis, including per‑host and per‑status statistics, and shell utilities for release deployment, queue worker management, and Oracle payment data export. Configuration, schema, and transformation files in the XML package support invoicing, reminder scheduling, shipment validation, and HTML statement rendering, while the other package bundles these capabilities into a cohesive toolkit. The overall architecture ties log aggregation, deployment automation, and invoicing workflows together through shared Perl modules and XML artifacts.

#### Packages
- other — A unified toolkit for log analysis, deployment automation, and invoicing workflows, providing reusable modules, scripts, and XML artifacts that process data, orchestrate services, and generate reports.
- other/perl — Aggregates web‑server access logs into per‑host and per‑status statistics, exposing a reusable module and a command‑line tool for reporting busiest hosts and slow requests.
- other/shell — A collection of shell utilities for deploying releases, managing a queue worker, and exporting daily payment data from Oracle.
- other/xml — A collection of configuration, schema, and transformation files that support an invoicing and reminder system, including a Spring scheduler, shipment XML validation, and HTML statement rendering.
- . — A unified Perl‑based toolkit that aggregates web‑server logs, automates deployment and data export, and supplies XML artifacts for an invoicing service.
