### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:23:43Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop order placement with product catalog, customer orders, and stock reservation.
#### Purpose
- Models web shop `product` inventory, `customer_order` headers, `order_item` lines
- Places order lines via `add_order_item` with stock check, total recalculation
#### Type
- table, procedure, MySQL 8, InnoDB `utf8mb4`, `AUTO_INCREMENT`, `ENUM`, `DELIMITER //`, `SIGNAL SQLSTATE`
#### Schema
- `product`: `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK, `sku VARCHAR(40) NOT NULL` UNIQUE, `name VARCHAR(200) NOT NULL`, `price_cents INT UNSIGNED NOT NULL`, `stock INT NOT NULL DEFAULT 0`, `PRIMARY KEY (product_id)`, `UNIQUE KEY uk_product_sku (sku)`
- `customer_order`: `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK, `customer_id BIGINT UNSIGNED NOT NULL`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`, `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`, `total_cents INT UNSIGNED NOT NULL DEFAULT 0`, `PRIMARY KEY (order_id)`, `KEY idx_order_customer (customer_id, created_at)`
- `order_item`: `order_id BIGINT UNSIGNED NOT NULL` PK FK, `product_id INT UNSIGNED NOT NULL` PK FK, `quantity SMALLINT UNSIGNED NOT NULL`, `unit_price_cents INT UNSIGNED NOT NULL`, `PRIMARY KEY (order_id, product_id)`, `CONSTRAINT fk_item_order`, `CONSTRAINT fk_item_product`
#### Reads
- `product.sku`, `product.stock`, `product.product_id`, `product.price_cents` via `WHERE sku = p_sku AND stock >= p_qty FOR UPDATE`
- `order_item.quantity`, `order_item.unit_price_cents` via `SUM(quantity * unit_price_cents) WHERE order_id = p_order_id`
#### Writes
- `INSERT INTO order_item (order_id, product_id, quantity, unit_price_cents)` with `ON DUPLICATE KEY UPDATE quantity`
- `UPDATE product.stock = stock - p_qty WHERE product_id`
- `UPDATE customer_order.total_cents WHERE order_id`
#### Relationships
- `order_item.order_id -> customer_order.order_id` `ON DELETE CASCADE`
- `order_item.product_id -> product.product_id`
#### Routine logic
- `PROCEDURE add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- `SELECT ... INTO v_product_id, v_price FROM product WHERE sku` with row lock, `SIGNAL SQLSTATE '45000'` on unknown SKU or insufficient `stock`
- `INSERT order_item`, decrement `product.stock`, recompute `customer_order.total_cents` from `order_item` sum
#### Dependencies
- `product`, `customer_order`, `order_item`
