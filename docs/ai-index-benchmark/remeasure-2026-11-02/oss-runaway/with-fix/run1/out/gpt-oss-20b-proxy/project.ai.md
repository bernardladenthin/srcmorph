### srcmorph-modelcard
- H: 1.0
- C: 382FED01
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:35Z
- G: 
- A: 0.0.0
- X: project
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
The project unifies platform error handling, e‑commerce cataloging, and payment ledger management. It defines a central error code catalog with immutable constants and reverse‑lookup support, while an XML catalog stores product definitions, inventory, pricing, and recall data for the e‑commerce platform. A PostgreSQL payments ledger schema, fully populated with customers, accounts, transfers, holds, and a daily volume view, ties inventory and error data into consistent transaction reporting. Together, these subsystems provide a single source of truth for errors, product information, and financial reporting.

#### Packages
- java/errors — A centralized catalog of platform error codes, each mapped to a unique integer and a human‑readable message, with reverse lookup by code.
- java — A centralized catalog of platform error codes with immutable constants and reverse‑lookup support.
- other/catalog — A single XML catalog that stores product inventory, pricing, and recall information for an e‑commerce platform.
- other — A single XML catalog that centralises product definitions, inventory levels, and recall data for an e‑commerce platform.
- . — A unified catalog of platform error codes, e‑commerce product data, and a PostgreSQL payments ledger schema, all designed for consistent lookup, inventory management, and transaction reporting.
- sql/ledger — Provides a fully‑populated PostgreSQL payments ledger schema for development and reporting, including customers, accounts, transfers, holds, and a daily volume view.
- sql — A PostgreSQL payments ledger schema with seeded data for development and reporting.
