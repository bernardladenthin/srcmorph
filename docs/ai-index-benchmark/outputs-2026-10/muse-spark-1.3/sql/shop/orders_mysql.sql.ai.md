### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:16:44Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop order placement with product catalog, customer orders, line items, stock reservation and order total maintenance.

#### Purpose
- Model `product` catalog, `customer_order` headers, `order_item` lines for web shop ordering.
- Provide `add_order_item` order-placement operation reserving stock and recalculating order total.

#### Type
- table, procedure, MySQL 8 dialect, `ENGINE=InnoDB`, `CHARSET=utf8mb4`, `DELIMITER //`, `AUTO_INCREMENT`, `ENUM`, `ON DUPLICATE KEY UPDATE`, `SIGNAL SQLSTATE '45000'`.

#### Schema
- `product.product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK, PRIMARY KEY `product_id`.
- `product.sku VARCHAR(40) NOT NULL` UNIQUE, UNIQUE KEY `uk_product_sku (sku)`.
- `product.name VARCHAR(200) NOT NULL`.
- `product.price_cents INT UNSIGNED NOT NULL`.
- `product.stock INT NOT NULL DEFAULT 0`.
- `customer_order.order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK, PRIMARY KEY `order_id`.
- `customer_order.customer_id BIGINT UNSIGNED NOT NULL`.
- `customer_order.status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`.
- `customer_order.created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`.
- `customer_order.total_cents INT UNSIGNED NOT NULL DEFAULT 0`.
- Index `idx_order_customer (customer_id, created_at)` on `customer_order`.
- `order_item.order_id BIGINT UNSIGNED NOT NULL` PK part, FK, part of PRIMARY KEY `(order_id, product_id)`, CONSTRAINT `fk_item_order`.
- `order_item.product_id INT UNSIGNED NOT NULL` PK part, FK, part of PRIMARY KEY `(order_id, product_id)`, CONSTRAINT `fk_item_product`.
- `order_item.quantity SMALLINT UNSIGNED NOT NULL`.
- `order_item.unit_price_cents INT UNSIGNED NOT NULL`.

#### Reads
- `product.product_id, price_cents` via `SELECT ... INTO v_product_id, v_price FROM product WHERE sku = p_sku`.
- `product.sku, stock` filter `sku = p_sku AND stock >= p_qty FOR UPDATE`.
- `order_item.quantity, unit_price_cents` via `SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id`.

#### Writes
- `order_item.order_id, product_id, quantity, unit_price_cents` via `INSERT INTO order_item ... ON DUPLICATE KEY UPDATE quantity = quantity + p_qty`.
- `product.stock` via `UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id`.
- `customer_order.total_cents` via `UPDATE customer_order SET total_cents = (...) WHERE order_id = p_order_id`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` via CONSTRAINT `fk_item_order`, `ON DELETE CASCADE`.
- `order_item.product_id -> product.product_id` via CONSTRAINT `fk_item_product`.

#### Routine logic
- Procedure `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`, locals `v_product_id INT UNSIGNED`, `v_price INT UNSIGNED`.
- Locks `product` row with `SELECT ... FOR UPDATE` checking `sku` and `stock >= p_qty`.
- Raises `SIGNAL SQLSTATE '45000'` with `unknown SKU or not enough stock` if `v_product_id IS NULL`.
- Upserts `order_item` line with `unit_price_cents = v_price`, increments `quantity` on duplicate key.
- Decrements `product.stock`, recomputes `customer_order.total_cents` as sum of line totals.

#### Dependencies
- `product`, `customer_order`, `order_item`.
- Procedure `add_order_item` depends on `product.sku, stock, price_cents`, `order_item.quantity, unit_price_cents`, `customer_order.total_cents`.
