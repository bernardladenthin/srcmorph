### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:00:26Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages e-commerce product inventory, customer order tracking, and the transactional processing of order line items and stock updates.

#### Purpose
* Maintain a product catalog with pricing and stock levels.
* Track customer order headers and associated line items.
* Ensure atomic stock reservation and order total calculations.

#### Responsibilities
* **Inventory & Catalog Management**: Tracking product SKUs, pricing, and available stock.
* **Order Management**: Managing order statuses and customer associations.
* **Transactional Processing**: Handling the addition of items to orders while maintaining data consistency across stock and totals.

#### Key units
* `product` table: Stores product metadata, SKUs, and current stock levels.
* `customer_order` table: Tracks order headers, customer IDs, status, and total cost.
* `order_item` table: Links products to orders with specific quantities and unit prices.
* `add_order_item` procedure: Orchestrates stock validation, item insertion/updates, and order total recalculation.

#### Data flow
1. `add_order_item` receives `p_order_id`, `p_sku`, and `p_qty`.
2. Validates `sku` and checks `product.stock` using a `FOR UPDATE` lock.
3. Updates `order_item` (insert or increment quantity) and decrements `product.stock`.
4. Aggregates all `order_item` costs to update `customer_order.total_cents`.

#### Dependencies
* Internal dependencies between `order_item`, `customer_order`, and `product` tables via foreign keys.

#### Cross-cutting
* **Concurrency Control**: Uses `FOR UPDATE` row-level locking in the `add_order_item` procedure to prevent over-selling.
* **Error Handling**: Uses SQL state '45000' for business logic failures (missing products or insufficient stock).
* **Data Integrity**: Implements `ON DELETE CASCADE` from `customer_order` to `order_item`.
