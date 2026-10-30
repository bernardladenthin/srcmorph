### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:27Z
- G: 
- A: 0.0.0
- X: file
---
> Models products, customer orders, and order lines; adding an order line reserves product stock and updates the order total.

#### Purpose
- Stores product inventory, customer order status and totals, and order-line quantities and prices.
- `add_order_item` adds a product to an order while reserving stock.

#### Type
- Tables and procedure; MySQL 8.

#### Schema
- `product`: `product_id INT UNSIGNED` (PK, NOT NULL, AUTO_INCREMENT); `sku VARCHAR(40)` (NOT NULL, UNIQUE: `uk_product_sku`); `name VARCHAR(200)` (NOT NULL); `price_cents INT UNSIGNED` (NOT NULL); `stock INT` (NOT NULL, DEFAULT 0).
- `customer_order`: `order_id BIGINT UNSIGNED` (PK, NOT NULL, AUTO_INCREMENT); `customer_id BIGINT UNSIGNED` (NOT NULL); `status ENUM('NEW','PAID','SHIPPED','CANCELLED')` (NOT NULL, DEFAULT `'NEW'`); `created_at DATETIME` (NOT NULL, DEFAULT `CURRENT_TIMESTAMP`); `total_cents INT UNSIGNED` (NOT NULL, DEFAULT 0).
- `customer_order` index: `idx_order_customer (customer_id, created_at)`.
- `order_item`: `order_id BIGINT UNSIGNED` (NOT NULL, FK); `product_id INT UNSIGNED` (NOT NULL, FK); `quantity SMALLINT UNSIGNED` (NOT NULL); `unit_price_cents INT UNSIGNED` (NOT NULL); composite PK `(order_id, product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` (`SELECT ... FOR UPDATE`).
- `order_item`: `quantity`, `unit_price_cents` (order-total aggregate).

#### Writes
- `order_item`: inserts `(order_id, product_id, quantity, unit_price_cents)`; on duplicate key, increments `quantity`.
- `product`: decrements `stock`.
- `customer_order`: sets `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, `ON DELETE CASCADE`).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED)`.
- Locks the matching in-stock product by SKU; signals SQLSTATE `45000` if no eligible product is found.
- Inserts or increments the order line, decrements stock, and recalculates the order total as the sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
