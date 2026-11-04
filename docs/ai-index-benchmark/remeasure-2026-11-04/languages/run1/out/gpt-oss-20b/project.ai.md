### srcmorph-modelcard
- H: 1.0
- C: A66CB7DA
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:34Z
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
The srcmorph‑modelcard project is a unified toolkit that combines web‑server log analysis, automated deployment, and invoicing workflows. It aggregates logs and builds releases via shell utilities, while a Perl module parses access logs to produce host‑ and status‑based statistics and JSON reports. XML support supplies build, configuration, and data‑formatting for invoicing and reminder systems, including Maven, Spring scheduling, shipment schemas, and XSLT rendering. Together, these components provide a cohesive pipeline from log collection to deployment and billing.

#### Packages
- other — A toolkit that aggregates web‑server logs, deploys application releases, and supports invoicing workflows through build, scheduling, and XML processing.
- other/perl — A Perl toolkit that parses web‑server access logs, aggregates per‑host and per‑status statistics, and reports the busiest hosts and slow requests, optionally as JSON.
- other/shell — A collection of shell utilities that deploy releases, manage a queue worker, and export nightly payment data.
- other/xml — Provides build, configuration, and data‑formatting support for an invoicing and reminder system, including Maven setup, Spring scheduling, XML schema for shipments, and XSLT for statement rendering.
- . — A unified toolkit for web‑server log analysis, automated deployment, and invoicing workflows, integrating Perl, shell, and XML components.
