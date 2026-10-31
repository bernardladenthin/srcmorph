### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:43:51Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop domain model for product catalog with stock levels, customer orders with status lifecycle, and order line items, plus a stored procedure that adds items to an order while reserving inventory and recomputing the order total.

#### Purpose
- Defines core e-commerce schema: products with SKU/price/stock, orders with customer and status, and per-order line items
- Provides add_order_item procedure to atomically add/merge a line item, decrement stock, and refresh the order total

#### Type
- Tables: product, customer_order, order_item — MySQL 8, ENGINE=InnoDB, CHARSET=utf8mb4
- Stored procedure: add_order_item — MySQL (DELIMITER //, SIGNAL SQLSTATE)

#### Schema
- product
  - product_id INT UNSIGNED NOT NULL AUTO_INCREMENT — PRIMARY KEY
  - sku VARCHAR(40) NOT NULL — UNIQUE KEY uk_product_sku
  - name VARCHAR(200) NOT NULL
  - price_cents INT UNSIGNED NOT NULL
  - stock INT NOT NULL DEFAULT 0
- customer_order
  - order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT — PRIMARY KEY
  - customer_id BIGINT UNSIGNED NOT NULL — KEY idx_order_customer (customer_id, created_at)
  - status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
  - created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
  - total_cents INT UNSIGNED NOT NULL DEFAULT 0
- order_item
  - order_id BIGINT UNSIGNED NOT NULL — PRIMARY KEY (composite), CONSTRAINT fk_item_order FOREIGN KEY REFERENCES customer_order(order_id) ON DELETE CASCADE
  - product_id INT UNSIGNED NOT NULL — PRIMARY KEY (composite), CONSTRAINT fk_item_product FOREIGN KEY REFERENCES product(product_id)
  - quantity SMALLINT UNSIGNED NOT NULL
  - unit_price_cents INT UNSIGNED NOT NULL

#### Reads
- add_order_item reads product(sku, stock, product_id, price_cents) via SELECT ... FOR UPDATE WHERE sku = p_sku AND stock >= p_qty
- add_order_item reads order_item(order_id, quantity, unit_price_cents) via subquery SUM(quantity * unit_price_cents) WHERE order_id = p_order_id

#### Writes
- DDL creates tables: product, customer_order, order_item
- add_order_item INSERTs order_item(order_id, product_id, quantity, unit_price_cents) with ON DUPLICATE KEY UPDATE quantity = quantity + p_qty
- add_order_item UPDATEs product.stock (stock = stock - p_qty) WHERE product_id = v_product_id
- add_order_item UPDATEs customer_order.total_cents (recomputed SUM) WHERE order_id = p_order_id

#### Relationships
- order_item.order_id -> customer_order.order_id (FK fk_item_order, ON DELETE CASCADE)
- order_item.product_id -> product.product_id (FK fk_item_product)
- customer_order indexed by idx_order_customer on (customer_id, created_at); no FK to a customer table defined in this file

#### Routine logic
- add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)
- SELECT product_id, price_cents INTO v_product_id, v_price FROM product WHERE sku = p_sku AND stock >= p_qty FOR UPDATE (row lock for stock check)
- IF v_product_id IS NULL THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'unknown SKU or not enough stock'
- INSERT INTO order_item ... ON DUPLICATE KEY UPDATE quantity = quantity + p_qty (upsert line item)
- UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id (reserve stock)
- UPDATE customer_order SET total_cents = (SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id) WHERE order_id = p_order_id (recompute total)

#### Dependencies
- Tables: product, customer_order, order_item
- MySQL built-ins: SIGNAL SQLSTATE, SUM, CURRENT_TIMESTAMP, ON DUPLICATE KEY UPDATE, FOR UPDATE, ENUM
