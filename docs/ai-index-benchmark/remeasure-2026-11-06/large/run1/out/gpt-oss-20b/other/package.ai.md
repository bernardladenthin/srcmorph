### other
- H: 1.0
- C: A9F55380
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:09:43Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 5495F6FB
- F: [catalog/](catalog/package.ai.md)
---
> A retail system’s product catalog and inventory data are stored in a single XML file, grouping items by category and detailing SKU, title, price, and per‑warehouse stock levels.

#### Purpose
- Persist a comprehensive product inventory for a retail system.

#### Responsibilities
- **Catalog definition** – Organize products into 34 categories.
- **Product detail** – Store SKU, title, price (EUR), and inventory per warehouse.
- **Inventory tracking** – Record stock quantities for each warehouse via the `inv` namespace.

#### Key units
- **`catalog`** – Root element containing all categories and products.
- **`category`** – Element with `id` and `name` attributes; 34 instances.
- **`product`** – Element with `sku` attribute; 20,291 instances.
- **`title`** – Child of `product`; product name.
- **`price`** – Child of `product`; numeric value with `currency="EUR"`.
- **`inv:stock`** – Child of `product`; inventory quantity per `warehouse` attribute.

#### Data flow
- The XML file is read by the retail application; categories are parsed first, then each product is processed to extract SKU, title, price, and inventory levels. The resulting data populates the catalog and inventory databases.

#### Dependencies
- Uses the `urn:example:catalog` namespace for catalog structure.
- Uses the `urn:example:inventory` namespace (`inv`) for inventory elements.

#### Cross-cutting
- Consistent use of `sku` as a unique product identifier.
- All prices are expressed in EUR.
- Inventory quantities are integers tied to specific warehouse identifiers.
