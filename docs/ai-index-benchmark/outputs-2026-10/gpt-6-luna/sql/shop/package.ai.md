### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:23:20Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Shop order placement records product lines, reserves available inventory, and keeps order totals synchronized.

#### Purpose
- Defines the MySQL schema for products, customer orders, and order items.
- Provides `add_order_item` to add or increment an order line, deduct stock, and recalculate the order total.

#### Responsibilities
- **Product inventory:** Stores SKU, name, price, and available stock.
- **Order tracking:** Stores customer, status, creation time, and total for each order.
- **Order lines:** Associates products and quantities with orders, preserving each line’s unit price.
- **Order placement:** Checks SKU and stock, updates the line and inventory, then refreshes the order total.

#### Key units
- `product` — product catalog and inventory.
- `customer_order` — customer order status and aggregate total.
- `order_item` — per-product order quantities and unit prices; cascades on order deletion.
- `add_order_item` — locks and checks a matching product, inserts or increments its order line, deducts stock, and recomputes the order total.

#### Data flow
- `add_order_item` receives an order ID, SKU, and quantity; looks up and locks a matching in-stock `product`.
- If no eligible product is found, signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- Inserts an `order_item` or increments its existing quantity, deducts the requested stock, and sets `customer_order.total_cents` from the summed line quantities and unit prices.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`.
- `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- MySQL 8 schema uses InnoDB tables and a stored procedure.
- Product rows are locked during stock checks; SQLSTATE `45000` reports unknown SKUs or insufficient stock.
