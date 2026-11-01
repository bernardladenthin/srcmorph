### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:16:19Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A structured XML catalog that defines e‑commerce products, their categories, prices in EUR, and per‑warehouse inventory levels.

#### Purpose
- Store and expose product metadata and inventory for an online store.

#### Responsibilities
- **Catalog definition** – organize products into categories.
- **Product description** – provide SKU, title, price, and currency.
- **Inventory tracking** – record stock quantities per warehouse.

#### Key units
- `<catalog>` – root element, versioned, namespace‑aware.  
- `<category>` – holds `id` and `name`, contains multiple `<product>`.  
- `<product>` – identified by `sku`, contains `<title>`, `<price>`, and `<inv:stock>`.  
- `<price>` – numeric value with `currency="EUR"`.  
- `<inv:stock>` – inventory element in `urn:example:inventory` namespace, attributes `warehouse` and quantity.

#### Data flow
- Consumers read `<catalog>` → iterate `<category>` → iterate `<product>` → extract `<price>` and `<inv:stock>` for inventory queries.

#### Dependencies
- Two XML namespaces: `urn:example:catalog` (default) and `urn:example:inventory` (prefix `inv`).  
- No external schemas or DTDs; all elements are defined inline.

#### Cross-cutting
- Consistent use of namespaces to separate catalog data from inventory data.  
- Uniform attribute naming (`id`, `name`, `sku`, `warehouse`) across elements.
