### srcmorph-modelcard
- H: 1.0
- C: 6EC85AA4
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:26:23Z
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
The project contains three main subsystems: a numbered catalog of platform error codes and messages, a categorized product catalog with EUR prices and warehouse inventory, and a customer-linked banking ledger. The ledger records positive-value transfers and aggregates booked volume by day.

#### Packages
- java/errors — Provides a stable numeric catalog of platform error codes and messages for billing, storage, network, reporting, import, and authentication failures.
- java — Provides a numbered catalog of platform failures so billing, storage, network, reporting, import, and authentication errors can be identified by code.
- other/catalog — Represents products grouped by category, with EUR pricing and warehouse-specific inventory quantities.
- other — Represents a categorized product catalog with EUR pricing and warehouse-specific inventory quantities.
- . — Provides reference data for platform error identification, categorized product and inventory lookup, and customer-linked banking transfers with daily volume reporting.
- sql/ledger — Defines a customer-linked banking ledger for accounts and positive-value transfers, with daily aggregation of booked transfer volume.
- sql — Stores customer-linked bank accounts and positive-value transfers, with daily totals of booked transfer volume.
