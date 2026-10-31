### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:20:44Z
- G: 
- A: 0.0.0
- X: file
---
> Models a web shop's product catalog, customer orders, and order placement logic for adding items with stock reservation.

#### Purpose
- Define core e-commerce schema for products, orders, and order items, and provide a stored procedure to add order lines while reserving stock and updating order totals.

#### Type
- SQL object kinds: table, procedure (MySQL 8)

#### Schema
- product: product_id INT UNSIGNED NOT NULL AUTO_INCREMENT, sku VARCHAR(40) NOT NULL, name VARCHAR(200) NOT NULL, price_cents INT UNSIGNED NOT NULL, stock INT NOT NULL DEFAULT 0; PRIMARY KEY (product_id); UNIQUE KEY uk_product_sku (sku); ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
- customer_order: order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, customer_id BIGINT UNSIGNED NOT NULL, status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, total_cents INT UNSIGNED NOT NULL DEFAULT 0; PRIMARY KEY (order_id); KEY idx_order_customer (customer_id, created_at); ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
- order_item: order_id BIGINT UNSIGNED NOT NULL, product_id INT UNSIGNED NOT NULL, quantity SMALLINT UNSIGNED NOT NULL, unit_price_cents INT UNSIGNED NOT NULL; PRIMARY KEY (order_id, product_id); FOREIGN KEY (order_id) REFERENCES customer_order (order_id) ON DELETE CASCADE; FOREIGN KEY (product_id) REFERENCES product (product_id)

#### Reads
- add_order_item procedure reads product (sku, stock, price_cents), order_item (order_id, product_id, quantity, unit_price_cents), customer_order (order_id, total_cents)

#### Writes
- product: stock updated via stock = stock - p_qty
- order_item: quantity and unit_price_cents updated via INSERT/UPDATE
- customer_order: total_cents updated via subquery

#### Relationships
- order_item.order_id -> customer_order.order_id (FK, ON DELETE CASCADE)
- order_item.product_id -> product.product_id (FK)

#### Routine logic
- Parameters: p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED
- SELECT product_id, price_cents FROM product WHERE sku = p_sku AND stock >= p_qty FOR UPDATE
- Signal SQLSTATE '45000' with message 'unknown SKU or not enough stock' if no matching product
- INSERT INTO order_item (order_id, product_id, quantity, unit_price_cents) VALUES (p_order_id, v_product_id, p_qty, v_price) ON DUPLICATE KEY UPDATE quantity = quantity + p_qty
- UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id
- UPDATE customer_order SET total_cents = (SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id) WHERE order_id = p_order_id

#### Dependencies
- product table (sku, price_cents, stock)
- customer_order table (order_id, total_cents)
- order_item table (order_id, product_id, quantity, unit_price_cents)
- MySQL 8 syntax (ENUM, AUTO_INCREMENT, DELIMITER)
