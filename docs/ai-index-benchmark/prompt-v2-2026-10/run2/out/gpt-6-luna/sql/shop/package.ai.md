### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:47:58Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Stores shop products and customer orders, while adding order items with stock reservation and order-total recalculation.

#### Purpose
- Define product inventory, customer order, and order-line storage.
- Provide `add_order_item` to add or increase an order line, reserve stock, and recalculate the order total.

#### Responsibilities
- **Product catalog and inventory:** Store SKU, name, price, and available stock in `product`.
- **Order tracking:** Store customer, status, creation time, and total in `customer_order`.
- **Order lines and updates:** Store per-product quantities and unit prices in `order_item`; procedure calls increment existing line quantities.

#### Key units
- `product` — product identity, SKU, price, and stock.
- `customer_order` — customer order status, timestamp, and total.
- `order_item` — order/product association, quantity, and captured unit price.
- `add_order_item` — validates SKU and stock, updates the line and inventory, then recalculates the order total.

#### Data flow
- `add_order_item(p_order_id, p_sku, p_qty)` looks up and locks a matching product with sufficient stock.
- On success, inserts an order line or increments its quantity, subtracts `p_qty` from product stock, and sets the order total to the sum of line quantity × unit price.
- If no matching product has sufficient stock, signals SQLSTATE `45000` with `unknown SKU or not enough stock`.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascade delete.
- `order_item.product_id` references `product.product_id`.
- Implemented with MySQL 8 tables and a stored procedure.

#### Cross-cutting
- Product lookup uses `FOR UPDATE` when checking stock.
- `order_item` uses composite primary key (`order_id`, `product_id`) to support one line per product per order.
