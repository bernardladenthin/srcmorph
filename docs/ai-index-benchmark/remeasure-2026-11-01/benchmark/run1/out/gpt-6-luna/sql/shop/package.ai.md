### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:23:57Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages a shop’s product inventory and customer order totals as order items are added or increased.

#### Purpose
- Stores products, stock levels, customer orders, and order-line quantities with unit-price snapshots.
- Provides `add_order_item` to add an item to an order, reserve stock, and recalculate the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and available stock.
- **Orders and lines:** `customer_order` tracks customer, status, creation time, and total; `order_item` records product quantities and unit prices.
- **Order-item processing:** `add_order_item` looks up and locks a sufficiently stocked product, writes or increments its order line, reduces stock, and updates the order total.

#### Key units
- `product` — product catalog and stock inventory, with unique SKU.
- `customer_order` — customer order metadata and status (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`).
- `order_item` — order-to-product lines, keyed by `(order_id, product_id)`.
- `add_order_item` — adds or increments an order line and updates stock and total; signals SQLSTATE `45000` for an unknown SKU or insufficient stock.

#### Data flow
- `add_order_item` receives an order ID, SKU, and quantity; finds and locks a matching product with enough stock, retrieving its ID and price.
- It inserts the line or increments its quantity, decrements product stock, then sets `customer_order.total_cents` to the sum of line quantities multiplied by unit prices.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascade delete; `order_item.product_id` references `product.product_id`.
- The procedure reads and writes `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- Uses MySQL 8 tables and a stored procedure; prices and totals are stored in cents.
- No explicit transaction handling is defined.
