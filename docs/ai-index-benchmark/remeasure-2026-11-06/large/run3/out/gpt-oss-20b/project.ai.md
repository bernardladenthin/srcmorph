### srcmorph-modelcard
- H: 1.0
- C: 9C6B6222
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:21:13Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: project
- P: 8CA89A71
- M: 2FA139DC
- K: 4257AF6A
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
The project provides a unified data‑management layer that centralises error codes, product inventory, and payment‑ledger test data for an e‑commerce platform. Error handling is standardized through a catalog of numeric codes and human‑readable messages shared across all domains. Product inventory is represented as a multi‑warehouse e‑commerce catalog in XML, detailing categories, products, pricing, and per‑warehouse stock levels. Payments are seeded into a PostgreSQL ledger via data‑load scripts, exposing a daily transfer volume view for financial analysis.

#### Packages
- java/errors — A centralized catalog of numeric error codes and messages for consistent platform‑wide error handling.
- java — A centralized, platform‑wide error catalog that maps numeric codes to human‑readable messages for consistent error handling across all domains.
- other/catalog — A multi‑warehouse e‑commerce product inventory catalog expressed in XML, detailing categories, products, pricing, and per‑warehouse stock levels.
- other — A multi‑warehouse e‑commerce product inventory catalog expressed in XML, detailing categories, products, pricing, and per‑warehouse stock levels.
- . — A unified data‑management layer that centralises error codes, product inventory, and payment‑ledger test data for an e‑commerce platform.
- sql/ledger — A PostgreSQL data‑load script that seeds a payments ledger with customers, accounts, transfers, and holds, and exposes a daily volume view.
- sql — A PostgreSQL data‑load package that seeds a payments ledger with customers, accounts, transfers, and holds, and exposes a daily transfer volume view.
