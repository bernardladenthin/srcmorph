### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:17:23Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop order placement with product catalog, customer orders, and stock reservation.
#### Purpose
- Models web shop products, customer orders, and order line items
- Places order lines via `add_order_item` with stock check and order total maintenance
#### Type
- table, procedure, MySQL 8, InnoDB `utf8mb4`, `DELIMITER //` routine
#### Schema
- `product`: `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK, `sku VARCHAR(40) NOT NULL` UNIQUE `uk_product_sku`, `name VARCHAR(200) NOT NULL`, `price_cents INT UNSIGNED NOT NULL`, `stock INT NOT NULL DEFAULT 0`
- `customer_order`: `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK, `customer_id BIGINT UNSIGNED NOT NULL`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`, `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`, `total_cents INT UNSIGNED NOT NULL DEFAULT 0`, index `KEY idx_order_customer (customer_id, created_at)`
- `order_item`: `order_id BIGINT UNSIGNED NOT NULL` PK part FK, `product_id INT UNSIGNED NOT NULL` PK part FK, `quantity SMALLINT UNSIGNED NOT NULL`, `unit_price_cents INT UNSIGNED NOT NULL`, composite `PRIMARY KEY (order_id, product_id)`
#### Reads
- `product.product_id, price_cents, sku, stock` via `SELECT ... WHERE sku = p_sku AND stock >= p_qty FOR UPDATE`
- `order_item.quantity, unit_price_cents, order_id` via `SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id`
#### Writes
- `order_item.order_id, product_id, quantity, unit_price_cents` via `INSERT ... ON DUPLICATE KEY UPDATE quantity = quantity + p_qty`
- `product.stock` via `UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id`
- `customer_order.total_cents` via `UPDATE customer_order SET total_cents = ... WHERE order_id = p_order_id`
#### Relationships
- `order_item.order_id -> customer_order.order_id` via `fk_item_order ON DELETE CASCADE`
- `order_item.product_id -> product.product_id` via `fk_item_product`
#### Routine logic
- `PROCEDURE add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`: adds one order line, reserves stock, recalculates total
- Declares `v_product_id INT UNSIGNED`, `v_price INT UNSIGNED`
- Locks matching `product` row with `FOR UPDATE`, signals `SQLSTATE '45000'` on `unknown SKU or not enough stock` if `v_product_id IS NULL`
- Upserts `order_item`, decrements `product.stock`, sets `customer_order.total_cents` to summed line totals
#### Dependencies
- `product`, `customer_order`, `order_item`, `uk_product_sku`, `idx_order_customer`, `fk_item_order`, `fk_item_product`
