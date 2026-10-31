### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:51:32Z
- G: 
- A: 0.0.0
- X: file
---
> Models a web shop catalog and order lifecycle, reserving product stock and maintaining order totals when items are added.

#### Purpose
- Defines product, customer_order, and order_item tables for a MySQL web shop.
- Provides add_order_item procedure to place an order line, reserve stock, and recalculate the order total.

#### Type
- Tables and stored procedure in MySQL 8 / InnoDB dialect.

#### Schema
- product: product_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, sku VARCHAR(40) NOT NULL UNIQUE KEY uk_product_sku, name VARCHAR(200) NOT NULL, price_cents INT UNSIGNED NOT NULL, stock INT NOT NULL DEFAULT 0.
- customer_order: order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, customer_id BIGINT UNSIGNED NOT NULL, status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, total_cents INT UNSIGNED NOT NULL DEFAULT 0, KEY idx_order_customer (customer_id, created_at).
- order_item: order_id BIGINT UNSIGNED NOT NULL, product_id INT UNSIGNED NOT NULL, quantity SMALLINT UNSIGNED NOT NULL, unit_price_cents INT UNSIGNED NOT NULL, PRIMARY KEY (order_id, product_id).

#### Reads
- add_order_item reads product.sku, product.stock, product.product_id, product.price_cents with FOR UPDATE.
- add_order_item reads order_item.quantity and order_item.unit_price_cents for the SUM subquery filtered by order_id.

#### Writes
- add_order_item writes order_item.order_id, order_item.product_id, order_item.quantity, order_item.unit_price_cents via INSERT ... ON DUPLICATE KEY UPDATE.
- add_order_item writes product.stock by subtracting p_qty.
- add_order_item writes customer_order.total_cents by recalculating from order_item.

#### Relationships
- order_item.order_id -> customer_order.order_id (FK fk_item_order, ON DELETE CASCADE).
- order_item.product_id -> product.product_id (FK fk_item_product).

#### Routine logic
- Procedure add_order_item takes IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED.
- Selects product_id and price_cents from product where sku matches and stock is sufficient, locking the row.
- Raises SQLSTATE 45000 if no product is found.
- Inserts into order_item or increments quantity on duplicate key.
- Decrements product.stock by p_qty.
- Updates customer_order.total_cents to the sum of quantity times unit_price_cents for that order_id.

#### Dependencies
- Tables: product, customer_order, order_item.
- Constraints: uk_product_sku, idx_order_customer, fk_item_order, fk_item_product.
