### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:01:19Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains a web shop’s product inventory and customer order totals as order items are added.

#### Purpose
- Defines the product catalog, customer orders, and order lines.
- Adds or increments order items while checking and reserving available stock and recalculating order totals.

#### Responsibilities
- Catalog and order storage: `product`, `customer_order`, and `order_item` tables, with SKU uniqueness, customer-order lookup indexing, and order-line foreign keys.
- Order-item operations: `add_order_item` validates SKU and stock, updates the line quantity and product stock, then refreshes the order total.

#### Key units
- `product` — stores SKU, name, price in cents, and stock.
- `customer_order` — stores customer, status, creation time, and total in cents.
- `order_item` — associates products with orders, recording quantity and unit price.
- `add_order_item` — locks an eligible product by SKU, inserts or increments its order line, decrements stock, and recalculates the order total.

#### Data flow
- `add_order_item` looks up and locks a product matching `p_sku` with sufficient stock; otherwise signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- It inserts or increments the matching `order_item`, subtracts `p_qty` from `product.stock`, and recomputes `customer_order.total_cents` from line quantities and unit prices.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`; `order_item.product_id` references `product.product_id`.
- The procedure coordinates reads and writes across `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- MySQL 8 schema and procedure; product-row locking uses `FOR UPDATE`.
- Prices and order totals are stored in cents; order-line primary key supports duplicate-key quantity increments.
