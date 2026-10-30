### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:44:18Z
- G: 
- A: 0.0.0
- X: file
---
The user wants me to index the provided SQL file.

I'm analyzing the SQL file to build a dense structured markdown summary. The schema contains three tables: product, customer_order, and order_item. It also includes an add_order_item procedure for MySQL 8. I'll organize this into sections using the exact format requested.> Web shop catalog and checkout: products with stock, customer orders, line items, and placing an order line that reserves stock and updates the order total.

#### Purpose
- Models web-shop products, customer orders, and order line items (MySQL 8).
- Places an order line by SKU: checks stock, inserts or increments the line, decrements stock, and recalculates the order total.

#### Type
- Tables: `product`, `customer_order`, `order_item`. Procedure: `add_order_item`. Dialect: MySQL 8 (InnoDB, `utf8mb4`, `ENUM`, `SIGNAL`, `DELIMITER`).

#### Schema
- `product`: `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK, `sku VARCHAR(40) NOT NULL` UNIQUE (`uk_product_sku`), `name VARCHAR(200) NOT NULL`, `price_cents INT UNSIGNED NOT NULL`, `stock INT NOT NULL DEFAULT 0`.
- `customer_order`: `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK, `customer_id BIGINT UNSIGNED NOT NULL`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`, `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`, `total_cents INT UNSIGNED NOT NULL DEFAULT 0`. Index `idx_order_customer (customer_id, created_at)`.
- `order_item`: `order_id BIGINT UNSIGNED NOT NULL`, `product_id INT UNSIGNED NOT NULL`, `quantity SMALLINT UNSIGNED NOT NULL`, `unit_price_cents INT UNSIGNED NOT NULL`. PK `(order_id, product_id)`. FK `fk_item_order` `order_id` → `customer_order.order_id` ON DELETE CASCADE. FK `fk_item_product` `product_id` → `product.product_id`.

#### Reads
- `product.product_id`, `product.price_cents`, `product.sku`, `product.stock` (`SELECT ... FOR UPDATE` where `sku = p_sku` and `stock >= p_qty`).
- `order_item.quantity`, `order_item.unit_price_cents`, `order_item.order_id` (`SUM(quantity * unit_price_cents)` for the order total).

#### Writes
- `order_item`: `INSERT` of `order_id`, `product_id`, `quantity`, `unit_price_cents`; `ON DUPLICATE KEY UPDATE quantity = quantity + p_qty`.
- `product.stock`: `UPDATE stock = stock - p_qty` where `product_id = v_product_id`.
- `customer_order.total_cents`: `UPDATE` to the sum of line amounts where `order_id = p_order_id`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE).
- `order_item.product_id -> product.product_id` (`fk_item_product`).
- `customer_order.customer_id` indexed with `created_at`; no FK to a customer table in this file.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`.
- Locks and reads `product` by `sku` only if `stock >= p_qty`; stores `product_id` and `price_cents`.
- If no row, `SIGNAL SQLSTATE '45000'` message `unknown SKU or not enough stock`.
- Inserts `order_item` or adds `p_qty` to existing `quantity` on PK `(order_id, product_id)`.
- Decrements `product.stock` by `p_qty`.
- Sets `customer_order.total_cents` to `SUM(quantity * unit_price_cents)` from `order_item` for that `order_id`.

#### Dependencies
- `add_order_item` depends on `product`, `order_item`, `customer_order`.
- `order_item` depends on `customer_order` and `product` via foreign keys.
