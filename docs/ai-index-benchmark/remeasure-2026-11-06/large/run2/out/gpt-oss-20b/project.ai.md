### srcmorph-modelcard
- H: 1.0
- C: 9C6B6222
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:15:23Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: project
- P: 8CA89A71
- M: 2FA139DC
- K: E91C0E6D
- F: [java/errors](java/errors/package.ai.md)
- F: [java](java/package.ai.md)
- F: [other/catalog](other/catalog/package.ai.md)
- F: [other](other/package.ai.md)
- F: [.](package.ai.md)
- F: [sql/ledger](sql/ledger/package.ai.md)
- F: [sql](sql/package.ai.md)
---
> Project index of srcmorph-modelcard: one line per package; leads here, clickable links in the header F list.

#### Overview
The srcmorph‑modelcard platform centralises error handling, product cataloging, and payment ledger data for downstream services. A type‑safe catalogue of platform error codes is exposed via the java and java/errors packages, allowing numeric ID lookup across all domains. Structured XML product catalogs are provided by the other and other/catalog packages, delivering SKU, pricing, inventory, and recall information to e‑commerce and warehouse systems. The sql and sql/ledger packages supply a PostgreSQL data‑dump that builds a payments ledger schema, seeds it with historical data, and exposes a daily volume view for analytics.

#### Packages
- java/errors — A type‑safe catalogue of platform error codes that can be looked up by numeric ID and used across all domains.
- java — A centralized, type‑safe catalogue of platform error codes that can be looked up by numeric ID and used across all domains.
- other/catalog — A structured XML product catalog providing SKU, title, EUR price, per‑warehouse inventory, and recall batch data for e‑commerce or warehouse systems.
- other — A structured XML product catalog that supplies SKU, pricing, inventory, and recall data to downstream e‑commerce and warehouse systems.
- . — A unified platform that centralises error handling, product cataloging, and payment ledger data for downstream services.
- sql/ledger — A PostgreSQL data‑dump that builds a payments ledger schema, seeds it with historical data, and exposes a daily volume view for analytics.
- sql — A PostgreSQL data‑dump that builds a payments ledger schema, seeds it with historical data, and exposes a daily volume view for analytics.
