### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:38Z
- G: 
- A: 0.0.0
- X: file
---
> Models shop products, customer orders, and order lines; adding an order line reserves product stock and recalculates the order total.

#### Purpose
- Stores product catalog and stock, customer orders, and per-product order lines.
- `add_order_item` adds or increases an order line, reserves stock, and updates the order total.

#### Type
- MySQL 8; tables and stored procedure.

#### Schema
- `product`: `product_id INT UNSIGNED` (PK, NOT NULL, AUTO_INCREMENT); `sku VARCHAR(40)` (NOT NULL, UNIQUE via `uk_product_sku`); `name VARCHAR(200)` (NOT NULL); `price_cents INT UNSIGNED` (NOT NULL); `stock INT` (NOT NULL, DEFAULT `0`).
- `customer_order`: `order_id BIGINT UNSIGNED` (PK, NOT NULL, AUTO_INCREMENT); `customer_id BIGINT UNSIGNED` (NOT NULL); `status ENUM('NEW','PAID','SHIPPED','CANCELLED')` (NOT NULL, DEFAULT `'NEW'`); `created_at DATETIME` (NOT NULL, DEFAULT `CURRENT_TIMESTAMP`); `total_cents INT UNSIGNED` (NOT NULL, DEFAULT `0`); index `idx_order_customer (customer_id, created_at)`.
- `order_item`: `order_id BIGINT UNSIGNED` (PK component, NOT NULL, FK); `product_id INT UNSIGNED` (PK component, NOT NULL, FK); `quantity SMALLINT UNSIGNED` (NOT NULL); `unit_price_cents INT UNSIGNED` (NOT NULL); composite PK `(order_id, product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` (SKU and stock filter; product row locked with `FOR UPDATE`).
- `order_item`: `quantity`, `unit_price_cents` (sum for the specified `order_id`).

#### Writes
- `order_item`: inserts `(order_id, product_id, quantity, unit_price_cents)`; on duplicate key, increments `quantity`.
- `product`: decrements `stock` for the selected `product_id`.
- `customer_order`: sets `total_cents` to the sum of line quantity × unit price for `p_order_id`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, `ON DELETE CASCADE`).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`.
- Locks and selects the product matching `p_sku` with `stock >= p_qty`; raises SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is found.
- Inserts the order line or increments its quantity on duplicate key, decrements product stock, then recalculates the order total.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
