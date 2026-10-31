### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:02:59Z
- G: 
- A: 0.0.0
- X: file
---
> Models web shop products, customer orders, and order items with stock reservation and order total recalculation on item addition.

#### Purpose
- Defines product catalog, customer orders, and order line items for a MySQL web shop.
- Provides a stored procedure to add an item to an order, reserve stock, and update the order total.

#### Type
- Tables and stored procedure in MySQL 8 (InnoDB, utf8mb4).

#### Schema
- product.product_id INT UNSIGNED NOT NULL AUTO_INCREMENT PK
- product.sku VARCHAR(40) NOT NULL UNIQUE KEY uk_product_sku
- product.name VARCHAR(200) NOT NULL
- product.price_cents INT UNSIGNED NOT NULL
- product.stock INT NOT NULL DEFAULT 0
- customer_order.order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PK
- customer_order.customer_id BIGINT UNSIGNED NOT NULL
- customer_order.status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'
- customer_order.created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
- customer_order.total_cents INT UNSIGNED NOT NULL DEFAULT 0
- customer_order KEY idx_order_customer (customer_id, created_at)
- order_item.order_id BIGINT UNSIGNED NOT NULL PK part
- order_item.product_id INT UNSIGNED NOT NULL PK part
- order_item.quantity SMALLINT UNSIGNED NOT NULL
- order_item.unit_price_cents INT UNSIGNED NOT NULL

#### Reads
- product.product_id, product.price_cents, product.stock filtered by product.sku
- order_item.quantity, order_item.unit_price_cents filtered by order_item.order_id

#### Writes
- order_item.order_id, order_item.product_id, order_item.quantity, order_item.unit_price_cents via INSERT ... ON DUPLICATE KEY UPDATE
- product.stock via UPDATE decrement
- customer_order.total_cents via UPDATE subquery

#### Relationships
- order_item.order_id -> customer_order.order_id ON DELETE CASCADE
- order_item.product_id -> product.product_id

#### Routine logic
- Procedure add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)
- Selects product_id and price_cents from product where sku matches and stock is sufficient, locking the row FOR UPDATE
- Signals SQLSTATE '45000' if product is not found or stock is insufficient
- Inserts into order_item or updates quantity on duplicate key
- Decrements product.stock by p_qty
- Recomputes customer_order.total_cents as sum of quantity * unit_price_cents for the order

#### Dependencies
- product
- customer_order
- order_item
