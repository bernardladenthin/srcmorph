### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:56:01Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages e-commerce product inventory, customer order tracking, and the transactional processing of order line items.

#### Purpose
* Maintain a product catalog with pricing and stock levels.
* Track customer orders and their associated line items.

#### Responsibilities
* **Inventory & Catalog Management**: Tracking product SKUs, pricing, and stock availability.
* **Order Lifecycle**: Managing order headers, statuses, and total cost calculations.
* **Transactional Order Processing**: Atomically handling the addition of items to orders, stock reservation, and price aggregation.

#### Key units
* `product` table: Stores SKU, name, price, and current stock levels.
* `customer_order` table: Tracks order status, customer association, and total cost.
* `order_item` table: Links products to orders with specific quantities and unit prices.
* `add_order_item` procedure: Orchestrates the atomic process of validating stock, updating inventory, inserting order items, and recalculating order totals.

#### Data flow
1. `add_order_item` receives an order ID, SKU, and quantity.
2. The procedure locks the `product` row to verify existence and stock.
3. `order_item` is updated or inserted with the product and quantity.
4. `product.stock` is decremented.
5. `customer_order.total_cents` is updated based on the sum of all related `order_item` entries.

#### Dependencies
* Internal table dependencies: `order_item` relies on `customer_order` and `product` via foreign keys.
