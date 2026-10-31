### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:34:35Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages a shop’s product inventory and customer orders, including adding order items while reserving stock and recalculating order totals.

#### Purpose
- Defines the product catalog, customer order headers, and order lines.
- Adds items to orders while decrementing available product stock and updating totals.

#### Responsibilities
- **Catalog and inventory:** Stores product SKUs, prices, and stock levels.
- **Order records:** Stores customer orders, statuses, timestamps, and totals.
- **Order-item processing:** Adds or increments order lines, reserves stock, and recomputes an order’s total.

#### Key units
- `product` — product identifiers, unique SKUs, prices, and stock.
- `customer_order` — customer order status and calculated total.
- `order_item` — per-product quantities and unit prices within an order.
- `add_order_item` — looks up and locks a stock-eligible product, adds or increments its order line, decrements stock, and recalculates the order total.

#### Data flow
- `add_order_item` receives an order ID, SKU, and quantity; finds a matching product with sufficient stock and retrieves its ID and price.
- It inserts or increments the matching `order_item`, decrements `product.stock`, then sets `customer_order.total_cents` from the order’s line quantities and unit prices.
- If no eligible product is found, it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`.
- `order_item.product_id` references `product.product_id`.

#### Cross-cutting
- Uses MySQL 8 SQL objects and foreign-key constraints.
- Product lookup uses a row lock; invalid SKU or insufficient stock is reported with a SQL signal.
