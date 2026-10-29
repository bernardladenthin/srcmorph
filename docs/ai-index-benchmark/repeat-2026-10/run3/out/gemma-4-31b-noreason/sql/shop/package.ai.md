### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:33:05Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages e-commerce product inventory, customer order tracking, and atomic stock reservation.

#### Purpose
* Maintain a product catalog with pricing and stock levels.
* Handle customer order headers and associated line items.

#### Responsibilities
* **Inventory & Catalog Management**: Tracking SKUs, pricing, and stock availability.
* **Order Processing**: Managing order statuses and calculating total costs.
* **Transactional Logic**: Ensuring atomic updates to stock and order totals during item addition.

#### Key units
* `product` table: Stores SKU, pricing, and current stock levels.
* `customer_order` table: Tracks order status and total cost per customer.
* `order_item` table: Links products to orders with specific quantities and unit prices.
* `add_order_item` procedure: Validates stock, updates inventory, inserts/updates order items, and refreshes order totals.

#### Data flow
1. `add_order_item` receives `p_order_id`, `p_sku`, and `p_qty`.
2. Row-level lock is placed on `product` to verify SKU and stock.
3. `order_item` is updated/inserted and `product.stock` is decremented.
4. `customer_order.total_cents` is recalculated via a sum of all related `order_item` entries.

#### Dependencies
* Internal table dependencies: `order_item` relies on `customer_order` (via CASCADE delete) and `product`.
